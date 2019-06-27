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

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import com.hybris.merchandising.model.Breadcrumb;


/**
 * Test class for {@link Breadcrumb}
 *
 */
public class BreadcrumbTest
{
	private static final String URL = "/Categories/c/categories";
	private static final String NAME = "Categories";
	Breadcrumb breadCrumb = new Breadcrumb();

	@Before
	public void setUp()
	{
		breadCrumb.setName(NAME);
		breadCrumb.setUrl(URL);
	}

	@Test
	public void testName()
	{
		assertEquals(NAME, breadCrumb.getName());
	}

	@Test
	public void testUrl()
	{
		assertEquals(URL, breadCrumb.getUrl());
	}

	@Test
	public void testSerializability()
	{
		final byte[] serialized = SerializationUtils.serialize(breadCrumb);
		assertNotNull("Expected breadcrumb to be serializable", serialized);
	}
}
