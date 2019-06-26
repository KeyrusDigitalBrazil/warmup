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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Support class for getting a base site linked to a SAPConfiguration. This is needed for the Backoffice
 * condition/action editors
 */
public class CPSBaseSiteProvider
{
	private static final Logger LOG = Logger.getLogger(CPSBaseSiteProvider.class);

	private static final String BASESITE_QUERY = "SELECT distinct({conf:sapproductconfig_basesite_cps}) FROM {SAPConfiguration AS conf} WHERE {conf:sapproductconfig_basesite_cps} is not null";
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Execute a flexible search on SAP Configuration and return the first found basesite assigned for CPS rules
	 *
	 * @return The assigned base site
	 */
	public BaseSiteModel getConfiguredBaseSite()
	{
		final SearchResult<BaseSiteModel> result = getFlexibleSearchService().search(BASESITE_QUERY);
		if (result.getCount() == 0)
		{
			throw new IllegalStateException("No BaseSite assigned to SAPConfiguration");
		}

		if (result.getCount() > 1)
		{
			LOG.warn("More than one BaseSite found in SAPConfiguration");
		}

		return result.getResult().get(0);
	}


	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * Set the FlexibleSearchService
	 *
	 * @param flexibleSearch
	 *           The service to run flexible search queries
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearch)
	{
		this.flexibleSearchService = flexibleSearch;
	}
}
