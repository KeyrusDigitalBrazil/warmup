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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.CPQConfiguratorSettingsModel;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@UnitTest
public class CPQConfigurationHandlerTest
{
	private CPQConfigurationHandler classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new CPQConfigurationHandler();
	}

	@Test
	public void testCreateProductInfo() throws IllegalArgumentException
	{
		final CPQConfiguratorSettingsModel productSettings = new CPQConfiguratorSettingsModel();
		assertNotNull(classUnderTest.createProductInfo(productSettings));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateProductInfoNonCPQSettings() throws IllegalArgumentException
	{
		classUnderTest.createProductInfo(null);
	}

	@Test
	public void testConvert()
	{
		final Collection<ProductConfigurationItem> items = null;
		final OrderEntryModel entry = null;
		assertTrue(classUnderTest.convert(items, entry).isEmpty());
	}
}
