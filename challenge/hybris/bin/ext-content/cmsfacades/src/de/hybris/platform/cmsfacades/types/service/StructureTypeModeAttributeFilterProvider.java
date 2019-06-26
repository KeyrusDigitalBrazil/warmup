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

import de.hybris.platform.cmsfacades.data.StructureTypeMode;

import java.util.List;

/**
 * Structure Type Mode Provider Interface
 */
public interface StructureTypeModeAttributeFilterProvider
{

	/**
	 * Returns the structure type mode list given its type code and structure mode. 
	 * @param typeCode the type code
	 * @param mode the structure type mode
	 * @return a list of {@link StructureTypeModeAttributeFilter}; never null. 
	 */
	List<StructureTypeModeAttributeFilter> getStructureTypeModeAttributeFilters(final String typeCode, final StructureTypeMode mode);

	/**
	 * Adds a new Structure Type Mode to the existing list of mode definitions. 
	 */
	void addStructureTypeModeAttributeFilter(StructureTypeModeAttributeFilter mode);
}
