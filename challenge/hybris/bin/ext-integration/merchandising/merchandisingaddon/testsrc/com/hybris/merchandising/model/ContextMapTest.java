/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ContextMap}.
 *
 */
public class ContextMapTest {
	public static final String TEST_KEY = "testKey";
	public static final String TEST_VALUE = "testValue";

	ContextMap map;

	@Before
	public void setUp()
	{
		map = new ContextMap();
	}

	@Test
	public void testAddProperty()
	{
		map.addProperty(TEST_KEY, TEST_VALUE);
		final Object retrievedProperty = map.getProperty(TEST_KEY);
		assertNotNull("Expected retrieved property from map to not be null", retrievedProperty);
	}

	@Test
	public void testRemoveProperty()
	{
		map.addProperty(TEST_KEY, TEST_VALUE);
		final Object retrievedProperty = map.getProperty(TEST_KEY);
		assertNotNull("Expected retrieved property from map to not be null", retrievedProperty);
		map.removeProperty(TEST_KEY);
		assertTrue("Expected map to contain no elements", map.getProperties().isEmpty());
	}

	@Test
	public void testGetProperties()
	{
		map.addProperty(TEST_KEY, TEST_VALUE);
		assertEquals("Expected map to contain one element", 1, map.getProperties().size());
	}

	@Test
	public void testSerializability()
	{
		final byte[] serialized = SerializationUtils.serialize(map);
		assertNotNull("Expected map to be serializable", serialized);
	}
}
