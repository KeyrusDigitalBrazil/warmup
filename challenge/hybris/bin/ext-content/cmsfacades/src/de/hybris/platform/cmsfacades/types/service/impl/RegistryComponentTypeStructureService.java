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

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of the {@link ComponentTypeStructureService} that first attempts to get type using the registered 
 * structure type {@link ComponentTypeStructureRegistry#getComponentTypeStructure(String)}.
 * As a fallback strategy, the service will then invoke {@link ComponentTypeStructureRegistry#getAbstractComponentTypeStructure(String)} 
 * in another attempt to return the  {@link ComponentTypeStructure}. 
 *
 * @deprecated since 1811, please use de.hybris.platform.cmsfacades.types.service.impl.GenericComponentTypeStructureService
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "1811")
public class RegistryComponentTypeStructureService implements ComponentTypeStructureService
{
	private ComponentTypeStructureRegistry componentTypeStructureRegistry;
	private TypeService typeService;
	private Set<String> cmsSupportedAbstractTypecodes;
	
	@Override
	public ComponentTypeStructure getComponentTypeStructure(final String typeCode)
	{
		return getComponentTypeStructureInternal(typeCode);
	}

	@Override
	public Collection<ComponentTypeStructure> getComponentTypeStructures()
	{
		return getComponentTypeStructureRegistry().getComponentTypeStructures()
				.stream()
				.map(ComponentTypeStructure::getTypecode)
				.filter(typeCode -> typeCode != null)
				.filter(typeCode -> !typeCode.equals(AbstractCMSComponentModel._TYPECODE)) //
				.map(typeCode -> getComponentTypeStructureInternal(typeCode))
				.collect(Collectors.toList());
	}

	/**
	 * Internal method to get the component type given its typeCode.
	 * Augments the attribute lists with the abstract type definition. 
	 * @param typeCode the type of the component to get
	 * @return the {@link ComponentTypeStructure} instance
	 */
	protected ComponentTypeStructure getComponentTypeStructureInternal(final String typeCode)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeCode);
		final ComponentTypeStructure componentType = new DefaultComponentTypeStructure(Optional
				.ofNullable(getComponentTypeStructureRegistry().getComponentTypeStructure(typeCode))
				.orElse(getComponentTypeStructureRegistry().getAbstractComponentTypeStructure(composedType.getItemtype())));
		
		// augment attributes from abstract type definition 
		getAbstractTypesForComponent(componentType) //
				.stream() //
				.forEach(abstractType -> augmentTypeAttributes(componentType, abstractType));
		return componentType;
	}

	/**
	 * Merges the type attributes from the abstract type to the child type
	 *
	 * @param componentType
	 *           - the child type which attributes will be augmented
	 * @param abstractType
	 *           - the abstract type which attributes will be added to the child type attributes
	 */
	protected void augmentTypeAttributes(final ComponentTypeStructure componentType, final ComponentTypeStructure abstractType)
	{
		componentType.getAttributes().addAll(abstractType.getAttributes().stream() //
				.filter(attribute -> !containsAttribute(attribute, componentType.getAttributes())) //
				.collect(Collectors.toList()));
	}

	/**
	 * Verifies that an attribute exists in the given list of attributes.
	 *
	 * @param attribute
	 *           - the attribute to check if its presence in the given list of attributes
	 * @param attributes
	 *           - the list of attributes against which the attribute is checked
	 * @return <tt>TRUE</tt> if the attribute is already defined in the list of attributes; <tt>FALSE</tt> otherwise
	 */
	protected boolean containsAttribute(final ComponentTypeAttributeStructure attribute,
			final Set<ComponentTypeAttributeStructure> attributes)
	{
		return attributes.stream().filter(attr -> attr.getQualifier().equals(attribute.getQualifier())).findAny().isPresent();
	}

	/**
	 * Find all abstract types structure for the category defined in the given component type
	 *
	 * @param componentType
	 *           - the component type specifying the category used for filtering
	 * @return all abstract types defined for a given category
	 */
	protected List<ComponentTypeStructure> getAbstractTypesForComponent(final ComponentTypeStructure componentType)
	{
		if (getCmsSupportedAbstractTypecodes().contains(componentType.getTypecode()))
		{
			return Collections.emptyList();
		}
		else
		{
			return getCmsSupportedAbstractTypecodes().stream() //
					.map(typeCode -> getComponentTypeStructureRegistry().getComponentTypeStructure(typeCode)) //
					.filter(structure -> structure != null) //
					.filter(structure -> structure.getCategory().equals(componentType.getCategory())) //
					.collect(Collectors.toList());
		}
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

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected Set<String> getCmsSupportedAbstractTypecodes()
	{
		return cmsSupportedAbstractTypecodes;
	}

	@Required
	public void setCmsSupportedAbstractTypecodes(final Set<String> cmsSupportedAbstractTypecodes)
	{
		this.cmsSupportedAbstractTypecodes = cmsSupportedAbstractTypecodes;
	}
}
