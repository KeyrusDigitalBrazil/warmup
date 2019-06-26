/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.types.service.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TYPE_CACHE_EXPIRATION;
import static java.util.function.Function.identity;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;


/**
 * Implementation of the {@link ComponentTypeStructureService} that first attempts to get type using the registered
 * structure type {@link ComponentTypeStructureRegistry#getComponentTypeStructure(String)}.
 * As a fallback strategy, it creates an instance of the {@link ComponentTypeStructure} by introspecting the Type and its
 * attributes.
 * This service will be first lookup for the {@link ComposedTypeModel} and check if one of its super type is {@link CMSItemModel}.
 * If it passes this verification, then it will convert an instance of {@link ComposedTypeModel} into a new instance of
 * {@link ComponentTypeStructure}.
 */
public class GenericComponentTypeStructureService implements ComponentTypeStructureService, InitializingBean
{
	protected static final Long DEFAULT_EXPIRATION_TIME = 360L;
	private ComponentTypeStructureRegistry componentTypeStructureRegistry;
	private TypeService typeService;
	private Set<String> typeBlacklistSet;
	private ConfigurationService configurationService;
	private Converter<ComposedTypeModel, ComponentTypeStructure> composedTypeToStructureConverter;
	
	private Supplier<Map<String, ComponentTypeStructure>> internalComponentTypeStructureMap = initializeInternalStructureMap(
			DEFAULT_EXPIRATION_TIME);

	/**
	 * @throws UnknownIdentifierException when the typeCode does not exist
	 * @throws ConversionException when the type requested does not extend CMSItem
	 */
	@Override
	public ComponentTypeStructure getComponentTypeStructure(final String typeCode)
	{
		return getInternalComponentTypeStructureMap().get().get(typeCode);
	}

	@Override
	public Collection<ComponentTypeStructure> getComponentTypeStructures()
	{
		// should contain all types inheriting from CMSItem (except for those in the type blacklist) PLUS the types solely defined in the registry
		return getInternalComponentTypeStructureMap().get().values();
	}

	/**
	 * Supplier function to initialize the ComponentTypeStructure map.
	 */
	protected Supplier<Map<String, ComponentTypeStructure>> initializeComponentTypeStructureMap()
	{
		return () ->
		{
			final Set<String> typeCodes = Sets.newLinkedHashSet();
			// add types that extend CMSItemModel
			final ComposedTypeModel cmsItemComposedType = getTypeService().getComposedTypeForCode(CMSItemModel._TYPECODE);
			typeCodes.add(cmsItemComposedType.getCode());
			cmsItemComposedType.getAllSubTypes()
					.stream()
					.forEach(subType -> typeCodes.add(subType.getCode()));

			// add all types from the registry
			typeCodes.addAll(getComponentTypeStructureRegistry().getComponentTypeStructures() //
					.stream() //
					.map(ComponentTypeStructure::getTypecode) //
					.collect(Collectors.toSet()));
			// the Set difference contains all valid types we want to support
			return typeCodes.stream()
					.filter(Objects::nonNull) //
					.filter(isAbstractType().negate()) //
					.filter(typeCode -> !getTypeBlacklistSet().contains(typeCode))
					.map(this::getComponentTypeStructureInternal) //
					.collect(Collectors.toMap(ComponentTypeStructure::getTypecode, identity(), (o, o2) -> o));
		};
	}

	/**
	 * Predicate to test if the composed type represented by the typeCode is abstract.
	 * <p>
	 * Returns <tt>TRUE</tt> if the composed type is abstract.
	 * </p>
	 */
	protected Predicate<String> isAbstractType()
	{
		return typeCode -> getTypeService().getComposedTypeForCode(typeCode).getAbstract();
	}

	/**
	 * Internal method to get the component type structure for a given typeCode
	 * @param typeCode the type code that represents the component type structure
	 * @return the {@link ComponentTypeStructure} represented by this typeCode.
	 */
	protected ComponentTypeStructure getComponentTypeStructureInternal(final String typeCode)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeCode);
		boolean isCMSItem = composedType.getCode().equals(CMSItemModel._TYPECODE) // 
				|| composedType.getAllSuperTypes() //
						.stream() //
						.anyMatch(superType -> StringUtils.equals(superType.getCode(), CMSItemModel._TYPECODE));

		// if it is not a CMS Item Type structure, then use what is defined in the registry only
		if (!isCMSItem)
		{
			return getComponentTypeStructureRegistry().getComponentTypeStructure(typeCode);
		}

		// manually set the category from the registry
		final ComponentTypeStructure componentTypeStructure = getComposedTypeToStructureConverter().convert(composedType);
		
		Optional.ofNullable(getComponentTypeStructureRegistry().getComponentTypeStructure(typeCode))
				.ifPresent(componentTypeStructureOnRegistry ->
						componentTypeStructure.setCategory(componentTypeStructureOnRegistry.getCategory()));

		// add populators from the registry
		collectTypePopulatorsFromTypeAndAncestorsInRegistry(composedType, componentTypeStructure.getPopulators());

		// adds all extra attributes from the registry
		collectExtraAttributesFromTypeAndAncestorsInRegistry(composedType, componentTypeStructure.getAttributes());

		return componentTypeStructure;
	}

	/**
	 * Recursively collects type populators from the registry until it reaches the root type (CMSItem)
	 * @param composedType the type being used to look for populators
	 * @param populators the list of populators that has to be modified
	 */
	protected void collectTypePopulatorsFromTypeAndAncestorsInRegistry(final ComposedTypeModel composedType,
			final List<Populator<ComposedTypeModel, ComponentTypeData>> populators)
	{
		// add populators from the registry
		Optional.ofNullable(getComponentTypeStructureRegistry().getComponentTypeStructure(composedType.getCode()))
				.ifPresent(componentTypeStructureOnRegistry -> populators.addAll(componentTypeStructureOnRegistry.getPopulators()));

		// check if it is necessary to continue digging for new attributes
		if (!StringUtils.equals(composedType.getCode(), CMSItemModel._TYPECODE) && composedType.getSuperType() != null)
		{
			collectTypePopulatorsFromTypeAndAncestorsInRegistry(composedType.getSuperType(), populators);
		}
	}

	/**
	 * Recursively collects extra attributes that are not in the Data Model, but it is present on the the registry
	 * @param composedType the type being used to look for populators
	 * @param attributes
	 */
	protected void collectExtraAttributesFromTypeAndAncestorsInRegistry(final ComposedTypeModel composedType,
			final Set<ComponentTypeAttributeStructure> attributes)
	{

		final Set<ComponentTypeAttributeStructure> attributesFromRegistry = Optional //
				.ofNullable(getComponentTypeStructureRegistry().getComponentTypeStructure(composedType.getCode())) //
				.map(ComponentTypeStructure::getAttributes) //
				.orElse(Sets.newHashSet());

		final Set<String> attrQualifiers = attributes.stream() //
				.map(ComponentTypeAttributeStructure::getQualifier) //
				.collect(Collectors.toSet());

		attributes.addAll(attributesFromRegistry.stream() //
				.filter(attributeOnRegistry -> !attrQualifiers.contains(attributeOnRegistry.getQualifier())) //
				.collect(Collectors.toList()));

		// check if it is necessary to continue digging for new attributes
		if (!StringUtils.equals(composedType.getCode(), CMSItemModel._TYPECODE) && composedType.getSuperType() != null)
		{
			collectExtraAttributesFromTypeAndAncestorsInRegistry(composedType.getSuperType(), attributes);
		}
	}

	protected Supplier<Map<String, ComponentTypeStructure>> getInternalComponentTypeStructureMap()
	{
		return internalComponentTypeStructureMap;
	}

	protected Supplier<Map<String, ComponentTypeStructure>> initializeInternalStructureMap(final Long expirationTime)
	{
		return Suppliers.memoizeWithExpiration(
				initializeComponentTypeStructureMap(),
				expirationTime,
				TimeUnit.MINUTES);
	}


	@Override
	public void afterPropertiesSet() throws Exception
	{
		this.internalComponentTypeStructureMap = initializeInternalStructureMap(
				getConfigurationService().getConfiguration().getLong(TYPE_CACHE_EXPIRATION, DEFAULT_EXPIRATION_TIME));

	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected Set<String> getTypeBlacklistSet()
	{
		return typeBlacklistSet;
	}

	@Required
	public void setTypeBlacklistSet(final Set<String> typeBlacklistSet)
	{
		this.typeBlacklistSet = typeBlacklistSet;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Converter<ComposedTypeModel, ComponentTypeStructure> getComposedTypeToStructureConverter()
	{
		return composedTypeToStructureConverter;
	}

	@Required
	public void setComposedTypeToStructureConverter(
			final Converter<ComposedTypeModel, ComponentTypeStructure> composedTypeToStructureConverter)
	{
		this.composedTypeToStructureConverter = composedTypeToStructureConverter;
	}

	protected ComponentTypeStructureRegistry getComponentTypeStructureRegistry()
	{
		return componentTypeStructureRegistry;
	}

	@Required
	public void setComponentTypeStructureRegistry(final ComponentTypeStructureRegistry componentTypeStructureRegistry)
	{
		this.componentTypeStructureRegistry = componentTypeStructureRegistry;
	}
}
