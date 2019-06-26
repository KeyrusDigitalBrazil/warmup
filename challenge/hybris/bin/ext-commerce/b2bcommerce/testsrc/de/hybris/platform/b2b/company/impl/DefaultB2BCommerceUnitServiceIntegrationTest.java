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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultB2BCommerceUnitServiceIntegrationTest extends ServicelayerTest
{
	private static final String CUSTOM_RETAIL = "Test Custom Retail";

	@Resource
	private DefaultB2BCommerceUnitService defaultB2BCommerceUnitService;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private BaseDao baseDao;

	@Rule
	public final ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setup() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultUsers();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getUserByLogin("customer.k@rustic-hw.com"));
	}

	@Test
	public void shouldGetOrganization()
	{
		final Collection<? extends B2BUnitModel> organizations = defaultB2BCommerceUnitService.getOrganization();
		assertNotNull(organizations);
		assertEquals(1, organizations.size());
		assertEquals("Test Rustic", organizations.iterator().next().getUid());
	}

	@Test
	public void shouldGetBranch()
	{
		final Collection<? extends B2BUnitModel> branch = defaultB2BCommerceUnitService.getBranch();
		assertNotNull(branch);
		assertEquals(1, branch.size());
		assertEquals(CUSTOM_RETAIL, branch.iterator().next().getUid());
	}

	@Test
	public void shouldGetRootUnit()
	{
		final B2BUnitModel rootUnit = defaultB2BCommerceUnitService.getRootUnit();
		assertNotNull(rootUnit);
		assertEquals("Test Rustic", rootUnit.getUid());
	}

	@Test
	public void shouldGetParentUnit()
	{
		final B2BUnitModel parentUnit = defaultB2BCommerceUnitService.getParentUnit();
		assertNotNull(parentUnit);
		assertEquals(CUSTOM_RETAIL, parentUnit.getUid());
	}

	@Test
	public void shouldGetAllUnitsOfOrganization()
	{
		final Collection<? extends B2BUnitModel> allUnits = defaultB2BCommerceUnitService.getAllUnitsOfOrganization();
		assertNotNull(allUnits);
		assertEquals(1, allUnits.size());
		assertEquals(CUSTOM_RETAIL, allUnits.iterator().next().getUid());
	}

	@Test
	public void shouldGetAllowedParentUnits()
	{
		final Collection<? extends B2BUnitModel> allUnits = defaultB2BCommerceUnitService
				.getAllowedParentUnits(baseDao.findUniqueByAttribute(B2BUnitModel.UID, CUSTOM_RETAIL, B2BUnitModel.class));
		assertNotNull(allUnits);
		assertEquals(1, allUnits.size());
		assertEquals("Test Rustic Retail", allUnits.iterator().next().getUid());
	}

	@Test
	public void shouldNotGetAllowedParentUnitsWithNullAsUnit()
	{
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unit can not be null!");
		defaultB2BCommerceUnitService.getAllowedParentUnits(null);
	}

	@Test
	public void shouldDisableAndEnableUnit()
	{
		final B2BUnitModel unit = baseDao.findUniqueByAttribute(B2BUnitModel.UID, CUSTOM_RETAIL, B2BUnitModel.class);
		assertNotNull(unit);
		assertTrue(unit.getActive());

		// disable
		defaultB2BCommerceUnitService.disableUnit(CUSTOM_RETAIL);
		assertFalse(unit.getActive());

		// enable
		defaultB2BCommerceUnitService.enableUnit(CUSTOM_RETAIL);
		assertTrue(unit.getActive());
	}
}
