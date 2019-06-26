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
package de.hybris.platform.commerceservices.organization.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitParameter;
import de.hybris.platform.commerceservices.organization.utils.OrgUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultOrgUnitServiceIntegrationTest extends BaseCommerceBaseTest
{
	private static final String ROOT_UNIT_UID = "rootUnit";
	private static final String CA_UNIT_UID = "canada";
	private static final String MT_UNIT_UID = "montreal";
	private static final String QC_UNIT_UID = "quebec";
	private static final String US_UNIT_UID = "usa";
	private static final String NY_UNIT_UID = "new york";
	private static final String EU_UNIT_UID = "europe";
	private static final String EU_UNIT_NAME = "Europe";
	private static final String EU_UNIT_DESCRIPTION = "Unit for Europe";
	private static final String NA_UNIT_UID = "northAmerica";
	private static final String NA_UNIT_NAME = "North America";
	private static final String NA_UNIT_DESCRIPTION = "Unit for North America";
	private static final String UPDATED_UNIT_UID = "updatedUnit";
	private static final String UPDATED_UNIT_NAME = "Updated Unit";
	private static final String UPDATED_UNIT_DESCRIPTION = "Updated Unit Description";

	private static final String ORG_ADMIN_UID = "orgadmingroup";
	private static final String ORG_MANAGER_UID = "orgmanagergroup";
	private static final String ORG_EMPLOYEE_UID = "orgemployeegroup";
	private static final String GLOBAL_ADMIN = "globalAdmin";
	private static final String CANADA_ADMIN = "canadaAdmin";
	private static final String CANADA_EMPLOYEE = "canadaEmployee";

	private static final String UNASSIGNED_EMPLOYEE = "unassignedEmployee";
	private static final String NA_EMPLOYEE = "northAmericaEmployee";
	private static final String USA_ADMIN = "usaAdmin";
	private static final String USA_MANAGER = "usaManager";
	private static final String USA_EMPLOYEE = "usaEmployee";

	private static final String CREATE_GROUPS_KEY = "commerceservices.organization.rights.create.groups";
	private static final String EDIT_GROUPS_KEY = "commerceservices.organization.rights.edit.groups";
	private static final String EDIT_PARENT_GROUPS_KEY = "commerceservices.organization.rights.edit.parent.groups";
	private static final String VIEW_GROUPS_KEY = "commerceservices.organization.rights.view.groups";
	private static final String ROLES_KEY = "commerceservices.organization.roles";

	@Resource(name = "defaultOrgUnitService")
	private DefaultOrgUnitService defaultOrgUnitService;

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
	private String rolesBackup;

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
		rolesBackup = Config.getString(ROLES_KEY, null);

		Config.setParameter(CREATE_GROUPS_KEY, "orgadmingroup");
		Config.setParameter(EDIT_GROUPS_KEY, "orgadmingroup,orgmanagergroup");
		Config.setParameter(EDIT_PARENT_GROUPS_KEY, "orgadmingroup");
		Config.setParameter(VIEW_GROUPS_KEY, "orgemployeegroup");
		Config.setParameter(ROLES_KEY, "orgadmingroup,orgmanagergroup,orgemployeegroup");
	}

	@After
	public void cleanUp() throws Exception
	{
		Config.setParameter(CREATE_GROUPS_KEY, createGroupsBackup);
		Config.setParameter(EDIT_GROUPS_KEY, editGroupsBackup);
		Config.setParameter(EDIT_PARENT_GROUPS_KEY, editParentGroupsBackup);
		Config.setParameter(VIEW_GROUPS_KEY, viewGroupsBackup);
		Config.setParameter(ROLES_KEY, rolesBackup);
	}

	@Test
	public void shouldGetUnitForUid()
	{
		validateGetUnit();
	}

	protected void validateGetUnit()
	{
		final OrgUnitModel northAmerica = OrgUnitServiceTestUtil.getUnit(NA_UNIT_UID, defaultOrgUnitService);

		Assert.assertTrue("Unit is not active.", northAmerica.getActive().booleanValue());
		Assert.assertEquals("Unexpexted uid.", NA_UNIT_UID, northAmerica.getUid());
		Assert.assertEquals("Unexpexted name.", NA_UNIT_NAME, northAmerica.getName());
		Assert.assertEquals("Unexpexted description.", NA_UNIT_DESCRIPTION, northAmerica.getDescription());
		Assert.assertTrue("No parent unit set.", CollectionUtils.isNotEmpty(northAmerica.getGroups()));

		Assert.assertTrue("Expected parent unit not set.",
				northAmerica.getGroups().contains(OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService)));
	}

	@Test
	public void shouldGetUnitForUidForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));
		validateGetUnit();
	}

	@Test
	public void shouldGetUnitForUidForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));
		validateGetUnit();
	}

	@Test
	public void shouldNotGetUnitForUidForNonOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(UNASSIGNED_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to view. User: " + UNASSIGNED_EMPLOYEE);

		defaultOrgUnitService.getUnitForUid(NA_UNIT_UID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetUnitForUidNull()
	{
		defaultOrgUnitService.getUnitForUid(null);
	}

	@Test
	public void shouldNotGetUnitForUidUnknown()
	{
		final Optional<OrgUnitModel> unknownUnitOptional = defaultOrgUnitService.getUnitForUid("unknown");
		Assert.assertFalse("OrgUnitModel for unknown identifier was returned.", unknownUnitOptional.isPresent());
	}

	@Test
	public void shouldCreateUnit()
	{
		validateCreateUnit();
	}

	protected void validateCreateUnit()
	{
		// retrieve the desired parent unit
		final OrgUnitModel rootUnit = OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService);

		defaultOrgUnitService.createUnit(OrgUnitServiceTestUtil.createOrgUnitParam(null, rootUnit, EU_UNIT_UID, EU_UNIT_NAME,
				Boolean.TRUE, EU_UNIT_DESCRIPTION));

		final OrgUnitModel europe = OrgUnitServiceTestUtil.getUnit(EU_UNIT_UID, defaultOrgUnitService);
		Assert.assertTrue("Unit is not active.", europe.getActive().booleanValue());
		Assert.assertEquals("Unexpexted uid..", EU_UNIT_UID, europe.getUid());
		Assert.assertEquals("Unexpexted name.", EU_UNIT_NAME, europe.getName());
		Assert.assertEquals("Unexpexted description.", EU_UNIT_DESCRIPTION, europe.getDescription());
		Assert.assertEquals("Unexpected path value.", "/" + ROOT_UNIT_UID + "/" + EU_UNIT_UID, europe.getPath());
		Assert.assertTrue("No parent unit set.", CollectionUtils.isNotEmpty(europe.getGroups()));
		Assert.assertTrue("Parent unit has not been set correctly.", europe.getGroups().contains(rootUnit));
	}

	@Test
	public void shouldCreateUnitForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));

		validateCreateUnit();
	}

	@Test
	public void shouldNotCreateUnitForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to create. User: " + USA_MANAGER);

		defaultOrgUnitService.createUnit(
				OrgUnitServiceTestUtil.createOrgUnitParam(null, OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService),
						EU_UNIT_UID, EU_UNIT_NAME, Boolean.TRUE, EU_UNIT_DESCRIPTION));
	}

	@Test
	public void shouldNotCreateUnitForOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to create. User: " + USA_EMPLOYEE);

		defaultOrgUnitService.createUnit(
				OrgUnitServiceTestUtil.createOrgUnitParam(null, OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService),
						EU_UNIT_UID, EU_UNIT_NAME, Boolean.TRUE, EU_UNIT_DESCRIPTION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateUnitUidNull()
	{
		defaultOrgUnitService.createUnit(
				OrgUnitServiceTestUtil.createOrgUnitParam(null, OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService),
						null, EU_UNIT_NAME, Boolean.TRUE, EU_UNIT_DESCRIPTION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateUnitNameNull()
	{
		defaultOrgUnitService.createUnit(
				OrgUnitServiceTestUtil.createOrgUnitParam(null, OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService),
						EU_UNIT_UID, null, Boolean.TRUE, EU_UNIT_DESCRIPTION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateUnitActiveNull()
	{
		defaultOrgUnitService.createUnit(
				OrgUnitServiceTestUtil.createOrgUnitParam(null, OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService),
						EU_UNIT_UID, EU_UNIT_NAME, null, EU_UNIT_DESCRIPTION));
	}

	@Test
	public void shouldUpdateUnit() throws ImpExException
	{
		importCsv("/commerceservices/test/orgUnitTestData_addPath.impex", "UTF-8");

		// retrieve the an existing unit
		final OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);
		// retrieve the current parent unit
		final OrgUnitModel northAmerica = OrgUnitServiceTestUtil.getUnit(NA_UNIT_UID, defaultOrgUnitService);

		defaultOrgUnitService.updateUnit(OrgUnitServiceTestUtil.createOrgUnitParam(montreal, northAmerica, UPDATED_UNIT_UID,
				UPDATED_UNIT_NAME, Boolean.FALSE, UPDATED_UNIT_DESCRIPTION));

		// retrieve the updated unit to do assertions
		final OrgUnitModel updatedUnit = OrgUnitServiceTestUtil.getUnit(UPDATED_UNIT_UID, defaultOrgUnitService);

		Assert.assertFalse("Unit has not been deactivated.", updatedUnit.getActive().booleanValue());
		Assert.assertEquals("Unexpexted uid.", UPDATED_UNIT_UID, updatedUnit.getUid());
		Assert.assertEquals("Unexpexted name.", UPDATED_UNIT_NAME, updatedUnit.getName());
		Assert.assertEquals("Unexpected path value.", "/" + ROOT_UNIT_UID + "/" + NA_UNIT_UID + "/" + UPDATED_UNIT_UID,
				updatedUnit.getPath());
		Assert.assertEquals("Unexpexted description.", UPDATED_UNIT_DESCRIPTION, updatedUnit.getDescription());
		Assert.assertTrue("No parent unit set.", CollectionUtils.isNotEmpty(updatedUnit.getGroups()));
		Assert.assertTrue("Expected parent unit not set.", updatedUnit.getGroups().contains(northAmerica));
	}

	@Test
	public void shouldUpdateUnitAndAddParent()
	{
		// Using orgAdmin since he heas the most permissions
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));

		final OrgUnitModel nyOrgUnit = OrgUnitServiceTestUtil.getUnit(NY_UNIT_UID, defaultOrgUnitService);
		final Optional<OrgUnitModel> actualParentUnitOptional = defaultOrgUnitService.getParent(nyOrgUnit);
		final OrgUnitModel newParent = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);

		Assert.assertFalse("Org Unit should NOT already have a parent to test adding a parent.",
				actualParentUnitOptional.isPresent());


		final OrgUnitParameter updateParameter = new OrgUnitParameter();
		updateParameter.setOrgUnit(nyOrgUnit);
		updateParameter.setParentUnit(newParent);

		defaultOrgUnitService.updateUnit(updateParameter);

		// retrieve the updated unit to do assertions
		final OrgUnitModel updatedUnit = OrgUnitServiceTestUtil.getUnit(NY_UNIT_UID, defaultOrgUnitService);
		final OrgUnitModel updatedParent = defaultOrgUnitService.getParent(updatedUnit).orElse(null);

		Assert.assertNotNull("Parent should have been added, but it's not.", updatedParent);
		Assert.assertEquals("Updated parent is not the expetced one.", newParent, updatedParent);
	}

	@Test
	public void shouldUpdateUnitAndRemoveParent()
	{
		// Using orgAdmin since he heas the most permissions
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));

		final OrgUnitModel usaOrgUnit = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		final OrgUnitModel actualParent = OrgUnitServiceTestUtil.getParentUnit(usaOrgUnit, defaultOrgUnitService);
		final OrgUnitModel newParent = null;

		Assert.assertNotNull("Org Unit should already have a parent to test removing a parent.", actualParent);
		Assert.assertNull("The new parent should be null to perform parent removal.", newParent);

		final OrgUnitParameter updateParameter = new OrgUnitParameter();
		updateParameter.setOrgUnit(usaOrgUnit);
		updateParameter.setParentUnit(null);

		defaultOrgUnitService.updateUnit(updateParameter);

		// retrieve the updated unit to do assertions
		final OrgUnitModel updatedUnit = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		final Optional<OrgUnitModel> updatedParentOptional = defaultOrgUnitService.getParent(updatedUnit);

		Assert.assertFalse("Parent should have been removed, but it's not.", updatedParentOptional.isPresent());
	}


	@Test
	public void shouldUpdateUnitWithoutUpdatingParentForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));

		// retrieve an existing unit
		final OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);
		// retrieve the current parent unit to call updateUnit with exaclty the same parent.
		// The permissions should not block this use case for the manager since the provided parent is the same.
		final OrgUnitModel actualParent = OrgUnitServiceTestUtil.getParentUnit(montreal, defaultOrgUnitService);

		defaultOrgUnitService.updateUnit(OrgUnitServiceTestUtil.createOrgUnitParam(montreal, actualParent, UPDATED_UNIT_UID,
				UPDATED_UNIT_NAME, Boolean.FALSE, UPDATED_UNIT_DESCRIPTION));

		// retrieve the updated unit to do assertions
		final OrgUnitModel updatedUnit = OrgUnitServiceTestUtil.getUnit(UPDATED_UNIT_UID, defaultOrgUnitService);

		Assert.assertFalse("Unit has not been deactivated.", updatedUnit.getActive().booleanValue());
		Assert.assertEquals("Unexpexted uid.", UPDATED_UNIT_UID, updatedUnit.getUid());
		Assert.assertEquals("Unexpexted name.", UPDATED_UNIT_NAME, updatedUnit.getName());
		Assert.assertEquals("Unexpexted description.", UPDATED_UNIT_DESCRIPTION, updatedUnit.getDescription());
		Assert.assertTrue("No parent unit set.", CollectionUtils.isNotEmpty(updatedUnit.getGroups()));
		Assert.assertTrue("Expected parent unit not set.", updatedUnit.getGroups().contains(actualParent));
	}


	@Test
	public void shouldNotUpdateUnitWithUpdatingParentForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));

		// retrieve the an existing unit
		final OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);
		// retrieve the current parent unit
		final OrgUnitModel actualParent = OrgUnitServiceTestUtil.getParentUnit(montreal, defaultOrgUnitService);
		// retrieve the current parent unit
		final OrgUnitModel newParent = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);

		Assert.assertNotEquals("Error in test data. The new parent unit must be different than the actual one.", newParent,
				actualParent);

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit parent unit. User: " + USA_MANAGER);

		defaultOrgUnitService.updateUnit(OrgUnitServiceTestUtil.createOrgUnitParam(montreal, newParent, UPDATED_UNIT_UID,
				UPDATED_UNIT_NAME, Boolean.FALSE, UPDATED_UNIT_DESCRIPTION));
	}

	@Test
	public void shouldNotUpdateUnitForOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit. User: " + USA_EMPLOYEE);

		// retrieve the an existing unit
		final OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);

		defaultOrgUnitService.updateUnit(OrgUnitServiceTestUtil.createOrgUnitParam(montreal, null, UPDATED_UNIT_UID,
				UPDATED_UNIT_NAME, Boolean.FALSE, UPDATED_UNIT_DESCRIPTION));
	}

	@Test
	public void shouldUpdateUnitAndChangeParent()
	{
		// retrieve the an existing unit
		final OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);

		// retrieve the current parent unit
		final OrgUnitModel canada = OrgUnitServiceTestUtil.getParentUnit(montreal, defaultOrgUnitService);
		defaultOrgUnitService.activateUnit(canada);
		defaultOrgUnitService.createUnit(
				OrgUnitServiceTestUtil.createOrgUnitParam(null, canada, QC_UNIT_UID, "Quebec", Boolean.TRUE, "Unit for Quebec"));

		// retrieve the new parent unit
		final OrgUnitModel quebec = OrgUnitServiceTestUtil.getUnit(QC_UNIT_UID, defaultOrgUnitService);

		// update the unit
		final OrgUnitParameter updateParameter = OrgUnitServiceTestUtil.createOrgUnitParam(montreal, canada, UPDATED_UNIT_UID,
				UPDATED_UNIT_NAME, Boolean.FALSE, UPDATED_UNIT_DESCRIPTION);
		updateParameter.setParentUnit(quebec);

		defaultOrgUnitService.updateUnit(updateParameter);

		// retrieve the updated unit to do assertions
		final OrgUnitModel updatedUnit = OrgUnitServiceTestUtil.getUnit(UPDATED_UNIT_UID, defaultOrgUnitService);

		Assert.assertFalse("Unit has not been deactivated.", updatedUnit.getActive().booleanValue());
		Assert.assertEquals("Unexpexted uid.", UPDATED_UNIT_UID, updatedUnit.getUid());
		Assert.assertEquals("Unexpexted name.", UPDATED_UNIT_NAME, updatedUnit.getName());
		Assert.assertEquals("Unexpexted description.", UPDATED_UNIT_DESCRIPTION, updatedUnit.getDescription());
		Assert.assertTrue("No parent unit set.", CollectionUtils.isNotEmpty(updatedUnit.getGroups()));

		Assert.assertTrue("New parent has not been set.", updatedUnit.getGroups().contains(quebec));
		Assert.assertFalse("Old parent has not been removed.", updatedUnit.getGroups().contains(canada));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateUnitOrgUnitNull()
	{
		// update the unit
		final OrgUnitParameter updateParameter = new OrgUnitParameter();
		updateParameter.setOrgUnit(null);

		defaultOrgUnitService.updateUnit(updateParameter);
	}

	@Test
	public void shouldActivateUnit()
	{
		validateActiveUnit();
	}

	protected void validateActiveUnit()
	{
		// Get disabled unit
		OrgUnitModel canada = OrgUnitServiceTestUtil.getUnit(CA_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Unit is not disabled.", canada.getActive().booleanValue());

		defaultOrgUnitService.activateUnit(canada);

		canada = OrgUnitServiceTestUtil.getUnit(CA_UNIT_UID, defaultOrgUnitService);
		Assert.assertTrue("Unit was not enabled.", canada.getActive().booleanValue());
	}

	@Test
	public void shouldActivateUnitForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));

		validateActiveUnit();
	}

	@Test
	public void shouldActivateUnitForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));

		validateActiveUnit();
	}

	@Test
	public void shouldNotActivateUnitForOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit. User: " + USA_EMPLOYEE);

		defaultOrgUnitService.activateUnit(OrgUnitServiceTestUtil.getUnit(CA_UNIT_UID, defaultOrgUnitService));
	}

	@Test
	public void shouldActivateUnitButNotActiveChildUnit()
	{
		// Get disabled unit montreal
		OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Unit is not disabled.", montreal.getActive().booleanValue());

		// Get disabled unit montreal's parent unit canada
		OrgUnitModel canada = OrgUnitServiceTestUtil.getParentUnit(montreal, defaultOrgUnitService);
		Assert.assertFalse("Unit is not disabled.", canada.getActive().booleanValue());

		Assert.assertEquals("montreal's parent unit should be canada", CA_UNIT_UID, canada.getUid());

		defaultOrgUnitService.activateUnit(canada);

		// unit should be active
		canada = OrgUnitServiceTestUtil.getUnit(CA_UNIT_UID, defaultOrgUnitService);
		Assert.assertTrue("Unit was not enabled.", canada.getActive().booleanValue());

		// child unit should not be active
		montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Unit is not disabled.", montreal.getActive().booleanValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotActivateUnitOrgUnitNull()
	{
		defaultOrgUnitService.activateUnit(null);
	}

	@Test
	public void shouldDeactivateUnit()
	{
		validateDeactivateUnit();
	}

	protected void validateDeactivateUnit()
	{
		// Get enabled unit
		OrgUnitModel usa = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		Assert.assertTrue("Unit is not enabled.", usa.getActive().booleanValue());

		defaultOrgUnitService.deactivateUnit(usa);

		usa = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Unit was not disabled.", usa.getActive().booleanValue());
	}

	@Test
	public void shouldDeactivateUnitForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));

		validateDeactivateUnit();
	}

	@Test
	public void shouldDeactivateUnitForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));

		validateDeactivateUnit();
	}

	@Test
	public void shouldDeactivateUnitAlsoDeactiveChildUnit()
	{
		// Get enabled unit usa
		OrgUnitModel usa = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		Assert.assertTrue("Unit was not enabled.", usa.getActive().booleanValue());

		// Get enabled unit usa's parent unit northAmerica
		OrgUnitModel northAmerica = OrgUnitServiceTestUtil.getParentUnit(usa, defaultOrgUnitService);
		Assert.assertEquals("usa's parent unit should be northAmerica", NA_UNIT_UID, northAmerica.getUid());

		defaultOrgUnitService.deactivateUnit(northAmerica);

		// unit should be disabled
		northAmerica = OrgUnitServiceTestUtil.getUnit(NA_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Unit is not disabled.", northAmerica.getActive().booleanValue());

		// child unit should be disabled, too
		usa = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Unit is not disabled.", usa.getActive().booleanValue());
	}


	@Test
	public void shouldNotDeactivateUnitForOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit. User: " + USA_EMPLOYEE);

		defaultOrgUnitService.deactivateUnit(OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService));
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDeactivateUnitOrgUnitNull()
	{
		defaultOrgUnitService.deactivateUnit(null);
	}

	@Test
	public void shouldAddMembers()
	{
		validateAddMembers();
	}

	protected void validateAddMembers()
	{
		EmployeeModel unassignedEmployee = userService.getUserForUID(UNASSIGNED_EMPLOYEE, EmployeeModel.class);
		final OrgUnitModel northAmerica = OrgUnitServiceTestUtil.getUnit(NA_UNIT_UID, defaultOrgUnitService);
		Assert.assertFalse("Members assigned to unexpected unit.", unassignedEmployee.getGroups().contains(northAmerica));
		final Set<EmployeeModel> employees = new HashSet<>();
		employees.add(unassignedEmployee);

		defaultOrgUnitService.addMembers(
				OrgUtils.createOrgUnitMemberParameter(NA_UNIT_UID, employees, EmployeeModel.class, createPageableData(0, 5)));

		unassignedEmployee = userService.getUserForUID(UNASSIGNED_EMPLOYEE, EmployeeModel.class);
		Assert.assertTrue("Employee not assigned to any unit.", CollectionUtils.isNotEmpty(unassignedEmployee.getGroups()));
		Assert.assertTrue("Employee not assigned to expected unit.", unassignedEmployee.getGroups().contains(northAmerica));
	}

	@Test
	public void shouldAddMembersForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));
		validateAddMembers();
	}

	@Test
	public void shouldAddMembersForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));
		validateAddMembers();
	}

	@Test
	public void shouldNotAddMembersForOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit. User: " + USA_EMPLOYEE);

		defaultOrgUnitService.addMembers(OrgUtils.createOrgUnitMemberParameter(CA_UNIT_UID, Collections.emptySet(),
				EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddMembersUidNull()
	{
		defaultOrgUnitService.addMembers(
				OrgUtils.createOrgUnitMemberParameter(null, Collections.emptySet(), EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddMembersCustomersNull()
	{
		defaultOrgUnitService
				.addMembers(OrgUtils.createOrgUnitMemberParameter(NA_UNIT_UID, null, EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test
	public void shouldRemoveMembers()
	{
		validateRemoveMembers();
	}

	protected void validateRemoveMembers()
	{
		EmployeeModel usaEmployee = userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class);
		final OrgUnitModel usa = OrgUnitServiceTestUtil.getUnit(US_UNIT_UID, defaultOrgUnitService);
		Assert.assertTrue("Members assigned to unexpected org unit.", usaEmployee.getGroups().contains(usa));

		final Set<EmployeeModel> employees = new HashSet<>();
		employees.add(usaEmployee);

		defaultOrgUnitService.removeMembers(
				OrgUtils.createOrgUnitMemberParameter(US_UNIT_UID, employees, EmployeeModel.class, createPageableData(0, 5)));

		usaEmployee = userService.getUserForUID(USA_EMPLOYEE, EmployeeModel.class);
		Assert.assertFalse("Employee still assigned to org unit.", usaEmployee.getGroups().contains(usa));
	}

	@Test
	public void shouldRemoveMembersForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));
		validateRemoveMembers();
	}

	@Test
	public void shouldRemoveMembersForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));
		validateRemoveMembers();
	}

	@Test
	public void shouldNotRemoveMembersForOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to edit. User: " + USA_EMPLOYEE);

		defaultOrgUnitService.removeMembers(OrgUtils.createOrgUnitMemberParameter(US_UNIT_UID, Collections.emptySet(),
				EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveMembersUidNull()
	{
		defaultOrgUnitService.removeMembers(
				OrgUtils.createOrgUnitMemberParameter(null, Collections.emptySet(), EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveMembersCustomersNull()
	{
		defaultOrgUnitService.removeMembers(
				OrgUtils.createOrgUnitMemberParameter(NA_UNIT_UID, null, EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test
	public void shouldGetMembers()
	{
		validateGetMembers();
	}

	protected void validateGetMembers()
	{
		final SearchPageData<EmployeeModel> searchPageData = defaultOrgUnitService.getMembers(OrgUtils
				.createOrgUnitMemberParameter(NA_UNIT_UID, Collections.emptySet(), EmployeeModel.class, createPageableData(0, 5)));
		final EmployeeModel northAmericaEmployee = userService.getUserForUID(NA_EMPLOYEE, EmployeeModel.class);

		Assert.assertTrue("No employees returned.", CollectionUtils.isNotEmpty(searchPageData.getResults()));
		Assert.assertEquals("Unexpected numer of employees returned.", 1, searchPageData.getResults().size());
		Assert.assertTrue("Expected employee not part of the unit.", searchPageData.getResults().contains(northAmericaEmployee));
	}

	@Test
	public void shouldGetMembersForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));
		validateGetMembers();
	}

	@Test
	public void shouldGetMembersForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));
		validateGetMembers();
	}

	@Test
	public void shouldNotGetMembersForNonOrgEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(UNASSIGNED_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to view. User: " + UNASSIGNED_EMPLOYEE);

		defaultOrgUnitService.getMembers(OrgUtils.createOrgUnitMemberParameter(NA_UNIT_UID, Collections.emptySet(),
				EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetMembersUidNull()
	{
		defaultOrgUnitService.getMembers(
				OrgUtils.createOrgUnitMemberParameter(null, Collections.emptySet(), EmployeeModel.class, createPageableData(0, 5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetMembersPageableDataNull()
	{
		defaultOrgUnitService
				.getMembers(OrgUtils.createOrgUnitMemberParameter(NA_UNIT_UID, Collections.emptySet(), EmployeeModel.class, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetMembersTypeNull()
	{
		defaultOrgUnitService.getMembers(
				OrgUtils.createOrgUnitMemberParameter(NA_UNIT_UID, Collections.emptySet(), null, createPageableData(0, 5)));
	}

	@Test
	public void shouldGetParent()
	{
		validateGetParent();
	}

	protected void validateGetParent()
	{
		final OrgUnitModel northAmerica = OrgUnitServiceTestUtil.getUnit(NA_UNIT_UID, defaultOrgUnitService);
		final OrgUnitModel parentUnit = OrgUnitServiceTestUtil.getParentUnit(northAmerica, defaultOrgUnitService);

		Assert.assertEquals("Unexpected parent unit returned.", "rootUnit", parentUnit.getUid());
	}

	@Test
	public void shouldGetParentForOrgAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_ADMIN));
		validateGetParent();
	}

	@Test
	public void shouldGetParentForOrgManager()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_MANAGER));
		validateGetParent();
	}

	@Test
	public void shouldNotGetParentForNonOrgEmpoyee()
	{
		userService.setCurrentUser(userService.getUserForUID(UNASSIGNED_EMPLOYEE));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not allowed to view. User: " + UNASSIGNED_EMPLOYEE);
		defaultOrgUnitService.getParent(OrgUnitServiceTestUtil.getUnit(NA_UNIT_UID, defaultOrgUnitService));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetParentOrgUnitNull()
	{
		defaultOrgUnitService.getParent(null);
	}

	@Test
	public void shouldGetRolesForOrgAdmin()
	{
		final EmployeeModel employee = (EmployeeModel) userService.getUserForUID(GLOBAL_ADMIN);
		final Set<PrincipalGroupModel> roleList = defaultOrgUnitService.getRolesForEmployee(employee);

		Assert.assertNotNull("Should have been assigned to a role", roleList);
		Assert.assertEquals(1, roleList.size());
		Assert.assertTrue("Should have been assigned to Orgnization Admin role", containsRoleUid(ORG_ADMIN_UID, roleList));
	}

	@Test
	public void shouldGetRolesForOrgManager()
	{
		final EmployeeModel employee = (EmployeeModel) userService.getUserForUID(USA_MANAGER);
		final Set<PrincipalGroupModel> roleList = defaultOrgUnitService.getRolesForEmployee(employee);

		Assert.assertNotNull("Should have been assigned to a role", roleList);
		Assert.assertEquals(1, roleList.size());
		Assert.assertTrue("Should have been assigned to Orgnization Manager role", containsRoleUid(ORG_MANAGER_UID, roleList));
	}

	@Test
	public void shouldGetRolesForOrgEmployee()
	{
		final EmployeeModel employee = (EmployeeModel) userService.getUserForUID(CANADA_EMPLOYEE);
		final Set<PrincipalGroupModel> roleList = defaultOrgUnitService.getRolesForEmployee(employee);

		Assert.assertNotNull("Should have been assigned to a role", roleList);
		Assert.assertEquals(1, roleList.size());
		Assert.assertTrue("Should have been assigned to Orgnization Employee role", containsRoleUid(ORG_EMPLOYEE_UID, roleList));
	}

	@Test
	public void shouldGetRolesForOrgAdminMultiRoles()
	{
		final EmployeeModel employee = (EmployeeModel) userService.getUserForUID(CANADA_ADMIN);
		final Set<PrincipalGroupModel> roleList = defaultOrgUnitService.getRolesForEmployee(employee);

		Assert.assertNotNull("Should have been assigned to a role", roleList);
		Assert.assertEquals(2, roleList.size());
		Assert.assertTrue("Should have found Orgnization Admin role", containsRoleUid(ORG_ADMIN_UID, roleList));
		Assert.assertTrue("Should have found Orgnization Manager role", containsRoleUid(ORG_MANAGER_UID, roleList));
	}

	/**
	 * @param searchRoleUid
	 *           A role Uid to search for
	 * @param roleSet
	 *           Set of roles to be searched against
	 * @return true if given searchRoleUid is found from the roleSet
	 */
	protected boolean containsRoleUid(final String searchRoleUid, final Set<PrincipalGroupModel> roleSet)
	{
		for (final PrincipalGroupModel role : roleSet)
		{
			if (StringUtils.equals(searchRoleUid, role.getUid()))
			{
				return true;
			}
		}

		return false;
	}

	protected PageableData createPageableData(final int currentPage, final int pageSize)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		return pageableData;
	}
}
