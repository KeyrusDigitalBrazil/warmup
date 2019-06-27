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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link DefaultCustomerListFacade}
 */
@UnitTest
public class DefaultCustomerListFacadeTest
{
	private static final String ACTUAL_VALUE = "Actual Value";
	private static final String TEST_EMPLOYEE_ID = "employee1";
	private static final String TEST_CUSTOMER_LIST_MODEL1 = "customerList1";
	private static final String TEST_CUSTOMER_LIST_MODEL2 = "customerList2";
	private DefaultCustomerListFacade defaultCustomerListFacade;
	private final Map<String, Converter<UserModel, CustomerData>> convertersMap = new HashMap<>();
	private static final String implementationType = "DUMMY_IMPL";
	@Mock
	private CustomerListService customerListService;

	@Mock
	private CustomerListSearchService customerListSearchService;

	@Mock
	private Converter<CustomerListModel, UserGroupData> userGroupConverter;

	@Mock
	private Converter<UserModel, CustomerData> customerConverter;

	@Mock
	private Converter<UserModel, CustomerData> strategyConverter;

	@Mock
	private Converter<CustomerListModel, CustomerListData> customerListConverter;

	@Mock
	private PageableData pageableData;

	@Mock
	private SearchPageData<CustomerModel> searchPageData;

	private CustomerListModel customerListModel3;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultCustomerListFacade = new DefaultCustomerListFacade();
		defaultCustomerListFacade.setUserGroupConverter(userGroupConverter);
		defaultCustomerListFacade.setCustomerListService(customerListService);
		defaultCustomerListFacade.setCustomerListSearchService(customerListSearchService);
		defaultCustomerListFacade.setCustomerListImplementationStrategiesConverter(convertersMap);
		defaultCustomerListFacade.setCustomerConverter(customerConverter);
		defaultCustomerListFacade.setCustomerListConverter(customerListConverter);


		final CustomerListModel customerListModel1 = new CustomerListModel();
		customerListModel1.setName(TEST_CUSTOMER_LIST_MODEL1);
		customerListModel1.setUid(TEST_CUSTOMER_LIST_MODEL1);

		final CustomerListModel customerListModel2 = new CustomerListModel();
		customerListModel2.setName(TEST_CUSTOMER_LIST_MODEL2);
		customerListModel2.setUid(TEST_CUSTOMER_LIST_MODEL2);



		customerListModel3 = new CustomerListModel();
		customerListModel3.setName(TEST_CUSTOMER_LIST_MODEL1);
		customerListModel3.setUid(TEST_CUSTOMER_LIST_MODEL1);
		customerListModel3.setImplementationType(implementationType);

		final List<CustomerListModel> customerListModels = new ArrayList<>();
		customerListModels.add(customerListModel1);
		customerListModels.add(customerListModel1);



		final UserGroupData userGroupData1 = new UserGroupData();

		userGroupData1.setUid(TEST_CUSTOMER_LIST_MODEL1);
		userGroupData1.setName(TEST_CUSTOMER_LIST_MODEL1);

		final UserGroupData userGroupData2 = new UserGroupData();

		userGroupData2.setUid(TEST_CUSTOMER_LIST_MODEL2);
		userGroupData2.setName(TEST_CUSTOMER_LIST_MODEL2);

		final List<UserGroupData> userGroupDataList = new ArrayList<>();

		userGroupDataList.add(userGroupData1);
		userGroupDataList.add(userGroupData1);

		given(customerListService.getCustomerListsForEmployee(TEST_EMPLOYEE_ID)).willReturn(customerListModels);

		given(customerListSearchService.getPagedCustomers(ACTUAL_VALUE, TEST_EMPLOYEE_ID, pageableData, null))
				.willReturn(searchPageData);

		given(userGroupConverter.convertAll(customerListModels)).willReturn(userGroupDataList);

		given(customerListService.getCustomerListForEmployee(ACTUAL_VALUE, TEST_EMPLOYEE_ID)).willReturn(customerListModel3);
	}

	@Test
	public void testGetCustomerListsForEmployee()
	{
		final List<UserGroupData> userGroupDataList = defaultCustomerListFacade.getCustomerListsForEmployee(TEST_EMPLOYEE_ID);

		Assert.assertEquals(2, userGroupDataList.size());
	}

	@Test
	public void testGetCustomerListsForEmptyEmployee()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("Parameter employeeUid can not be null"));

		defaultCustomerListFacade.getCustomerListsForEmployee(null);
	}

	@Test
	public void testGetCustomerListsForEmptyCustomerListUID()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("Parameter customerListUid can not be null"));

		defaultCustomerListFacade.getPagedCustomersForCustomerListUID(null, TEST_EMPLOYEE_ID, null, null);
	}

	@Test
	public void testGetCustomerListsForInvalidPaginationData()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("Parameter pageableData can not be null"));

		defaultCustomerListFacade.getPagedCustomersForCustomerListUID("", TEST_EMPLOYEE_ID, null, null);
	}

	@Test
	public void testGetCustomerListsForInvalidCustomerListUID()
	{
		exception.expect(IllegalArgumentException.class);

		exception.expectMessage(Matchers.containsString("No CustomerList found for"));

		defaultCustomerListFacade.getPagedCustomersForCustomerListUID("INVALID CUSTOMER LIST", TEST_EMPLOYEE_ID, new PageableData(),
				null);
	}

	@Test
	public void testGetCustomerListsForWithValidUIDAndDefaultConverter()
	{
		defaultCustomerListFacade.getPagedCustomersForCustomerListUID(ACTUAL_VALUE, TEST_EMPLOYEE_ID, pageableData, null);
		Mockito.verify(customerConverter, times(1)).convertAll(searchPageData.getResults());
	}

	@Test
	public void testGetCustomerListsForWithValidUIDAndStrategyConverter()
	{
		convertersMap.put(implementationType, strategyConverter);

		defaultCustomerListFacade.getPagedCustomersForCustomerListUID(ACTUAL_VALUE, TEST_EMPLOYEE_ID, pageableData, null);
		Mockito.verify(customerConverter, times(0)).convertAll(searchPageData.getResults());
	}

	@Test
	public void shouldGetCustomerListForEmployee()
	{
		defaultCustomerListFacade.getCustomerListForUid(ACTUAL_VALUE, TEST_EMPLOYEE_ID);
		Mockito.verify(customerListConverter).convert(customerListModel3);
	}

	@Test
	public void shouldNotGetCustomerListForNullCustomerListUID()
	{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(Matchers.containsString("Parameter customerListUid can not be null"));

		defaultCustomerListFacade.getCustomerListForUid(null, TEST_EMPLOYEE_ID);
	}

	@Test
	public void shouldNotGetCustomerListForNullEmployeeUID()
	{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(Matchers.containsString("Parameter employeeUid can not be null"));

		defaultCustomerListFacade.getCustomerListForUid(ACTUAL_VALUE, null);
	}
}
