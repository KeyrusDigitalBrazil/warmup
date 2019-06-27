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
package de.hybris.platform.cms2.usergroups.service.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.service.NamedQueryService;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUserGroupNamedQuerySearchServiceTest
{
	private static final String TEXT = "text";
	@Mock
	private NamedQueryService namedQueryService;

	@InjectMocks
	private DefaultUserGroupNamedQuerySearchService userGroupSearchService;

	@Mock
	private PageableData pageableData;
	@Mock
	private SearchResult searchResult;
	@Mock
	private UserGroupModel userGroupModel;

	@Before
	public void setup()
	{
		when(searchResult.getResult()).thenReturn(Arrays.asList(userGroupModel));
		when(namedQueryService.getSearchResult(any())).thenReturn(searchResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedQueryForProductSearchWithNullValues()
	{
		userGroupSearchService.getNamedQueryForUserGroupSearch(null, null);
	}

	@Test
	public void testGetNamedQueryForProductSearchWithNullTextAndNullSort()
	{
		final NamedQuery namedQuery = userGroupSearchService.getNamedQueryForUserGroupSearch(null, new PageableData());
		assertThat(namedQuery.getSort().get(0).getParameter(), is(UserGroupModel.NAME));
		assertThat(namedQuery.getSort().get(0).getDirection(), is(SortDirection.ASC));
		assertThat(namedQuery.getParameters().get(UserGroupModel.NAME), is("%%"));
		assertThat(namedQuery.getParameters().get(UserGroupModel.UID), is("%%"));
	}

	@Test
	public void testFindUserGroups()
	{
		final SearchResult<UserGroupModel> userGroups = userGroupSearchService.findUserGroups(TEXT, pageableData);
		verify(namedQueryService).getSearchResult(any());
		assertThat(userGroups.getResult(), hasItem(userGroupModel));
	}

}
