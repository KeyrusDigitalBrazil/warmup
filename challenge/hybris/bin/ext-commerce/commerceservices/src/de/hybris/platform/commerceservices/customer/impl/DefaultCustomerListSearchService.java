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
package de.hybris.platform.commerceservices.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.customer.CustomerListSearchService;
import de.hybris.platform.commerceservices.customer.CustomerListService;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.model.CustomerListModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Concrete implementation for the customer list search service which internally uses strategies and delegate the actual
 * search to the strategy
 *
 */
public class DefaultCustomerListSearchService implements CustomerListSearchService
{
	private Map<String, CustomerListSearchStrategy> customerListSearchStrategyMap;

	private CustomerListService customerListService;

	@Override
	public <T extends CustomerModel> SearchPageData<T> getPagedCustomers(final String customerListUid, final String employeeUid,
			final PageableData pageableData, final Map<String, Object> parameterMap)
	{

		validateParameterNotNullStandardMessage("customerListUid", customerListUid);

		validateParameterNotNullStandardMessage("pageableData", pageableData);

		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		final CustomerListModel customerListModel = getCustomerListService().getCustomerListForEmployee(customerListUid,
				employeeUid);

		validateParameterNotNull(customerListModel,
				String.format("No CustomerList found for customerListUid '%1$s'", customerListUid));

		final String implementationType = customerListModel.getImplementationType();

		validateParameterNotNull(implementationType,
				String.format("Implementation is empty for customerListUid '%1$s'", customerListUid));

		final CustomerListSearchStrategy customerListSearchStrategyImpl = getCustomerListSearchStrategyMap()
				.get(implementationType);

		validateParameterNotNull(customerListSearchStrategyImpl,
				String.format("No Implementation '%1$s' found for customerListUid '%2$s'", implementationType, customerListUid));

		return customerListSearchStrategyImpl.getPagedCustomers(customerListUid, employeeUid, pageableData, parameterMap);
	}

	protected Map<String, CustomerListSearchStrategy> getCustomerListSearchStrategyMap()
	{
		return customerListSearchStrategyMap;
	}

	@Required
	public void setCustomerListSearchStrategyMap(final Map<String, CustomerListSearchStrategy> customerListSearchStrategyMap)
	{
		this.customerListSearchStrategyMap = customerListSearchStrategyMap;
	}

	protected CustomerListService getCustomerListService()
	{
		return customerListService;
	}

	@Required
	public void setCustomerListService(final CustomerListService customerListService)
	{
		this.customerListService = customerListService;
	}
}
