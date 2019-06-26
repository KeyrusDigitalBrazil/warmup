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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.strategies.AsCacheKey;
import de.hybris.platform.adaptivesearch.strategies.AsCacheScope;
import de.hybris.platform.regioncache.region.CacheRegion;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.tenant.TenantService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsCacheStrategyTest
{
	private static final String MASTER_TENANT = "master";

	private static final String CACHE_VALUE = "cacheValue";

	@Mock
	private TenantService tenantService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private CacheRegion cacheRegion;

	private DefaultAsCacheStrategy defaultAsCacheStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		when(tenantService.getCurrentTenantId()).thenReturn(MASTER_TENANT);
		when(configurationService.getConfiguration()).thenReturn(configuration);

		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_CACHE_ENABLED_KEY, true))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_LOAD_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_CALCULATION_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.TRUE);

		defaultAsCacheStrategy = new DefaultAsCacheStrategy();
		defaultAsCacheStrategy.setTenantService(tenantService);
		defaultAsCacheStrategy.setConfigurationService(configurationService);
		defaultAsCacheStrategy.setCacheRegion(cacheRegion);
	}

	@Test
	public void getWithCacheEnabled()
	{
		// given
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_CACHE_ENABLED_KEY, true))).thenReturn(Boolean.TRUE);
		defaultAsCacheStrategy.loadCacheSettings();

		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		verify(cacheRegion).getWithLoader(any(), any());
	}

	@Test
	public void getWithCacheDisabled()
	{
		// given
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.FALSE);
		defaultAsCacheStrategy.loadCacheSettings();

		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		verify(cacheRegion, never()).getWithLoader(any(), any());
	}

	@Test
	public void getWithLoadCacheEnabled()
	{
		// given
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_LOAD_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.TRUE);
		defaultAsCacheStrategy.loadCacheSettings();

		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		verify(cacheRegion).getWithLoader(any(), any());
	}

	@Test
	public void getWithLoadCacheDisabled()
	{
		// given
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_LOAD_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.FALSE);
		defaultAsCacheStrategy.loadCacheSettings();

		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		verify(cacheRegion, never()).getWithLoader(any(), any());
	}

	@Test
	public void getWithCalculationCacheEnabled()
	{
		// given
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_CALCULATION_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.TRUE);
		defaultAsCacheStrategy.loadCacheSettings();

		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.CALCULATION, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		verify(cacheRegion).getWithLoader(any(), any());
	}

	@Test
	public void getWithCalculationCacheDisabled()
	{
		// given
		when(Boolean.valueOf(configuration.getBoolean(DefaultAsCacheStrategy.AS_CALCULATION_CACHE_ENABLED_KEY, true)))
				.thenReturn(Boolean.FALSE);
		defaultAsCacheStrategy.loadCacheSettings();

		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.CALCULATION, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		verify(cacheRegion, never()).getWithLoader(any(), any());
	}
}
