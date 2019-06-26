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
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.getSelectedUsers;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.isCustomerIncluded;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.isUserIncluded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.testframework.Transactional;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


@Transactional
@IntegrationTest
public class DefaultB2BUserGroupFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	@Resource
	private DefaultB2BUserGroupFacade defaultB2BUserGroupFacade;

	@Resource
	private DefaultB2BUnitFacade defaultB2BUnitFacade;

	// paging
	private static final PageableData PAGEABLE_DATA_0_5_BY_NAME = createPageableData(0, 5, "byName");

	@Test
	public void testGetPagedCustomersForUserGroup()
	{
		final SearchPageData<CustomerData> searchPageData = defaultB2BUserGroupFacade.getPagedCustomersForUserGroup(
				PAGEABLE_DATA_0_5_BY_NAME, "DC");
		assertSearchPageData(5, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 9, searchPageData.getPagination().getTotalNumberOfResults());
		final List<CustomerData> selectedUsers = getSelectedUsers(searchPageData.getResults());
		assertNotNull("Selected users are null.", selectedUsers);
		assertEquals("Unexpected number of selected users.", 1, selectedUsers.size());
		assertTrue("Expected user was not selected.", isUserIncluded(selectedUsers, "DC CEO"));
	}

	@Test
	public void testUpdateUserGroup()
	{
		final B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group unit is null.", userGroup.getUnit());
		assertEquals("Unexpected user group unit id.", "DC", userGroup.getUnit().getUid());
		assertNull("User group name is not null.", userGroup.getName());

		// update some fields
		userGroup.setName("Europe Manager Permission Group");
		userGroup.setUnit(defaultB2BUnitFacade.getUnitForUid("DC Sales"));

		defaultB2BUserGroupFacade.updateUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", userGroup);

		final B2BUserGroupData updatedUserGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", updatedUserGroup);
		assertNotNull("User group unit is null.", updatedUserGroup.getUnit());
		assertEquals("Unexpected user group unit id.", "DC Sales", updatedUserGroup.getUnit().getUid());
		assertEquals("Unexpected user group name.", "Europe Manager Permission Group", updatedUserGroup.getName());
	}

	@Test
	public void testCreateUserGroup()
	{
		final B2BUserGroupData userGroup = new B2BUserGroupData();
		userGroup.setUid("DC New");
		userGroup.setName("New User Group");
		userGroup.setUnit(defaultB2BUnitFacade.getUnitForUid("DC"));

		defaultB2BUserGroupFacade.updateUserGroup("DC New", userGroup);

		final B2BUserGroupData newUserGroup = defaultB2BUserGroupFacade.getB2BUserGroup("DC New");
		assertNotNull("User group is null.", newUserGroup);
		assertNotNull("User group unit is null.", newUserGroup.getUnit());
		assertEquals("Unexpected user group unit id.", "DC", newUserGroup.getUnit().getUid());
		assertEquals("Unexpected user group name.", "New User Group", newUserGroup.getName());
	}

	@Test
	public void testDisableUserGroup()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertTrue("User group does not have any members.", userGroup.getMembers().size() > 0);

		defaultB2BUserGroupFacade.disableUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNull("User group members are not null.", userGroup.getMembers());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDisableUserGroupNullUserGroupUid()
	{
		defaultB2BUserGroupFacade.disableUserGroup(null);
	}

	@Test
	public void testRemoveUserGroup()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);

		defaultB2BUserGroupFacade.removeUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNull("User group is null.", userGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserGroupNullUserGroupUid()
	{
		defaultB2BUserGroupFacade.removeUserGroup(null);
	}

	@Test
	public void testGetPagedUserData()
	{
		final SearchPageData<CustomerData> searchPageData = defaultB2BUserGroupFacade.getPagedUserData(PAGEABLE_DATA_0_5_BY_NAME);
		assertSearchPageData(5, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected number of total results.", 9, searchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void testGetPagedB2BUserGroups()
	{
		final SearchPageData<B2BUserGroupData> searchPageData = defaultB2BUserGroupFacade
				.getPagedB2BUserGroups(PAGEABLE_DATA_0_5_BY_NAME);
		assertSearchPageData(4, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected number of total results.", 4, searchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void testGetB2BUserGroup()
	{
		final B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertEquals("Unexpected user group uid.", "EUROPE_MANAGER_PERM_GROUP_DC", userGroup.getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetB2BUserGroupNullUid()
	{
		defaultB2BUserGroupFacade.getB2BUserGroup(null);
	}

	@Test
	public void testAddMemberToUserGroup()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertEquals("Unexpected number of user group members.", 1, userGroup.getMembers().size());
		assertNotEquals("Unexpected user group member.", "DC S HH", userGroup.getMembers().get(0).getUid());

		final CustomerData user = defaultB2BUserGroupFacade.addMemberToUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC S HH");
		assertNotNull("User is null.", user);
		assertEquals("Unexpexted user uid", "DC S HH", user.getUid());
		assertTrue("User is not selected", user.isSelected());

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertEquals("Unexpected number of user group members.", 2, userGroup.getMembers().size());
		assertTrue("Expected member not found.", isCustomerIncluded(userGroup.getMembers(), "DC S HH"));
	}

	@Test
	public void testRemoveMemberFromUserGroup()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertEquals("Unexpected number of user group members.", 1, userGroup.getMembers().size());
		assertEquals("Expected member not found.", "DC Sales DE Boss", userGroup.getMembers().get(0).getUid());

		final CustomerData user = defaultB2BUserGroupFacade.removeMemberFromUserGroup("EUROPE_MANAGER_PERM_GROUP_DC",
				"DC Sales DE Boss");
		assertNotNull("User is null.", user);
		assertEquals("Unexpexted user uid.", "DC Sales DE Boss", user.getUid());
		assertFalse("User is selected.", user.isSelected());

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNull("User group members are not null.", userGroup.getMembers());
	}

	@Test
	public void testGetUserGroupDataForUid()
	{
		final UserGroupData userGroup = defaultB2BUserGroupFacade.getUserGroupDataForUid("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertEquals("Unexpected user group uid.", "EUROPE_MANAGER_PERM_GROUP_DC", userGroup.getUid());
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bcommercefacades/test/testOrganizations.csv";
	}

}
