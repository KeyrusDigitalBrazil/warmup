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
package de.hybris.platform.cmsfacades.types;


import de.hybris.platform.cmsfacades.data.ComponentTypeData;

import java.util.List;


/**
 * Facade for getting CMS component type information about available types and their attributes.
 */
public interface ComponentTypeFacade
{
	/**
	 * Find all cms component types. This does not include abstract component types nor action component types.
	 *
	 * @return list of component types; never <tt>null</tt>
	 */
	List<ComponentTypeData> getAllComponentTypes();

	/**
	 * Find all component types by category. This does not include abstract component types nor action component types.
	 *
	 * @param category
	 *           - the category of the component type to retrieve
	 * @return list of types; never <tt>null</tt>
	 */
	List<ComponentTypeData> getAllComponentTypes(final String category);

	/**
	 * Get a single cms component type.
	 *
	 * @param code
	 *           - the type code of the component type to retrieve
	 * @return the cms component type
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing types
	 */
	ComponentTypeData getComponentTypeByCode(final String code) throws ComponentTypeNotFoundException;
	
	
	/**
	 * Get a single component type structure for a given structure type mode.
	 *
	 * @param code
	 *           - the type code of the component type to retrieve
	 * @param mode
	 *           - the mode of the structure type
	 * @return the component type structure or null when the code and mode provided do not match any existing types
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing types
	 */
	ComponentTypeData getComponentTypeByCodeAndMode(final String code, String mode) throws ComponentTypeNotFoundException;

}
