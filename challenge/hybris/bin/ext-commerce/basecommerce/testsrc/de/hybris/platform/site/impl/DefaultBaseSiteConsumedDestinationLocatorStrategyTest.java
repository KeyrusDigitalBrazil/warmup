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

package de.hybris.platform.site.impl;

import static de.hybris.platform.apiregistryservices.strategies.impl.DefaultConsumedDestinationLocatorStrategy.CLIENT_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultBaseSiteConsumedDestinationLocatorStrategyTest
{

	private static final String TEST_SITE = "testSite";
	private static final String TEST_CLIENT = "testClient";

	@Mock
	private DestinationService<AbstractDestinationModel> destinationService;

	@Mock
	private BaseSiteService baseSiteService;

	@InjectMocks
	private DefaultBaseSiteConsumedDestinationLocatorStrategy defaultBaseSiteConsumedDestinationLocatorStrategy;

	private final List<AbstractDestinationModel> destinations = new ArrayList<>();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid(TEST_SITE);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);

		for (int i = 0; i < 20; i++)
		{
			final ConsumedDestinationModel destination = new ConsumedDestinationModel();

			final Map<String, String> additionalProperties = new HashMap<>();
			additionalProperties.put(DefaultBaseSiteConsumedDestinationLocatorStrategy.BASE_SITE, TEST_SITE);
			additionalProperties.put(CLIENT_CLASS_NAME, TEST_CLIENT + i);
			destination.setAdditionalProperties(additionalProperties);

			destinations.add(destination);
		}

		when(destinationService.getAllDestinations()).thenReturn(destinations);
	}

	@Test
	public void testLookupWithExistingClientName()
	{
		final ConsumedDestinationModel destination = defaultBaseSiteConsumedDestinationLocatorStrategy.lookup(TEST_CLIENT + 1);

		assertNotNull(destination);
		assertEquals(destination.getAdditionalProperties().get(CLIENT_CLASS_NAME), TEST_CLIENT + 1);
		assertEquals(TEST_SITE,
				destination.getAdditionalProperties().get(DefaultBaseSiteConsumedDestinationLocatorStrategy.BASE_SITE));
	}

	@Test
	public void testLookupWithNonExistingClientName()
	{
		final ConsumedDestinationModel destination = defaultBaseSiteConsumedDestinationLocatorStrategy.lookup(TEST_CLIENT + 100);

		assertNull(destination);
	}

	@Test
	public void testLookupWithExposedDestination()
	{
		final ExposedDestinationModel exposedDestination = new ExposedDestinationModel();

		final Map<String, String> additionalProperties = new HashMap<>();
		additionalProperties.put(DefaultBaseSiteConsumedDestinationLocatorStrategy.BASE_SITE, TEST_SITE);
		additionalProperties.put(CLIENT_CLASS_NAME, TEST_CLIENT + 30);
		exposedDestination.setAdditionalProperties(additionalProperties);

		destinations.add(exposedDestination);

		final AbstractDestinationModel destination = defaultBaseSiteConsumedDestinationLocatorStrategy.lookup(TEST_CLIENT + 30);

		assertNull(destination);
	}

	@Test
	public void testLookupWithNoBaseSite()
	{
		when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
		final ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();

		final Map<String, String> additionalProperties = new HashMap<>();
		additionalProperties.put(CLIENT_CLASS_NAME, TEST_CLIENT + 30);
		consumedDestination.setAdditionalProperties(additionalProperties);

		destinations.add(consumedDestination);

		final AbstractDestinationModel destination = defaultBaseSiteConsumedDestinationLocatorStrategy.lookup(TEST_CLIENT + 30);

		assertNotNull(destination);
		assertEquals(destination.getAdditionalProperties().get(CLIENT_CLASS_NAME), TEST_CLIENT + 30);
		assertFalse(destination.getAdditionalProperties().containsKey(DefaultBaseSiteConsumedDestinationLocatorStrategy.BASE_SITE));
	}


}
