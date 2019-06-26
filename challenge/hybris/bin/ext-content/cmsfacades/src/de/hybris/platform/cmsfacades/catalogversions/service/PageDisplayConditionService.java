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
package de.hybris.platform.cmsfacades.catalogversions.service;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.DisplayConditionData;

import java.util.List;


/**
 * Service to determine the display conditions available for all page types defined in a catalog version. A display
 * condition determines if a page is a PRIMARY page or a VARIATION page. It is used with page restrictions.
 */
public interface PageDisplayConditionService
{
	/**
	 * Finds all display conditions available for the active catalog version.
	 *
	 * @return a list of {@DisplayConditionData}
	 */
	List<DisplayConditionData> getDisplayConditions();

	/**
	 * Finds all display conditions available for the given catalog version.
	 *
	 * @param catalogVersion the catalog version
	 * @return a list of {@DisplayConditionData}
	 */
	List<DisplayConditionData> getDisplayConditions(CatalogVersionModel catalogVersion);
}
