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
package de.hybris.platform.chinesepaymentservices.order.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.TaskModel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultChineseOrderDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private DefaultChineseOrderDao chineseOrderDao;

	@Resource
	private ModelService modelService;

	private AbstractOrderModel order;
	private TaskModel task;
	private UserModel user;
	private BaseStoreModel baseStore;
	private CurrencyModel currency;

	private static String ORDER_CODE1 = "10001000";
	private static String ORDER_CODE2 = "10001001";
	private static final long MILLI_SECOND = 2 * 3600 * 1000;
	@Before
	public void prepare()
	{
		currency = new CurrencyModel();
		currency.setIsocode("en");
		currency.setSymbol("$");

		user = new UserModel();
		user.setUid("testuser");
		
		baseStore = new BaseStoreModel();
		baseStore.setUid("testStore");
		modelService.save(baseStore);

		order = new OrderModel();
		order.setCode(ORDER_CODE1);
		order.setCurrency(currency);
		order.setDate(new Date());
		order.setUser(user);
		order.setPaymentStatus(PaymentStatus.NOTPAID);
		order.setStatus(OrderStatus.CREATED);
		modelService.save(order);

		task = new TaskModel();
		final Map<String, Object> contextData = new HashMap();
		task.setRunnerBean("submitOrderEventTask");
		contextData.put("baseStore", baseStore);
		contextData.put("currentUser", user);
		task.setContextItem(order);
		task.setContext(contextData);
		modelService.save(task);
	}

	@Test
	public void testFindUnpaidOrders()
	{
		final Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, 1990);
		order.setDate(date.getTime());
		modelService.save(order);
		final List<AbstractOrderModel> orders = chineseOrderDao.findUnpaidOrders(MILLI_SECOND);

		Assert.assertEquals(ORDER_CODE1, orders.get(0).getCode());
	}

	@Test
	public void testFindOrderByCode()
	{
		AbstractOrderModel result = chineseOrderDao.findOrderByCode(ORDER_CODE1);
		Assert.assertNotNull(result);

		result = chineseOrderDao.findOrderByCode(ORDER_CODE2);
		Assert.assertNull(result);
	}

	@Test
	public void testFindSubmitOrderEventTask_normalResult()
	{
		final TaskModel result1 = chineseOrderDao.findSubmitOrderEventTask(ORDER_CODE1);
		Assert.assertNotNull(result1);
		final Map<String, Object> contextData = (Map<String, Object>) result1.getContext();
		Assert.assertEquals(user, contextData.get("currentUser"));
		Assert.assertEquals(baseStore, contextData.get("baseStore"));
		Assert.assertEquals(order, result1.getContextItem());

		final TaskModel result2 = chineseOrderDao.findSubmitOrderEventTask(ORDER_CODE2);
		Assert.assertNull(result2);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void testFindSubmitOrderEventTask_abnormalResult()
	{
		final TaskModel taskmodel = new TaskModel();
		taskmodel.setContextItem(order);
		taskmodel.setRunnerBean("submitOrderEventTask");
		modelService.save(taskmodel);
		final TaskModel result = chineseOrderDao.findSubmitOrderEventTask(ORDER_CODE1);
	}
}
