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
package de.hybris.platform.commerceservices.search.flexiblesearch.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultPagedFlexibleSearchServiceIntegrationTest extends BaseCommerceBaseTest
{
	private static final String USER_GROUP_QUERY = "SELECT {ug:pk} FROM {UserGroup as ug} WHERE {ug:uid} LIKE 'DefaultPagedFlexibleSearchServiceIntegrationTest%'";
	private static final String SORT_CODE_UID = "byUid";
	private static final String SORT_CODE_LOCNAME = "byLocname";

	@Resource
	private DefaultPagedFlexibleSearchService defaultPagedFlexibleSearchService;

	private PageableData pageable;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/user-groups.impex", "UTF-8");
		pageable = new PageableData();
		pageable.setCurrentPage(2);
		pageable.setPageSize(2);
		pageable.setSort("uid");
	}

	@Test
	public void shouldSearchQueryPageable() throws ImpExException
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(USER_GROUP_QUERY);
		final SearchPageData<UserGroupModel> searchResult = defaultPagedFlexibleSearchService.search(query, pageable);

		Assert.assertNotNull("Search page data is null", searchResult);
		Assert.assertNotNull("Search results are null", searchResult.getResults());
		Assert.assertEquals("Unexpected number of results", 2, searchResult.getResults().size());
		Assert.assertEquals("Unexpected uid", "DefaultPagedFlexibleSearchServiceIntegrationTest-test4",
				searchResult.getResults().get(0).getUid());
		Assert.assertEquals("Unexpected uid", "DefaultPagedFlexibleSearchServiceIntegrationTest-test5",
				searchResult.getResults().get(1).getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchQueryIfPageableIsNull() throws ImpExException
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(USER_GROUP_QUERY);
		pageable.setCurrentPage(-1);
		defaultPagedFlexibleSearchService.search(query, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchQueryIfCurrentPageIsNegative() throws ImpExException
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(USER_GROUP_QUERY);
		defaultPagedFlexibleSearchService.search(query, null);
	}

	@Test
	public void shouldSearchWithQueryParams() throws ImpExException
	{
		final String query = "SELECT {ug:pk} FROM {UserGroup as ug} WHERE {ug:uid} LIKE ?uid";
		final HashMap<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("uid", "DefaultPagedFlexibleSearchServiceIntegrationTest%");
		final SearchPageData<UserGroupModel> searchResult = defaultPagedFlexibleSearchService.search(query, queryParams, pageable);

		Assert.assertNotNull("Search page data is null", searchResult);
		Assert.assertNotNull("Search results are null", searchResult.getResults());
		Assert.assertEquals("Unexpected number of results", 2, searchResult.getResults().size());
		Assert.assertEquals("Unexpected uid", "DefaultPagedFlexibleSearchServiceIntegrationTest-test4",
				searchResult.getResults().get(0).getUid());
		Assert.assertEquals("Unexpected uid", "DefaultPagedFlexibleSearchServiceIntegrationTest-test5",
				searchResult.getResults().get(1).getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchWithQueryParamsIfQueryIsNull() throws ImpExException
	{
		final HashMap<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("uid", "DefaultPagedFlexibleSearchServiceIntegrationTest%");
		defaultPagedFlexibleSearchService.search(null, queryParams, pageable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldSearchWithQueryParamsIfPageableIsNull() throws ImpExException
	{
		final String query = "SELECT {ug:pk} FROM {UserGroup as u} WHERE {ug:uid} LIKE ?uid";
		final HashMap<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("uid", "DefaultPagedFlexibleSearchServiceIntegrationTest%");
		defaultPagedFlexibleSearchService.search(query, queryParams, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchWithQueryParamsIfPageableCurrentPageIsNegative() throws ImpExException
	{
		final String query = "SELECT {ug:pk} FROM {UserGroup as u} WHERE {ug:uid} LIKE ?uid";
		final HashMap<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("uid", "DefaultPagedFlexibleSearchServiceIntegrationTest%");
		pageable.setCurrentPage(-1);
		defaultPagedFlexibleSearchService.search(query, queryParams, pageable);
	}

	@Test
	public void shouldSearchSortQueriesPageable() throws ImpExException
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_CODE_UID, USER_GROUP_QUERY + " ORDER BY {ug:uid} "),
				createSortQueryData(SORT_CODE_LOCNAME, USER_GROUP_QUERY + " ORDER BY {ug:locname} "));
		final SearchPageData<UserGroupModel> searchResult = defaultPagedFlexibleSearchService.search(sortQueries, SORT_CODE_LOCNAME,
				new HashMap<String, Object>(), pageable);

		Assert.assertNotNull("Search page data is null", searchResult);
		Assert.assertNotNull("Search results are null", searchResult.getResults());
		Assert.assertEquals("Unexpected number of results", 2, searchResult.getResults().size());
		Assert.assertEquals("Unexpected uid", "DefaultPagedFlexibleSearchServiceIntegrationTest-test2",
				searchResult.getResults().get(0).getUid());
		Assert.assertEquals("Unexpected uid", "DefaultPagedFlexibleSearchServiceIntegrationTest-test1",
				searchResult.getResults().get(1).getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchIfSortQueriesIsNull() throws ImpExException
	{
		defaultPagedFlexibleSearchService.search(null, SORT_CODE_LOCNAME, new HashMap<String, Object>(), pageable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchSortQueriesIfSortCodeIsNull() throws ImpExException
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_CODE_UID, USER_GROUP_QUERY + " ORDER BY {ug:uid} "),
				createSortQueryData(SORT_CODE_LOCNAME, USER_GROUP_QUERY + " ORDER BY {ug:locname} "));
		defaultPagedFlexibleSearchService.search(sortQueries, null, new HashMap<String, Object>(), pageable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchIfSortQueriesIsEmpty() throws ImpExException
	{
		defaultPagedFlexibleSearchService.search(new ArrayList<>(), SORT_CODE_LOCNAME, new HashMap<String, Object>(), pageable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSearchSortQueriesIfPageableIsNull() throws ImpExException
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_CODE_UID, USER_GROUP_QUERY + " ORDER BY {ug:uid} "),
				createSortQueryData(SORT_CODE_LOCNAME, USER_GROUP_QUERY + " ORDER BY {ug:locname} "));
		defaultPagedFlexibleSearchService.search(sortQueries, SORT_CODE_LOCNAME, new HashMap<String, Object>(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldSearchSortQueriesIfPageableCurrentPageIsNegative() throws ImpExException
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_CODE_UID, USER_GROUP_QUERY + " ORDER BY {ug:uid} "),
				createSortQueryData(SORT_CODE_LOCNAME, USER_GROUP_QUERY + " ORDER BY {ug:locname} "));
		pageable.setCurrentPage(-1);
		defaultPagedFlexibleSearchService.search(sortQueries, SORT_CODE_LOCNAME, new HashMap<String, Object>(), pageable);
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}
}
