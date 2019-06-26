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
package de.hybris.platform.cms2.services.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.daos.impl.DefaultCMSComponentDao;
import de.hybris.platform.cms2.services.SortQueryData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultSortQueryDataRegistryTest
{
	private static final String INVALID = "invalid";
	private static final String QUERY_DEFAULT = "order by {uid} asc";
	private static final String QUERY_BY_NAME = "order by {name} desc";

	private final Set<SortQueryData> allSortQueries = new HashSet<>();
	private final DefaultSortQueryDataRegistry registry = new DefaultSortQueryDataRegistry();

	private DefaultSortQueryData sortQueryData1;
	private DefaultSortQueryData sortQueryData2;

	@Before
	public void setUp() throws Exception
	{
		sortQueryData1 = new DefaultSortQueryData();
		sortQueryData1.setTypeClass(DefaultCMSComponentDao.class);
		sortQueryData1.setQuery(QUERY_DEFAULT);
		sortQueryData1.setSortCode("uid");
		sortQueryData1.setDefault(true);

		sortQueryData2 = new DefaultSortQueryData();
		sortQueryData2.setTypeClass(DefaultCMSComponentDao.class);
		sortQueryData2.setQuery(QUERY_BY_NAME);
		sortQueryData2.setSortCode("name");
		sortQueryData2.setDefault(false);

		allSortQueries.add(sortQueryData1);
		allSortQueries.add(sortQueryData2);

		registry.setAllSortQueries(allSortQueries);
		registry.afterPropertiesSet();
	}

	@Test
	public void shouldPopulateSortQueryDataInAfterPropertiesSet()
	{
		final Collection<SortQueryData> result = registry.getAllSortQueryData();
		assertThat(result.size(), equalTo(2));
		assertThat(result, containsInAnyOrder(sortQueryData1, sortQueryData2));
	}

	@Test
	public void shouldFindSortQueryDataByTypeClass()
	{
		final Optional<SortQueryData> result = registry.getSortQueryData(DefaultCMSComponentDao.class, "name");
		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get().getTypeClass(), equalTo(DefaultCMSComponentDao.class));
		assertThat(result.get().getQuery(), equalTo(QUERY_BY_NAME));
		assertFalse(result.get().isDefault());
	}

	@Test
	public void shouldFindDefaultSortQueryData()
	{
		final Optional<SortQueryData> result = registry.getDefaultSortQueryData(DefaultCMSComponentDao.class);
		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get().getTypeClass(), equalTo(DefaultCMSComponentDao.class));
		assertThat(result.get().getQuery(), equalTo(QUERY_DEFAULT));
		assertTrue(result.get().isDefault());
	}

	@Test
	public void shouldNotFindSortQueryData()
	{
		final Optional<SortQueryData> result = registry.getSortQueryData(DefaultCMSComponentDao.class, INVALID);
		assertThat(result.isPresent(), equalTo(false));
	}

}
