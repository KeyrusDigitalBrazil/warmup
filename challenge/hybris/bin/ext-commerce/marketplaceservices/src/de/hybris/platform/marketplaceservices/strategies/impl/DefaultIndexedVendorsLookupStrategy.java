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
package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.marketplaceservices.strategies.IndexedVendorsLookupStrategy;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.VendorModel;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class DefaultIndexedVendorsLookupStrategy implements IndexedVendorsLookupStrategy
{

	private VendorDao vendorDao;

	@Override
	public SearchPageData<VendorModel> getIndexVendors(final PageableData pageableData)
	{
		return getVendorDao().findPagedActiveVendors(pageableData);
	}

	protected VendorDao getVendorDao()
	{
		return vendorDao;
	}

	@Required
	public void setVendorDao(final VendorDao vendorDao)
	{
		this.vendorDao = vendorDao;
	}

}
