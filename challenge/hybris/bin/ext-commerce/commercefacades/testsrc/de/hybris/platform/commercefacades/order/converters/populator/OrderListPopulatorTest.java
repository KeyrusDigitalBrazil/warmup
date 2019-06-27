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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OrderListPopulatorTest
{

	private static final String ORDER_CODE = "orderCode";
	private static final String STATUS_DISPLAY = "StatusDisplay";
	private static final Double totalPrice = Double.valueOf(1020.42);

	@Mock
	private OrderModel orderModel;
	@Mock
	private Date createDate;
	@Mock
	private PriceData priceData;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private PriceDataFactory priceDataFactory;

	private final OrderListPopulator orderListPopulator = new OrderListPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		orderListPopulator.setPriceDataFactory(priceDataFactory);
		when(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(totalPrice), currencyModel)).thenReturn(priceData);
	}

	@Test
	public void testConvert()
	{
		given(orderModel.getCode()).willReturn(ORDER_CODE);
		given(orderModel.getStatusDisplay()).willReturn(STATUS_DISPLAY);
		given(orderModel.getDate()).willReturn(createDate);
		given(orderModel.getCurrency()).willReturn(currencyModel);
		given(orderModel.getTotalPrice()).willReturn(totalPrice);

		final OrderData orderData = new OrderData();
		orderListPopulator.populate(orderModel, orderData);

		Assert.assertEquals(ORDER_CODE, orderData.getCode());
		Assert.assertEquals(STATUS_DISPLAY, orderData.getStatusDisplay());
		Assert.assertEquals(createDate, orderData.getCreated());
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(totalPrice), currencyModel);
		Assert.assertEquals(priceData, orderData.getTotalPrice());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSourceNull()
	{
		orderListPopulator.populate(null, mock(OrderData.class));
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testtargetNull()
	{
		orderListPopulator.populate(orderModel, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}
}
