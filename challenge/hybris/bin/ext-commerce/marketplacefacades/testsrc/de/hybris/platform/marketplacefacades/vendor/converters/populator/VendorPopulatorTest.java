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
package de.hybris.platform.marketplacefacades.vendor.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.VendorRatingData;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.url.impl.VendorUrlResolver;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;


@UnitTest
public class VendorPopulatorTest
{
	private static final double DELTA = 0.0001;
	private static final String VENDOR_CODE = "sweex";
	private static final String VENDOR_NAME = "Sweex";
	private static final String VENDOR_URL = "/v/url";
	private static final String LOCALE = "en";

	@Mock
	private LocaleProvider localeProvider;

	@Mock
	private VendorUrlResolver vendorUrlResolver;

	private VendorPopulator vendorPopulator;

	private VendorModel vendorModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		vendorPopulator = new VendorPopulator();
		vendorPopulator.setVendorUrlResolver(vendorUrlResolver);
		vendorModel = new VendorModel();
		vendorModel.setCategories(new ArrayList<CategoryModel>());
		getContext(vendorModel).setLocaleProvider(localeProvider);
		given(localeProvider.getCurrentDataLocale()).willReturn(new Locale(LOCALE));
		given(vendorUrlResolver.resolve(Mockito.any())).willReturn(VENDOR_URL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateVendorDataWithSourceNull()
	{
		vendorPopulator.populate(null, new VendorData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateVendorDataWithTargetNull()
	{
		vendorPopulator.populate(vendorModel, null);
	}

	@Test
	public void testPopulateVendorDataWithoutLogo()
	{
		vendorModel.setCode(VENDOR_CODE);
		vendorModel.setName(VENDOR_NAME);

		final VendorData vendorData = new VendorData();
		vendorPopulator.populate(vendorModel, vendorData);
		Assert.assertEquals(VENDOR_CODE, vendorData.getCode());
		Assert.assertEquals(VENDOR_NAME, vendorData.getName());
		Assert.assertEquals(VENDOR_URL, vendorData.getUrl());
	}

	@Test
	public void testPopulateWithoutRating()
	{
		final VendorData vendorData = new VendorData();
		vendorPopulator.populate(vendorModel, vendorData);
		final VendorRatingData rating = vendorData.getRating();
		assertEquals(0.0, rating.getSatisfaction(), DELTA);
		assertEquals(0.0, rating.getDelivery(), DELTA);
		assertEquals(0.0, rating.getCommunication(), DELTA);
		assertEquals(0.0, rating.getAverage(), DELTA);
		assertEquals(0, rating.getReviewCount().longValue());
	}

	@Test
	public void testPopulateWithRating()
	{
		vendorModel.setSatisfactionRating(1.0);
		vendorModel.setCommunicationRating(2.0);
		vendorModel.setDeliveryRating(3.0);
		vendorModel.setAverageRating(2.0);
		vendorModel.setReviewCount(2L);

		final VendorData vendorData = new VendorData();
		vendorPopulator.populate(vendorModel, vendorData);
		final VendorRatingData rating = vendorData.getRating();
		assertEquals(1.0, rating.getSatisfaction(), DELTA);
		assertEquals(3.0, rating.getDelivery(), DELTA);
		assertEquals(2.0, rating.getCommunication(), DELTA);
		assertEquals(2.0, rating.getAverage(), DELTA);
		assertEquals(2, rating.getReviewCount().longValue());
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}
}
