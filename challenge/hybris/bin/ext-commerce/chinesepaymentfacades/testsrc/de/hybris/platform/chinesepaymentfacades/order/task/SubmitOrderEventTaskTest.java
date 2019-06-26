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

package de.hybris.platform.chinesepaymentfacades.order.task;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.checkout.ChineseCheckoutService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.strategies.impl.EventPublishingSubmitOrderStrategy;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SubmitOrderEventTaskTest
{
	@Mock
	private ChineseCheckoutService chineseCheckoutService;
	@Mock
	private EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy;
	@Mock
	private UserService userService;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private TaskService taskService;
	@Mock
	private BaseStoreModel baseStoreModel;
	private final String orderCode = "testOrderCode";
	private SubmitOrderEventTask submitOrderEventTask;
	private Map<String, Object> contextData;
	private TaskModel taskModel;
	private OrderModel orderModel;
	private UserModel userModel;
	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		submitOrderEventTask = Mockito.spy(new SubmitOrderEventTask());
		submitOrderEventTask.setChineseCheckoutService(chineseCheckoutService);
		submitOrderEventTask.setEventPublishingSubmitOrderStrategy(eventPublishingSubmitOrderStrategy);
		submitOrderEventTask.setUserService(userService);
		submitOrderEventTask.setCustomerAccountService(customerAccountService);

		taskModel = new TaskModel();
		orderModel = new OrderModel();
		userModel = new CustomerModel();
		contextData = new HashMap<String, Object>();
		contextData.put("baseStore", baseStoreModel);
		contextData.put("currentUser", userModel);
		orderModel.setCode(orderCode);
		taskModel.setContextItem(orderModel);
		taskModel.setContext(contextData);
	}

	@Test
	public void testGetOrderForCurrentStoreAndUser_userNull()
	{
		contextData.remove("currentUser");
		final OrderModel result = submitOrderEventTask.getOrderForCurrentStoreAndUser(taskModel);
		Assert.assertNull(result);
	}

	@Test
	public void testGetOrderForCurrentStoreAndUser_anonymousUser()
	{
		Mockito.when(userService.isAnonymousUser(userModel)).thenReturn(true);
		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreModel)).thenReturn(orderModel);
		final OrderModel result = submitOrderEventTask.getOrderForCurrentStoreAndUser(taskModel);
		Mockito.verify(customerAccountService, Mockito.times(1)).getOrderForCode(orderCode, baseStoreModel);
		Assert.assertEquals(result, orderModel);
	}

	@Test
	public void testGetOrderForCurrentStoreAndUser_normalUser()
	{
		Mockito.when(userService.isAnonymousUser(userModel)).thenReturn(false);
		Mockito.when(customerAccountService.getOrderForCode((CustomerModel) userModel, orderCode, baseStoreModel)).thenReturn(
				orderModel);
		final OrderModel result = submitOrderEventTask.getOrderForCurrentStoreAndUser(taskModel);
		Mockito.verify(customerAccountService, Mockito.times(0)).getOrderForCode(orderCode, baseStoreModel);
		Mockito.verify(customerAccountService, Mockito.times(1)).getOrderForCode((CustomerModel) userModel, orderCode,
				baseStoreModel);
		Assert.assertEquals(result, orderModel);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetOrderForCurrentStoreAndUser_userNotFound()
	{
		Mockito.when(userService.isAnonymousUser(userModel)).thenReturn(false);
		Mockito.when(customerAccountService.getOrderForCode((CustomerModel) userModel, orderCode, baseStoreModel)).thenThrow(
				ModelNotFoundException.class);
		submitOrderEventTask.getOrderForCurrentStoreAndUser(taskModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRunTask_taskIsNull()
	{
		submitOrderEventTask.run(taskService, null);
	}

	@Test(expected = RetryLaterException.class)
	public void testRunTask_orderNullAndRetry()
	{
		taskModel.setRetry(0);
		Mockito.doReturn(null).when(submitOrderEventTask).getOrderForCurrentStoreAndUser(taskModel);
		submitOrderEventTask.run(taskService, taskModel);
	}

	@Test(expected = IllegalStateException.class)
	public void testRunTask_orderNotPaidAndNotRetry()
	{
		taskModel.setRetry(4);
		orderModel.setPaymentStatus(PaymentStatus.NOTPAID);
		Mockito.doReturn(orderModel).when(submitOrderEventTask).getOrderForCurrentStoreAndUser(taskModel);
		submitOrderEventTask.run(taskService, taskModel);
	}

	@Test
	public void testRunTask()
	{
		orderModel.setPaymentStatus(PaymentStatus.PAID);
		orderModel.setStatus(OrderStatus.CREATED);
		Mockito.doReturn(orderModel).when(submitOrderEventTask).getOrderForCurrentStoreAndUser(taskModel);
		Mockito.doNothing().when(chineseCheckoutService).deleteStockLevelReservationHistoryEntry(orderCode);
		Mockito.doNothing().when(eventPublishingSubmitOrderStrategy).submitOrder(orderModel);
		submitOrderEventTask.run(taskService, taskModel);
		Mockito.verify(chineseCheckoutService, Mockito.times(1)).deleteStockLevelReservationHistoryEntry(orderCode);
		Mockito.verify(eventPublishingSubmitOrderStrategy, Mockito.times(1)).submitOrder(orderModel);
	}
}
