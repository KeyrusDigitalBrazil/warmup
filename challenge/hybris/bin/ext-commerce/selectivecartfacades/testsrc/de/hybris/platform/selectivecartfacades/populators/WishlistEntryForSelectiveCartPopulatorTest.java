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
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.selectivecartfacades.data.Wishlist2EntryData;
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
 * Junit test suite for {@link WishlistEntryForSelectiveCartPopulator}
 */
@UnitTest
public class WishlistEntryForSelectiveCartPopulatorTest
{

	WishlistEntryForSelectiveCartPopulator wishlistEntryForSelectiveCartPopulator = null;
	private static final Integer NUMS = 100;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	@Mock
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		wishlistEntryForSelectiveCartPopulator = new WishlistEntryForSelectiveCartPopulator();
		wishlistEntryForSelectiveCartPopulator.setProductPriceAndStockConverter(productPriceAndStockConverter);
	}

	@Test
	public void testPopulateWithSuccessfulResult()
	{
		wishlistEntryForSelectiveCartPopulator.setProductConverter(productConverter);

		final ProductData productData = new ProductData();

		productData.setCode("000001");
		productData.setName("testProduct");

		final Wishlist2EntryModel wishlist2EntryModel = new Wishlist2EntryModel();
		final Date date = new Date();
		wishlist2EntryModel.setAddedDate(date);
		wishlist2EntryModel.setQuantity(NUMS);
		final Wishlist2EntryData wishlist2EntryData  = new Wishlist2EntryData();

		given(productConverter.convert(Mockito.any())).willReturn(productData);
		given(productPriceAndStockConverter.convert(Mockito.any(), Mockito.any())).willReturn(productData);

		wishlistEntryForSelectiveCartPopulator.populate(wishlist2EntryModel, wishlist2EntryData);

		Assert.assertSame(date, wishlist2EntryData.getAddedDate());
		Assert.assertSame(NUMS, wishlist2EntryData.getQuantity());
		Assert.assertSame(productData.getCode(), wishlist2EntryData.getProduct().getCode());
		Assert.assertSame(productData.getName(), wishlist2EntryData.getProduct().getName());

	}

}
