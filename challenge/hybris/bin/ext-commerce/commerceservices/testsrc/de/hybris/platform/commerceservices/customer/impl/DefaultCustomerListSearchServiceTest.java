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

import static org.mockito.BDDMockito.given;

import de.hybris.platform.commerceservices.customer.CustomerListService;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.model.CustomerListModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultCustomerListSearchService}
 *
 */
public class DefaultCustomerListSearchServiceTest
{
	private static final String TEST_CUSTOMER_LIST_MODEL1 = "customerList1";
	private static final String TEST_CUSTOMER_LIST_MODEL2 = "customerList2";

	private DefaultCustomerListSearchService defaultCustomerListSearchService;

	@Mock
	private CustomerListService customerListService;

	@Mock
	private Map<String, CustomerListSearchStrategy> commerceCustomerListImplementationStrategies;

	@Mock
	private CustomerListSearchStrategy customerListSearchStrategy;

	@Mock
	private PageableData pageableData;

	private final String employeeId = "employeeId";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultCustomerListSearchService = new DefaultCustomerListSearchService();
		defaultCustomerListSearchService.setCustomerListService(customerListService);

		defaultCustomerListSearchService.setCustomerListSearchStrategyMap(commerceCustomerListImplementationStrategies);


		final CustomerListModel customerListModel1 = new CustomerListModel();
		customerListModel1.setName(TEST_CUSTOMER_LIST_MODEL1);
		customerListModel1.setUid(TEST_CUSTOMER_LIST_MODEL1);

		final String implementationType = "DUMMY_IMPL";

		final CustomerListModel customerListModel2 = new CustomerListModel();
		customerListModel2.setName(TEST_CUSTOMER_LIST_MODEL2);
		customerListModel2.setUid(TEST_CUSTOMER_LIST_MODEL2);
		customerListModel2.setImplementationType(implementationType);


		final CustomerListModel customerListModel3 = new CustomerListModel();
		customerListModel3.setName(TEST_CUSTOMER_LIST_MODEL2);
		customerListModel3.setUid(TEST_CUSTOMER_LIST_MODEL2);
		customerListModel3.setImplementationType("NO IMPL FOUND");


		final CustomerModel customerModel = new CustomerModel();

		customerModel.setName("Customer1");

		final CustomerModel customerModel2 = new CustomerModel();

		customerModel2.setName("Customer2");

		final List<CustomerModel> customerModelList = new ArrayList<>();

		customerModelList.add(customerModel);
		customerModelList.add(customerModel2);

		final SearchPageData searchResults = new SearchPageData();

		searchResults.setResults(customerModelList);


		given(customerListService.getCustomerListForEmployee("Dummy Value", employeeId)).willReturn(customerListModel1);
		given(customerListService.getCustomerListForEmployee("Actual Value", employeeId)).willReturn(customerListModel2);
		given(customerListService.getCustomerListForEmployee("NO IMPL FOUND", employeeId)).willReturn(customerListModel3);


		given(defaultCustomerListSearchService.getCustomerListSearchStrategyMap().get(implementationType))
				.willReturn(customerListSearchStrategy);

		given(customerListSearchStrategy.getPagedCustomers("Actual Value", employeeId, pageableData, null))
				.willReturn(searchResults);
	}

	@Test
	public void testGetCustomerListsForUId()
	{
		final SearchPageData<CustomerModel> CustomerModelList = defaultCustomerListSearchService.getPagedCustomers("Actual Value",
				employeeId, pageableData, null);

		Assert.assertEquals(2, CustomerModelList.getResults().size());
	}

	@Test
	public void testGetCustomerListsForEmptyUid()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("Parameter customerListUid can not be null"));

		defaultCustomerListSearchService.getPagedCustomers(null, employeeId, pageableData, null);
	}

	@Test
	public void testGetCustomerForInvalidUid()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("No CustomerList found for customerListUid"));

		defaultCustomerListSearchService.getPagedCustomers("invalid UID", employeeId, pageableData, null);
	}

	@Test
	public void testGetCustomerForValidCustomerListWithNoImpl()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("Implementation is empty for"));

		defaultCustomerListSearchService.getPagedCustomers("Dummy Value", employeeId, pageableData, null);
	}

	@Test
	public void testGetCustomerForValidCustomerListWithImplButNoMapping()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("No Implementation 'NO IMPL FOUND' found for"));

		defaultCustomerListSearchService.getPagedCustomers("NO IMPL FOUND", employeeId, pageableData, null);
	}

	@Test
	public void testGetCustomerListsNoPageableData()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("Parameter pageableData can not be null"));

		defaultCustomerListSearchService.getPagedCustomers("Actual Value", employeeId, null, null);
	}
}
