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
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.model.CustomerListModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultCustomerListService}
 */
@IntegrationTest
public class DefaultCustomerListServiceTest extends ServicelayerTransactionalTest
{

	private static final String CUSTOMER_LIST_1 = "CustomerList1";
	private static final String CUSTOMER_LIST_2 = "CustomerList2";
	private static final String VALID_EMPLOYEE_ID = "employee1";

	private static final String TEST_AGENT1 = "testagent";
	private static final String TEST_AGENT2 = "testagent2";

	@Mock
	private UserService mockUserService;
	@Mock
	private UserModel user;

	@Resource
	private UserService userService;

	private DefaultCustomerListService customerListService;
	private CustomerListModel customerListModel1;
	private CustomerListModel customerListModel2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		customerListService = new DefaultCustomerListService();

		customerListService.setUserService(mockUserService);

		customerListModel1 = new CustomerListModel();

		customerListModel1.setName(CUSTOMER_LIST_1);
		customerListModel1.setUid(CUSTOMER_LIST_1);
		customerListModel1.setPriority(Integer.valueOf(0));

		customerListModel2 = new CustomerListModel();

		customerListModel2.setName(CUSTOMER_LIST_2);
		customerListModel2.setUid(CUSTOMER_LIST_2);
		customerListModel2.setPriority(Integer.valueOf(1));

		final Set<CustomerListModel> customerLists = new HashSet<CustomerListModel>();

		customerLists.add(customerListModel1);
		customerLists.add(customerListModel2);


		given(mockUserService.getUserGroupForUID(CUSTOMER_LIST_1, CustomerListModel.class)).willReturn(customerListModel1);
		given(mockUserService.getUserGroupForUID(CUSTOMER_LIST_2, CustomerListModel.class)).willReturn(customerListModel2);
		given(mockUserService.getUserForUID(VALID_EMPLOYEE_ID)).willReturn(user);
		given(new Boolean(mockUserService.isUserExisting(VALID_EMPLOYEE_ID))).willReturn(Boolean.TRUE);
		given(mockUserService.getAllUserGroupsForUser(user, CustomerListModel.class)).willReturn(customerLists);
		given(new Boolean(mockUserService.isMemberOfGroup(user, customerListModel1))).willReturn(Boolean.TRUE);
		given(new Boolean(mockUserService.isMemberOfGroup(user, customerListModel2))).willReturn(Boolean.TRUE);

	}

	@Test
	public void testGetCustomerListsForValidEmployee()
	{
		final List<CustomerListModel> customerListModels = customerListService.getCustomerListsForEmployee(VALID_EMPLOYEE_ID);

		Assert.assertEquals(2, customerListModels.size());
	}

	@Test
	public void testGetCustomerListForEmployee()
	{
		final CustomerListModel customerModel1 = customerListService.getCustomerListForEmployee(CUSTOMER_LIST_1, VALID_EMPLOYEE_ID);
		final CustomerListModel customerModel2 = customerListService.getCustomerListForEmployee(CUSTOMER_LIST_2, VALID_EMPLOYEE_ID);

		Assert.assertEquals(CUSTOMER_LIST_1, customerModel1.getName());
		Assert.assertEquals(CUSTOMER_LIST_1, customerModel1.getUid());

		Assert.assertEquals(CUSTOMER_LIST_2, customerModel2.getUid());
		Assert.assertEquals(CUSTOMER_LIST_2, customerModel2.getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetInvalidCustomerListForUId()
	{
		customerListService.getCustomerListForEmployee(null, VALID_EMPLOYEE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetInvalidCustomerListForEmployee()
	{
		customerListService.getCustomerListsForEmployee(null);
	}

	@Test
	public void testGetCustomerListForImpexEmployees() throws Exception
	{
		importCsv("/commerceservices/test/customerLists.impex", "UTF-8");

		customerListService.setUserService(userService);
		Assert.assertEquals(1, customerListService.getCustomerListsForEmployee(TEST_AGENT1).size()); //should get the parent list only as there is no other group is parent to this group

		Assert.assertEquals(2, customerListService.getCustomerListsForEmployee(TEST_AGENT2).size()); //should get the child list and its parent as well
	}
}
