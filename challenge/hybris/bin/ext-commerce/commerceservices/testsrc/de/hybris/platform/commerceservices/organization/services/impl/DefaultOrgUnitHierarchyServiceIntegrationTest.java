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
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultOrgUnitHierarchyService}.
 */
@IntegrationTest
public class DefaultOrgUnitHierarchyServiceIntegrationTest extends BaseCommerceBaseTest
{
	private static final String ROOT_UNIT_UID = "rootUnit";
	private static final String ROOT_UNIT_PATH = "/" + ROOT_UNIT_UID;
	private static final String NA_UNIT_UID = "northAmerica";
	private static final String NA_UNIT_PATH = ROOT_UNIT_PATH + "/" + NA_UNIT_UID;
	private static final String US_UNIT_UID = "usa";
	private static final String US_UNIT_PATH = NA_UNIT_PATH + "/" + US_UNIT_UID;
	private static final String CA_UNIT_UID = "canada";
	private static final String CA_UNIT_PATH = NA_UNIT_PATH + "/" + CA_UNIT_UID;
	private static final String CLF_UNIT_UID = "california";
	private static final String CLF_UNIT_PATH = US_UNIT_PATH + "/" + CLF_UNIT_UID;
	private static final String MTL_UNIT_UID = "montreal";
	private static final String MTL_UNIT_PATH = CA_UNIT_PATH + "/" + MTL_UNIT_UID;
	private static final String AF_UNIT_UID = "africa";
	private static final String AF_UNIT_PATH = ROOT_UNIT_PATH + "/" + AF_UNIT_UID;
	private static final String NI_UNIT_UID = "nigeria";
	private static final String NI_UNIT_PATH = AF_UNIT_PATH + "/" + NI_UNIT_UID;

	@Resource(name = "defaultOrgUnitHierarchyService")
	private DefaultOrgUnitHierarchyService defaultOrgUnitHierarchyService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "userService")
	private UserService userService;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commerceservices/test/orgUnitTestData.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		userService.setCurrentUser(userService.getAdminUser());
	}

	@Test
	public void shouldGenerateUnitPaths()
	{
		defaultOrgUnitHierarchyService.generateUnitPaths(OrgUnitModel.class);

		// Assert that paths have been generated correctly
		final OrgUnitModel rootUnit = getUnitForUid(ROOT_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", ROOT_UNIT_PATH, rootUnit.getPath());

		final OrgUnitModel naUnit = getUnitForUid(NA_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", NA_UNIT_PATH, naUnit.getPath());

		final OrgUnitModel usUnit = getUnitForUid(US_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", US_UNIT_PATH, usUnit.getPath());

		final OrgUnitModel caUnit = getUnitForUid(CA_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", CA_UNIT_PATH, caUnit.getPath());

		final OrgUnitModel clfUnit = getUnitForUid(CLF_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", CLF_UNIT_PATH, clfUnit.getPath());

		final OrgUnitModel mtlUnit = getUnitForUid(MTL_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", MTL_UNIT_PATH, mtlUnit.getPath());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGenerateUnitPathsTypeNull()
	{
		defaultOrgUnitHierarchyService.generateUnitPaths(null);
	}

	@Test
	public void shouldSaveChangesUpdateUnitPath()
	{
		defaultOrgUnitHierarchyService.generateUnitPaths(OrgUnitModel.class);

		// Assert that paths have been generated correctly
		final OrgUnitModel rootUnit = getUnitForUid(ROOT_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", ROOT_UNIT_PATH, rootUnit.getPath());

		final OrgUnitModel naUnit = getUnitForUid(NA_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", NA_UNIT_PATH, naUnit.getPath());

		OrgUnitModel usUnit = getUnitForUid(US_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", US_UNIT_PATH, usUnit.getPath());

		OrgUnitModel clfUnit = getUnitForUid(CLF_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", CLF_UNIT_PATH, clfUnit.getPath());

		// Change the parent unit of "usa" from "na" to "root"
		final Set<PrincipalGroupModel> usGroups = new HashSet<>(usUnit.getGroups());
		usGroups.remove(naUnit);
		usGroups.add(rootUnit);
		usUnit.setGroups(usGroups);

		// Update the path for "usa"
		defaultOrgUnitHierarchyService.saveChangesAndUpdateUnitPath(usUnit);

		// Assert that paths "usa" and its child "california" have been updated correctly
		usUnit = getUnitForUid(US_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", "/" + ROOT_UNIT_UID + "/" + US_UNIT_UID, usUnit.getPath());

		clfUnit = getUnitForUid(CLF_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", "/" + ROOT_UNIT_UID + "/" + US_UNIT_UID + "/" + CLF_UNIT_UID,
				clfUnit.getPath());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSaveChangesAndUpdateUnitPathUnitNull()
	{
		defaultOrgUnitHierarchyService.saveChangesAndUpdateUnitPath(null);
	}

	protected OrgUnitModel getUnitForUid(final String uid)
	{
		final OrgUnitModel unit = userService.getUserGroupForUID(uid, OrgUnitModel.class);
		Assert.assertNotNull(String.format("Unit [%s] does not exist.", uid), unit);
		return unit;
	}

	@Test
	public void shouldSkipBranchPathGenerationIfMultipleParentsAreDetected() throws ImpExException
	{
		// /rootUnit/northAmerica/usa/northAmerica is skipped
		importCsv("/commerceservices/test/orgUnitTestData_addParent.impex", "UTF-8");
		defaultOrgUnitHierarchyService.generateUnitPaths(OrgUnitModel.class);

		// generated paths
		final OrgUnitModel rootUnit = getUnitForUid(ROOT_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", ROOT_UNIT_PATH, rootUnit.getPath());

		final OrgUnitModel africaUnit = getUnitForUid(AF_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", AF_UNIT_PATH, africaUnit.getPath());

		final OrgUnitModel nigeriaUnit = getUnitForUid(NI_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", NI_UNIT_PATH, nigeriaUnit.getPath());

		// branch northAmerica is skipped due to northAmerica has more then one parent: rootUnit and usa
		final OrgUnitModel naUnit = getUnitForUid(NA_UNIT_UID);
		Assert.assertNull("Unexpected path value.", naUnit.getPath());

		final OrgUnitModel usUnit = getUnitForUid(US_UNIT_UID);
		Assert.assertNull("Unexpected path value.", usUnit.getPath());

		final OrgUnitModel caUnit = getUnitForUid(CA_UNIT_UID);
		Assert.assertNull("Unexpected path value.", caUnit.getPath());

		final OrgUnitModel clfUnit = getUnitForUid(CLF_UNIT_UID);
		Assert.assertNull("Unexpected path value.", clfUnit.getPath());

		final OrgUnitModel mtlUnit = getUnitForUid(MTL_UNIT_UID);
		Assert.assertNull("Unexpected path value.", mtlUnit.getPath());
	}

	@Test(expected = OrgUnitHierarchyException.class)
	public void shouldThrowExceptionIfCycleIsDetectedWhenUnitPathIsUpdated()
	{
		defaultOrgUnitHierarchyService.generateUnitPaths(OrgUnitModel.class);

		// Assert that paths have been generated correctly
		final OrgUnitModel rootUnit = getUnitForUid(ROOT_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", ROOT_UNIT_PATH, rootUnit.getPath());

		final OrgUnitModel naUnit = getUnitForUid(NA_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", NA_UNIT_PATH, naUnit.getPath());

		final OrgUnitModel usUnit = getUnitForUid(US_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", US_UNIT_PATH, usUnit.getPath());

		final OrgUnitModel clfUnit = getUnitForUid(CLF_UNIT_UID);
		Assert.assertEquals("Unexpected path value.", CLF_UNIT_PATH, clfUnit.getPath());

		// Change the parent unit of "usa" from "na" to "root"
		final Set<PrincipalGroupModel> rootGroups = new HashSet<>(rootUnit.getGroups());
		rootGroups.add(usUnit);
		rootUnit.setGroups(rootGroups);

		// Update the path for "usa"
		defaultOrgUnitHierarchyService.saveChangesAndUpdateUnitPath(rootUnit);
	}

}
