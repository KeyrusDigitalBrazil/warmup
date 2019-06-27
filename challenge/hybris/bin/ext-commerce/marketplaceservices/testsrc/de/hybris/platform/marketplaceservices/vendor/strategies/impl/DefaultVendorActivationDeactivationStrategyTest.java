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
package de.hybris.platform.marketplaceservices.vendor.strategies.impl;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.marketplaceservices.strategies.VendorActivationStrategy;
import de.hybris.platform.marketplaceservices.strategies.VendorDeactivationStrategy;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 *
 */
@IntegrationTest
public class DefaultVendorActivationDeactivationStrategyTest extends ServicelayerTransactionalTest
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "vendorActivationStrategy")
	private VendorActivationStrategy vendorActivationStrategy;

	@Resource(name = "vendorDeactivationStrategy")
	private VendorDeactivationStrategy vendorDeactivationStrategy;

	private VendorModel vendor;

	private VendorUserModel vendorUser;

	@Before
	public void prepare()
	{
		vendorUser = new VendorUserModel();
		vendorUser.setUid("testvendoruser");

		vendor = new VendorModel();
		vendor.setCode("Test");
		vendor.setVendorUsers(Collections.singletonList(vendorUser));
		vendorUser.setVendor(vendor);
		modelService.save(vendor);
	}

	@Test
	public void testActivate()
	{
		vendorActivationStrategy.activateVendor(vendor);
		Assert.assertTrue(vendor.isActive());
		Assert.assertFalse(vendorUser.isLoginDisabled());
	}

	@Test
	public void testDeactivate()
	{
		vendorDeactivationStrategy.deactivateVendor(vendor);
		Assert.assertFalse(vendor.isActive());
		Assert.assertTrue(vendorUser.isLoginDisabled());
	}
}
