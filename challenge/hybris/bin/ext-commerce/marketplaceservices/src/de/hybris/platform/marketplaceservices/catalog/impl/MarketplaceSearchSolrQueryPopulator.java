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
package de.hybris.platform.marketplaceservices.catalog.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSolrQueryPopulator;
import de.hybris.platform.marketplaceservices.vendor.VendorService;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Required;


/**
 * Override implementation of get session product catalogVersions in {@link SearchSolrQueryPopulator}
 */
public class MarketplaceSearchSolrQueryPopulator extends SearchSolrQueryPopulator
{
	private VendorService vendorService;

	@Override
	protected Collection<CatalogVersionModel> getSessionProductCatalogVersions()
	{
		final Collection<CatalogVersionModel> sessionProductCatalogVersions = new HashSet<>();
		sessionProductCatalogVersions.addAll(super.getSessionProductCatalogVersions());
		sessionProductCatalogVersions.addAll(getVendorService().getActiveProductCatalogVersions());
		return sessionProductCatalogVersions;
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}
}
