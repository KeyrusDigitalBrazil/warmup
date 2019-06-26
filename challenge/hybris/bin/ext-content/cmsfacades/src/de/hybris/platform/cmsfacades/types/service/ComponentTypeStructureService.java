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
package de.hybris.platform.cmsfacades.types.service;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;

/**
 * Service that computes the <code>ComponentTypeStructure</code> from a given TYPECODE.
 */
public interface ComponentTypeStructureService
{
	/**
	 * Get a <code>ComponentTypeStructure</code> by its typecode.
	 *
	 * @param typeCode
	 *           - the typeCode of the element to retrieve.
	 * @return the element matching the typeCode
	 * @throws UnknownIdentifierException when the typeCode does not exist
	 * @throws ConversionException when the type requested does not extend CMSItem 
	 */
	ComponentTypeStructure getComponentTypeStructure(String typeCode);

	/**
	 * Get all elements in the registry.
	 *
	 * @return all items in the registry or an empty collection if no elements are found.
	 */
	Collection<ComponentTypeStructure> getComponentTypeStructures();
}
