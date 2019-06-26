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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration tests for {@link DefaultB2BUnitFacade}.
 */
@IntegrationTest
public class DefaultB2BUnitFacadeSkipPathGenerationIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	private static final String UNEXPECTED_VALUE_FOR_NAME_MSG = "Unexpected value for name.";
	private static final String UNIT_IS_NOT_ACTIVE_MSG = "Unit is not active.";
	private static final String UNIT_IS_ACTIVE_MSG = "Unit is active.";
	private static final String UNIT_ADRESSES_ARE_NULL_MSG = "Unit adresses are null.";
	private static final String UNIT_ADRESSES_ARE_NOT_NULL_MSG = "Unit adresses are not null.";
	private static final String UNIT_IS_NULL_MSG = "Unit is null.";

	@Resource
	private DefaultB2BUnitFacade b2bUnitFacade;

	@Resource
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	private UserService userService;

	private boolean isUpdatePathEnabledBackup;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		// Temporarily change properties
		isUpdatePathEnabledBackup = Config.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);

		Config.setParameter(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, "false");
	}

	@After
	public void cleanUp() throws Exception
	{
		Config.setParameter(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, String.valueOf(isUpdatePathEnabledBackup));
	}

	@Test
	public void testUpdateOrCreateBusinessUnit()
	{
		final B2BUnitData parentUnit = b2bUnitFacade.getUnitForUid("DC");

		// create a new business unit
		final B2BUnitData unit = new B2BUnitData();
		final String newUnitId = "DC New Unit";
		unit.setUid(newUnitId);
		unit.setName("New Unit");
		unit.setUnit(parentUnit);
		b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit);

		// assert unit has been created
		final B2BUnitData newUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertNotNull("New unit has not been created.", newUnit);
		assertEquals("Unexpected unit name.", "New Unit", newUnit.getName());
		Assert.assertNull("Unexpected path value.", getUnitForUid(newUnitId).getPath());
	}

	protected OrgUnitModel getUnitForUid(final String uid)
	{
		final OrgUnitModel unit = userService.getUserGroupForUID(uid, OrgUnitModel.class);
		Assert.assertNotNull(String.format("Unit [%s] does not exist.", uid), unit);
		return unit;
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bcommercefacades/test/testOrganizations.csv";
	}

}
