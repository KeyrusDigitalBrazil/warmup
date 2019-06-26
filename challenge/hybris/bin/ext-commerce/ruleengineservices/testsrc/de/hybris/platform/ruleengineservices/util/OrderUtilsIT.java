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
package de.hybris.platform.ruleengineservices.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class OrderUtilsIT extends ServicelayerTransactionalTest
{
	@Resource
	private ProductService productService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderUtils orderUtils;
	@Resource
	private OrderService orderService;
	@Resource
	private ModelService modelService;
	@Resource
	private CalculationService calculationService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testChangeSomeQuantitiesInOrder() throws InvalidCartException, CalculationException
	{
		final ProductModel product0 = productService.getProductForCode("testProduct0");
		final ProductModel product1 = productService.getProductForCode("testProduct1");
		final ProductModel product2 = productService.getProductForCode("testProduct2");
		final ProductModel product3 = productService.getProductForCode("testProduct3");
		assertNotNull(product0);
		assertNotNull(product1);
		assertNotNull(product2);
		assertNotNull(product3);

		final CartModel cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product0, 10, null);
		cartService.addNewEntry(cart, product1, 15, null);
		cartService.addNewEntry(cart, product2, 1, null);
		cartService.addNewEntry(cart, product3, 18, null);
		modelService.save(cart);

		final OrderModel order = orderService.createOrderFromCart(cart);// getOrderForCart(cart);
		modelService.save(order);

		assertEquals("Number of entries", 4, order.getEntries().size());
		calculationService.calculate(order);
		assertEquals("Number of entries", 4, order.getEntries().size());

		assertEquals(Long.valueOf(10), orderService.getEntryForNumber(order, 0).getQuantity());
		assertEquals(Long.valueOf(15), orderService.getEntryForNumber(order, 1).getQuantity());
		assertEquals(Long.valueOf(1), orderService.getEntryForNumber(order, 2).getQuantity());
		assertEquals(Long.valueOf(18), orderService.getEntryForNumber(order, 3).getQuantity());

		final Map<Integer, Long> newQuantities = new HashMap<Integer, Long>();
		newQuantities.put(Integer.valueOf(0), Long.valueOf(5));
		newQuantities.put(Integer.valueOf(1), Long.valueOf(0));
		newQuantities.put(Integer.valueOf(2), Long.valueOf(0));
		newQuantities.put(Integer.valueOf(3), Long.valueOf(10));
		orderUtils.updateOrderQuantities(order, newQuantities);

		//zero values
		assertEquals("Number of entries", 2, order.getEntries().size());
		calculationService.calculate(order);
		assertEquals("Number of entries", 2, order.getEntries().size());

		assertEquals(Long.valueOf(5), orderService.getEntryForNumber(order, 0).getQuantity());
		assertEquals(Long.valueOf(10), orderService.getEntryForNumber(order, 3).getQuantity());

		newQuantities.clear();
		newQuantities.put(Integer.valueOf(3), Long.valueOf(30));

		orderUtils.updateOrderQuantities(order, newQuantities);
		assertEquals("Number of entries", 2, order.getEntries().size());

		calculationService.calculate(order);
		assertEquals("Number of entries", 2, order.getEntries().size());

		assertEquals(Long.valueOf(5), orderService.getEntryForNumber(order, 0).getQuantity());
		assertEquals(Long.valueOf(30), orderService.getEntryForNumber(order, 3).getQuantity());

		newQuantities.clear();
		newQuantities.put(Integer.valueOf(0), Long.valueOf(-5)); // means: remove since it's < 1
		newQuantities.put(Integer.valueOf(3), Long.valueOf(5));

		orderUtils.updateOrderQuantities(order, newQuantities);
		assertEquals("Number of entries", 1, order.getEntries().size());

		calculationService.calculate(order);
		assertEquals("Number of entries", 1, order.getEntries().size());

		assertEquals(Long.valueOf(5), orderService.getEntryForNumber(order, 3).getQuantity());

		newQuantities.clear();
		newQuantities.put(Integer.valueOf(100), Long.valueOf(5)); // invalid entry number
		try
		{
			orderUtils.updateOrderQuantities(order, newQuantities);
			assertTrue("IllegalArgumentException expected here", false);
		}
		catch (final IllegalArgumentException ex)
		{
			assertTrue(true);
		}

	}
}
