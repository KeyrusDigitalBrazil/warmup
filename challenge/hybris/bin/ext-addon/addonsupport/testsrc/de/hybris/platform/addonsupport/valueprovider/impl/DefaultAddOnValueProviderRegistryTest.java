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
package de.hybris.platform.addonsupport.valueprovider.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.addonsupport.valueprovider.AddOnValueProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for the {@link DefaultAddOnValueProviderRegistry} class.
 */
@UnitTest
public class DefaultAddOnValueProviderRegistryTest
{
	private DefaultAddOnValueProviderRegistry registry;

	@Before
	public void setUp() throws Exception
	{
		final AddOnValueProvider valueProvider = new DefaultAddOnValueProvider();
		final Map<String, AddOnValueProvider> valueProviders = new HashMap<>();

		valueProviders.put("myaddon", valueProvider);

		registry = new DefaultAddOnValueProviderRegistry();
		registry.setValueProviders(valueProviders);
	}

	@Test
	public void testShouldGetValueProvider()
	{
		final Optional<AddOnValueProvider> optional = registry.get("myaddon");
		Assert.assertTrue("Optional value provider was empty.", optional.isPresent());
	}

	@Test
	public void testShouldGetEmptyOptional()
	{
		final Optional<AddOnValueProvider> optional = registry.get("unknown");
		Assert.assertFalse("Optional value provider was not empty.", optional.isPresent());
	}

}
