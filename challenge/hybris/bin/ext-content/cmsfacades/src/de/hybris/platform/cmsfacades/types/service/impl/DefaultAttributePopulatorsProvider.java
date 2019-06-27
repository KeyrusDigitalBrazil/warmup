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

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.AttributePopulatorsProvider;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;


public class DefaultAttributePopulatorsProvider implements AttributePopulatorsProvider, InitializingBean
{

	private ComponentTypeStructureRegistry componentTypeStructureRegistry;

	private Map<Predicate<AttributeDescriptorModel>, List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>>> attributePredicatePopulatorListMap;

	/**
	 * merge the registry overrides into the attributePredicatePopulatorListMap
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		getComponentTypeStructureRegistry().getComponentTypeStructures()
				.stream()
				.flatMap(ComponentTypeStructure -> ComponentTypeStructure.getAttributes().stream())
				.filter(attr -> attr.getClass().equals(DefaultComponentTypeAttributeStructure.class))
				//
				.forEach(
						componentTypeAttributeStructure ->
						{

							Predicate<AttributeDescriptorModel> predicate = (attributeDescriptorModel) -> attributeDescriptorModel
									.getEnclosingType().getCode().equals(componentTypeAttributeStructure.getTypecode())
									&& attributeDescriptorModel.getQualifier().equals(componentTypeAttributeStructure.getQualifier());

							attributePredicatePopulatorListMap.put(predicate, componentTypeAttributeStructure.getPopulators());
						});

	}

	@Override
	public List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> getAttributePopulators(
			final AttributeDescriptorModel attributeDescriptor)
	{
		return getAttributePredicatePopulatorListMap()
				.entrySet() //
				.stream() //
				.filter(entry -> entry.getKey().test(attributeDescriptor)) //
				.flatMap(entry -> entry.getValue().stream()) //
				.collect(Collectors.toList());
	}

	protected Map<Predicate<AttributeDescriptorModel>, List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>>>
			getAttributePredicatePopulatorListMap()
	{
		return attributePredicatePopulatorListMap;
	}

	@Required
	public void setAttributePredicatePopulatorListMap(
			final Map<Predicate<AttributeDescriptorModel>, List<Populator<AttributeDescriptorModel,
			ComponentTypeAttributeData>>> attributePredicatePopulatorListMap)
	{
		this.attributePredicatePopulatorListMap = attributePredicatePopulatorListMap;
	}

	@Required
	public void setComponentTypeStructureRegistry(ComponentTypeStructureRegistry componentTypeStructureRegistry)
	{
		this.componentTypeStructureRegistry = componentTypeStructureRegistry;
	}

	protected ComponentTypeStructureRegistry getComponentTypeStructureRegistry()
	{
		return componentTypeStructureRegistry;
	}
}
