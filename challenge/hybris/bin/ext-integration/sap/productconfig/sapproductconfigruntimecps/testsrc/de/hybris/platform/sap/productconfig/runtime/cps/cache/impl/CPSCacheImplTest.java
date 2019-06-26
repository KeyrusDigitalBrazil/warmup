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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CPSCacheImplTest
{
	private static final String TYPE_CODE = "type code";
	private static final String CPS_SERVICE_TENANT = "cps service tenant";
	private static final String CPS_SERVICE_URL = "cps service url";
	private static final String TENANT_ID = "tenant id";
	private static final String CONFIG_ID = "config Id";
	private static final String COOKIE_TWO = "cookie two";
	private static final String COOKIE_ONE = "cookie one";
	private static final String KB_ID = "kb id";
	private static final String PRODUCT_ID = "pId";
	@InjectMocks
	CPSCacheImpl classUnderTest;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<String>> cookieCache;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> valuePricesCache;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSConfiguration> configurationCache;

	@Mock
	private CPSCacheKeyGenerator keyGenerator;

	private final List<String> cookieList = new ArrayList<>();
	private final Map<String, String> cacheKeyMap = new HashMap();
	private final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap = new HashMap<>();
	private final CPSConfiguration configuration = new CPSConfiguration();

	private final ProductConfigurationCacheKey cookieCacheKey = new ProductConfigurationCacheKey(cacheKeyMap, TYPE_CODE,
			TENANT_ID);
	private final ProductConfigurationCacheKey valuePricesCacheKey = new ProductConfigurationCacheKey(cacheKeyMap, TYPE_CODE,
			TENANT_ID);

	private final ProductConfigurationCacheKey configurationCacheKey = new ProductConfigurationCacheKey(cacheKeyMap, TYPE_CODE,
			TENANT_ID);

	@Before
	public void setup()
	{
		cookieList.add(COOKIE_ONE);
		cookieList.add(COOKIE_TWO);
		Mockito.when(keyGenerator.createCookieCacheKey(CONFIG_ID)).thenReturn(cookieCacheKey);
		Mockito.when(keyGenerator.createValuePricesCacheKey(KB_ID, PRODUCT_ID)).thenReturn(valuePricesCacheKey);
		Mockito.when(keyGenerator.createConfigurationCacheKey(CONFIG_ID)).thenReturn(configurationCacheKey);

		Mockito.when(cookieCache.get(cookieCacheKey)).thenReturn(cookieList);
		Mockito.when(valuePricesCache.get(valuePricesCacheKey)).thenReturn(valuePricesMap);
		Mockito.when(configurationCache.get(configurationCacheKey)).thenReturn(configuration);

	}

	@Test
	public void testSetCookies()
	{
		classUnderTest.setCookies(CONFIG_ID, cookieList);
		Mockito.verify(cookieCache).put(cookieCacheKey, cookieList);
		Mockito.verify(keyGenerator).createCookieCacheKey(CONFIG_ID);
	}

	@Test
	public void testGetCookies()
	{
		assertEquals(cookieList, classUnderTest.getCookies(CONFIG_ID));
		Mockito.verify(cookieCache).get(cookieCacheKey);
		Mockito.verify(keyGenerator).createCookieCacheKey(CONFIG_ID);
	}

	@Test
	public void testRemoveCookies()
	{
		classUnderTest.removeCookies(CONFIG_ID);
		Mockito.verify(cookieCache).remove(cookieCacheKey);
		Mockito.verify(keyGenerator).createCookieCacheKey(CONFIG_ID);
	}

	@Test
	public void testSetValuePricesMap()
	{
		classUnderTest.setValuePricesMap(KB_ID, PRODUCT_ID, valuePricesMap);
		Mockito.verify(valuePricesCache).put(valuePricesCacheKey, valuePricesMap);
		Mockito.verify(keyGenerator).createValuePricesCacheKey(KB_ID, PRODUCT_ID);
	}

	@Test
	public void testGetValuePricesMap()
	{
		classUnderTest.getValuePricesMap(KB_ID, PRODUCT_ID);
		Mockito.verify(valuePricesCache).get(valuePricesCacheKey);
		Mockito.verify(keyGenerator).createValuePricesCacheKey(KB_ID, PRODUCT_ID);
	}

	@Test
	public void testGetConfiguration()
	{
		final CPSConfiguration result = classUnderTest.getConfiguration(CONFIG_ID);
		assertNotNull(result);
		assertEquals(configuration, result);
		verify(keyGenerator).createConfigurationCacheKey(CONFIG_ID);
		verify(configurationCache).get(configurationCacheKey);
	}

	@Test
	public void testSetConfiguration()
	{
		classUnderTest.setConfiguration(CONFIG_ID, configuration);
		verify(configurationCache).put(configurationCacheKey, configuration);
		verify(keyGenerator).createConfigurationCacheKey(CONFIG_ID);
	}

	@Test
	public void testRemoveConfiguration()
	{
		classUnderTest.removeConfiguration(CONFIG_ID);
		verify(configurationCache).remove(configurationCacheKey);
		verify(keyGenerator).createConfigurationCacheKey(CONFIG_ID);
	}
}
