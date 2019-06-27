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
package de.hybris.platform.commerceservices.organization.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultOrgUnitAuthorizationStrategyTest extends BaseCommerceBaseTest
{
	private static final String GLOBAL_ADMIN = "globalAdmin";
	private static final String USA_ADMIN = "usaAdmin";
	private static final String USA_MANAGER = "usaManager";
	private static final String USA_EMPLOYEE = "usaEmployee";
	private static final String UNASSIGNED_EMPLOYEE = "unassignedEmployee";

	private static final String CREATE_GROUPS_KEY = "commerceservices.organization.rights.create.groups";
	private static final String EDIT_GROUPS_KEY = "commerceservices.organization.rights.edit.groups";
	private static final String EDIT_PARENT_GROUPS_KEY = "commerceservices.organization.rights.edit.parent.groups";
	private static final String VIEW_GROUPS_KEY = "commerceservices.organization.rights.view.groups";

	@Resource(name = "defaultOrgUnitAuthorizationStrategy")
	private DefaultOrgUnitAuthorizationStrategy defaultOrgUnitAuthorizationStrategy;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private String createGroupsBackup;
	private String editGroupsBackup;
	private String editParentGroupsBackup;
	private String viewGroupsBackup;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commerceservices/test/orgUnitTestData.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);

		// Temporarily change organization related authorization properties

		createGroupsBackup = Config.getString(CREATE_GROUPS_KEY, null);
		editGroupsBackup = Config.getString(EDIT_GROUPS_KEY, null);
		editParentGroupsBackup = Config.getString(EDIT_PARENT_GROUPS_KEY, null);
		viewGroupsBackup = Config.getString(VIEW_GROUPS_KEY, null);

		Config.setParameter(CREATE_GROUPS_KEY, "orgadmingroup");
		Config.setParameter(EDIT_GROUPS_KEY, "orgadmingroup,orgmanagergroup");
		Config.setParameter(EDIT_PARENT_GROUPS_KEY, "orgadmingroup");
		Config.setParameter(VIEW_GROUPS_KEY, "orgemployeegroup");
	}

	@After
	public void cleanUp()
	{
		Config.setParameter(CREATE_GROUPS_KEY, createGroupsBackup);
		Config.setParameter(EDIT_GROUPS_KEY, editGroupsBackup);
		Config.setParameter(EDIT_PARENT_GROUPS_KEY, editParentGroupsBackup);
		Config.setParameter(VIEW_GROUPS_KEY, viewGroupsBackup);
	}

	@Test
	public void shouldValidateCreatePermissionAllowSalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateCreatePermission(userService.getUserForUID(GLOBAL_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldValidateCreatePermissionAllowUSASalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateCreatePermission(userService.getUserForUID(USA_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldValidateCreatePermissionNotAllowSalesEmployee()
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to create. User: " + USA_EMPLOYEE);

		defaultOrgUnitAuthorizationStrategy.validateCreatePermission(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class));
	}

	@Test
	public void shouldValidateCreatePermissionEmptyCreateGroupsProperty()
	{
		Config.setParameter(CREATE_GROUPS_KEY, StringUtils.EMPTY);

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Property is empty or not configured. Property name: " + CREATE_GROUPS_KEY);

		defaultOrgUnitAuthorizationStrategy.validateCreatePermission(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class));
	}

	@Test
	public void shouldValidateEditPermissionAllowSalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateEditPermission(userService.getUserForUID(GLOBAL_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldValidateEditPermissionAllowUSASalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateEditPermission(userService.getUserForUID(USA_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldValidateEditPermissionNotAllowSalesEmployee()
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit. User: " + USA_EMPLOYEE);

		defaultOrgUnitAuthorizationStrategy.validateEditPermission(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class));
	}

	@Test
	public void shouldValidateViewPermissionAllowSalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateViewPermission(userService.getUserForUID(GLOBAL_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldValidateViewPermissionAllowUSASalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateViewPermission(userService.getUserForUID(USA_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldValidateViewPermissionAllowSalesEmployee()
	{
		defaultOrgUnitAuthorizationStrategy.validateViewPermission(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class));
	}

	@Test
	public void shouldValidateViewPermissionNotAllowNonSales()
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to view. User: " + UNASSIGNED_EMPLOYEE);

		defaultOrgUnitAuthorizationStrategy
				.validateViewPermission(userService.getUserForUID(UNASSIGNED_EMPLOYEE, EmployeeModel.class));
	}

	@Test
	public void shouldAllowEditParentForSalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy
				.validateEditParentPermission(userService.getUserForUID(GLOBAL_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldAllowEditParentForUSASalesAdmin()
	{
		defaultOrgUnitAuthorizationStrategy.validateEditParentPermission(userService.getUserForUID(USA_ADMIN, EmployeeModel.class));
	}

	@Test
	public void shouldNotAllowEditParentForSalesManager()
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit parent unit. User: " + USA_MANAGER);

		defaultOrgUnitAuthorizationStrategy
				.validateEditParentPermission(userService.getUserForUID(USA_MANAGER, EmployeeModel.class));
	}

	@Test
	public void shouldNotAllowEditParentForSalesEmployee()
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit parent unit. User: " + USA_EMPLOYEE);

		defaultOrgUnitAuthorizationStrategy
				.validateEditParentPermission(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class));
	}

	@Test
	public void shouldBeAbleToEditParentForeSalesAdmin()
	{
		Assert.assertTrue(GLOBAL_ADMIN + " should be able to edit parent.",
				defaultOrgUnitAuthorizationStrategy.canEditParentUnit(userService.getUserForUID(GLOBAL_ADMIN, EmployeeModel.class)));
	}

	@Test
	public void shouldBeAbleToEditParentForUSASalesAdmin()
	{
		Assert.assertTrue(USA_ADMIN + " should be able to edit parent.",
				defaultOrgUnitAuthorizationStrategy.canEditParentUnit(userService.getUserForUID(USA_ADMIN, EmployeeModel.class)));
	}

	@Test
	public void shouldNotBeAbleToEditParentForSalesManager()
	{
		Assert.assertFalse(USA_MANAGER + " should not be able to edit parent.",
				defaultOrgUnitAuthorizationStrategy.canEditParentUnit(userService.getUserForUID(USA_MANAGER, EmployeeModel.class)));
	}

	@Test
	public void shouldNotBeAbleToEditParentForSalesEmployee()
	{
		Assert.assertFalse(USA_EMPLOYEE + " should not be able to edit parent.",
				defaultOrgUnitAuthorizationStrategy.canEditParentUnit(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class)));
	}

	@Test
	public void shouldBeAbleToEditForeSalesAdmin()
	{
		Assert.assertTrue(GLOBAL_ADMIN + " should be able to edit unit.",
				defaultOrgUnitAuthorizationStrategy.canEditUnit(userService.getUserForUID(GLOBAL_ADMIN, EmployeeModel.class)));
	}

	@Test
	public void shouldBeAbleToEditForUSASalesAdmin()
	{
		Assert.assertTrue(USA_ADMIN + " should be able to edit unit.",
				defaultOrgUnitAuthorizationStrategy.canEditUnit(userService.getUserForUID(USA_ADMIN, EmployeeModel.class)));
	}

	@Test
	public void shouldNotBeAbleToEditForSalesManager()
	{
		Assert.assertTrue(USA_MANAGER + " should not be able to edit unit.",
				defaultOrgUnitAuthorizationStrategy.canEditUnit(userService.getUserForUID(USA_MANAGER, EmployeeModel.class)));
	}

	@Test
	public void shouldNotBeAbleToEditForSalesEmployee()
	{
		Assert.assertFalse(USA_EMPLOYEE + " should not be able to edit unit.",
				defaultOrgUnitAuthorizationStrategy.canEditParentUnit(userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class)));
	}
}
