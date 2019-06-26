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
package de.hybris.platform.b2bacceleratorfacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bapprovalprocessfacades.util.B2BApprovalProcessUnitTestUtils.getSelectedPermissions;
import static de.hybris.platform.b2bapprovalprocessfacades.util.B2BApprovalProcessUnitTestUtils.isPermissionIncluded;
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
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.testframework.Transactional;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@SuppressWarnings("deprecation")
@Transactional
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:b2bacceleratorfacades/test/b2bacceleratorfacades-test-spring.xml" })
public class DefaultB2BCommerceB2BUserGroupFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	@Resource
	private DefaultB2BCommerceB2BUserGroupFacade legacyB2BCommerceB2BUserGroupFacade;

	@Resource
	private DefaultB2BCommerceUnitFacade legacyB2BCommerceUnitFacade;

	// paging
	private static final PageableData PAGEABLE_DATA_0_5_BY_NAME = createPageableData(0, 5, "byName");
	private static final PageableData PAGEABLE_DATA_0_20_BY_NAME = createPageableData(0, 20, "byName");

	@Test
	public void testGetPagedCustomersForUserGroup()
	{
		final SearchPageData<CustomerData> searchPageData = legacyB2BCommerceB2BUserGroupFacade.getPagedCustomersForUserGroup(
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
	public void testGetPagedPermissionsForUserGroup()
	{
		final SearchPageData<B2BPermissionData> searchPageData = legacyB2BCommerceB2BUserGroupFacade
				.getPagedPermissionsForUserGroup(PAGEABLE_DATA_0_20_BY_NAME, "DC_CEO_PERMISSIONS");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		final List<B2BPermissionData> selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 7, selectedPermissions.size());
	}

	@Test
	public void testAddPermissionToUserGroup()
	{
		SearchPageData<B2BPermissionData> searchPageData = legacyB2BCommerceB2BUserGroupFacade.getPagedPermissionsForUserGroup(
				PAGEABLE_DATA_0_20_BY_NAME, "EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		List<B2BPermissionData> selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 2, selectedPermissions.size());
		assertFalse("Unexpected permission is selected.", isPermissionIncluded(selectedPermissions, "DC UNLIMITED TIMESPAN EUR"));

		legacyB2BCommerceB2BUserGroupFacade.addPermissionToUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC UNLIMITED TIMESPAN EUR");

		searchPageData = legacyB2BCommerceB2BUserGroupFacade.getPagedPermissionsForUserGroup(PAGEABLE_DATA_0_20_BY_NAME,
				"EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 3, selectedPermissions.size());
		assertTrue("Expected permission is not selected.", isPermissionIncluded(selectedPermissions, "DC UNLIMITED TIMESPAN EUR"));
	}

	@Test
	public void testRemovePermissionFromUserGroup()
	{
		SearchPageData<B2BPermissionData> searchPageData = legacyB2BCommerceB2BUserGroupFacade.getPagedPermissionsForUserGroup(
				PAGEABLE_DATA_0_20_BY_NAME, "EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		List<B2BPermissionData> selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 2, selectedPermissions.size());
		assertTrue("Expected permission is not selected.", isPermissionIncluded(selectedPermissions, "DC 100000 TIMESPAN EUR"));

		legacyB2BCommerceB2BUserGroupFacade.removePermissionFromUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC 100000 TIMESPAN EUR");

		searchPageData = legacyB2BCommerceB2BUserGroupFacade.getPagedPermissionsForUserGroup(PAGEABLE_DATA_0_20_BY_NAME,
				"EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 1, selectedPermissions.size());
		assertFalse("Unexpected permission is selected.", isPermissionIncluded(selectedPermissions, "DC 100000 TIMESPAN EUR"));
	}

	@Test
	public void testUpdateUserGroup()
	{
		final B2BUserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group unit is null.", userGroup.getUnit());
		assertEquals("Unexpected user group unit id.", "DC", userGroup.getUnit().getUid());
		assertNull("User group name is not null.", userGroup.getName());

		// update some fields
		userGroup.setName("Europe Manager Permission Group");
		userGroup.setUnit(legacyB2BCommerceUnitFacade.getUnitForUid("DC Sales"));

		legacyB2BCommerceB2BUserGroupFacade.updateUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", userGroup);

		final B2BUserGroupData updatedUserGroup = legacyB2BCommerceB2BUserGroupFacade
				.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
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
		userGroup.setUnit(legacyB2BCommerceUnitFacade.getUnitForUid("DC"));

		legacyB2BCommerceB2BUserGroupFacade.updateUserGroup("DC New", userGroup);

		final B2BUserGroupData newUserGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("DC New");
		assertNotNull("User group is null.", newUserGroup);
		assertNotNull("User group unit is null.", newUserGroup.getUnit());
		assertEquals("Unexpected user group unit id.", "DC", newUserGroup.getUnit().getUid());
		assertEquals("Unexpected user group name.", "New User Group", newUserGroup.getName());
	}

	@Test
	public void testDisableUserGroup()
	{
		B2BUserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertTrue("User group does not have any members.", userGroup.getMembers().size() > 0);

		legacyB2BCommerceB2BUserGroupFacade.disableUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");

		userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNull("User group members are not null.", userGroup.getMembers());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDisableUserGroupNullUserGroupUid()
	{
		legacyB2BCommerceB2BUserGroupFacade.disableUserGroup(null);
	}

	@Test
	public void testRemoveUserGroup()
	{
		B2BUserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);

		legacyB2BCommerceB2BUserGroupFacade.removeUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");

		userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNull("User group is null.", userGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserGroupNullUserGroupUid()
	{
		legacyB2BCommerceB2BUserGroupFacade.removeUserGroup(null);
	}

	@Test
	public void testGetPagedUserData()
	{
		final SearchPageData<CustomerData> searchPageData = legacyB2BCommerceB2BUserGroupFacade
				.getPagedUserData(PAGEABLE_DATA_0_5_BY_NAME);
		assertSearchPageData(5, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected number of total results.", 9, searchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void testGetPagedB2BUserGroups()
	{
		final SearchPageData<B2BUserGroupData> searchPageData = legacyB2BCommerceB2BUserGroupFacade
				.getPagedB2BUserGroups(PAGEABLE_DATA_0_5_BY_NAME);
		assertSearchPageData(4, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected number of total results.", 4, searchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void testGetB2BUserGroup()
	{
		final B2BUserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertEquals("Unexpected user group uid.", "EUROPE_MANAGER_PERM_GROUP_DC", userGroup.getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetB2BUserGroupNullUid()
	{
		legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup(null);
	}

	@Test
	public void testAddMemberToUserGroup()
	{
		B2BUserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertEquals("Unexpected number of user group members.", 1, userGroup.getMembers().size());
		assertNotEquals("Unexpected user group member.", "DC S HH", userGroup.getMembers().get(0).getUid());

		final CustomerData user = legacyB2BCommerceB2BUserGroupFacade.addMemberToUserGroup("EUROPE_MANAGER_PERM_GROUP_DC",
				"DC S HH");
		assertNotNull("User is null.", user);
		assertEquals("Unexpexted user uid", "DC S HH", user.getUid());
		assertTrue("User is not selected", user.isSelected());

		userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertEquals("Unexpected number of user group members.", 2, userGroup.getMembers().size());
		assertTrue("Expected member not found.", isCustomerIncluded(userGroup.getMembers(), "DC S HH"));
	}

	@Test
	public void testRemoveMemberFromUserGroup()
	{
		B2BUserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNotNull("User group members are null.", userGroup.getMembers());
		assertEquals("Unexpected number of user group members.", 1, userGroup.getMembers().size());
		assertEquals("Expected member not found.", "DC Sales DE Boss", userGroup.getMembers().get(0).getUid());

		final CustomerData user = legacyB2BCommerceB2BUserGroupFacade.removeMemberFromUserGroup("EUROPE_MANAGER_PERM_GROUP_DC",
				"DC Sales DE Boss");
		assertNotNull("User is null.", user);
		assertEquals("Unexpexted user uid.", "DC Sales DE Boss", user.getUid());
		assertFalse("User is selected.", user.isSelected());

		userGroup = legacyB2BCommerceB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertNull("User group members are not null.", userGroup.getMembers());
	}

	@Test
	public void testGetUserGroupDataForUid()
	{
		final UserGroupData userGroup = legacyB2BCommerceB2BUserGroupFacade.getUserGroupDataForUid("EUROPE_MANAGER_PERM_GROUP_DC");
		assertNotNull("User group is null.", userGroup);
		assertEquals("Unexpected user group uid.", "EUROPE_MANAGER_PERM_GROUP_DC", userGroup.getUid());
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bacceleratorfacades/test/testOrganizations.csv";
	}

}