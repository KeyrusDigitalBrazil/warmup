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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.strategies.AsCacheKey;
import de.hybris.platform.adaptivesearch.strategies.AsCacheScope;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultAsCacheStrategyIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CACHE_VALUE = "cacheValue";
	private static final String CACHE_VALUE1 = "cacheValue1";
	private static final String CACHE_VALUE2 = "cacheValue2";
	private static final String FAKE_CACHE_VALUE = "fakeCacheValue";

	@Resource
	private ModelService modelService;

	@Resource
	private DefaultAsCacheStrategy defaultAsCacheStrategy;

	@Test
	public void getWithValueLoader()
	{
		// given
		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);

		// when
		final Object cachedValue = defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);

		// then
		assertEquals(CACHE_VALUE, cachedValue);
	}

	@Test
	public void getFromCache()
	{
		// given
		final String cacheKeyFragment = "fragment";
		final AsCacheKey cacheKey = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey, key -> CACHE_VALUE);
		final Object cachedValue = defaultAsCacheStrategy.getWithLoader(cacheKey, key -> FAKE_CACHE_VALUE);

		// then
		assertEquals(CACHE_VALUE, cachedValue);
	}

	@Test
	public void getFromCacheDifferentKeys()
	{
		// given
		final String cacheKeyFragment1 = "fragment1";
		final AsCacheKey cacheKey1 = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment1);

		final String cacheKeyFragment2 = "fragment2";
		final AsCacheKey cacheKey2 = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment2);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey1, key -> CACHE_VALUE1);
		defaultAsCacheStrategy.getWithLoader(cacheKey2, key -> CACHE_VALUE2);

		final Object cachedValue1 = defaultAsCacheStrategy.getWithLoader(cacheKey1, key -> FAKE_CACHE_VALUE);
		final Object cachedValue2 = defaultAsCacheStrategy.getWithLoader(cacheKey2, key -> FAKE_CACHE_VALUE);

		// then
		assertEquals(CACHE_VALUE1, cachedValue1);
		assertEquals(CACHE_VALUE2, cachedValue2);
	}

	@Test
	public void getWithDifferentScopes()
	{
		// given
		final String cacheKeyFragment = "fragmentScopes1";
		final AsCacheKey cacheKey1 = new DefaultAsCacheKey(AsCacheScope.LOAD, cacheKeyFragment);
		final AsCacheKey cacheKey2 = new DefaultAsCacheKey(AsCacheScope.CALCULATION, cacheKeyFragment);

		// when
		defaultAsCacheStrategy.getWithLoader(cacheKey1, key -> CACHE_VALUE1);
		defaultAsCacheStrategy.getWithLoader(cacheKey2, key -> CACHE_VALUE2);

		final Object cachedValue1 = defaultAsCacheStrategy.getWithLoader(cacheKey1, key -> FAKE_CACHE_VALUE);
		final Object cachedValue2 = defaultAsCacheStrategy.getWithLoader(cacheKey2, key -> FAKE_CACHE_VALUE);

		// then
		assertEquals(CACHE_VALUE1, cachedValue1);
		assertEquals(CACHE_VALUE2, cachedValue2);
	}
}
