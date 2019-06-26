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
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
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

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;


/**
 *
 */
@UnitTest
public class DefaultSapC4cCustomerPopulatorTest
{

	@InjectMocks
	private final DefaultSapC4cCustomerPopulator sapC4cCustomerPopulator = new DefaultSapC4cCustomerPopulator();

	private static final String DUMMY_TEXT = "dummy";

	@Mock
	private CustomerNameStrategy customerNameStrategy;
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
		final CustomerModel customer = new CustomerModel();
		final Configuration config = Mockito.mock(Configuration.class);
		final String[] name = new String[2];
		name[0] = "electronics";
		name[1] = "customer";
		setCustomerDetails(customer);

		when(customerNameStrategy.splitName(Mockito.anyString())).thenReturn(name);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);

		final C4CCustomerData customerData = new C4CCustomerData();

		sapC4cCustomerPopulator.populate(customer, customerData);

		Assert.assertEquals(customerData.getCustomerId(), customer.getCustomerID());
		Assert.assertEquals(customerData.getFirstName(), name[0]);
		Assert.assertEquals(customerData.getLastName(), name[1]);

		Assert.assertNotEquals(customerData.getGender(), null);

		Assert.assertNotEquals(customerData.getHeader().getId(), null);
		Assert.assertNotEquals(customerData.getHeader().getUuid(), null);
		Assert.assertNotEquals(customerData.getHeader().getTimestamp(), null);

	}

	private void setCustomerDetails(final CustomerModel customer)
	{
		customer.setCustomerID("12345");
		customer.setName("electronics customer");

	}

}
