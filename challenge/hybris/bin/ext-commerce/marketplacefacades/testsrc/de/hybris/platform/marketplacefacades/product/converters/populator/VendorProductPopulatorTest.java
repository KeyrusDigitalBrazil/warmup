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
package de.hybris.platform.marketplacefacades.product.converters.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class VendorProductPopulatorTest
{

	@Mock
	private VendorService vendorService;

	@Mock
	private Converter<VendorModel, VendorData> vendorConverter;

	private VendorProductPopulator productVendorPopulator;

	private static final String PRODUCT_CODE = "00000010";
	private static final String VENDOR_CODE = "vendor1";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		productVendorPopulator = new VendorProductPopulator();
		productVendorPopulator.setVendorConverter(vendorConverter);
		productVendorPopulator.setVendorService(vendorService);
	}

	@Test
	public void testPoulateProductToAddVendorData()
	{
		final ProductModel productModel = new ProductModel();
		productModel.setCode(PRODUCT_CODE);
		productModel.setSaleable(Boolean.TRUE);
		final VendorModel vendorModel = new VendorModel();
		vendorModel.setCode(VENDOR_CODE);

		Mockito.doReturn(Optional.of(vendorModel)).when(vendorService).getVendorByProduct(productModel);

		Mockito.doAnswer(new Answer<VendorData>()
		{
			@Override
			public VendorData answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				final VendorData vendorData = (VendorData) args[1];
				vendorData.setCode(VENDOR_CODE);
				return null;
			}
		}).when(vendorConverter).convert(Mockito.any(VendorModel.class), Mockito.any(VendorData.class));


		final ProductData productData = new ProductData();

		productVendorPopulator.populate(productModel, productData);

		assertEquals(VENDOR_CODE, productData.getVendor().getCode());
		assertEquals(Boolean.TRUE, productData.getSaleable());
	}

}
