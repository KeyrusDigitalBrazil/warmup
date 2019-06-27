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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class SearchResultVendorProductPopulatorTest
{
	private SearchResultVendorProductPopulator searchResultVendorProductPopulator;
	private SearchResultValueData searchResultValueData;
	private ProductData result;

	private static final String VENDOR_CODE = "vendor1";
	@Mock
	private VendorService vendorService;
	@Mock
	private Converter<VendorModel, VendorData> vendorConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		searchResultVendorProductPopulator = new SearchResultVendorProductPopulator();
		searchResultVendorProductPopulator.setVendorConverter(vendorConverter);
		searchResultVendorProductPopulator.setVendorService(vendorService);
		searchResultValueData = new SearchResultValueData();
		result = new ProductData();
	}

	@Test
	public void testPopulateVendorProduct()
	{
		VendorModel vendorModel = new VendorModel();
		vendorModel.setCode(VENDOR_CODE);

		Mockito.doReturn(Optional.of(vendorModel)).when(vendorService).getVendorByCode(VENDOR_CODE);

		Mockito.doAnswer(new Answer<VendorData>()
		{
			@Override
			public VendorData answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				VendorData vendorData = (VendorData) args[1];
				vendorData.setCode(VENDOR_CODE);
				return null;
			}
		}).when(vendorConverter).convert(Mockito.any(VendorModel.class), Mockito.any(VendorData.class));
		populateData();

		searchResultVendorProductPopulator.populate(searchResultValueData, result);

		Assert.assertEquals(result.getVendor().getCode(), VENDOR_CODE);
	}

	private void populateData()
	{
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "variant1");
		map.put("name", "name variant1");
		map.put("itemtype", "variantModel");
		map.put("vendor", Arrays.asList(VENDOR_CODE));
		searchResultValueData.setValues(map);

	}

}
