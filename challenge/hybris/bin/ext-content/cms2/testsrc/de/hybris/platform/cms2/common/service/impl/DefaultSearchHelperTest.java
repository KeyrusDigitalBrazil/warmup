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
package de.hybris.platform.cms2.common.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.Sort;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSearchHelperTest
{
	private static final String FIELD = "someField";

	@InjectMocks
	private DefaultSearchHelper searchHelper;

	@Test
	public void testConvertSortBlockWithValidSortDirection()
	{
		//WHEN
		final Sort result = searchHelper.convertSortBlock(FIELD + ":ASC", SortDirection.DESC);

		//THEN
		assertThat(result.getParameter(), is(FIELD));
		assertThat(result.getDirection(), is(SortDirection.ASC));
	}

	@Test
	public void testConvertSortBlockWithValidSortDirectionCaseSensitive()
	{
		//WHEN
		final Sort result = searchHelper.convertSortBlock(FIELD + ":asc", SortDirection.DESC);

		//THEN
		assertThat(result.getParameter(), is(FIELD));
		assertThat(result.getDirection(), is(SortDirection.ASC));
	}

	@Test
	public void testConvertSortBlockWithInvalidSortDirection()
	{
		//WHEN
		final Sort result = searchHelper.convertSortBlock(FIELD + ":blah", SortDirection.DESC);

		//THEN
		assertThat(result.getParameter(), is(FIELD));
		assertThat(result.getDirection(), is(SortDirection.DESC));
	}

	@Test
	public void testConvertSort()
	{
		//WHEN
		final List<Sort> result = searchHelper.convertSort("field1:ASC,field2:DESC", SortDirection.DESC);

		//THEN
		assertNotNull(result);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getParameter(), is("field1"));
		assertThat(result.get(0).getDirection(), is(SortDirection.ASC));
		assertThat(result.get(1).getParameter(), is("field2"));
		assertThat(result.get(1).getDirection(), is(SortDirection.DESC));
	}
}
