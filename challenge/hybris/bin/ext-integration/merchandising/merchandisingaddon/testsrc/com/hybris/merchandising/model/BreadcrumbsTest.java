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

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests {@link Breadcrumbs}
 *
 */
public class BreadcrumbsTest
{
	private static final String BAGS_WOMEN = "Bags women";
	private static final String ACCESSORIES_WOMEN = "Accessories women";
	private static final String CATEGORIES = "Categories";
	private static final String CATEGORIES_URL = "/Categories/c/categories";
	private static final String ACC_WOMEN_BAG_URL = "/Categories/Accessories-women/Bags-women/c/160400";
	private static final String ACC_WOMEN_URL = "/Categories/Accessories-women/c/370000";
	Breadcrumbs breadcrumbs = new Breadcrumbs();

	@Before
	public void setUp()
	{
		breadcrumbs.setListOfBreadcrumbs(Arrays.asList(new Breadcrumb(CATEGORIES_URL, CATEGORIES),
				new Breadcrumb(ACC_WOMEN_URL, ACCESSORIES_WOMEN),
				new Breadcrumb(ACC_WOMEN_BAG_URL, BAGS_WOMEN)));
	}

	@Test
	public void testBreadcrumbs()
	{
		assertEquals(3, breadcrumbs.getListOfBreadcrumbs().size());
		assertTrue(breadcrumbs.getListOfBreadcrumbs().stream().map(Breadcrumb::getUrl).collect(Collectors.toList())
				.containsAll(Arrays.asList(CATEGORIES_URL, ACC_WOMEN_URL, ACC_WOMEN_BAG_URL)));
		assertTrue(breadcrumbs.getListOfBreadcrumbs().stream().map(Breadcrumb::getName).collect(Collectors.toList())
				.containsAll(Arrays.asList(CATEGORIES, ACCESSORIES_WOMEN, BAGS_WOMEN)));
	}

	@Test
	public void testSerializability()
	{
		final byte[] serialized = SerializationUtils.serialize(breadcrumbs);
		assertNotNull("Expected breadcrumbs to be serializable", serialized);
	}
}
