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
package de.hybris.platform.selectivecartfacades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.selectivecartservices.enums.CartSourceType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Junit test suite for {@link Wishlish2EntryModelToOrderEntryPopulator}
 */
@UnitTest
public class Wishlish2EntryModelToOrderEntryPopulatorTest
{
	Wishlish2EntryModelToOrderEntryPopulator wishlish2EntryModelToOrderEntryPopulator = null;
	private static final int NUMS = 10;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	@Mock
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		wishlish2EntryModelToOrderEntryPopulator = new Wishlish2EntryModelToOrderEntryPopulator();
		wishlish2EntryModelToOrderEntryPopulator.setProductPriceAndStockConverter(productPriceAndStockConverter);
	}

	@Test
	public void testPopulateWithSuccessfulResult()
	{
		wishlish2EntryModelToOrderEntryPopulator.setProductConverter(productConverter);
		final ProductData productData = new ProductData();

		productData.setCode("000001");
		productData.setName("testProduct");


		final Wishlist2EntryModel wishlist2EntryModel = new Wishlist2EntryModel();
		final Date date = new Date();
		wishlist2EntryModel.setAddedDate(date);
		wishlist2EntryModel.setQuantity(NUMS);
		wishlist2EntryModel.setAddToCartTime(date);

		final OrderEntryData orderEntryData = new OrderEntryData();

		given(productConverter.convert(Mockito.any())).willReturn(productData);
		given(productPriceAndStockConverter.convert(Mockito.any(), Mockito.any())).willReturn(productData);

		wishlish2EntryModelToOrderEntryPopulator.populate(wishlist2EntryModel, orderEntryData);

		Assert.assertEquals(productData.getCode(), orderEntryData.getProduct().getCode());
		Assert.assertEquals(NUMS, orderEntryData.getQuantity().intValue());
		Assert.assertEquals(date, orderEntryData.getAddToCartTime());
		Assert.assertNull(orderEntryData.getDeliveryPointOfService());
		Assert.assertEquals(CartSourceType.WISHLIST, orderEntryData.getCartSourceType());
	}

	@Test
	public void testPopulateWithSuccessfulResult_empty_addToCartTime()
	{
		wishlish2EntryModelToOrderEntryPopulator.setProductConverter(productConverter);
		final ProductData productData = new ProductData();

		productData.setCode("000001");
		productData.setName("testProduct");


		final Wishlist2EntryModel wishlist2EntryModel = new Wishlist2EntryModel();
		final Date date = new Date();
		wishlist2EntryModel.setAddedDate(date);
		wishlist2EntryModel.setQuantity(NUMS);

		final OrderEntryData orderEntryData = new OrderEntryData();

		given(productConverter.convert(Mockito.any())).willReturn(productData);
		given(productPriceAndStockConverter.convert(Mockito.any(), Mockito.any())).willReturn(productData);

		wishlish2EntryModelToOrderEntryPopulator.populate(wishlist2EntryModel, orderEntryData);

		Assert.assertEquals(productData.getCode(), orderEntryData.getProduct().getCode());
		Assert.assertEquals(NUMS, orderEntryData.getQuantity().intValue());
		Assert.assertNull(orderEntryData.getDeliveryPointOfService());
		Assert.assertEquals(CartSourceType.WISHLIST, orderEntryData.getCartSourceType());
	}
}
