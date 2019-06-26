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
package de.hybris.platform.sap.productconfig.facades.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@UnitTest
public class OverviewFilterListTest
{
	private OverviewFilterList classUnderTest = new OverviewFilterList();
	private List<ConfigOverviewFilter> filters;

	@Before
	public void setUp()
	{
		filters = new ArrayList<>();
		filters.add(new VisibleValueFilter());
		classUnderTest.setFilters(filters);
	}

	@Test
	public void testSetAndGetFilter()
	{
		List<ConfigOverviewFilter> filtersToCheck = classUnderTest.getFilters();

		assertEquals(filters, filtersToCheck);
		assertNotSame(filters, filtersToCheck);
	}

	@Test
	public void testAppliedFiltersIncluded()
	{
		List<FilterEnum> appliedFilterIDs = new ArrayList<>();
		appliedFilterIDs.add(FilterEnum.VISIBLE);
		List<ConfigOverviewFilter> appliedFilters = classUnderTest.getAppliedFilters(appliedFilterIDs);

		assertEquals(1, appliedFilters.size());
		assertEquals(VisibleValueFilter.class, appliedFilters.get(0).getClass());
	}


	@Test
	public void testAppliedFiltersNotIncluded()
	{
		List<FilterEnum> appliedFilterIDs = new ArrayList<>();
		appliedFilterIDs.add(FilterEnum.PRICE_RELEVANT);
		List<ConfigOverviewFilter> appliedFilters = classUnderTest.getAppliedFilters(appliedFilterIDs);

		assertEquals(0, appliedFilters.size());
	}
}
