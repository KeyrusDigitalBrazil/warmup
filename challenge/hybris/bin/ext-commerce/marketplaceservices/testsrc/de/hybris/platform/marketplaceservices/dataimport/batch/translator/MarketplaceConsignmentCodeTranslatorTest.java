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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.DescriptorParams;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;


@UnitTest
public class MarketplaceConsignmentCodeTranslatorTest
{
	private static final String VENDOR_CODE = "Canon";
	private static final String CONSIGNMENT_CODE = "a0010011";

	private MarketplaceConsignmentCodeTranslator translator;
	private TestDescriptorParams params;
	private VendorModel vendor;
	private Item item;

	@Mock
	private VendorService vendorService;


	@Mock
	private StandardColumnDescriptor descriptor;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new MarketplaceConsignmentCodeTranslator();
		translator.init(descriptor);
		translator.setVendorService(vendorService);

		vendor = new VendorModel();
		vendor.setCode(VENDOR_CODE);

		params = new TestDescriptorParams(ImmutableMap.of("vendor", VENDOR_CODE));

		Mockito.when(descriptor.getDescriptorData()).thenReturn(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoConsignmentCode()
	{
		translator.importValue("", item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidVendor()
	{
		Mockito.when(vendorService.getVendorForConsignmentCode(CONSIGNMENT_CODE)).thenReturn(Optional.empty());
		translator.importValue(CONSIGNMENT_CODE, item);
	}

	@Test
	public void testValidVendor()
	{
		Mockito.when(vendorService.getVendorForConsignmentCode(CONSIGNMENT_CODE)).thenReturn(Optional.of(vendor));
		assertEquals(translator.importValue(CONSIGNMENT_CODE, item), CONSIGNMENT_CODE);
	}

	private static final class TestDescriptorParams extends DescriptorParams
	{
		public TestDescriptorParams(Map<String, String> m)
		{
			this.addAllModifier(m);
		}
	}
}
