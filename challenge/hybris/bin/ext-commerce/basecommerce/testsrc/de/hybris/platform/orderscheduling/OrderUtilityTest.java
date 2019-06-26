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
package de.hybris.platform.orderscheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class OrderUtilityTest extends ServicelayerTransactionalTest
{
	@Resource
	private OrderUtility orderUtility;
	@Resource
	private ModelService modelService;
	@Resource
	private CartService cartService;
	@Resource
	private UserService userService;
	@Resource
	private ProductService productService;
	@Resource
	private OrderService orderService;
	@Resource
	private CalculationService calculationService;

	private AddressModel deliveryAddress;
	private DebitPaymentInfoModel paymentInfo;
	private CartModel cart;

	@Before
	public void initTest() throws Exception //NOPMD
	{
		createCoreData();
		createDefaultCatalog();

		cart = cartService.getSessionCart();

		deliveryAddress = modelService.create(AddressModel.class);
		deliveryAddress.setFirstname("Krzysztof");
		deliveryAddress.setLastname("Kwiatosz");
		deliveryAddress.setTown("Katowice");
		deliveryAddress.setOwner(userService.getCurrentUser());

		modelService.save(deliveryAddress);

		paymentInfo = modelService.create(DebitPaymentInfoModel.class);
		paymentInfo.setCode(UUID.randomUUID().toString());
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("Bank");
		paymentInfo.setUser(userService.getCurrentUser());
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("I");

		cartService.addNewEntry(cart, productService.getProductForCode("testProduct1"), 1, null);
		cartService.addNewEntry(cart, productService.getProductForCode("testProduct2"), 1, null);

	}

	@Test
	public void testDoNotCreateOrderFromEmptyCart() throws InvalidCartException
	{
		cartService.removeSessionCart();
		cart = cartService.getSessionCart();

		assertNull("No order should be placed", orderUtility.createOrderFromCart(cart, deliveryAddress, null, paymentInfo));
	}

	@Test
	public void testCreateOrderFromCart() throws InvalidCartException
	{
		final OrderModel order = orderUtility.createOrderFromCart(cart, deliveryAddress, deliveryAddress, paymentInfo);
		assertNotNull("No order should be placed", order);
		assertEquals("There should be 2 order entries", order.getEntries().size(), 2);

	}

	@Test
	public void testCreateOrderFromOrderTemplate() throws InvalidCartException, CalculationException
	{
		cart.setDeliveryAddress(deliveryAddress);
		cart.setPaymentInfo(paymentInfo);


		final OrderModel orderTemplate = orderService.createOrderFromCart(cart);
		modelService.save(orderTemplate);
		calculationService.calculate(orderTemplate);

		final OrderModel order = orderUtility.createOrderFromOrderTemplate(orderTemplate);

		assertNotNull("No order should be placed", order);
		assertEquals("There should be 2 order entries", order.getEntries().size(), 2);
	}



}
