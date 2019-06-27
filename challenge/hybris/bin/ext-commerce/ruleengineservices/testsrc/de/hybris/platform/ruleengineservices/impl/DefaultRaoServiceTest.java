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
package de.hybris.platform.ruleengineservices.impl;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.rao.util.DefaultRaoService;
import de.hybris.platform.ruleengineservices.ruleengine.impl.CartRaoBuilder;
import de.hybris.platform.ruleengineservices.util.RaoUtils;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test the Rao Service default implementation
 *
 */
public class DefaultRaoServiceTest
{

	/**
	 *
	 */
	private static final double DELTA = 0.005d;
	private DefaultRaoService raoService;
	private CartRAO cart;
	private ProductRAO product;
	private OrderEntryRAO entry;
	private ProductRAO promotedProduct;
	private RaoUtils raoUtils;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		raoUtils = new RaoUtils();

		cart = new CartRAO();
		cart.setCurrencyIsoCode("USD");
		product = new ProductRAO();
		entry = new OrderEntryRAO();
		raoService = new DefaultRaoService();
		promotedProduct = new ProductRAO();
		promotedProduct.setCode("23456");
	}

	@Test
	public void testAbsoluteDiscountAddedToCart()
	{
		raoService.addCartDiscount(true, 1.0, cart);

		Assert.assertNotNull(cart.getActions());
		Assert.assertEquals(1, cart.getActions().size());
		final DiscountRAO discountRAO = (DiscountRAO) cart.getActions().stream().findFirst().get();
		Assert.assertEquals(1.0d, discountRAO.getValue().doubleValue(), 0.005);
		Assert.assertTrue(raoUtils.isAbsolute(discountRAO));

	}

	@Test
	public void testNonAbsoluteDiscountAddedToCart()
	{
		raoService.addCartDiscount(false, 0.05, cart);

		Assert.assertNotNull(cart.getActions());
		Assert.assertEquals(1, cart.getActions().size());
		final DiscountRAO discountRAO = (DiscountRAO) cart.getActions().stream().findFirst().get();
		Assert.assertEquals(0.05d, discountRAO.getValue().doubleValue(), 0.005);
		Assert.assertFalse(raoUtils.isAbsolute(discountRAO));

	}


	@Test
	public void testCreatePopulatedOrderEntry()
	{

		final OrderEntryRAO orderEntryRAO = raoService.createOrderEntry(CartRaoBuilder.newCart("code").setCurrency("USD").getCart(),
				product, 12.34d, 3, 5);

		Assert.assertEquals(12.34d, orderEntryRAO.getBasePrice().doubleValue(), DELTA);
		Assert.assertEquals(product, orderEntryRAO.getProduct());
		Assert.assertEquals(3, orderEntryRAO.getQuantity());
		Assert.assertEquals(Integer.valueOf(5), orderEntryRAO.getEntryNumber());
		Assert.assertNull(orderEntryRAO.getCurrencyIsoCode());
		Assert.assertNull(orderEntryRAO.getActions());
	}

	@Test
	public void testAddEntryDiscount()
	{
		final OrderEntryRAO entry = raoService.createOrderEntry(CartRaoBuilder.newCart("code").setCurrency("USD").getCart(),
				product, 12.34d, 3, 0);

		raoService.addEntryDiscount(true, 4.32d, entry);

		Assert.assertEquals(1, entry.getActions().size());
		final DiscountRAO discount = (DiscountRAO) entry.getActions().stream().findFirst().get();
		Assert.assertEquals(4.32d, discount.getValue().doubleValue(), 0.005);
		Assert.assertTrue(raoUtils.isAbsolute(discount));
		Assert.assertNotNull(discount.getCurrencyIsoCode());
		Assert.assertNull(discount.getFiredRuleCode());
		Assert.assertNull(discount.getActionStrategyKey());
	}

	@Test
	public void testAddEntryToCart()
	{
		Assert.assertNull(cart.getEntries());

		raoService.addEntry(entry, cart);

		Assert.assertNotNull(cart.getEntries());
		Assert.assertEquals(1, cart.getEntries().size());
		final OrderEntryRAO orderEntryRAO = cart.getEntries().stream().findFirst().get();
		Assert.assertEquals(entry, orderEntryRAO);

	}

	@Test
	public void testAddPromotedProduct()
	{
		raoService.addPromotedProduct(promotedProduct, 1, 1.0, 0.0, cart);

		Assert.assertNull(cart.getActions());
		Assert.assertEquals(1, cart.getEntries().size());
		final OrderEntryRAO entry = cart.getEntries().stream().findFirst().get();
		Assert.assertEquals(promotedProduct, entry.getProduct());
		Assert.assertEquals(promotedProduct.getCode(), entry.getProduct().getCode());
		Assert.assertEquals(1.0d, entry.getBasePrice().doubleValue(), DELTA);
		Assert.assertEquals(1, entry.getQuantity());
		Assert.assertEquals(Integer.valueOf(0), entry.getEntryNumber());
		Assert.assertNotNull(entry.getActions());
		Assert.assertNull(entry.getCurrencyIsoCode());
		final DiscountRAO discount = (DiscountRAO) entry.getActions().stream().findFirst().get();
		Assert.assertTrue(raoUtils.isAbsolute(discount));
		Assert.assertEquals(0.0d, discount.getValue().doubleValue(), DELTA);
		Assert.assertNull(discount.getFiredRuleCode());
		Assert.assertNull(discount.getActionStrategyKey());
	}

	@Test
	public void testCreateProduct()
	{
		final ProductRAO productRao = raoService.createProduct();
		assertBasicProduct(productRao);
	}

	/**
	 * @param productRao
	 */
	private void assertBasicProduct(final ProductRAO productRao)
	{
		Assert.assertNotNull(productRao);
		Assert.assertNotNull(productRao.getCategories());
		Assert.assertTrue(productRao.getCategories().isEmpty());
	}

	@Test
	public void testCreateTypesafeCart()
	{
		final CartRAO cartRao = raoService.createCart();
		Assert.assertNotNull(cartRao.getActions());
		Assert.assertTrue(cartRao.getActions().isEmpty());
		Assert.assertNotNull(cartRao.getOriginalTotal());
		Assert.assertEquals(BigDecimal.ZERO, cartRao.getOriginalTotal());
		Assert.assertNotNull(cartRao.getEntries());
		Assert.assertTrue(cartRao.getEntries().isEmpty());
		Assert.assertNotNull(cartRao.getTotal());
		Assert.assertEquals(BigDecimal.ZERO, cartRao.getTotal());
	}

	@Test
	public void testCreateTypesafeCategory()
	{
		final CategoryRAO categoryRao = raoService.createCategory();
		Assert.assertNotNull(categoryRao);
	}

	@Test
	public void testCreateTypesafeOrderEntry()
	{
		final OrderEntryRAO orderEntryRao = raoService.createOrderEntry();
		Assert.assertNotNull(orderEntryRao);
		Assert.assertNotNull(orderEntryRao.getBasePrice());
		Assert.assertEquals(BigDecimal.ZERO, orderEntryRao.getBasePrice());
		Assert.assertNotNull(orderEntryRao.getActions());
		Assert.assertTrue(orderEntryRao.getActions().isEmpty());

	}

	@Test
	public void testCreateTypesafeDiscount()
	{
		final DiscountRAO discountRao = raoService.createDiscount();
		Assert.assertNotNull(discountRao);
		Assert.assertNotNull(discountRao.getValue());
		Assert.assertEquals(BigDecimal.ZERO, discountRao.getValue());
	}

	@Test
	public void testCreateProductString()
	{
		final ProductRAO productRao = raoService.createProduct("123");
		assertBasicProduct(productRao);
		Assert.assertEquals("123", productRao.getCode());
	}
}
