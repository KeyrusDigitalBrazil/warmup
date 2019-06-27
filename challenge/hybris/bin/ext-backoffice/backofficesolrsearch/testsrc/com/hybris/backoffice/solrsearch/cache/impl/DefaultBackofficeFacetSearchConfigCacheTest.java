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
package com.hybris.backoffice.solrsearch.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.solrsearch.cache.BackofficeFacetSearchConfigCache;
import com.hybris.backoffice.solrsearch.model.BackofficeIndexedTypeToSolrFacetSearchConfigModel;


public class DefaultBackofficeFacetSearchConfigCacheTest
{

	protected static final String KEY_WITH_EXISTING_VALUE = "existingValue";
	protected static final String KEY_WITH_NULL_VALUE = "nullValue";
	protected static final String KEY_WITHOUT_VALUE = "noValue";

	protected static final BackofficeIndexedTypeToSolrFacetSearchConfigModel VALUE = new BackofficeIndexedTypeToSolrFacetSearchConfigModel();

	protected BackofficeFacetSearchConfigCache cache = new DefaultBackofficeFacetSearchConfigCache();


	@Before
	public void setUp()
	{
		cache.putSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE, VALUE);
		cache.putSearchConfigForTypeCode(KEY_WITH_NULL_VALUE, null);
	}

	@Test
	public void testExistingValue()
	{
		assertTrue(cache.containsSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE));
		assertEquals(VALUE, cache.getSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE));
	}

	@Test
	public void testNullValue()
	{
		assertTrue(cache.containsSearchConfigForTypeCode(KEY_WITH_NULL_VALUE));
		assertNull(cache.getSearchConfigForTypeCode(KEY_WITH_NULL_VALUE));
	}

	@Test
	public void testNoValue()
	{
		assertFalse(cache.containsSearchConfigForTypeCode(KEY_WITHOUT_VALUE));
		assertNull(cache.getSearchConfigForTypeCode(KEY_WITHOUT_VALUE));
	}

	@Test
	public void testInvalidation()
	{
		testExistingValue();
		cache.invalidateCache();
		assertFalse(cache.containsSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE));
	}

	@Test
	public void testConcurrency() throws InterruptedException
	{
		final Thread t1 = new ReadingThread(10 + new Random().nextInt(10));
		final Thread t2 = new ReadingThread(10 + new Random().nextInt(10));
		final Thread t3 = new ReadingThread(10 + new Random().nextInt(10));
		final Thread t4 = new ReadingAndWritingThread(30);

		t1.run();
		t2.run();
		t3.run();
		t4.run();

		t1.join();
		t2.join();
		t3.join();
		t4.join();

		assertFalse(cache.containsSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE));
		assertFalse(cache.containsSearchConfigForTypeCode(KEY_WITH_NULL_VALUE));
		assertFalse(cache.containsSearchConfigForTypeCode(KEY_WITHOUT_VALUE));
	}


	private class ReadingThread extends Thread
	{

		private final int iterations;

		public ReadingThread(final int iterations)
		{
			this.iterations = iterations;
		}

		@Override
		public void run()
		{
			for (int i = 0; i < iterations; i++)
			{
				final boolean contains = cache.containsSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE);
				final BackofficeIndexedTypeToSolrFacetSearchConfigModel value = cache
						.getSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE);
				System.out.println(Boolean.toString(contains) + " " + value);
				try
				{
					Thread.sleep(20L);
				}
				catch (final InterruptedException ignore)
				{
					// empty
				}
			}
		}

	}

	private class ReadingAndWritingThread extends Thread
	{

		private final int iterations;

		public ReadingAndWritingThread(final int iterations)
		{
			this.iterations = iterations;
		}

		@Override
		public void run()
		{
			for (int i = 0; i < iterations; i++)
			{
				cache.putSearchConfigForTypeCode(KEY_WITHOUT_VALUE, VALUE);
				cache.putSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE, null);
				cache.getSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE);
				cache.invalidateCache();
				cache.getSearchConfigForTypeCode(KEY_WITH_EXISTING_VALUE);
			}
		}

	}

}
