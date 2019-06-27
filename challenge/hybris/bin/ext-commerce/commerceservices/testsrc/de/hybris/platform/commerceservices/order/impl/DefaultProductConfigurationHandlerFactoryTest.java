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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


@UnitTest
public class DefaultProductConfigurationHandlerFactoryTest
{
	public static final String TEST_HANDLER_TYPE = "SAMPLE";

	private DefaultProductConfigurationHandlerFactory factory = new DefaultProductConfigurationHandlerFactory();

	@Before
	public void setup()
	{
		final Map<String, ProductConfigurationHandler> handlers = new HashMap<>();
		final ProductConfigurationHandler handler = mock(ProductConfigurationHandler.class);
		handlers.put(TEST_HANDLER_TYPE, handler);
		factory.setRegisteredHandlers(handlers);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDeclineNullConfiguratorType()
	{
		factory.handlerOf(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDeclineUnknownConfiguratorType()
	{
		factory.handlerOf(ConfiguratorType.valueOf("UNKNOWN"));
	}

	@Test
	public void shouldReturnHandlerByType()
	{
		assertEquals(
				factory.getRegisteredHandlers().values().iterator().next(),
				factory.handlerOf(ConfiguratorType.valueOf(TEST_HANDLER_TYPE)));
	}
}
