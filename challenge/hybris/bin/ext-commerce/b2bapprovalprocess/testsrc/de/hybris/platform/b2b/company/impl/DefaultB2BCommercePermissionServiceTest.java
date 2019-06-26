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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BPermissionService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.SetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BCommercePermissionServiceTest
{
	private static final String USER_GROUP_ID = "userGroup";
	private static final String USER_ID = "user";
	private static final String PERMISSION1_ID = "permission1";
	private static final String PERMISSION2_ID = "permission2";
	private static final String PERMISSION3_ID = "permission3";

	private final DefaultB2BCommercePermissionService b2bCommercePermissionService = new DefaultB2BCommercePermissionService();

	private B2BCustomerModel user;
	private B2BUserGroupModel userGroup;

	private B2BPermissionModel permission1;
	private B2BPermissionModel permission2;
	private B2BPermissionModel permission3;

	@Mock
	private PagedGenericDao<B2BPermissionModel> pagedB2BPermissionDao;
	@Mock
	private B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2bPermissionService;
	@Mock
	private PageableData pageableData;
	@Mock
	private SearchPageData<B2BPermissionModel> searchPageData;
	@Mock
	private UserService userService;
	@Mock
	private ModelService modelService;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		permission1 = new B2BPermissionModel();
		permission1.setCode("permission1");
		permission2 = new B2BPermissionModel();
		permission2.setCode("permission2");
		permission3 = new B2BPermissionModel();
		permission3.setCode("permission3");

		userGroup = new B2BUserGroupModel();
		final List<B2BPermissionModel> permissions = new ArrayList<B2BPermissionModel>();
		permissions.add(permission1);
		permissions.add(permission2);
		userGroup.setUid(USER_GROUP_ID);
		userGroup.setPermissions(permissions);

		user = new B2BCustomerModel();
		user.setUid(USER_ID);
		user.setPermissions(SetUtils.EMPTY_SET);

		// set up user service mock
		BDDMockito.given(userService.getUserForUID(USER_ID, B2BCustomerModel.class)).willReturn(user);
		BDDMockito.given(userService.getUserGroupForUID(USER_GROUP_ID, B2BUserGroupModel.class)).willReturn(userGroup);
		BDDMockito.given(userService.getUserGroupForUID("doesNotExist", B2BUserGroupModel.class)).willThrow(
				new UnknownIdentifierException("User group does not exist."));
		BDDMockito.given(userService.getUserGroupForUID(null, B2BUserGroupModel.class)).willThrow(
				new IllegalArgumentException("Parameter [uid] can not be null"));

		// set up permission service mock
		BDDMockito.given(b2bPermissionService.getB2BPermissionForCode(PERMISSION1_ID)).willReturn(permission1);
		BDDMockito.given(b2bPermissionService.getB2BPermissionForCode(PERMISSION2_ID)).willReturn(permission2);
		BDDMockito.given(b2bPermissionService.getB2BPermissionForCode(PERMISSION3_ID)).willReturn(permission3);

		b2bCommercePermissionService.setB2bPermissionService(b2bPermissionService);
		b2bCommercePermissionService.setPagedB2BPermissionDao(pagedB2BPermissionDao);
		b2bCommercePermissionService.setUserService(userService);
		b2bCommercePermissionService.setModelService(modelService);
	}

	@Test
	public void testShouldGetPagedPermissions()
	{
		Mockito.when(pagedB2BPermissionDao.find(pageableData)).thenReturn(searchPageData);
		assertThat(b2bCommercePermissionService.getPagedPermissions(pageableData), equalTo(searchPageData));
	}

	@Test
	public void testShouldGetPermissionForCode()
	{
		assertThat(b2bCommercePermissionService.getPermissionForCode(PERMISSION1_ID), equalTo(permission1));
	}

	@Test
	public void testShouldAddPermissionToCustomer()
	{
		final B2BPermissionModel permissionModel = b2bCommercePermissionService.addPermissionToCustomer(USER_ID, PERMISSION1_ID);
		Assert.assertEquals("Incorrect permission added", permissionModel, permission1);
		Assert.assertEquals("Incorrect number of permissions", 1, user.getPermissions().size());
		Assert.assertTrue("Permission has not been added to user", user.getPermissions().contains(permission1));
	}

	@Test
	public void testShouldRemovePermissionFromCustomer()
	{
		final Set<B2BPermissionModel> permissions = new HashSet<B2BPermissionModel>();
		permissions.add(permission1);
		user.setPermissions(permissions);

		final B2BPermissionModel permissionModel = b2bCommercePermissionService.removePermissionFromCustomer(USER_ID,
				PERMISSION1_ID);
		Assert.assertEquals("Incorrect permission removed", permissionModel, permission1);
		Assert.assertEquals("Incorrect number of permissions", 0, user.getPermissions().size());
		Assert.assertFalse("Permission has not been removed from user", user.getPermissions().contains(permission1));
	}

	@Test
	public void testShouldAddPermissionToUserGroup()
	{
		final B2BPermissionModel addedPermission = b2bCommercePermissionService.addPermissionToUserGroup(USER_GROUP_ID,
				PERMISSION3_ID);
		Assert.assertEquals("Unexpected permission added", permission3, addedPermission);
		Assert.assertNotNull("User group permissions is null", userGroup.getPermissions());
		Assert.assertEquals("Unexpected number of permissions assigned to user group", 3, userGroup.getPermissions().size());
		Assert.assertTrue("Permission has not been assigned to user group", userGroup.getPermissions().contains(permission3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddPermissionToUserGroupUidNull()
	{
		b2bCommercePermissionService.addPermissionToUserGroup(null, PERMISSION3_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddPermissionToUserGroupPermissionIdNull()
	{
		b2bCommercePermissionService.addPermissionToUserGroup(USER_GROUP_ID, null);
	}

	@Test
	public void testShouldRemovePermissionFromUserGroup()
	{
		final B2BPermissionModel removedPermission = b2bCommercePermissionService.removePermissionFromUserGroup(USER_GROUP_ID,
				PERMISSION2_ID);
		Assert.assertEquals("Unexpected permission removed", permission2, removedPermission);
		Assert.assertNotNull("User group permissions is null", userGroup.getPermissions());
		Assert.assertEquals("Unexpected number of permissions assigned to user group", 1, userGroup.getPermissions().size());
		Assert.assertFalse("Permission has not been removed from user group", userGroup.getPermissions().contains(permission2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemovePermissionFromUserGroupUidNull()
	{
		b2bCommercePermissionService.removePermissionFromUserGroup(null, PERMISSION3_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemovePermissionFromUserGroupPermissionIdNull()
	{
		b2bCommercePermissionService.removePermissionFromUserGroup(USER_GROUP_ID, null);
	}

}
