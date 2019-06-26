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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.marketplaceservices.strategies.VendorActivationStrategy;
import de.hybris.platform.marketplaceservices.strategies.VendorDeactivationStrategy;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.VendorModel;


/**
 *
 */
@UnitTest
public class DefaultVendorServiceTest
{
	private static final String VENDOR_CODE = "default";

	private DefaultVendorService vendorService;

	private VendorModel vendor;

	@Mock
	private VendorDao vendorDao;

	@Mock
	private VendorActivationStrategy vendorActivationStrategy;

	@Mock
	private VendorDeactivationStrategy vendorDeactivationStrategy;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		vendorService = new DefaultVendorService();
		vendorService.setVendorDao(vendorDao);
		vendorService.setVendorActivationStrategy(vendorActivationStrategy);
		vendorService.setVendorDeactivationStrategy(vendorDeactivationStrategy);

		vendor = new VendorModel();
		vendor.setCode(VENDOR_CODE);
		vendor.setActive(true);
	}

	@Test
	public void testGetVendorByCode()
	{
		Mockito.doReturn(Optional.of(vendor)).when(vendorDao).findVendorByCode(Mockito.any());
		assertEquals(VENDOR_CODE, vendor.getCode());
	}

	@Test
	public void testActivate()
	{
		Mockito.doNothing().when(vendorService.getVendorActivationStrategy()).activateVendor(Mockito.any());
		vendorService.activateVendor(vendor);
		verify(vendorService.getVendorActivationStrategy(), times(1)).activateVendor(vendor);
	}

	@Test
	public void testDeactivate()
	{
		Mockito.doNothing().when(vendorService.getVendorDeactivationStrategy()).deactivateVendor(Mockito.any());
		vendorService.deactivateVendor(vendor);
		verify(vendorService.getVendorDeactivationStrategy(), times(1)).deactivateVendor(vendor);
	}

}
