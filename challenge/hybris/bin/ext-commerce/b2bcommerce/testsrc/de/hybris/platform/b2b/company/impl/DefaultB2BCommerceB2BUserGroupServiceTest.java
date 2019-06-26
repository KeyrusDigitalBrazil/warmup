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
package de.hybris.platform.b2b.company.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link DefaultB2BCommerceB2BUserGroupService}.
 */
@UnitTest
public class DefaultB2BCommerceB2BUserGroupServiceTest
{
	private static final int NUMBER_OF_GROUPS = 25;
	private static final int DEFAULT_PAGE_SIZE = 10;

	private DefaultB2BCommerceB2BUserGroupService defaultB2BCommerceB2BUserGroupService;

	private B2BUserGroupModel userGroup1;
	private B2BUserGroupModel userGroup2;
	private B2BUserGroupModel userGroup3;
	private B2BUserGroupModel userGroup4;

	private B2BCustomerModel user1;
	private B2BCustomerModel user2;
	private B2BCustomerModel user3;

	private List<B2BUserGroupModel> userGroups;

	private PageableData pageSize10FirstPage;
	private PageableData pageSize10SecondPage;
	private PageableData pageSize10ThirdPage;


	@Mock
	private PagedGenericDao<B2BUserGroupModel> pagedB2BUserGroupDao;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		userGroups = createUserGroupResults();

		pageSize10FirstPage = createPageableData(DEFAULT_PAGE_SIZE, 0);
		pageSize10SecondPage = createPageableData(DEFAULT_PAGE_SIZE, 1);
		pageSize10ThirdPage = createPageableData(DEFAULT_PAGE_SIZE, 2);

		user1 = new B2BCustomerModel();
		user1.setUid("user1");
		user2 = new B2BCustomerModel();
		user2.setUid("user2");
		user3 = new B2BCustomerModel();
		user3.setUid("user3");

		userGroup1 = new B2BUserGroupModel();
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>();
		members.add(user1);
		members.add(user2);
		userGroup1.setUid("userGroup1");
		userGroup1.setMembers(members);
		userGroup2 = new B2BUserGroupModel();
		userGroup2.setUid("userGroup2");
		userGroup3 = new B2BUserGroupModel();
		userGroup3.setUid("userGroup3");
		userGroup4 = new B2BUserGroupModel();
		userGroup4.setUid("userGroup4");


		// set up user group dao mock
		BDDMockito.given(pagedB2BUserGroupDao.find(pageSize10FirstPage)).willReturn(createSearchPageData(pageSize10FirstPage));
		BDDMockito.given(pagedB2BUserGroupDao.find(pageSize10SecondPage)).willReturn(createSearchPageData(pageSize10SecondPage));
		BDDMockito.given(pagedB2BUserGroupDao.find(pageSize10ThirdPage)).willReturn(createSearchPageData(pageSize10ThirdPage));

		// set up user service mock
		BDDMockito.given(userService.getUserGroupForUID("userGroup1", B2BUserGroupModel.class)).willReturn(userGroup1);
		BDDMockito.given(userService.getUserGroupForUID("doesNotExist", B2BUserGroupModel.class)).willThrow(
				new UnknownIdentifierException("User group does not exist."));
		BDDMockito.given(userService.getUserGroupForUID(null, B2BUserGroupModel.class)).willThrow(
				new IllegalArgumentException("Parameter [uid] can not be null"));
		BDDMockito.given(userService.getUserGroupForUID("userGroup1")).willReturn(userGroup1);
		BDDMockito.given(userService.getUserGroupForUID("userGroup2")).willReturn(userGroup2);
		BDDMockito.given(userService.getUserGroupForUID("userGroup3")).willReturn(userGroup3);
		BDDMockito.given(userService.getUserGroupForUID("userGroup4")).willReturn(userGroup4);
		BDDMockito.given(userService.getUserForUID("user1", B2BCustomerModel.class)).willReturn(user1);
		BDDMockito.given(userService.getUserForUID("user2", B2BCustomerModel.class)).willReturn(user2);
		BDDMockito.given(userService.getUserForUID("user3", B2BCustomerModel.class)).willReturn(user3);

		// prepare service under test
		defaultB2BCommerceB2BUserGroupService = new DefaultB2BCommerceB2BUserGroupService();
		defaultB2BCommerceB2BUserGroupService.setPagedB2BUserGroupDao(pagedB2BUserGroupDao);
		defaultB2BCommerceB2BUserGroupService.setUserService(userService);
		defaultB2BCommerceB2BUserGroupService.setModelService(modelService);
	}

	@Test
	public void testGetPagedB2BUserGroupsPageSize10FirstPage()
	{
		final SearchPageData<B2BUserGroupModel> pagedB2BUserGroups = defaultB2BCommerceB2BUserGroupService
				.getPagedB2BUserGroups(pageSize10FirstPage);

		Assert.assertNotNull("Returned SearchPageData may not be null", pagedB2BUserGroups);
		assertPagination(pagedB2BUserGroups, pageSize10FirstPage);
		assertResults(pagedB2BUserGroups, 10);
	}

	@Test
	public void testGetPagedB2BUserGroupsPageSize10SecondPage()
	{
		final SearchPageData<B2BUserGroupModel> pagedB2BUserGroups = defaultB2BCommerceB2BUserGroupService
				.getPagedB2BUserGroups(pageSize10SecondPage);

		Assert.assertNotNull("Returned SearchPageData may not be null", pagedB2BUserGroups);
		assertPagination(pagedB2BUserGroups, pageSize10SecondPage);
		assertResults(pagedB2BUserGroups, 10);
	}

	@Test
	public void testGetPagedB2BUserGroupsPageSize10ThirdPage()
	{
		final SearchPageData<B2BUserGroupModel> pagedB2BUserGroups = defaultB2BCommerceB2BUserGroupService
				.getPagedB2BUserGroups(pageSize10ThirdPage);

		Assert.assertNotNull("Returned SearchPageData may not be null", pagedB2BUserGroups);
		assertPagination(pagedB2BUserGroups, pageSize10ThirdPage);
		assertResults(pagedB2BUserGroups, 5);
	}

	@Test
	public void testUpdateUserGroups()
	{
		final Collection<String> availableUserGroups = createStringCollection("userGroup1", "userGroup2", "userGroup3",
				"userGroup4");
		final Collection<String> selectedUserGroups = createStringCollection("userGroup1", "userGroup2", "userGroup3");
		final Set<PrincipalGroupModel> expectedUserGroups = createGroupSet(userGroup1, userGroup2, userGroup3);
		final Set<PrincipalGroupModel> customerGroups = createGroupSet(userGroup1);
		user1.setGroups(customerGroups);

		final Set<PrincipalGroupModel> updatedCustomerGroups = defaultB2BCommerceB2BUserGroupService.updateUserGroups(
				availableUserGroups, selectedUserGroups, user1);

		Assert.assertTrue("Unexpected set of groups returned",
				CollectionUtils.isEqualCollection(expectedUserGroups, updatedCustomerGroups));
		Assert.assertEquals("Customer groups haven't been updated as expected", expectedUserGroups, user1.getGroups());
	}

	@Test
	public void testUpdateUserGroupsUnavailableGroup()
	{
		final Collection<String> availableUserGroups = createStringCollection("userGroup1", "userGroup2", "userGroup3",
				"userGroup4");
		final Collection<String> selectedUserGroups = createStringCollection("userGroup2", "userGroup4", "unavailableUserGroup");
		final Set<PrincipalGroupModel> expectedUserGroups = createGroupSet(userGroup2, userGroup4);
		final Set<PrincipalGroupModel> customerGroups = createGroupSet(userGroup1);
		user1.setGroups(customerGroups);

		final Set<PrincipalGroupModel> updatedCustomerGroups = defaultB2BCommerceB2BUserGroupService.updateUserGroups(
				availableUserGroups, selectedUserGroups, user1);

		Assert.assertTrue("Unexpected set of groups returned",
				CollectionUtils.isEqualCollection(expectedUserGroups, updatedCustomerGroups));
		Assert.assertEquals("Customer groups haven't been updated as expected", expectedUserGroups, user1.getGroups());
	}

	@Test
	public void testUpdateUserGroupsSelectedGroupsNull()
	{
		final Collection<String> availableUserGroups = createStringCollection("userGroup1", "userGroup2", "userGroup3",
				"userGroup4");
		final Set<PrincipalGroupModel> customerGroups = createGroupSet(userGroup1);
		user1.setGroups(customerGroups);

		final Set<PrincipalGroupModel> updatedCustomerGroups = defaultB2BCommerceB2BUserGroupService.updateUserGroups(
				availableUserGroups, null, user1);

		Assert.assertTrue("Unexpected set of groups returned",
				CollectionUtils.isEqualCollection(customerGroups, updatedCustomerGroups));
		Assert.assertEquals("Customer groups haven't been updated as expected", customerGroups, user1.getGroups());
	}

	@Test
	public void testGetUserGroupForUID()
	{
		final B2BUserGroupModel userGroup = defaultB2BCommerceB2BUserGroupService.getUserGroupForUID("userGroup1",
				B2BUserGroupModel.class);
		Assert.assertNotNull("User group is null", userGroup);
		Assert.assertEquals("Uid of user group does not match expected value", "userGroup1", userGroup.getUid());
	}

	@Test
	public void testGetUserGroupForUIDDoesNotExist()
	{
		final B2BUserGroupModel userGroup = defaultB2BCommerceB2BUserGroupService.getUserGroupForUID("doesNotExist",
				B2BUserGroupModel.class);
		Assert.assertNull("User group is not null", userGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUserGroupForUIDNull()
	{
		defaultB2BCommerceB2BUserGroupService.getUserGroupForUID(null, B2BUserGroupModel.class);
	}

	@Test
	public void testDisableUserGroup()
	{
		defaultB2BCommerceB2BUserGroupService.disableUserGroup("userGroup1");
		Assert.assertTrue("User group still has members after being deactivated", CollectionUtils.isEmpty(userGroup1.getMembers()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDisableUserGroupUidNull()
	{
		defaultB2BCommerceB2BUserGroupService.disableUserGroup(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserGroupUidNull()
	{
		defaultB2BCommerceB2BUserGroupService.removeUserGroup(null);
	}

	@Test
	public void testAddMemberToUserGroup()
	{
		final B2BCustomerModel addedMember = defaultB2BCommerceB2BUserGroupService.addMemberToUserGroup("userGroup1", "user3");
		Assert.assertEquals("Unexpected customer added", user3, addedMember);
		Assert.assertNotNull("User group member list is null", userGroup1.getMembers());
		Assert.assertEquals("Unexpected number of members assigned to user group", 3, userGroup1.getMembers().size());
		Assert.assertTrue("Member has not been added to user group", userGroup1.getMembers().contains(user3));
	}

	@Test
	public void testRemoveMemberFromUserGroup()
	{
		final B2BCustomerModel addedMember = defaultB2BCommerceB2BUserGroupService.removeMemberFromUserGroup("userGroup1", "user2");
		Assert.assertEquals("Unexpected customer removed", user2, addedMember);
		Assert.assertNotNull("User group member list is null", userGroup1.getMembers());
		Assert.assertEquals("Unexpected number of members assigned to user group", 1, userGroup1.getMembers().size());
		Assert.assertFalse("Member has not been removed from user group", userGroup1.getMembers().contains(user2));
	}

	protected PageableData createPageableData(final int pageSize, final int currentPage)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		pageableData.setCurrentPage(currentPage);
		return pageableData;
	}

	protected List<B2BUserGroupModel> createUserGroupResults()
	{
		final List<B2BUserGroupModel> userGroups = new ArrayList<B2BUserGroupModel>();
		for (int i = 0; i < NUMBER_OF_GROUPS; i++)
		{
			final B2BUserGroupModel userGroup = new B2BUserGroupModel();
			userGroup.setUid("userGroup" + i);
			userGroup.setName("User Group " + i);
			userGroups.add(userGroup);
		}
		return userGroups;
	}

	protected SearchPageData<B2BUserGroupModel> createSearchPageData(final PageableData pageableData)
	{
		final SearchPageData<B2BUserGroupModel> searchPageData = new SearchPageData<B2BUserGroupModel>();

		createPaginationData(pageableData);

		searchPageData.setPagination(createPaginationData(pageableData));
		searchPageData.setResults(createResults(pageableData));

		return searchPageData;
	}

	protected List<B2BUserGroupModel> createResults(final PageableData pageableData)
	{
		final int fromIndex = pageableData.getCurrentPage() * pageableData.getPageSize();
		final int rest = NUMBER_OF_GROUPS - pageableData.getCurrentPage() * pageableData.getPageSize();
		final int itemsOnPage = rest < pageableData.getPageSize() ? rest : pageableData.getPageSize();

		final List<B2BUserGroupModel> results = userGroups.subList(fromIndex, fromIndex + itemsOnPage);
		return results;
	}

	protected PaginationData createPaginationData(final PageableData pageableData)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(pageableData.getCurrentPage());
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setNumberOfPages((NUMBER_OF_GROUPS + pageableData.getPageSize() - 1) / pageableData.getPageSize());
		paginationData.setTotalNumberOfResults(NUMBER_OF_GROUPS);
		return paginationData;
	}

	protected void assertResults(final SearchPageData<B2BUserGroupModel> pagedB2BUserGroups, final int expectedSize)
	{
		Assert.assertNotNull("Result list may not be null", pagedB2BUserGroups.getResults());
		Assert.assertEquals("Number of returned results doesn't match the expected value", expectedSize, pagedB2BUserGroups
				.getResults().size());
	}

	protected void assertPagination(final SearchPageData<B2BUserGroupModel> pagedB2BUserGroups, final PageableData pageableData)
	{
		Assert.assertNotNull("Pagination may not be null", pagedB2BUserGroups.getPagination());
		Assert.assertEquals("Current Page does not match the expected value", pageableData.getCurrentPage(), pagedB2BUserGroups
				.getPagination().getCurrentPage());
		Assert.assertEquals("Page Size does not match the expected value", pageableData.getPageSize(), pagedB2BUserGroups
				.getPagination().getPageSize());
		Assert.assertEquals("Number of pages does not match the expected value", 3, pagedB2BUserGroups.getPagination()
				.getNumberOfPages());
		Assert.assertEquals("Total number of results does not match the expected value", NUMBER_OF_GROUPS, pagedB2BUserGroups
				.getPagination().getTotalNumberOfResults());
	}

	protected Collection<String> createStringCollection(final String... grouIds)
	{
		final Collection<String> result = new ArrayList<String>();
		for (final String groupId : grouIds)
		{
			result.add(groupId);
		}
		return result;
	}

	protected Set<PrincipalGroupModel> createGroupSet(final PrincipalGroupModel... userGroups)
	{
		final Set<PrincipalGroupModel> result = new HashSet<PrincipalGroupModel>();
		for (final PrincipalGroupModel userGroup : userGroups)
		{
			result.add(userGroup);
		}
		return result;
	}

}
