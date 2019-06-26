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
package de.hybris.platform.selectivecartfacades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.impl.DefaultProductService;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;
import de.hybris.platform.selectivecartfacades.data.Wishlist2EntryData;
import de.hybris.platform.selectivecartfacades.populators.WishlistForSelectiveCartPopulator;
import de.hybris.platform.selectivecartservices.SelectiveCartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Junit test suite for {@link DefaultSelectiveCartFacade}
 */
@UnitTest
public class DefaultSelectiveCartFacadeTest
{
	private static final Integer NUMS1 = 100;
	private static final Integer NUMS2 = 88;
	private static final String CART_CODE = "testcart";
	private static final int ENTRY_NUMBER = 10;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private SelectiveCartService selectiveCartService;

	@Mock
	private Converter<Wishlist2Model, Wishlist2Data> wishlistConverter;

	@Mock
	private DefaultProductService productService;

	@Mock
	private CartFacade cartFacade;

	@Mock
	private Converter<Wishlist2EntryModel, Wishlist2EntryData> wishlistEntryConverter;

	@Mock
	private Converter<PrincipalModel, PrincipalData> principalConverter;

	DefaultSelectiveCartFacade defaultSelectiveCartFacade;

	WishlistForSelectiveCartPopulator wishlistForSelectiveCartPopulator;

	Wishlist2Model wishlist2Model;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSelectiveCartFacade = new DefaultSelectiveCartFacade();
		defaultSelectiveCartFacade.setSelectiveCartService(selectiveCartService);
		defaultSelectiveCartFacade.setProductService(productService);
		defaultSelectiveCartFacade.setWishlistConverter(wishlistConverter);
		defaultSelectiveCartFacade.setCartFacade(cartFacade);

		wishlist2Model = new Wishlist2Model();

		wishlist2Model.setName("wishlist");
		final UserModel userModel = new UserModel();
		userModel.setUid("u000001");
		userModel.setName("jove");
		wishlist2Model.setUser(userModel);
	}

	@Test
	public void testWishlistForSelectiveCart()
	{
		Mockito.when(selectiveCartService.getWishlistForSelectiveCart()).thenReturn(wishlist2Model);
		defaultSelectiveCartFacade.getWishlistForSelectiveCart();

		Mockito.verify(wishlistConverter).convert(Mockito.any());
	}

	@Test
	public void testRemoveWishlistEntryForProduct()
	{
		final List<Wishlist2EntryModel> entries = new ArrayList<>();
		final Wishlist2EntryModel wishlist2EntryModel1 = new Wishlist2EntryModel();
		final Wishlist2EntryModel wishlist2EntryModel2 = new Wishlist2EntryModel();
		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode("p000001");
		wishlist2EntryModel1.setProduct(productModel1);
		wishlist2EntryModel1.setQuantity(NUMS1);
		final ProductModel productModel2 = new ProductModel();
		productModel2.setCode("p000002");
		wishlist2EntryModel2.setProduct(productModel2);
		wishlist2EntryModel2.setQuantity(NUMS2);
		entries.add(wishlist2EntryModel1);
		entries.add(wishlist2EntryModel2);
		wishlist2Model.setEntries(entries);

		Mockito.when(selectiveCartService.getWishlistForSelectiveCart()).thenReturn(wishlist2Model);
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenReturn(productModel1);

		defaultSelectiveCartFacade.removeWishlistEntryForProduct(productModel1.getCode());

		Mockito.verify(selectiveCartService).removeWishlistEntryForProduct(Mockito.any(), Mockito.any());
	}

	@Test
	public void testAddToCartFromWishlist() throws CommerceCartModificationException
	{
		final List<Wishlist2EntryModel> entries = new ArrayList<>();
		final Wishlist2EntryModel wishlist2EntryModel1 = new Wishlist2EntryModel();
		final Wishlist2EntryModel wishlist2EntryModel2 = new Wishlist2EntryModel();
		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode("p000001");
		final Date now = new Date();
		wishlist2EntryModel1.setProduct(productModel1);
		wishlist2EntryModel1.setQuantity(NUMS1);
		wishlist2EntryModel1.setAddToCartTime(now);
		final ProductModel productModel2 = new ProductModel();
		productModel2.setCode("p000002");
		wishlist2EntryModel2.setProduct(productModel2);
		wishlist2EntryModel2.setQuantity(NUMS2);
		wishlist2EntryModel2.setAddToCartTime(now);
		entries.add(wishlist2EntryModel1);
		entries.add(wishlist2EntryModel2);
		wishlist2Model.setEntries(entries);

		final CartModificationData cartModification = new CartModificationData();
		final OrderEntryData orderEntry = new OrderEntryData();
		orderEntry.setEntryNumber(ENTRY_NUMBER);
		cartModification.setCartCode(CART_CODE);
		cartModification.setEntry(orderEntry);

		Mockito.when(selectiveCartService.getWishlistForSelectiveCart()).thenReturn(wishlist2Model);
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenReturn(productModel1);
		Mockito.when(selectiveCartService.getWishlistEntryForProduct(Mockito.any(), Mockito.any()))
				.thenReturn(wishlist2EntryModel1);
		Mockito.when(cartFacade.addToCart(Mockito.anyString(), Mockito.anyInt())).thenReturn(cartModification);
		Mockito.doNothing().when(selectiveCartService).updateCartTimeForOrderEntry(Mockito.anyString(), Mockito.anyInt(),
				Mockito.any());

		defaultSelectiveCartFacade.addToCartFromWishlist("p000001");

		Mockito.verify(cartFacade).addToCart("p000001", NUMS1);
	}

	@Test
	public void testAddToWishlistFromCartByOrderEntry() throws CommerceCartModificationException
	{
		final List<Wishlist2EntryModel> entries = new ArrayList<>();
		final Wishlist2EntryModel wishlist2EntryModel1 = new Wishlist2EntryModel();
		final Wishlist2EntryModel wishlist2EntryModel2 = new Wishlist2EntryModel();
		final Date now = new Date();
		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode("p000001");
		wishlist2EntryModel1.setProduct(productModel1);
		wishlist2EntryModel1.setQuantity(NUMS1);
		wishlist2EntryModel1.setAddToCartTime(now);
		final ProductModel productModel2 = new ProductModel();
		productModel2.setCode("p000002");
		wishlist2EntryModel2.setProduct(productModel2);
		wishlist2EntryModel2.setQuantity(NUMS2);
		wishlist2EntryModel2.setAddToCartTime(now);
		entries.add(wishlist2EntryModel1);
		entries.add(wishlist2EntryModel2);
		wishlist2Model.setEntries(entries);

		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setQuantity(Long.valueOf(NUMS1));
		orderEntryData.setEntryNumber(ENTRY_NUMBER);

		final ProductData productData = new ProductData();
		productData.setCode("p00001");
		orderEntryData.setProduct(productData);

		Mockito.when(selectiveCartService.getWishlistForSelectiveCart()).thenReturn(wishlist2Model);
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenReturn(productModel1);
		Mockito.when(selectiveCartService.saveWishlistEntryForProduct(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(wishlist2EntryModel1);

		defaultSelectiveCartFacade.addToWishlistFromCart(orderEntryData);

		Mockito.verify(selectiveCartService).updateQuantityForWishlistEntry(Mockito.any(), Mockito.any());
	}

	@Test
	public void testaddToWishlistFromCartByProductCode() throws CommerceCartModificationException
	{
		final List<String> productCodes = new ArrayList<>();
		productCodes.add("p000001");
		productCodes.add("p000002");

		final CartData cartData = new CartData();
		final OrderEntryData orderEntryData1 = new OrderEntryData();
		final ProductData productData1 = new ProductData();
		productData1.setCode("p000001");
		orderEntryData1.setProduct(productData1);
		orderEntryData1.setQuantity(Long.valueOf(NUMS1));
		orderEntryData1.setEntryNumber(ENTRY_NUMBER);

		final OrderEntryData orderEntryData2 = new OrderEntryData();
		final ProductData productData2 = new ProductData();
		productData2.setCode("p000002");
		orderEntryData2.setProduct(productData2);
		orderEntryData2.setQuantity(Long.valueOf(NUMS1));
		orderEntryData2.setEntryNumber(ENTRY_NUMBER);

		final List<OrderEntryData> OrderEntryList = new ArrayList<>();
		OrderEntryList.add(orderEntryData1);
		OrderEntryList.add(orderEntryData2);

		cartData.setEntries(OrderEntryList);

		Mockito.when(cartFacade.getSessionCart()).thenReturn(cartData);

		DefaultSelectiveCartFacade defaultSelectiveCartFacade = new DefaultSelectiveCartFacade();
		defaultSelectiveCartFacade.setSelectiveCartService(selectiveCartService);
		defaultSelectiveCartFacade.setProductService(productService);
		defaultSelectiveCartFacade.setWishlistConverter(wishlistConverter);
		defaultSelectiveCartFacade.setCartFacade(cartFacade);
		defaultSelectiveCartFacade = Mockito.spy(defaultSelectiveCartFacade);

		Mockito.doNothing().when(defaultSelectiveCartFacade).addToWishlistFromCart(Mockito.any(OrderEntryData.class));

		defaultSelectiveCartFacade.addToWishlistFromCart(productCodes);

		Mockito.verify(defaultSelectiveCartFacade, Mockito.times(2)).addToWishlistFromCart(Mockito.any(OrderEntryData.class));
	}
}
