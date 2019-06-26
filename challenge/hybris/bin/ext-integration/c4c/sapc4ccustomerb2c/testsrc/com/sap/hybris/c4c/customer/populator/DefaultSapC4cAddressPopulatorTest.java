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
package com.sap.hybris.c4c.customer.populator;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.testframework.Assert;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.dto.C4CAddressData;


/**
 *
 */
@UnitTest
public class DefaultSapC4cAddressPopulatorTest
{

	@InjectMocks
	private final DefaultSapC4cAddressPopulator sapC4cAddressPopulator = new DefaultSapC4cAddressPopulator();


	private static final String DUMMY_TEXT = "dummy";

	@Mock
	private ConfigurationService configurationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate() throws ConversionException
	{

		final AddressModel address = new AddressModel();
		final Configuration config = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);
		createAddress(address);

		final C4CAddressData addressData = new C4CAddressData();
		sapC4cAddressPopulator.populate(address, addressData);

		Assert.assertEquals(addressData.getEmailId(), address.getEmail());
		Assert.assertEquals(addressData.getTown(), address.getTown());
		Assert.assertEquals(addressData.getDistrict(), null);

		Assert.assertNotEquals(addressData.getAddressUsageCodes(), null);

	}

	private void createAddress(final AddressModel address)
	{

		final CustomerModel customer = new CustomerModel();
		customer.setDefaultShipmentAddress(address);
		customer.setDefaultPaymentAddress(address);

		address.setEmail("address@mail.com");
		address.setStreetnumber("street number");
		address.setStreetname("street name");
		address.setTown("town");
		final CountryModel country = Mockito.mock(CountryModel.class);
		when(country.getIsocode()).thenReturn("DE");
		address.setCountry(country);
		address.setPostalcode("12345");
		address.setPobox("12345");
		address.setPhone1("9123123123");
		address.setCellphone("9121212121");
		address.setOwner(customer);

	}
}
