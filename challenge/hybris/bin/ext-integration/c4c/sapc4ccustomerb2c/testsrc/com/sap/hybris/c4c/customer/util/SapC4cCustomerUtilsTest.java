/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.util;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.testframework.Assert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.dto.C4CAddressData;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;

@UnitTest
public class SapC4cCustomerUtilsTest
{

	@InjectMocks
	private final SapC4cCustomerUtils c4cCustomerUtils = new SapC4cCustomerUtils();

	@Mock
	private Populator addressPopulator;
	@Mock
	private Populator customerPopulator;
	@Mock
	private ConfigurationService configurationService;

	private static final String ADDRESS_MAIL = "address@mail.com";
	private static final String DUMMY_TEXT = "dummy";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetAddressWithEmail()
	{

		final Configuration config = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);

		final C4CAddressData addressData = c4cCustomerUtils.getAddressWithEmail(ADDRESS_MAIL);

		Assert.assertEquals(ADDRESS_MAIL, addressData.getEmailId());
		Assert.assertEquals(DUMMY_TEXT, addressData.getEmailUsageCode());

	}

	@Test
	public void testGetAdressListForCustomerWithEmail()
	{

		final Configuration config = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);

		doNothing().when(addressPopulator).populate(Mockito.any(AddressModel.class), Mockito.any(C4CAddressData.class));
		final CustomerModel customerModel = new CustomerModel();
		final AddressModel addressModel = new AddressModel();
		setCustomerDetails(customerModel, addressModel);

		final List<C4CAddressData> addresses = c4cCustomerUtils.getAdressListForCustomer(customerModel.getAddresses());

		verify(addressPopulator, times(1)).populate(Mockito.any(AddressModel.class), Mockito.any(C4CAddressData.class));
		Assert.assertEquals(addresses.size(), 2);
	}

	@Test
	public void testGetAdressListForCustomerWithoutEmail()
	{

		final Configuration config = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);
		doNothing().when(addressPopulator).populate(Mockito.any(AddressModel.class), Mockito.any(C4CAddressData.class));

		final CustomerModel customerModel = new CustomerModel();
		final AddressModel addressModel = new AddressModel();
		setCustomerDetails(customerModel, addressModel);
		addressModel.setEmail(null);

		final List<C4CAddressData> addresses = c4cCustomerUtils.getAdressListForCustomer(customerModel.getAddresses());

		verify(addressPopulator, times(1)).populate(Mockito.any(AddressModel.class), Mockito.any(C4CAddressData.class));
		Assert.assertEquals(addresses.size(), 1);
	}

	@Test
	public void testGetCustomerDataForCustomerWithoutAddresses()
	{

		final Configuration config = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);
		doNothing().when(customerPopulator).populate(Mockito.any(CustomerModel.class), Mockito.any(C4CCustomerData.class));

		final CustomerModel customerModel = new CustomerModel();
		final AddressModel addressModel = new AddressModel();
		setCustomerDetails(customerModel, addressModel);
		customerModel.setAddresses(null);

		final C4CCustomerData customerData = c4cCustomerUtils.getCustomerDataForCustomer(customerModel,
				customerModel.getAddresses());

		verify(customerPopulator, times(1)).populate(Mockito.any(CustomerModel.class), Mockito.any(C4CCustomerData.class));
		Assert.assertEquals(customerData.getAddresses().length, 1);
	}

	@Test
	public void testGetCustomerDataForCustomerWithAddresses()
	{

		final Configuration config = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);
		doNothing().when(customerPopulator).populate(Mockito.any(CustomerModel.class), Mockito.any(C4CCustomerData.class));
		doNothing().when(addressPopulator).populate(Mockito.any(AddressModel.class), Mockito.any(C4CAddressData.class));

		final CustomerModel customerModel = new CustomerModel();
		final AddressModel addressModel = new AddressModel();
		setCustomerDetails(customerModel, addressModel);

		final C4CCustomerData customerData = c4cCustomerUtils.getCustomerDataForCustomer(customerModel,
				customerModel.getAddresses());

		verify(customerPopulator, times(1)).populate(Mockito.any(CustomerModel.class), Mockito.any(C4CCustomerData.class));
		verify(addressPopulator, times(1)).populate(Mockito.any(AddressModel.class), Mockito.any(C4CAddressData.class));
		Assert.assertEquals(customerData.getAddresses().length, 2);
	}



	private void setCustomerDetails(final CustomerModel customer, final AddressModel addressModel)
	{
		customer.setCustomerID("12345");
		customer.setName("electronics customer");
		customer.setUid("uid@mail,com");

		customer.setDefaultShipmentAddress(addressModel);
		customer.setDefaultPaymentAddress(addressModel);

		addressModel.setEmail("address@mail.com");
		addressModel.setStreetnumber("street number");
		addressModel.setStreetname("street name");
		addressModel.setTown("town");
		final CountryModel country = Mockito.mock(CountryModel.class);
		when(country.getIsocode()).thenReturn("DE");
		addressModel.setCountry(country);
		addressModel.setPostalcode("12345");
		addressModel.setPobox("12345");
		addressModel.setPhone1("9123123123");
		addressModel.setCellphone("9121212121");
		addressModel.setOwner(customer);

		final List<AddressModel> addresses = new ArrayList<>();
		addresses.add(addressModel);
		customer.setAddresses(addresses);
	}

}
