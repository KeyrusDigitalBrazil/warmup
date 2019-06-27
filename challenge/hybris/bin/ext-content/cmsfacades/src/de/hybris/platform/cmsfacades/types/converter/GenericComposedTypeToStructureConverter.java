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
package de.hybris.platform.cmsfacades.types.converter;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.AttributePopulatorsProvider;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.converters.Populator;	
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Sets;

/**
 * Converter class from {@link ComposedTypeModel} to {@link ComponentTypeStructure}. 
 */
public class GenericComposedTypeToStructureConverter
		implements Converter<ComposedTypeModel, ComponentTypeStructure>, InitializingBean
{

	// prototype beans
	private ObjectFactory<ComponentTypeStructure> componentTypeStructureObjectFactory;
	private ObjectFactory<ComponentTypeAttributeStructure> componentTypeAttributeStructureObjectFactory;

	// Type converter
	private Function<ComposedTypeModel, Class> typeDataClassFunction;
	private Map<String, String> structureTypeBlacklistAttributeMap;
	private Map<String, Set<String>> blacklistAttributes;

	// Attribute converter
	private AttributePopulatorsProvider attributePopulatorsProvider;
	
	@Override
	public ComponentTypeStructure convert(final ComposedTypeModel source)
	{
		final ComponentTypeStructure componentTypeStructure = getComponentTypeStructureObjectFactory().getObject();

		componentTypeStructure.setTypecode(source.getCode());
		componentTypeStructure.setTypeDataClass(getTypeDataClassFunction().apply(source));

		final List<ComponentTypeAttributeStructure> attributes = new LinkedList<>();

		collectAttributes(source, attributes);

		// set type code for all attributes
		attributes.stream().forEach(componentTypeAttributeStructure -> {
			componentTypeAttributeStructure.setTypecode(source.getCode());
		});

		componentTypeStructure.getAttributes().addAll(attributes);

		return componentTypeStructure;
	}


	/**
	 * Recursively collects attributes of a given type that is not blacklisted until it reaches the root type (CMSItem). 
	 * @param composedType the type which the attributes are collected from.
	 * @param attributes the attribute list that will be augmented by introspecting the <code>composedType</code>. 
	 */
	protected void collectAttributes(final ComposedTypeModel composedType,
			final List<ComponentTypeAttributeStructure> attributes)
	{
		final Set<String> blackListAttributes = Optional //
				.ofNullable(getBlacklistAttributes().get(composedType.getCode())) //
				.orElse(Sets.newHashSet());
		attributes.addAll(composedType.getDeclaredattributedescriptors() //
				.stream() //
				.filter(attribute -> !blackListAttributes.contains(attribute.getQualifier())) //
				.map(attribute -> convertAttributeDescriptor(attribute)) //
				.collect(toList()));

		// check if it is necessary to continue digging for new attributes
		if (!StringUtils.equals(composedType.getCode(), CMSItemModel._TYPECODE) && composedType.getSuperType() != null)
		{
			collectAttributes(composedType.getSuperType(), attributes);
		}
	}


	/**
	 * Converts an attribute descriptor into an instance of {@link ComponentTypeAttributeStructure}. 
	 * Applies registry configuration on top of each attribute as well. 
	 * @param attributeDescriptor the attribute descriptor to be converted/ 
	 * @return instance of ComponentTypeAttributeStructure populated with values from configuration on top of type service and registry. 
	 */
	protected ComponentTypeAttributeStructure convertAttributeDescriptor(final AttributeDescriptorModel attributeDescriptor)
	{
		final ComponentTypeAttributeStructure componentTypeAttributeStructure = getComponentTypeAttributeStructureObjectFactory().getObject();

		componentTypeAttributeStructure.setQualifier(attributeDescriptor.getQualifier());
		// do not set the type code on the attribute level, leave it for the parent populator to do set the value.

		final List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators = Optional
				.ofNullable(componentTypeAttributeStructure.getPopulators())
				.orElse(new LinkedList<>());

		populators.addAll(getAttributePopulatorsProvider().getAttributePopulators(attributeDescriptor));

		componentTypeAttributeStructure.setPopulators(populators);

		return componentTypeAttributeStructure;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		
		blacklistAttributes = getStructureTypeBlacklistAttributeMap()
				.entrySet().stream() //
				.collect(toMap(entry -> entry.getKey(), entry -> {
					final String[] attributes = entry.getValue().replaceAll("^[,\\s]+", "").split("[,\\s]+");
					return Sets.newHashSet(attributes);
				}));
	}


	protected Function<ComposedTypeModel, Class> getTypeDataClassFunction()
	{
		return typeDataClassFunction;
	}

	@Required
	public void setTypeDataClassFunction(final Function<ComposedTypeModel, Class> typeDataClassFunction)
	{
		this.typeDataClassFunction = typeDataClassFunction;
	}

	public Map<String, String> getStructureTypeBlacklistAttributeMap()
	{
		return structureTypeBlacklistAttributeMap;
	}

	@Required
	public void setStructureTypeBlacklistAttributeMap(final Map<String, String> structureTypeBlacklistAttributeMap)
	{
		this.structureTypeBlacklistAttributeMap = structureTypeBlacklistAttributeMap;
	}

	protected Map<String, Set<String>> getBlacklistAttributes()
	{
		return blacklistAttributes;
	}

	public void setBlacklistAttributes(final Map<String, Set<String>> blacklistAttributes)
	{
		this.blacklistAttributes = blacklistAttributes;
	}

	protected AttributePopulatorsProvider getAttributePopulatorsProvider()
	{
		return attributePopulatorsProvider;
	}

	@Required
	public void setAttributePopulatorsProvider(final AttributePopulatorsProvider attributePopulatorsProvider)
	{
		this.attributePopulatorsProvider = attributePopulatorsProvider;
	}

	protected ObjectFactory<ComponentTypeAttributeStructure> getComponentTypeAttributeStructureObjectFactory()
	{
		return componentTypeAttributeStructureObjectFactory;
	}

	@Required
	public void setComponentTypeAttributeStructureObjectFactory(
			final ObjectFactory<ComponentTypeAttributeStructure> componentTypeAttributeStructureObjectFactory)
	{
		this.componentTypeAttributeStructureObjectFactory = componentTypeAttributeStructureObjectFactory;
	}

	protected ObjectFactory<ComponentTypeStructure> getComponentTypeStructureObjectFactory()
	{
		return componentTypeStructureObjectFactory;
	}

	@Required
	public void setComponentTypeStructureObjectFactory(
			final ObjectFactory<ComponentTypeStructure> componentTypeStructureObjectFactory)
	{
		this.componentTypeStructureObjectFactory = componentTypeStructureObjectFactory;
	}

}
