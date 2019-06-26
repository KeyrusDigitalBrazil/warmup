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
package de.hybris.platform.commerceservices.customer.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultCustomerAccountDao}
 */
@IntegrationTest
public class DefaultCustomerAccountDaoTest extends ServicelayerTest
{
	private static final String TEST_CUSTOMER_UID = "accountcustomer@test.com";

	@Resource
	private UserService userService;

	@Resource
	private DefaultCustomerAccountDao defaultCustomerAccountDao;

	@Resource
	private CommonI18NService commonI18NService;

	private CustomerModel customer;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		importCsv("/commerceservices/test/testCustomerAccount.impex", "utf-8");
		customer = userService.getUserForUID(TEST_CUSTOMER_UID, CustomerModel.class);
	}

	@Test
	public void shouldGetDeliveryAddress()
	{
		final List<CountryModel> deliveryCountries = new ArrayList<>();
		deliveryCountries.add(commonI18NService.getCountry("US"));
		List<AddressModel> deliveryAddress = defaultCustomerAccountDao.findAddressBookDeliveryEntriesForCustomer(customer,
				deliveryCountries);

		assertNotNull("deliveryAddress is null", deliveryAddress);
		assertEquals("deliveryAddress size is not 1", 1, deliveryAddress.size());

		deliveryCountries.add(commonI18NService.getCountry("GB"));
		deliveryAddress = defaultCustomerAccountDao.findAddressBookDeliveryEntriesForCustomer(customer, deliveryCountries);
		assertNotNull("deliveryAddress is null", deliveryAddress);
		assertEquals("deliveryAddress size is not 2", 2, deliveryAddress.size());
	}

	@Test
	public void shouldNotGetDeliveryAddress()
	{
		final List<CountryModel> deliveryCountries = new ArrayList<>();
		deliveryCountries.add(commonI18NService.getCountry("NONEXIST"));
		final List<AddressModel> deliveryAddress = defaultCustomerAccountDao.findAddressBookDeliveryEntriesForCustomer(customer,
				deliveryCountries);

		assertNotNull("deliveryAddress is null", deliveryAddress);
		assertEquals("deliveryAddress size is not 0", 0, deliveryAddress.size());
	}

}
