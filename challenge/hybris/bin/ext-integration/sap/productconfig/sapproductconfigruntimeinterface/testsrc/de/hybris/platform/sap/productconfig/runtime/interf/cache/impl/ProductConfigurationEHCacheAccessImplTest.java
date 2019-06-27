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
package de.hybris.platform.sap.productconfig.runtime.interf.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.AbstractCacheKey;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigurationEHCacheAccessImplTest
{
	private static final String TYPE_CODE = "typeCode1";
	private static final String TYPE_CODE_TWO = "typeCode2";
	private static final String TENANT_ID = "tenant id";
	private static final String KEY_INVALID = "invalid";
	private static final String KEY_ONE = "key1";
	private static final String KEY_TWO = "key2";
	private static final String VALUE = "value";
	private static final String NEW_VALUE = "newValue";
	private static StringCacheKey cacheKey1;
	private static StringCacheKey cacheKey2;
	private static StringCacheKey invalidCacheKey;
	private Collection<CacheKey> keys;

	@InjectMocks
	private ProductConfigurationEHCacheAccessImpl<StringCacheKey, String> classUnderTest;
	@Mock
	private EHCacheRegion mockedCache;
	@Mock
	private CacheValueLoader<String> mockedLoader;

	@Before
	public void setup()
	{
		cacheKey1 = new StringCacheKey(TYPE_CODE, TENANT_ID, KEY_ONE);
		cacheKey2 = new StringCacheKey(TYPE_CODE, TENANT_ID, KEY_TWO);
		invalidCacheKey = new StringCacheKey(TYPE_CODE, TENANT_ID, KEY_INVALID);
		when(mockedCache.get(cacheKey1)).thenReturn(VALUE);
		when(mockedCache.get(invalidCacheKey)).thenReturn(null);

		keys = new ArrayList<>();
		keys.add(cacheKey1);
		keys.add(cacheKey2);
		assertEquals(2, keys.size());
		when(mockedCache.getAllKeys()).thenReturn(keys);
		when(mockedCache.containsKey(cacheKey1)).thenReturn(true);
	}

	@Test
	public void testGet()
	{
		final String result = classUnderTest.get(cacheKey1);
		assertNotNull(result);
		assertEquals(VALUE, result);
		verify(mockedCache).get(cacheKey1);
	}

	@Test
	public void testGetNull()
	{
		final String result = classUnderTest.get(invalidCacheKey);
		assertNull(result);
		verify(mockedCache).get(invalidCacheKey);
	}

	@Test
	public void testGetKeys()
	{
		final Set<StringCacheKey> keysResult = classUnderTest.getKeys();
		assertNotNull(keysResult);
		assertEquals(2, keysResult.size());
		verify(mockedCache).getAllKeys();
	}

	@Test
	public void testPutIsAbsent()
	{
		final ArgumentCaptor<CacheValueLoader> cacheValueLoader = ArgumentCaptor.forClass(CacheValueLoader.class);
		classUnderTest.put(cacheKey2, NEW_VALUE);
		verify(mockedCache, times(0)).remove(cacheKey2, false);
		verify(mockedCache).getWithLoader(eq(cacheKey2), cacheValueLoader.capture());
		assertEquals(NEW_VALUE, cacheValueLoader.getValue().load(cacheKey2));
	}

	@Test
	public void testPutIsPresent()
	{
		final ArgumentCaptor<CacheValueLoader> cacheValueLoader = ArgumentCaptor.forClass(CacheValueLoader.class);
		classUnderTest.put(cacheKey1, NEW_VALUE);
		verify(mockedCache).remove(cacheKey1, false);
		verify(mockedCache).getWithLoader(eq(cacheKey1), cacheValueLoader.capture());
		assertEquals(NEW_VALUE, cacheValueLoader.getValue().load(cacheKey1));
	}

	@Test
	public void testPutIfAbsentIsAbsent()
	{
		final ArgumentCaptor<CacheValueLoader> cacheValueLoader = ArgumentCaptor.forClass(CacheValueLoader.class);
		classUnderTest.putIfAbsent(cacheKey2, NEW_VALUE);
		verify(mockedCache).getWithLoader(eq(cacheKey2), cacheValueLoader.capture());
		assertEquals(NEW_VALUE, cacheValueLoader.getValue().load(cacheKey2));
	}

	@Test
	public void testPutIfAbsentIsPresent()
	{
		classUnderTest.putIfAbsent(cacheKey1, NEW_VALUE);
		verify(mockedCache, times(0)).getWithLoader(eq(cacheKey1), any());
	}

	@Test
	public void testRemove()
	{
		classUnderTest.remove(cacheKey1);
		verify(mockedCache).remove(cacheKey1, false);
	}

	@Test
	public void testClearCache()
	{
		classUnderTest.clearCache();
		verify(mockedCache).clearCache();
	}

	@Test
	public void testGetWithLoader()
	{
		classUnderTest.getWithLoader(cacheKey1, mockedLoader);
		verify(mockedCache).getWithLoader(cacheKey1, mockedLoader);
	}

	@Test
	public void testGetWithSupplier()
	{
		final ArgumentCaptor<CacheValueLoader> cacheValueLoader = ArgumentCaptor.forClass(CacheValueLoader.class);
		classUnderTest.getWithSupplier(cacheKey2, new SimpleSupplier(NEW_VALUE));
		verify(mockedCache).getWithLoader(eq(cacheKey2), cacheValueLoader.capture());
		assertEquals(NEW_VALUE, cacheValueLoader.getValue().load(cacheKey2));
	}

	private class SimpleSupplier implements Supplier<String>
	{
		private final String value;

		public SimpleSupplier(final String value)
		{
			this.value = value;
		}

		@Override
		public String get()
		{
			return value;
		}

	}


	public class StringCacheKey extends AbstractCacheKey
	{
		private final String key;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}


		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (!super.equals(obj))
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final StringCacheKey cacheKey = (StringCacheKey) obj;
			if (this.key == null)
			{
				if (cacheKey.getKey() != null)
				{
					return false;
				}
			}
			else if (!this.key.equals(cacheKey.getKey()))
			{
				return false;
			}
			return true;
		}


		public StringCacheKey(final Object typeCode, final String tenantId, final String key)
		{
			super(typeCode, tenantId);
			this.key = key;
		}


		public String getKey()
		{
			return key;
		}
	}

}
