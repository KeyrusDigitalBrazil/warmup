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
package de.hybris.platform.sap.productconfig.services.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CacheKeyGeneratorImplTest
{
	private static final String USER_ID = "user id";
	private static final String CONFIG_ID = "config id";
	private static final String PRODUCT_CODE = "product code";

	@InjectMocks
	private CacheKeyGeneratorImpl classUnderTest;

	@Mock
	private ProductConfigurationUserIdProvider userIdProvider;

	@Mock
	private SessionService sessionService;

	@Before
	public void setup()
	{
		when(userIdProvider.getCurrentUserId()).thenReturn(USER_ID);
	}

	@Test
	public void testSessionService()
	{
		assertEquals(sessionService, classUnderTest.getSessionService());
	}

	@Test
	public void testSessionBoundToConfiguration()
	{
		assertTrue(classUnderTest.isSessionBoundToConfiguration());
	}

	@Test
	public void testSessionBoundToConfigurationAttributeNoBoolean()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS)).thenReturn("Huhu");
		assertTrue(classUnderTest.isSessionBoundToConfiguration());
	}

	@Test
	public void testSessionBoundToConfigurationOCC()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS))
				.thenReturn(Boolean.valueOf(true));
		assertFalse(classUnderTest.isSessionBoundToConfiguration());
	}

	@Test
	public void testCreatePriceSummaryCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createPriceSummaryCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_PRICE_SUMMARY, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(2, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_USER_ID));
	}

	@Test
	public void testCreateAnalyticsDataCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createAnalyticsDataCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_ANALYTICS_DATA, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(2, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_USER_ID));
	}

	@Test
	public void testCreateConfigCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createConfigCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_CONFIG, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(2, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_USER_ID));
	}

	@Test
	public void testCreateClassificationSystemCPQAttributesCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createClassificationSystemCPQAttributesCacheKey(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(1, result.getKeys().size());
		assertEquals(PRODUCT_CODE, result.getKeys().get(CacheKeyGeneratorImpl.KEY_PRODUCT_CODE));
	}

	@Test
	public void testGetTenantId()
	{
		final String result = classUnderTest.getTenantId();
		if (Registry.hasCurrentTenant())
		{
			assertEquals(Registry.getCurrentTenant().getTenantID(), result);
		}
		else
		{
			assertEquals(CacheKeyGeneratorImpl.NO_ACTIVE_TENANT, result);
		}
	}
}
