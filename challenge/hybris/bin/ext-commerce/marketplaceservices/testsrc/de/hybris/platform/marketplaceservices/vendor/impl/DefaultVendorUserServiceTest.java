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
package de.hybris.platform.marketplaceservices.vendor.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultVendorUserServiceTest
{

	private DefaultVendorUserService vendorUserService;

	@Mock
	private ModelService modelService;

	private VendorUserModel vendorUser;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		vendorUserService = new DefaultVendorUserService();
		vendorUserService.setModelService(modelService);
		vendorUser = new VendorUserModel();
		Mockito.doNothing().when(vendorUserService.getModelService()).save(vendorUser);
	}

	@Test
	public void testActivate()
	{
		vendorUserService.activateUser(vendorUser);
		assertFalse(vendorUser.isLoginDisabled());
	}

	@Test
	public void testDeactivate()
	{
		vendorUserService.deactivateUser(vendorUser);
		assertTrue(vendorUser.isLoginDisabled());
	}
}
