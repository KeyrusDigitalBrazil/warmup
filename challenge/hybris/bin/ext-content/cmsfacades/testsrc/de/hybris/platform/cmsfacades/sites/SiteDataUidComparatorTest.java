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
package de.hybris.platform.cmsfacades.sites;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import de.hybris.platform.cmsfacades.data.SiteData;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;


public class SiteDataUidComparatorTest
{

	private static final SiteData FIRST = new SiteData();
	{
		FIRST.setUid("1");
		FIRST.setName(new HashMap<>());
	}

	private static final SiteData SECOND = new SiteData();
	{
		SECOND.setUid("2");
		SECOND.setName(new HashMap<>());
	}

	private static final SiteData[] reverseOrdered = new SiteData[]
			{ SECOND, FIRST };
	private static final SiteData[] ordered = new SiteData[]
			{ FIRST, SECOND };

	private final Comparator<SiteData> comparator = new SiteDataUidComparator();

	@Test
	public void comparatorWillReturnInOrder() throws Exception
	{
		final List<SiteData> collectionToSort = newArrayList(reverseOrdered);
		sort(collectionToSort, comparator);
		assertThat(collectionToSort, contains(ordered));
	}

	@Test
	public void comparatorWillReturnInReverseOrder() throws Exception
	{
		final List<SiteData> collectionToSort = newArrayList(ordered);
		sort(collectionToSort, comparator.reversed());
		assertThat(collectionToSort, contains(reverseOrdered));
	}
}
