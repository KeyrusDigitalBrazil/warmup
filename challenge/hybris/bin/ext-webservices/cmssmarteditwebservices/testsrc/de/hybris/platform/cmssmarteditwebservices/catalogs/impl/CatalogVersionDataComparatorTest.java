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
package de.hybris.platform.cmssmarteditwebservices.catalogs.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CatalogVersionDataComparatorTest
{
	private final CatalogVersionDataComparator comparator = new CatalogVersionDataComparator();
	private CatalogVersionData online;
	private CatalogVersionData staged1;
	private CatalogVersionData staged2;

	@Before
	public void setUp()
	{
		online = new CatalogVersionData();
		staged1 = new CatalogVersionData();
		staged2 = new CatalogVersionData();

		online.setActive(true);
		online.setVersion("welcome");
		staged1.setActive(false);
		staged1.setVersion("hello");
		staged2.setActive(false);
		staged2.setVersion("allo");
	}

	@Test
	public void testActiveFirst()
	{
		final int value = comparator.compare(online, staged1);
		assertThat(value, lessThan(0));
	}

	@Test
	public void testCorrectInactiveOrdering()
	{
		final int value = comparator.compare(staged1, staged2);
		assertThat(value, greaterThan(0));
	}

	@Test
	public void testSortCollection()
	{
		final List<CatalogVersionData> values = Stream.of(staged1, staged2, online).sorted(comparator).collect(Collectors.toList());

		assertThat(values, contains(online, staged2, staged1));
	}

}
