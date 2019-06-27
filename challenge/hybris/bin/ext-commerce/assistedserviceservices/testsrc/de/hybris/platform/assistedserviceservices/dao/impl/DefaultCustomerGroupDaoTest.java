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
package de.hybris.platform.assistedserviceservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.dao.CustomerGroupDao;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Test class for {@link DefaultCustomerGroupDao}
 */
@IntegrationTest
public class DefaultCustomerGroupDaoTest extends ServicelayerTest
{
	@Resource
	private CustomerGroupDao customerGroupDao;

	@Resource
	private UserService userService;

	private PageableData pageableData;

	@Before
	public void setup() throws Exception
	{
		pageableData = new PageableData();
		pageableData.setPageSize(5);

		importCsv("/assistedserviceservices/test/instore_data.impex", "UTF-8");
		importCsv("/assistedserviceservices/test/pos_data.impex", "UTF-8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void nakanoStoreCustomersEmptyPaginationTest()
	{
		customerGroupDao.findAllCustomersByGroups(new ArrayList(), null);
	}

	@Test
	public void nakanoStoreCustomersTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel customerUserGroup = userService.getUserGroupForUID("POS_NAKANO");

		assertNotNull(customerUserGroup);

		userGroups.add(customerUserGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(1, customers.getResults().size());
	}

	@Test
	public void ichikawaStoreCustomersTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel customerUserGroup = userService.getUserGroupForUID("POS_ICHIKAWA");

		assertNotNull(customerUserGroup);

		userGroups.add(customerUserGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(2, customers.getResults().size());
	}

	@Test
	public void ichikawaAndNakanoStoreCustomersTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel ichikawaCustomerUserGroup = userService.getUserGroupForUID("POS_ICHIKAWA");
		final UserGroupModel nakanoCustomerUserGroup = userService.getUserGroupForUID("POS_NAKANO");

		assertNotNull(ichikawaCustomerUserGroup);
		assertNotNull(nakanoCustomerUserGroup);

		userGroups.add(ichikawaCustomerUserGroup);
		userGroups.add(nakanoCustomerUserGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(3, customers.getResults().size());
	}

	@Test
	public void getCustomersByNakanoPosTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final StoreEmployeeGroupModel nakanoEmployeeGroup = userService.getUserGroupForUID("nakanostoreemployees", StoreEmployeeGroupModel.class);

		assertNotNull(nakanoEmployeeGroup);

		userGroups.add(nakanoEmployeeGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(Collections.singletonList(nakanoEmployeeGroup.getStore()), pageableData);

		assertEquals(2, customers.getResults().size());
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user2@test.net"));
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user1@test.net"));
	}

	@Test
	public void getCustomersByIchikawaPosTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final StoreEmployeeGroupModel ichikawaEmployeeGroup = userService.getUserGroupForUID("ichikawastoreemployees", StoreEmployeeGroupModel.class);

		assertNotNull(ichikawaEmployeeGroup);

		userGroups.add(ichikawaEmployeeGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(Collections.singletonList(ichikawaEmployeeGroup.getStore()), pageableData);

		assertEquals(1, customers.getResults().size());
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user2@test.net"));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void invalidCustomerGroup()
	{
		userService.getUserGroupForUID("ICHIKAWA");
	}
}
