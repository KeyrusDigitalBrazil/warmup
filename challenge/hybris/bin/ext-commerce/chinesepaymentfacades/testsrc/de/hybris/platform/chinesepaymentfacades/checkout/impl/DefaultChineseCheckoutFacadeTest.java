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

package de.hybris.platform.chinesepaymentfacades.checkout.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.checkout.ChineseCheckoutService;
import de.hybris.platform.chinesepaymentservices.order.service.impl.DefaultChineseOrderService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.strategies.impl.EventPublishingSubmitOrderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultChineseCheckoutFacadeTest
{
	@Mock
	private Converter<CartModel, CartData> cartConverter;
	@Mock
	private ChineseCheckoutService chineseCheckoutService;
	@Mock
	private DefaultChineseOrderService chineseOrderService;
	@Mock
	private Converter<CartModel, CartData> cartChinesePaymentInfoConverter;
	@Mock
	private EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private TaskService taskService;
	private DefaultChineseCheckoutFacade chineseCheckoutFacade;
	private CartModel cartModel;
	private CartData cartData;
	private UserModel userModel;
	private OrderModel orderModel;
	private OrderData orderData;
	private PaymentModeModel paymentMode;
	private TaskModel taskModel;
	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		chineseCheckoutFacade = Mockito.spy(new DefaultChineseCheckoutFacade());
		chineseCheckoutFacade.setCartConverter(cartConverter);
		chineseCheckoutFacade.setChineseCheckoutService(chineseCheckoutService);
		chineseCheckoutFacade.setChineseOrderService(chineseOrderService);
		chineseCheckoutFacade.setCartChinesePaymentInfoConverter(cartChinesePaymentInfoConverter);
		chineseCheckoutFacade.setEventPublishingSubmitOrderStrategy(eventPublishingSubmitOrderStrategy);
		chineseCheckoutFacade.setConfigurationService(configurationService);
		chineseCheckoutFacade.setTaskService(taskService);
		cartModel = new CartModel();
		userModel = new UserModel();
		cartModel.setUser(userModel);
		cartData = new CartData();
		orderModel = new OrderModel();
		orderData = new OrderData();
		paymentMode = new PaymentModeModel();
		taskModel = new TaskModel();

		Mockito.when(cartConverter.convert(cartModel)).thenReturn(cartData);
		Mockito.doReturn(cartModel).when(chineseCheckoutFacade).getCart();
	}

	@Test
	public void testConvertCart()
	{
		final CartData result = chineseCheckoutFacade.convertCart(cartModel);
		Assert.assertEquals(result, cartData);
	}

	@Test
	public void testSetPaymentMode()
	{
		Mockito.doNothing().when(chineseCheckoutService).setPaymentMode(paymentMode, cartModel);
		chineseCheckoutFacade.setPaymentMode(paymentMode);
		Mockito.verify(chineseCheckoutService, Mockito.times(1)).setPaymentMode(paymentMode, cartModel);
	}

	@Test
	public void testAuthorizePayment()
	{
		final String securityCode = "securityCode";
		Mockito.when(chineseCheckoutService.authorizePayment(securityCode, cartModel)).thenReturn(true);
		final boolean result = chineseCheckoutFacade.authorizePayment(securityCode);
		Mockito.verify(chineseCheckoutService, Mockito.times(1)).authorizePayment(securityCode, cartModel);
		Assert.assertTrue(result);
	}

	@Test
	public void testReserveStock() throws InsufficientStockLevelException
	{
		final String orderCode = "orderCode";
		final String productCode = "productCode";
		final int quantity = 0;
		final Optional<PointOfServiceModel> pos = Optional.empty();
		Mockito.when(chineseCheckoutService.reserveStock(orderCode, productCode, quantity, pos)).thenReturn(true);
		final boolean result = chineseCheckoutFacade.reserveStock(orderCode, productCode, quantity, pos);
		Assert.assertTrue(result);
	}

	@Test
	public void testSubmitOrder_taskExists()
	{
		final String orderCode = "orderCode";
		Mockito.when(chineseCheckoutService.getSubmitOrderEventTask(orderCode)).thenReturn(Optional.of(taskModel));
		chineseCheckoutFacade.submitOrder(orderCode);
		Mockito.verify(chineseCheckoutService, Mockito.times(0)).getOrderByCode(Mockito.any());
		Mockito.verify(chineseCheckoutFacade, Mockito.times(0)).createSubmitOrderEventTask(Mockito.any());
	}

	@Test
	public void testSubmitOrder_taskNotExists()
	{
		final String orderCode = "orderCode";
		Mockito.when(chineseCheckoutService.getSubmitOrderEventTask(orderCode)).thenReturn(Optional.empty());
		Mockito.when(chineseCheckoutService.getOrderByCode(orderCode)).thenReturn(orderModel);
		Mockito.doReturn(taskModel).when(chineseCheckoutFacade).createSubmitOrderEventTask(orderModel);
		Mockito.doNothing().when(taskService).scheduleTask(taskModel);
		chineseCheckoutFacade.submitOrder(orderCode);
		Mockito.verify(chineseCheckoutService, Mockito.times(1)).getOrderByCode(orderCode);
		Mockito.verify(chineseCheckoutFacade, Mockito.times(1)).createSubmitOrderEventTask(orderModel);
		Mockito.verify(taskService, Mockito.times(1)).scheduleTask(taskModel);
	}
}
