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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BPermissionService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BCommerceUserServiceTest
{
	private static final String USER_ID = "user";
	private static final String APPROVER_ID = "approver";
	private static final String APPROVER2_ID = "approver2";
	private static final String INVALID_ID = "doesNotExist";
	private static final String GROUP_ID = "group";
	private static final String GROUP2_ID = "group2";
	private static final String GROUP_ROLE_ID = "groupRole";
	private static final String PERMISSION_ID = "permission";
	private static final String PERMISSION2_ID = "permission2";

	private DefaultB2BCommerceUserService defaultB2BCommerceUserService;

	private B2BCustomerModel user;
	private B2BCustomerModel approver;
	private B2BCustomerModel approver2;

	private B2BUserGroupModel userGroup;
	private B2BUserGroupModel userGroup2;
	private B2BUserGroupModel userGroupRole;
	private B2BUserGroupModel approverGroup;

	private B2BPermissionModel userPermission;
	private B2BPermissionModel userPermission2;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Mock
	private B2BPermissionService permissionService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		// Users
		user = new B2BCustomerModel();
		user.setUid(USER_ID);
		user.setActive(Boolean.TRUE);

		approver = new B2BCustomerModel();
		approver.setUid(APPROVER_ID);

		approver2 = new B2BCustomerModel();
		approver2.setUid(APPROVER2_ID);

		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>();
		approvers.add(approver2);
		user.setApprovers(approvers);

		// Groups
		userGroup = new B2BUserGroupModel();
		userGroup.setUid(GROUP_ID);

		userGroup2 = new B2BUserGroupModel();
		userGroup2.setUid(GROUP2_ID);

		userGroupRole = new B2BUserGroupModel();
		userGroupRole.setUid(GROUP_ROLE_ID);

		approverGroup = new B2BUserGroupModel();
		approverGroup.setUid(B2BConstants.B2BAPPROVERGROUP);


		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		groups.add(userGroup);
		groups.add(userGroupRole);
		user.setGroups(groups);
		approver.setGroups(groups);

		// Permissions
		userPermission = new B2BPermissionModel();
		userPermission.setCode(PERMISSION_ID);

		userPermission2 = new B2BPermissionModel();
		userPermission2.setCode(PERMISSION2_ID);

		final Set<B2BPermissionModel> permissions = new HashSet<B2BPermissionModel>();
		permissions.add(userPermission);
		user.setPermissions(permissions);

		// userService
		BDDMockito.given(userService.getUserForUID(USER_ID, B2BCustomerModel.class)).willReturn(user);
		BDDMockito.given(userService.getUserForUID(APPROVER_ID, B2BCustomerModel.class)).willReturn(approver);
		BDDMockito.given(userService.getUserForUID(APPROVER2_ID, B2BCustomerModel.class)).willReturn(approver2);
		BDDMockito.given(userService.getUserForUID(INVALID_ID, B2BCustomerModel.class)).willReturn(null);
		BDDMockito.given(userService.getUserGroupForUID(GROUP_ID, B2BUserGroupModel.class)).willReturn(userGroup);
		BDDMockito.given(userService.getUserGroupForUID(GROUP2_ID, B2BUserGroupModel.class)).willReturn(userGroup2);
		BDDMockito.given(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).willReturn(approverGroup);
		BDDMockito.given(userService.getUserGroupForUID(GROUP2_ID)).willReturn(userGroup2);
		BDDMockito.given(userService.getUserGroupForUID(GROUP_ROLE_ID)).willReturn(userGroupRole);

		// permissionService
		BDDMockito.given(permissionService.getB2BPermissionForCode(PERMISSION_ID)).willReturn(userPermission);
		BDDMockito.given(permissionService.getB2BPermissionForCode(PERMISSION2_ID)).willReturn(userPermission2);

		// defaultB2BCommerceUserService
		defaultB2BCommerceUserService = new DefaultB2BCommerceUserService();
		defaultB2BCommerceUserService.setUserService(userService);
		defaultB2BCommerceUserService.setModelService(modelService);
		defaultB2BCommerceUserService.setB2BPermissionService(permissionService);
	}

	@Test
	public void testShouldAddB2BUserGroupToCustomer()
	{
		Assert.assertFalse(user.getGroups().contains(userGroup2));

		final B2BUserGroupModel returnedGroup = defaultB2BCommerceUserService.addB2BUserGroupToCustomer(USER_ID, GROUP2_ID);
		Assert.assertEquals("Incorrect user group added", returnedGroup, userGroup2);
		Assert.assertEquals("Incorrect number of user groups", 3, user.getGroups().size());
		Assert.assertTrue(user.getGroups().contains(userGroup2));
	}

	@Test
	public void testShouldDeselectB2BUserGroupFromCustomer()
	{
		Assert.assertTrue(user.getGroups().contains(userGroup));

		final B2BUserGroupModel returnedGroup = defaultB2BCommerceUserService.deselectB2BUserGroupFromCustomer(USER_ID, GROUP_ID);
		Assert.assertEquals("Incorrect user group deselected", returnedGroup, userGroup);
		Assert.assertEquals("Incorrect number of user groups", 1, user.getGroups().size());
		Assert.assertFalse("User group was not deselected", user.getGroups().contains(returnedGroup));
	}

	@Test
	public void testShouldRemoveB2BUserGroupFromCustomerGroups()
	{
		Assert.assertTrue(user.getGroups().contains(userGroup));

		defaultB2BCommerceUserService.removeB2BUserGroupFromCustomerGroups(USER_ID, GROUP_ID);
		Assert.assertEquals("Incorrect number of user groups", 1, user.getGroups().size());
		Assert.assertFalse("User group was not removed", user.getGroups().contains(userGroup));
	}

	@Test
	public void testShouldAddApproverToCustomer()
	{
		Assert.assertFalse(user.getApprovers().contains(approver));

		final B2BCustomerModel returnedApprover = defaultB2BCommerceUserService.addApproverToCustomer(USER_ID, APPROVER_ID);
		Assert.assertEquals("Unexpected approver returned", returnedApprover, approver);
		Assert.assertNotNull("Approver groups is null", returnedApprover.getGroups());
		Assert.assertTrue("Approver was not added to approver group", returnedApprover.getGroups().contains(approverGroup));
		Assert.assertTrue("Approver was not added to user", user.getApprovers().contains(approver));
	}

	@Test
	public void testShouldRemoveApproverFromCustomer()
	{
		Assert.assertTrue(user.getApprovers().contains(approver2));

		final B2BCustomerModel returnedApprover = defaultB2BCommerceUserService.removeApproverFromCustomer(USER_ID, APPROVER2_ID);
		Assert.assertEquals("Unexpected approver returned", returnedApprover, approver2);
		Assert.assertFalse("Approver was not removed from user", user.getApprovers().contains(approver2));
	}


	@Test
	public void testShouldRemoveUserRole()
	{
		Assert.assertTrue(user.getGroups().contains(userGroupRole));

		final B2BCustomerModel returnedUser = defaultB2BCommerceUserService.removeUserRole(USER_ID, GROUP_ROLE_ID);
		Assert.assertEquals("Unexpected user returned", returnedUser, user);
		Assert.assertNotNull("User role groups are null", returnedUser.getGroups());
		Assert.assertEquals("Incorrect number of user roles", 1, returnedUser.getGroups().size());
		Assert.assertFalse("Role was not removed from user", returnedUser.getGroups().contains(userGroupRole));
	}

	@Test
	public void testShouldAddUserRole()
	{
		Assert.assertFalse(user.getGroups().contains(userGroup2));

		final B2BCustomerModel returnedUser = defaultB2BCommerceUserService.addUserRole(USER_ID, GROUP2_ID);
		Assert.assertEquals("Unexpected user returned", returnedUser, user);
		Assert.assertNotNull("User role groups are null", returnedUser.getGroups());
		Assert.assertEquals("Incorrect number of user roles", 3, returnedUser.getGroups().size());
		Assert.assertTrue("Role was not added to user", returnedUser.getGroups().contains(userGroup2));
	}

	@Test
	public void testShouldRemovePermissionFromCustomer()
	{
		Assert.assertTrue(user.getPermissions().contains(userPermission));

		final B2BPermissionModel permissionModel = defaultB2BCommerceUserService.removePermissionFromCustomer(USER_ID,
				PERMISSION_ID);
		Assert.assertEquals("Incorrect permission removed", permissionModel, userPermission);
		Assert.assertEquals("Incorrect number of permissions", 0, user.getPermissions().size());
		Assert.assertFalse("Permission has not been removed from user", user.getPermissions().contains(userPermission));
	}

	@Test
	public void testShouldAddPermissionToCustomer()
	{
		Assert.assertFalse(user.getPermissions().contains(userPermission2));

		final B2BPermissionModel permissionModel = defaultB2BCommerceUserService.addPermissionToCustomer(USER_ID, PERMISSION2_ID);
		Assert.assertEquals("Incorrect permission added", permissionModel, userPermission2);
		Assert.assertEquals("Incorrect number of permissions", 2, user.getPermissions().size());
		Assert.assertTrue("Permission has not been added to user", user.getPermissions().contains(userPermission2));
	}

	@Test
	public void testShouldDisableCustomer() throws DuplicateUidException
	{
		Assert.assertTrue(user.getActive().booleanValue());

		defaultB2BCommerceUserService.disableCustomer(USER_ID);

		Assert.assertFalse("User was not disabled", user.getActive().booleanValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotDisableCustomer() throws DuplicateUidException
	{
		defaultB2BCommerceUserService.disableCustomer(null);
	}

	@Test
	public void testShouldEnableCustomer() throws DuplicateUidException
	{
		user.setActive(Boolean.FALSE);

		defaultB2BCommerceUserService.enableCustomer(USER_ID);

		Assert.assertTrue("User was not enabled", user.getActive().booleanValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotEnableCustomer() throws DuplicateUidException
	{
		defaultB2BCommerceUserService.enableCustomer(null);
	}
}
