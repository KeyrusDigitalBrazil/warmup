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

import static com.google.common.collect.Maps.newHashMap;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TYPE_CACHE_EXPIRATION;
import static de.hybris.platform.cmsfacades.data.StructureTypeMode.DEFAULT;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmsfacades.types.service.AttributeModePopulatorsProvider;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilter;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilterProvider;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;


/**
 * Default implementation of {@link StructureTypeModeService}
 */
public class DefaultStructureTypeModeService implements StructureTypeModeService, InitializingBean
{

	protected static final Long DEFAULT_EXPIRATION_TIME = 360l;
	private ComponentTypeStructureService componentTypeStructureService;
	private TypeService typeService;
	private ConfigurationService configurationService;
	private StructureTypeModeAttributeFilterProvider structureTypeModeAttributeFilterProvider;
	private AttributeModePopulatorsProvider attributeModePopulatorsProvider;

	private Supplier<Map<String, Map<StructureTypeMode, ComponentTypeStructure>>> componentTypeDataSupplierMap = initializeInternalStructureMap(
			DEFAULT_EXPIRATION_TIME);

	@Override
	public ComponentTypeStructure getComponentTypeByCodeAndMode(final String typeCode, final StructureTypeMode structureTypeMode)
	{
		return ofNullable(getComponentTypeDataSupplierMap().get().get(typeCode)) //
				.orElse(newHashMap()).get(structureTypeMode);
	}

	/**
	 * Internal method to initialize the Map that will serve as a data store for this service.
	 *
	 * @return the supplier method to get the {@code Map<String, Map<StructureTypeMode, ComponentTypeData>>}
	 */
	protected Supplier<Map<String, Map<StructureTypeMode, ComponentTypeStructure>>> initializeComponentTypeDataSupplier()
	{
		return () -> {
			final Map<String, Map<StructureTypeMode, ComponentTypeStructure>> componentTypeDataMap;

			// get all types that are defined
			componentTypeDataMap = getComponentTypeStructureService().getComponentTypeStructures() //
					.stream() //
					.map(ComponentTypeStructure::getTypecode) //
					.collect(toMap(typeCode -> typeCode, typeCode -> getComponentTypesByCodeInternal(typeCode), (o1, o2) -> o1));
			return componentTypeDataMap;
		};
	}

	/**
	 * Internal method to build the <code>Map<StructureTypeMode, ComponentTypeData></code> for a given type code.
	 *
	 * @param typeCode
	 *           the Item Type Code
	 * @return a map where the keys are the {@code StructureTypeMode} and the values are {@code ComponentTypeData}
	 */
	protected Map<StructureTypeMode, ComponentTypeStructure> getComponentTypesByCodeInternal(final String typeCode)
	{
		final Map<StructureTypeMode, ComponentTypeStructure> structureTypeModeMap = new EnumMap(StructureTypeMode.class);

		// merge with the information from this extension's registry
		final ComponentTypeStructure baseTypeStructure = getComponentTypeStructureService().getComponentTypeStructure(typeCode);

		// update the map with the other modes, but now inheriting also configuration from DEFAULT mode
		stream(StructureTypeMode.values()) //
				.forEach(structureTypeMode -> structureTypeModeMap.put(structureTypeMode,
						convertTypeStructureData(structureTypeMode, baseTypeStructure)));

		// for each attribute and mode, augment the ppulator list
		structureTypeModeMap.entrySet() //
				.forEach(entry -> augmentAttributePopulators(entry.getKey(), entry.getValue()));

		return structureTypeModeMap;
	}

	/**
	 * Augment the attribute's populator list using the {@link AttributeModePopulatorsProvider}.
	 *
	 * @param mode
	 *           the current mode being inspected.
	 * @param typeStructure
	 *           the typeStructure with the attributes to be inspected.
	 */
	protected void augmentAttributePopulators(final StructureTypeMode mode, final ComponentTypeStructure typeStructure)
	{
		typeStructure.getAttributes().stream()
				.forEach(attribute -> getAttributeDescriptor(typeStructure.getTypecode(), attribute.getQualifier()) //
						.ifPresent(attributeDescriptor -> attribute.getPopulators() //
								.addAll(getAttributeModePopulatorsProvider().getAttributePopulators(attributeDescriptor, mode))));
	}

	protected Optional<AttributeDescriptorModel> getAttributeDescriptor(final String typecode, final String qualifier)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typecode);
		return Stream.of(composedType.getDeclaredattributedescriptors(), composedType.getInheritedattributedescriptors()) //
				.flatMap(Collection::stream) //
				.filter(attribute -> attribute.getQualifier().equals(qualifier)) //
				.findFirst();
	}

	/**
	 * Convert the {@link ComponentTypeStructure} into {@link ComponentTypeData}. It creates a new object as the image of
	 * the DEFAULT, if not empty, or the BASE, which is never null.
	 *
	 * @param mode
	 *           the mode for this current type structure view.
	 * @param baseTypeStructure
	 *           the BASE type structure
	 * @para defaultTypeStructureOptional optional object the DEFAULT type structure
	 * @return the component type structure
	 */
	protected ComponentTypeStructure convertTypeStructureData(final StructureTypeMode mode,
			final ComponentTypeStructure baseTypeStructure)
	{
		// creates the new Structure as the image of the DEFAULT, if it is present, otherwise, create as the image of the BASE
		final ComponentTypeStructure typeStructure = new DefaultComponentTypeStructure(baseTypeStructure);
		// clear attributes before adding the ones related to this mode
		typeStructure.getAttributes().clear();

		// collect unique attributes
		final Set<ComponentTypeAttributeStructure> attributes = baseTypeStructure //
				.getAttributes() //
				.stream() //
				.map(typeAttributeStructure -> new DefaultComponentTypeAttributeStructure(typeAttributeStructure)) //
				.collect(toCollection(LinkedHashSet::new));

		final Set<String> attributeOrder = new LinkedHashSet<>();

		// apply DEFAULT mode first
		applyStructureTypeModeData(typeStructure.getTypecode(), DEFAULT, baseTypeStructure, attributes, attributeOrder);
		// apply the current mode, so it inherits the DEFAULT configuration
		applyStructureTypeModeData(typeStructure.getTypecode(), mode, baseTypeStructure, attributes, attributeOrder);

		// sets the attribute's position
		attributes.stream() //
				.forEach(attribute -> setAttributePosition(attribute, attributeOrder));

		// final list of ordered attributes defined in the mode data.
		// attribute set is an instance of LinkedHashSet; should follow order.
		typeStructure.getAttributes().addAll(attributes //
				.stream() //
				.sorted((o1, o2) -> o1.getPosition().compareTo(o2.getPosition())) //
				.collect(Collectors.toList()) //
		);

		return typeStructure;
	}

	/**
	 * Apply the Structure Type Mode defined for this Type.
	 *
	 * @param typeCode
	 *           the type code we are want to apply the mode into.
	 * @param mode
	 *           the mode definition
	 * @param baseTypeStructure
	 *           the base type Structure that is mode-agnostic.
	 * @param attributes
	 *           the pre-built set of attributes defined by the base.
	 * @param attributeOrder
	 *           the final attribute order
	 */
	protected void applyStructureTypeModeData(final String typeCode, final StructureTypeMode mode,
			final ComponentTypeStructure baseTypeStructure, final Set<ComponentTypeAttributeStructure> attributes,
			final Set<String> attributeOrder)
	{
		// selects the last mode data that applies. If it does not exist, then returns an empty version of the mode data
		final Deque<StructureTypeModeAttributeFilter> structureTypeModeAttributeFilterList = getStructureTypeModeData(typeCode,
				mode);

		structureTypeModeAttributeFilterList.descendingIterator().forEachRemaining(structureTypeModeData -> {
			final Set<String> includes = new HashSet<>(structureTypeModeData.getIncludes());
			final Set<String> excludes = new HashSet<>(structureTypeModeData.getExcludes());
			final List<String> order = new ArrayList<>(structureTypeModeData.getOrder());

			// include attributes from BASE mode.
			// first remove attributes already included to avoid duplication
			attributes.removeIf(attribute -> includes.contains(attribute.getQualifier()));
			// second adds attributes that must be included.
			attributes.addAll(baseTypeStructure //
					.getAttributes().stream() //
					.filter(attribute -> includes.contains(attribute.getQualifier())) //
					.map(typeAttributeStructure -> new DefaultComponentTypeAttributeStructure(typeAttributeStructure)) //
					.collect(toSet()));

			// excludes attributes from the list and sort in order
			attributes.removeIf(attribute -> excludes.contains(attribute.getQualifier()));

			// updates the final attribute order list
			attributeOrder.removeAll(order);
			attributeOrder.addAll(order);

		});
	}

	/**
	 * Get the mode defintion for this type or its super type, if one does not exist.
	 *
	 * @param typeCode
	 *           the type code that defines a mode.
	 * @param mode
	 *           the structure mode required for this type
	 * @return the StructureTypeModeData defined in the configuration, or empty() if none.
	 */
	protected Deque<StructureTypeModeAttributeFilter> getStructureTypeModeData(final String typeCode, final StructureTypeMode mode)
	{
		final Deque<StructureTypeModeAttributeFilter> structureModeDataList = new LinkedList<>();

		getStructureTypeModeData(typeCode, mode, structureModeDataList);

		return structureModeDataList;

	}

	/**
	 * Recursively collects the the mode definition for its type and for its parent's type.
	 *
	 * @param typeCode
	 *           the current type code possibly owning the mode definition
	 * @param mode
	 *           the mode definition
	 * @param structureModeDataList
	 *           the final list of modes collected.
	 */
	protected void getStructureTypeModeData(final String typeCode, final StructureTypeMode mode,
			final Deque<StructureTypeModeAttributeFilter> structureModeDataList)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeCode);

		final Deque<StructureTypeModeAttributeFilter> internalDeque = new LinkedList<>(
				getStructureTypeModeAttributeFilterProvider().getStructureTypeModeAttributeFilters(typeCode, mode));

		internalDeque.descendingIterator()
				.forEachRemaining(structureTypeModeData -> structureModeDataList.add(structureTypeModeData));

		if (composedType.getSuperType() != null)
		{
			getStructureTypeModeData(composedType.getSuperType().getCode(), mode, structureModeDataList);
		}
	}

	/**
	 * Sets the attribute's position to be equal to the position of the attribute in the ordered list, or to be
	 * Integer.MAX_VALUE, if the attribute is not present in the ordered list.
	 *
	 * @param attribute
	 *           the ComponentTypeAttributeStructure we want to set the position
	 * @param attributeOrder
	 *           the ordered list
	 */
	protected void setAttributePosition(final ComponentTypeAttributeStructure attribute, final Set<String> attributeOrder)
	{
		final List<String> order = new ArrayList<>(attributeOrder);

		final Integer position = order.indexOf(attribute.getQualifier());

		((DefaultComponentTypeAttributeStructure) attribute).setPosition(position == -1 ? Integer.MAX_VALUE : position);
	}

	protected Supplier<Map<String, Map<StructureTypeMode, ComponentTypeStructure>>> getComponentTypeDataSupplierMap()
	{
		return componentTypeDataSupplierMap;
	}

	protected Supplier<Map<String, Map<StructureTypeMode, ComponentTypeStructure>>> initializeInternalStructureMap(
			final Long expirationTime)
	{
		componentTypeDataSupplierMap = Suppliers.memoizeWithExpiration(initializeComponentTypeDataSupplier(), expirationTime,
				TimeUnit.MINUTES);
		return componentTypeDataSupplierMap;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		this.componentTypeDataSupplierMap = initializeInternalStructureMap(
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

	protected ComponentTypeStructureService getComponentTypeStructureService()
	{
		return componentTypeStructureService;
	}

	@Required
	public void setComponentTypeStructureService(final ComponentTypeStructureService componentTypeStructureService)
	{
		this.componentTypeStructureService = componentTypeStructureService;
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

	protected AttributeModePopulatorsProvider getAttributeModePopulatorsProvider()
	{
		return attributeModePopulatorsProvider;
	}

	@Required
	public void setAttributeModePopulatorsProvider(final AttributeModePopulatorsProvider attributeModePopulatorsProvider)
	{
		this.attributeModePopulatorsProvider = attributeModePopulatorsProvider;
	}

	protected StructureTypeModeAttributeFilterProvider getStructureTypeModeAttributeFilterProvider()
	{
		return structureTypeModeAttributeFilterProvider;
	}

	@Required
	public void setStructureTypeModeAttributeFilterProvider(
			final StructureTypeModeAttributeFilterProvider structureTypeModeAttributeFilterProvider)
	{
		this.structureTypeModeAttributeFilterProvider = structureTypeModeAttributeFilterProvider;
	}
}
