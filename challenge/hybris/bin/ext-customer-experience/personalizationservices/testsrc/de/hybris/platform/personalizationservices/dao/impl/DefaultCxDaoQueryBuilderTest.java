/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationservices.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.dao.CxDaoStrategy;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import jersey.repackaged.com.google.common.collect.Sets;



@UnitTest
public class DefaultCxDaoQueryBuilderTest
{
	private DefaultCxDaoQueryBuilder builder;
	private Map<String, Object> queryParams;
	private Map<String, String> extraParams;
	private List<CxDaoStrategy> strategies;
	private FlexibleSearchQuery defaultQuery;

	@Before
	public void setup()
	{
		queryParams = new HashMap<>();
		extraParams = new HashMap<>();
		builder = new DefaultCxDaoQueryBuilder();

		builder.setCxDaoStrategySelector(new DefaultCxDaoStrategySelector());

		final TestDaoStrategy aStrategy = new TestDaoStrategy(Sets.newHashSet("a"),
				p -> new FlexibleSearchQuery("SELECT * FROM table WHERE a = ?", map(p, "a")));

		final TestDaoStrategy bStrategy = new TestDaoStrategy(Sets.newHashSet("b"),
				p -> new FlexibleSearchQuery("SELECT * FROM table WHERE b = ?", map(p, "b")));

		final TestDaoStrategy abStrategy = new TestDaoStrategy(Sets.newHashSet("a", "b"),
				p -> new FlexibleSearchQuery("SELECT * FROM table WHERE a = ? AND b = ?", map(p, "a", "b")));

		strategies = Lists.newArrayList(aStrategy, bStrategy, abStrategy);
		defaultQuery = new FlexibleSearchQuery("SELECT * FROM table WHERE z = ?", Collections.singletonMap("z", "Z"));
	}

	private Map<String, Object> map(final Map<String, String> map, final String... keys)
	{
		final Map<String, Object> result = new HashMap<>();
		for (final String key : keys)
		{
			result.put(key, map.get(key));
		}
		return result;
	}

	@Test
	public void testBuildBaseQuery()
	{
		//given
		final String expectedQuery = "SELECT * FORM table";

		//when
		final FlexibleSearchQuery buildQuery = builder.buildQuery(expectedQuery, Collections.emptyMap());

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals(expectedQuery, buildQuery.getQuery());
		Assert.assertEquals(Collections.emptyMap(), buildQuery.getQueryParameters());
	}

	@Test
	public void testBuildQueryWithoutStrategies()
	{
		//when
		final FlexibleSearchQuery buildQuery = builder.buildQueryFromStrategy(defaultQuery, Collections.emptyList(), extraParams);

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals(defaultQuery.getQuery(), buildQuery.getQuery());
		Assert.assertEquals(defaultQuery.getQueryParameters(), buildQuery.getQueryParameters());
	}

	@Test
	public void testBuildQueryWithoutExtraParmeters()
	{
		//when
		final FlexibleSearchQuery buildQuery = builder.buildQueryFromStrategy(defaultQuery, strategies, extraParams);

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals(defaultQuery.getQuery(), buildQuery.getQuery());
		Assert.assertEquals(defaultQuery.getQueryParameters(), buildQuery.getQueryParameters());
	}

	@Test
	public void testBuildQueryWithAParameter()
	{
		//given
		extraParams.put("a", "A");

		queryParams.put("a", "A");
		queryParams.put("z", "Z");

		//when
		final FlexibleSearchQuery buildQuery = builder.buildQueryFromStrategy(defaultQuery, strategies, extraParams);

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals("SELECT * FROM table WHERE a = ?", buildQuery.getQuery());
		Assert.assertEquals(queryParams, buildQuery.getQueryParameters());
	}

	@Test
	public void testBuildQueryWithBParameter()
	{
		//given
		extraParams.put("b", "A");

		queryParams.put("b", "A");
		queryParams.put("z", "Z");

		//when
		final FlexibleSearchQuery buildQuery = builder.buildQueryFromStrategy(defaultQuery, strategies, extraParams);

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals("SELECT * FROM table WHERE b = ?", buildQuery.getQuery());
		Assert.assertEquals(queryParams, buildQuery.getQueryParameters());
	}

	@Test
	public void testBuildQueryWithABParameter()
	{
		//given
		extraParams.put("a", "A");
		extraParams.put("b", "B");

		queryParams.put("a", "A");
		queryParams.put("b", "B");
		queryParams.put("z", "Z");

		//when
		final FlexibleSearchQuery buildQuery = builder.buildQueryFromStrategy(defaultQuery, strategies, extraParams);

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals("SELECT * FROM table WHERE a = ? AND b = ?", buildQuery.getQuery());
		Assert.assertEquals(queryParams, buildQuery.getQueryParameters());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildQueryWithDuplicatedStrategies()
	{
		//given
		extraParams.put("a", "A");

		//when
		builder.buildQueryFromStrategy(defaultQuery, Lists.newArrayList(strategies.get(0), strategies.get(0)), extraParams);
	}


	@Test
	public void testBuildQueryWithUnknownParameter()
	{
		extraParams.put("a", "A");
		extraParams.put("b", "B");
		extraParams.put("c", "C");

		queryParams.put("a", "A");
		queryParams.put("b", "B");
		queryParams.put("z", "Z");
		//when
		final FlexibleSearchQuery buildQuery = builder.buildQueryFromStrategy(defaultQuery, strategies, extraParams);

		//then
		Assert.assertNotNull(buildQuery);
		Assert.assertEquals("SELECT * FROM table WHERE a = ? AND b = ?", buildQuery.getQuery());
		Assert.assertEquals(queryParams, buildQuery.getQueryParameters());
	}

	private static class TestDaoStrategy implements CxDaoStrategy
	{
		private final Set<String> requiredParameters;
		private final Function<Map<String, String>, FlexibleSearchQuery> queryFunction;

		public TestDaoStrategy(final Set<String> requiredParameters,
				final Function<Map<String, String>, FlexibleSearchQuery> queryFunction)
		{
			this.requiredParameters = requiredParameters;
			this.queryFunction = queryFunction;
		}

		@Override
		public Set<String> getRequiredParameters()
		{
			return requiredParameters;
		}

		@Override
		public FlexibleSearchQuery getQuery(final Map<String, String> params)
		{
			return queryFunction.apply(params);
		}
	}
}
