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
package de.hybris.platform.cmsfacades.media.populator;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class BasicNamedQueryDataPopulatorTest
{

	private static final String QUERY_NAME = "QUERY_NAME";
	private static final String CURRENT_PAGE = "1";
	private static final String PAGE_SIZE = "100";
	private static final List<Sort> SORT_LIST = Arrays.asList( //
			new Sort().withParameter("param1").withDirection(SortDirection.ASC), //
			new Sort().withParameter("param2").withDirection(SortDirection.DESC));
	private static final String SORT = //
			SORT_LIST.get(0).getParameter() + ":" + SORT_LIST.get(0).getDirection().name() + "," + SORT_LIST.get(1).getParameter()
			+ ":" + SORT_LIST.get(1).getDirection().name();

	private static final String BAD_SORT = ":ASC:";


	@Test
	public void testSuccessfulPopulation()
	{
		final BasicNamedQueryDataPopulator basicNamedQueryDataPopulator = new BasicNamedQueryDataPopulator();
		final NamedQueryData namedQueryData = new NamedQueryData();
		namedQueryData.setNamedQuery(QUERY_NAME);
		namedQueryData.setCurrentPage(CURRENT_PAGE);
		namedQueryData.setPageSize(PAGE_SIZE);
		namedQueryData.setSort(SORT);

		final NamedQuery namedQuery = new NamedQuery();
		basicNamedQueryDataPopulator.populate(namedQueryData, namedQuery);

		Assert.assertEquals(QUERY_NAME, namedQuery.getQueryName());
		Assert.assertEquals(Integer.valueOf(CURRENT_PAGE), namedQuery.getCurrentPage());
		Assert.assertEquals(Integer.valueOf(PAGE_SIZE), namedQuery.getPageSize());
		Assert.assertFalse(namedQuery.getSort().isEmpty());
		Assert.assertEquals(SORT_LIST.size(), namedQuery.getSort().size());
		Assert.assertEquals(SORT_LIST.get(0).getParameter(), namedQuery.getSort().get(0).getParameter());
		Assert.assertEquals(SORT_LIST.get(1).getParameter(), namedQuery.getSort().get(1).getParameter());
		Assert.assertEquals(SORT_LIST.get(0).getDirection(), namedQuery.getSort().get(0).getDirection());
		Assert.assertEquals(SORT_LIST.get(1).getDirection(), namedQuery.getSort().get(1).getDirection());
	}

	@Test(expected = ConversionException.class)
	public void testPopulatorWithBadValues()
	{
		final BasicNamedQueryDataPopulator basicNamedQueryDataPopulator = new BasicNamedQueryDataPopulator();
		final NamedQueryData namedQueryData = new NamedQueryData();
		namedQueryData.setCurrentPage(CURRENT_PAGE);
		namedQueryData.setPageSize(PAGE_SIZE);
		namedQueryData.setSort(BAD_SORT);

		final NamedQuery namedQuery = new NamedQuery();
		basicNamedQueryDataPopulator.populate(namedQueryData, namedQuery);
	}
}
