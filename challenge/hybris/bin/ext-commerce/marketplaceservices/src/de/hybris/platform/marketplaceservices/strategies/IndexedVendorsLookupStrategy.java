/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplaceservices.strategies;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ordersplitting.model.VendorModel;


/**
 *
 */
public interface IndexedVendorsLookupStrategy
{
	/**
	 * Get the vendors need to shown in index page
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return search page data of vendors
	 */
	SearchPageData<VendorModel> getIndexVendors(PageableData pageableData);
}
