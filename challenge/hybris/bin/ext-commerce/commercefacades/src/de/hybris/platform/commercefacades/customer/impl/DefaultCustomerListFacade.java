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
package de.hybris.platform.commercefacades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.customer.CustomerListFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.CustomerListData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.customer.CustomerListSearchService;
import de.hybris.platform.commerceservices.customer.CustomerListService;
import de.hybris.platform.commerceservices.model.CustomerListModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Default customer list facade concrete class which implements {@link CustomerListFacade}
 *
 */
public class DefaultCustomerListFacade implements CustomerListFacade
{
	//holds different customer list implementations
	private CustomerListService customerListService;
	private CustomerListSearchService customerListSearchService;
	private Converter<CustomerListModel, UserGroupData> userGroupConverter;
	private Map<String, Converter<UserModel, CustomerData>> customerListImplementationStrategiesConverter;
	private Converter<UserModel, CustomerData> customerConverter;
	private Converter<CustomerListModel, CustomerListData> customerListConverter;


	@Override
	public List<UserGroupData> getCustomerListsForEmployee(final String employeeUid)
	{
		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		return getUserGroupConverter().convertAll(getCustomerListService().getCustomerListsForEmployee(employeeUid));
	}

	@Override
	public <T extends CustomerData> SearchPageData<T> getPagedCustomersForCustomerListUID(final String customerListUid,
			final String employeeUid, final PageableData pageableData, final Map<String, Object> parameterMap)
	{
		validateParameterNotNullStandardMessage("customerListUid", customerListUid);

		validateParameterNotNullStandardMessage("pageableData", pageableData);

		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		final CustomerListModel customerListModel = getCustomerListService().getCustomerListForEmployee(customerListUid,
				employeeUid);

		validateParameterNotNull(customerListModel,
				String.format("No CustomerList found for customerListUid '%1$s'", customerListUid));

		final String implementationType = customerListModel.getImplementationType();

		final SearchPageData<CustomerModel> searchPageData = getCustomerListSearchService().getPagedCustomers(customerListUid,
				employeeUid, pageableData, parameterMap);

		List<CustomerData> customerDataList = null;

		final Converter<UserModel, CustomerData> strategyConverter = getCustomerListImplementationStrategiesConverter()
				.get(implementationType);

		if (null == strategyConverter)
		{
			customerDataList = getCustomerConverter().convertAll(searchPageData.getResults());
		}
		else
		{
			customerDataList = strategyConverter.convertAll(searchPageData.getResults());
		}

		final SearchPageData<T> customersSearchPageData = new SearchPageData<T>();

		customersSearchPageData.setResults((List<T>) customerDataList);
		customersSearchPageData.setPagination(searchPageData.getPagination());
		customersSearchPageData.setSorts(searchPageData.getSorts());

		return customersSearchPageData;
	}

	@Override
	public CustomerListData getCustomerListForUid(final String customerListUid, final String employeeUid)
	{
		validateParameterNotNullStandardMessage("customerListUid", customerListUid);
		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		return getCustomerListConverter()
				.convert(getCustomerListService().getCustomerListForEmployee(customerListUid, employeeUid));
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

	protected Converter<CustomerListModel, UserGroupData> getUserGroupConverter()
	{
		return userGroupConverter;
	}

	@Required
	public void setUserGroupConverter(final Converter<CustomerListModel, UserGroupData> userGroupConverter)
	{
		this.userGroupConverter = userGroupConverter;
	}

	protected Map<String, Converter<UserModel, CustomerData>> getCustomerListImplementationStrategiesConverter()
	{
		return customerListImplementationStrategiesConverter;
	}

	@Required
	public void setCustomerListImplementationStrategiesConverter(
			final Map<String, Converter<UserModel, CustomerData>> customerListImplementationStrategiesConverter)
	{
		this.customerListImplementationStrategiesConverter = customerListImplementationStrategiesConverter;
	}

	protected Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	@Required
	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

	protected CustomerListSearchService getCustomerListSearchService()
	{
		return customerListSearchService;
	}

	@Required
	public void setCustomerListSearchService(final CustomerListSearchService customerListSearchService)
	{
		this.customerListSearchService = customerListSearchService;
	}

	protected Converter<CustomerListModel, CustomerListData> getCustomerListConverter()
	{
		return customerListConverter;
	}

	@Required
	public void setCustomerListConverter(final Converter<CustomerListModel, CustomerListData> customerListConverter)
	{
		this.customerListConverter = customerListConverter;
	}

}
