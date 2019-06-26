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
package de.hybris.platform.cms2.namedquery;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.SortDirection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class NamedQueryTest
{

	private static final String NAMED_QUERY_NAME = "some-query-name";
	private static final Integer PAGE_SIZE = 10;
	private static final Integer CURRENT_PAGE = 1;
	private static final List<Sort> SORT_PARAMS = Arrays.asList(
			new Sort().withDirection(SortDirection.ASC).withParameter("param1"),
			new Sort().withDirection(SortDirection.ASC).withParameter("param2"));
	private static final Map<String, ? extends Object> PARAMS = new HashMap<>();

	/**
	 * This test is only here to test the bean generation using simpleJavaBeanUsingWithMethods-template.vm
	 */
	@Test
	public void buildNamedQuerySuccessfully()
	{
		final NamedQuery namedQuery = new NamedQuery() //
				.withQueryName(NAMED_QUERY_NAME) //
				.withPageSize(PAGE_SIZE) //
				.withCurrentPage(CURRENT_PAGE) //
				.withParameters(PARAMS) //
				.withSort(SORT_PARAMS);

		Assert.assertEquals(NAMED_QUERY_NAME, namedQuery.getQueryName());
		Assert.assertEquals(PAGE_SIZE, namedQuery.getPageSize());
		Assert.assertEquals(CURRENT_PAGE, namedQuery.getCurrentPage());
		Assert.assertEquals(PARAMS, namedQuery.getParameters());
		Assert.assertEquals(SORT_PARAMS, namedQuery.getSort());
	}
}
