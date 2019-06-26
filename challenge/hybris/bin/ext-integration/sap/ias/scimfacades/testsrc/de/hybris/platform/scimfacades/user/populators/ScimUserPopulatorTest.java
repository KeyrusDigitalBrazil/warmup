/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimfacades.user.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ScimUserPopulatorTest
{

	@InjectMocks
	private final ScimUserPopulator populator = new ScimUserPopulator();

	@Mock
	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;

	@Mock
	private EmployeeModel employee;

	private ScimUser scimUser;

	@Mock
	private ScimUserGroupModel scimUserGroup;

	@Mock
	private UserGroupModel userGroup;

	@Test
	public void testPopulateWithGroups()
	{
		scimUser = new ScimUser();

		Mockito.when(employee.getScimUserId()).thenReturn("external-id");
		Mockito.when(employee.getName()).thenReturn("firstName lastName");

		Mockito.when(employee.getUid()).thenReturn("sample@email.com");
		Mockito.when(employee.isLoginDisabled()).thenReturn(Boolean.FALSE);

		Mockito.when(scimUserGroupGenericDao.find()).thenReturn(Collections.singletonList(scimUserGroup));
		Mockito.when(scimUserGroup.getUserGroups()).thenReturn(Collections.singletonList(userGroup));
		Mockito.when(employee.getGroups()).thenReturn(Collections.singleton(userGroup));
		Mockito.when(scimUserGroup.getScimUserGroup()).thenReturn("scim-group");

		populator.populate(employee, scimUser);

		Assert.assertTrue(scimUser.getActive());
		Assert.assertTrue(scimUser.getVerified());
		Assert.assertEquals("external-id", scimUser.getId());
		Assert.assertEquals("firstName", scimUser.getName().getGivenName());
		Assert.assertEquals("lastName", scimUser.getName().getFamilyName());

		Assert.assertTrue(scimUser.getEmails().get(0).getPrimary());
		Assert.assertEquals("sample@email.com", scimUser.getEmails().get(0).getValue());

		Assert.assertEquals("employee", scimUser.getUserType());

		Assert.assertEquals("scim-group", scimUser.getGroups().get(0).getValue());
	}

	@Test
	public void testPopulateWithoutGroups()
	{
		scimUser = new ScimUser();
		Mockito.when(employee.getScimUserId()).thenReturn("external-id");
		Mockito.when(employee.getName()).thenReturn("firstName lastName");

		Mockito.when(employee.getUid()).thenReturn("sample@email.com");
		Mockito.when(employee.isLoginDisabled()).thenReturn(Boolean.FALSE);

		populator.populate(employee, scimUser);

		Assert.assertTrue(scimUser.getActive());
		Assert.assertTrue(scimUser.getVerified());
		Assert.assertEquals("external-id", scimUser.getId());
		Assert.assertEquals("firstName", scimUser.getName().getGivenName());
		Assert.assertEquals("lastName", scimUser.getName().getFamilyName());

		Assert.assertTrue(scimUser.getEmails().get(0).getPrimary());
		Assert.assertEquals("sample@email.com", scimUser.getEmails().get(0).getValue());

		Assert.assertEquals("employee", scimUser.getUserType());

		Assert.assertNull(scimUser.getGroups());
	}
}
