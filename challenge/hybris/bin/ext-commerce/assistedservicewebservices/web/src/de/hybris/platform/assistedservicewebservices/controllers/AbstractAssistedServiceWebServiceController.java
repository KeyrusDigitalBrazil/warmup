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
package de.hybris.platform.assistedservicewebservices.controllers;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customer.CustomerListFacade;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import javax.annotation.Resource;


public abstract class AbstractAssistedServiceWebServiceController
{
	@Resource(name = "customerListFacade")
	private CustomerListFacade customerListFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	protected <T> SearchPageData<T> createSearchPageData(final List<T> entries, final PaginationData paginationData)
	{
		final SearchPageData<T> customerSearchPageData = new SearchPageData<>();
		customerSearchPageData.setResults(entries);
		customerSearchPageData.setPagination(paginationData);
		return customerSearchPageData;
	}

	public CustomerListFacade getCustomerListFacade()
	{
		return customerListFacade;
	}

	public CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}
}
