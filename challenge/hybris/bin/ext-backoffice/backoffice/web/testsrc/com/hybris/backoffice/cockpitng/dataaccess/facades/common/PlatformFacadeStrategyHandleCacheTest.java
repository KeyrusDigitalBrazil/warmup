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
package com.hybris.backoffice.cockpitng.dataaccess.facades.common;


import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.type.ViewTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class PlatformFacadeStrategyHandleCacheTest
{

	private PlatformFacadeStrategyHandleCache platformFacadeStrategyHandleCache;

	@Before
	public void setUp()
	{
		platformFacadeStrategyHandleCache = new PlatformFacadeStrategyHandleCache();
	}

	@Test
	public void testCacheHandledType()
	{
		// assign
		final String typeCode = "user";
		final TypeService typeService = Mockito.mock(TypeService.class);
		final TypeModel typeModel = Mockito.mock(TypeModel.class);
		Mockito.when(typeService.getTypeForCode(typeCode)).thenReturn(typeModel);
		getPlatformFacadeStrategyHandleCache().setTypeService(typeService);

		// act
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryBefore1stInvocation = getCacheEntry(typeCode);
		final boolean canHandle1stInvocation = getPlatformFacadeStrategyHandleCache().canHandle(typeCode);
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryAfter1stInvocation = getCacheEntry(typeCode);
		final boolean canHandle2ndInvocation = getPlatformFacadeStrategyHandleCache().canHandle(typeCode);
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryAfter2ndInvocation = getCacheEntry(typeCode);

		// assert
		Assert.assertNull(cacheEntryBefore1stInvocation);
		Assert.assertTrue(canHandle1stInvocation);
		Assert.assertTrue(canHandle2ndInvocation);
		Assert.assertFalse(cacheEntryAfter1stInvocation.isViewType());
		Assert.assertNotSame(PlatformFacadeStrategyHandleCache.NOT_HANDLED_TYPE, cacheEntryAfter1stInvocation);
		Assert.assertSame(cacheEntryAfter1stInvocation, cacheEntryAfter2ndInvocation);
	}

	@Test
	public void testCacheNotHandledType()
	{
		// assign
		final String typeCode = "user";
		final TypeService typeService = Mockito.mock(TypeService.class);
		Mockito.when(typeService.getTypeForCode(typeCode)).thenReturn(null);
		getPlatformFacadeStrategyHandleCache().setTypeService(typeService);

		// act
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryBefore1stInvocation = getCacheEntry(typeCode);
		final boolean canHandle1stInvocation = getPlatformFacadeStrategyHandleCache().canHandle(typeCode);
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryAfter1stInvocation = getCacheEntry(typeCode);
		final boolean canHandle2ndInvocation = getPlatformFacadeStrategyHandleCache().canHandle(typeCode);
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryAfter2ndInvocation = getCacheEntry(typeCode);

		// assert
		Assert.assertNull(cacheEntryBefore1stInvocation);
		Assert.assertFalse(canHandle1stInvocation);
		Assert.assertFalse(canHandle2ndInvocation);
		Assert.assertFalse(cacheEntryAfter1stInvocation.isViewType());
		Assert.assertSame(PlatformFacadeStrategyHandleCache.NOT_HANDLED_TYPE, cacheEntryAfter1stInvocation);
		Assert.assertSame(cacheEntryAfter1stInvocation, cacheEntryAfter2ndInvocation);
	}

	@Test
	public void testCacheHandledViewType()
	{
		// assign
		final String typeCode = "user";
		final TypeService typeService = Mockito.mock(TypeService.class);
		final TypeModel typeModel = Mockito.mock(TypeModel.class);
		Mockito.when(typeService.getTypeForCode(typeCode)).thenReturn(typeModel);
		Mockito.doReturn(Boolean.TRUE).when(typeService).isAssignableFrom(typeCode, ViewTypeModel._TYPECODE);
		getPlatformFacadeStrategyHandleCache().setTypeService(typeService);

		// act
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryBefore1stInvocation = getCacheEntry(typeCode);
		final boolean canHandle1stInvocation = getPlatformFacadeStrategyHandleCache().canHandle(typeCode);
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryAfter1stInvocation = getCacheEntry(typeCode);
		final boolean canHandle2ndInvocation = getPlatformFacadeStrategyHandleCache().canHandle(typeCode);
		final PlatformFacadeStrategyHandleCache.CacheEntry cacheEntryAfter2ndInvocation = getCacheEntry(typeCode);

		// assert
		Assert.assertNull(cacheEntryBefore1stInvocation);
		Assert.assertTrue(canHandle1stInvocation);
		Assert.assertTrue(canHandle2ndInvocation);
		Assert.assertTrue(cacheEntryAfter1stInvocation.isViewType());
		Assert.assertNotSame(PlatformFacadeStrategyHandleCache.NOT_HANDLED_TYPE, cacheEntryAfter1stInvocation);
		Assert.assertSame(cacheEntryAfter1stInvocation, cacheEntryAfter2ndInvocation);
	}

	private PlatformFacadeStrategyHandleCache.CacheEntry getCacheEntry(final String typeCode)
	{
		return getPlatformFacadeStrategyHandleCache().getHandleCache().get(typeCode);
	}

	public PlatformFacadeStrategyHandleCache getPlatformFacadeStrategyHandleCache()
	{
		return platformFacadeStrategyHandleCache;
	}

}
