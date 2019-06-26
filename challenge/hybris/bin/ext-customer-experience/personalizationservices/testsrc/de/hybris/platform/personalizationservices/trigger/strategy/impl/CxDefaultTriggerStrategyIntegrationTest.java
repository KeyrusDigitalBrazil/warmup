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
/**
 *
 */
package de.hybris.platform.personalizationservices.trigger.strategy.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.trigger.strategy.CxTriggerStrategy;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


public class CxDefaultTriggerStrategyIntegrationTest extends AbstractCxServiceTest
{
	private static final String ANONYMOUS = "anonymous";
	private static final String CUSTOMER_1 = "customer1@hybris.com";

	private static final String DEFAULT_VARIATION = "defaultVariation";

	@Resource(name = "cxDefaultTriggerStrategy")
	private CxTriggerStrategy defaultTriggerStrategy;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
	}

	@Test
	public void testAnonymousUserVariations()
	{
		//given
		final UserModel user = userService.getUserForUID(ANONYMOUS);

		//when
		final Collection<CxVariationModel> variations = defaultTriggerStrategy.getVariations(user, catalogVersion);

		//then
		assertVariations(variations, DEFAULT_VARIATION);
	}

	@Test
	public void testCustomer1Variations()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_1);

		//when
		final Collection<CxVariationModel> variations = defaultTriggerStrategy.getVariations(user, catalogVersion);

		//then
		assertVariations(variations, DEFAULT_VARIATION);
	}

	private void assertVariations(final Collection<CxVariationModel> actual, final String... expected)
	{
		final Set<String> expectedCodes = Sets.newHashSet(expected);
		Assert.assertEquals(expectedCodes, actual.stream().map(CxVariationModel::getCode).collect(Collectors.toSet()));
	}
}
