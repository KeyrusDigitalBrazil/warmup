/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommerceConfigAddToCartStrategyTest
{

	private CommerceConfigAddToCartStrategy classUndertest;
	private BaseStoreModel baseStoreModel;

	@Mock
	private BaseStoreService baseStoreService;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUndertest = new CommerceConfigAddToCartStrategy();
		classUndertest.setBaseStoreService(baseStoreService);
		baseStoreModel = new BaseStoreModel();
		baseStoreModel.setSAPConfiguration(new SAPConfigurationModel());
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
	}

	@Test
	public void testIsSyncOrdermgmtEnabledTrue()
	{
		baseStoreModel.getSAPConfiguration().setSapordermgmt_enabled(true);
		assertTrue(classUndertest.isSyncOrdermgmtEnabled());
	}

	@Test
	public void testIsSyncOrdermgmtEnabledFalse()
	{
		baseStoreModel.getSAPConfiguration().setSapordermgmt_enabled(false);
		assertFalse(classUndertest.isSyncOrdermgmtEnabled());
	}

	@Test
	public void testIsSyncOrdermgmtEnabledFalseNoConfig()
	{
		baseStoreModel.setSAPConfiguration(null);
		assertFalse(classUndertest.isSyncOrdermgmtEnabled());
	}

	@Test
	public void testGetAllowedCartAdjustmentForProductSOMActive()
	{
		baseStoreModel.getSAPConfiguration().setSapordermgmt_enabled(true);
		assertEquals(3, classUndertest.getAllowedCartAdjustmentForProduct(null, null, 3, null));
	}

	@Test(expected = NullPointerException.class)
	public void testGetAllowedCartAdjustmentForProductSOMInActive()
	{
		classUndertest.getAllowedCartAdjustmentForProduct(null, null, 3, null);
	}





}
