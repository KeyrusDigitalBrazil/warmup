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
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customersupport.CommerceCustomerSupportService;
import de.hybris.platform.ruleengineservices.rao.CustomerSupportRAO;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigCustomerSupportRAOProviderTest
{
	private ProductConfigCustomerSupportRAOProvider classUnderTest;

	@Mock
	CommerceCustomerSupportService customerSupportService;
	@Mock
	UserModel emulatedCustomer;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigCustomerSupportRAOProvider();
		classUnderTest.setCommerceCustomerSupportService(customerSupportService);
	}

	@Test
	public void testExpandFactModelActive()
	{
		Mockito.when(Boolean.valueOf(customerSupportService.isCustomerSupportAgentActive())).thenReturn(Boolean.TRUE);
		Mockito.when(customerSupportService.getEmulatedCustomer()).thenReturn(emulatedCustomer);
		final Set<CustomerSupportRAO> result = classUnderTest.expandFactModel(null);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		final CustomerSupportRAO customerSupportRAO = result.iterator().next();
		assertTrue(customerSupportRAO.getCustomerSupportAgentActive().booleanValue());
		assertTrue(customerSupportRAO.getCustomerEmulationActive().booleanValue());
	}

	@Test
	public void testExpandFactModelInactive()
	{
		Mockito.when(Boolean.valueOf(customerSupportService.isCustomerSupportAgentActive())).thenReturn(Boolean.FALSE);
		final Set<CustomerSupportRAO> result = classUnderTest.expandFactModel(null);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		final CustomerSupportRAO customerSupportRAO = result.iterator().next();
		assertFalse(customerSupportRAO.getCustomerSupportAgentActive().booleanValue());
		assertFalse(customerSupportRAO.getCustomerEmulationActive().booleanValue());
	}

	@Test
	public void testExpandFactModelNoEmulation()
	{
		Mockito.when(Boolean.valueOf(customerSupportService.isCustomerSupportAgentActive())).thenReturn(Boolean.TRUE);
		Mockito.when(customerSupportService.getEmulatedCustomer()).thenReturn(null);
		final Set<CustomerSupportRAO> result = classUnderTest.expandFactModel(null);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		final CustomerSupportRAO customerSupportRAO = result.iterator().next();
		assertTrue(customerSupportRAO.getCustomerSupportAgentActive().booleanValue());
		assertFalse(customerSupportRAO.getCustomerEmulationActive().booleanValue());
	}
}
