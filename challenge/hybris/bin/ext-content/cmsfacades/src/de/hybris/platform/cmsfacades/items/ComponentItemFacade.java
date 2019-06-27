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
package de.hybris.platform.cmsfacades.items;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.Collection;


/**
 * Component facade interface which deals with methods related to component operations.
 *
 */
public interface ComponentItemFacade
{

	/**
	 * Returns the {@link AbstractCMSComponentData} object by component id restricted by categoryCode or productCode or
	 * catalogCode.
	 *
	 * @param componentId
	 *           the component id
	 * @param categoryCode
	 *           the optional category code
	 * @param productCode
	 *           the optional product code
	 * @param catalogCode
	 *           the optional catalog code
	 * @return the {@link AbstractCMSComponentData} object
	 * @throws CMSItemNotFoundException
	 *            if the component is restricted or not visible.
	 */
	AbstractCMSComponentData getComponentById(final String componentId, final String categoryCode, final String productCode,
			final String catalogCode) throws CMSItemNotFoundException;

	/**
	 * Returns the list of found {@link AbstractCMSComponentData} in {@link SearchPageData}. The result is restricted by
	 * categoryCode or productCode or catalogCode.
	 *
	 * @param componentIds
	 *           the list of component id
	 * @param categoryCode
	 *           the optional category code
	 * @param productCode
	 *           the optional product code
	 * @param catalogCode
	 *           the optional catalog code
	 * @param searchPageData
	 *           the searchPageData contains requested pagination and sorting information
	 * @return the list of found {@link AbstractCMSComponentData} in {@link SearchPageData}. If nothing is found the
	 *         empty list is returned.
	 */
	SearchPageData<AbstractCMSComponentData> getComponentsByIds(final Collection<String> componentIds, final String categoryCode,
			final String productCode, final String catalogCode, final SearchPageData searchPageData);
}
