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
package com.hybris.merchandising.context.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import com.hybris.merchandising.context.ContextRepository;
import com.hybris.merchandising.model.ContextMap;


/**
 * Tests functionality of {@link ContextRepository}
 *
 */
public class ContextRepositoryTest
{
	private static final String VALUE2 = "value2";
	private static final String PROPERTY2 = "property2";
	private static final String VALUE1 = "value1";
	private static final String PROPERTY1 = "property1";
	DefaultContextRepository contextRepository = new DefaultContextRepository();

	@Before
	public void setUp()
	{
		final ContextMap contextMap1 = new ContextMap();
		contextMap1.addProperty(PROPERTY1, VALUE1);
		contextRepository.put(PROPERTY1, contextMap1);

		final ContextMap contextMap2 = new ContextMap();
		contextMap2.addProperty(PROPERTY2, VALUE2);
		contextRepository.put(PROPERTY2, contextMap2);
	}

	@Test
	public void testContext()
	{
		assertTrue(contextRepository.keys().hasMoreElements());
		assertEquals(VALUE1, contextRepository.get(PROPERTY1).getProperty(PROPERTY1));
		assertEquals(VALUE2, contextRepository.get(PROPERTY2).getProperty(PROPERTY2));
		assertTrue(contextRepository.size() == 2);
		contextRepository.clear();
		assertFalse(contextRepository.keys().hasMoreElements());
	}

	@Test
	public void testSerializability()
	{
		final byte[] serialized = SerializationUtils.serialize(contextRepository);
		assertNotNull("Expected contextRepository to be serializable", serialized);
	}
}
