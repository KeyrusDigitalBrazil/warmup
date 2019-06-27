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
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultOrgUnitServiceSkipPathGenerationIntegrationTest extends BaseCommerceBaseTest
{
	private static final String ROOT_UNIT_UID = "rootUnit";
	private static final String CA_UNIT_UID = "canada";
	private static final String MT_UNIT_UID = "montreal";
	private static final String EU_UNIT_UID = "europe";
	private static final String EU_UNIT_NAME = "Europe";
	private static final String EU_UNIT_DESCRIPTION = "Unit for Europe";
	private static final String NA_UNIT_UID = "northAmerica";
	private static final String UPDATED_UNIT_UID = "updatedUnit";
	private static final String UPDATED_UNIT_NAME = "Updated Unit";
	private static final String UPDATED_UNIT_DESCRIPTION = "Updated Unit Description";

	private static final String ROOT_UNIT_PATH = "/" + ROOT_UNIT_UID;
	private static final String NA_UNIT_PATH = ROOT_UNIT_PATH + "/" + NA_UNIT_UID;
	private static final String CA_UNIT_PATH = NA_UNIT_PATH + "/" + CA_UNIT_UID;
	private static final String MTL_UNIT_UID = "montreal";
	private static final String MTL_UNIT_PATH = CA_UNIT_PATH + "/" + MTL_UNIT_UID;

	@Resource(name = "defaultOrgUnitService")
	private DefaultOrgUnitService defaultOrgUnitService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private boolean isUpdatePathEnabledBackup;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commerceservices/test/orgUnitTestData.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);

		// Temporarily change properties
		isUpdatePathEnabledBackup = Config.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);

		Config.setParameter(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, "false");
	}

	@After
	public void cleanUp() throws Exception
	{
		Config.setParameter(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED,
				String.valueOf(isUpdatePathEnabledBackup));
	}

	@Test
	public void shouldNotUpdatePathWhenCreateUnit()
	{
		// retrieve the desired parent unit
		final OrgUnitModel rootUnit = OrgUnitServiceTestUtil.getUnit(ROOT_UNIT_UID, defaultOrgUnitService);

		defaultOrgUnitService.createUnit(OrgUnitServiceTestUtil.createOrgUnitParam(null, rootUnit, EU_UNIT_UID, EU_UNIT_NAME,
				Boolean.TRUE, EU_UNIT_DESCRIPTION));

		final OrgUnitModel europe = OrgUnitServiceTestUtil.getUnit(EU_UNIT_UID, defaultOrgUnitService);
		Assert.assertEquals("Unexpexted uid..", EU_UNIT_UID, europe.getUid());
		Assert.assertNull("Unexpected path value.", europe.getPath());
	}

	@Test
	public void shouldNotUpdatePathWhenUpdateUnit()
	{
		// retrieve the an existing unit
		final OrgUnitModel montreal = OrgUnitServiceTestUtil.getUnit(MT_UNIT_UID, defaultOrgUnitService);
		// retrieve the current parent unit
		final OrgUnitModel canada = OrgUnitServiceTestUtil.getParentUnit(montreal, defaultOrgUnitService);
		Assert.assertNull("Unexpexted path.", montreal.getPath());

		defaultOrgUnitService.updateUnit(OrgUnitServiceTestUtil.createOrgUnitParam(montreal, canada, UPDATED_UNIT_UID,
				UPDATED_UNIT_NAME, Boolean.FALSE, UPDATED_UNIT_DESCRIPTION));

		// retrieve the updated unit to do assertions
		final OrgUnitModel updatedUnit = OrgUnitServiceTestUtil.getUnit(UPDATED_UNIT_UID, defaultOrgUnitService);

		Assert.assertFalse("Unit has not been deactivated.", updatedUnit.getActive().booleanValue());
		Assert.assertEquals("Unexpexted uid.", UPDATED_UNIT_UID, updatedUnit.getUid());
		Assert.assertNull("Unexpexted path.", montreal.getPath());
	}
}
