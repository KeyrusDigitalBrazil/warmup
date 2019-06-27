/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */

package de.hybris.platform.warehousing.returns.service.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRefundAmountCalculationServiceTest
{
	private AbstractOrderEntryModel orderEntryModel1;
	private AbstractOrderEntryModel orderEntryModel2;
	private ReturnProcessModel returnProcessModel;
	private RefundEntryModel returnEntryModel1;
	private RefundEntryModel returnEntryModel2;
	private ReturnRequestModel returnRequest;
	private ReturnRequestModel returnWithShippingCost;

	@Mock
	private OrderModel orderModel;

	@Mock
	private CurrencyModel currencyModel;

	@Mock
	private ModelService modelService;

	@Mock
	private ReturnService returnService;

	@Mock
	private UserService userService;

	@InjectMocks
	private DefaultRefundAmountCalculationService refundAmountCalculationService = new DefaultRefundAmountCalculationService();

	@Before
	public void setup()
	{
		when(userService.getCurrentUser()).thenReturn(new UserModel());

		when(orderModel.getCode()).thenReturn("ORDER1");
		when(orderModel.getDeliveryCost()).thenReturn(10.2D);

		when(orderModel.getCurrency()).thenReturn(currencyModel);
		when(currencyModel.getDigits()).thenReturn(5);

		orderEntryModel1 = spy(new AbstractOrderEntryModel());
		when(orderEntryModel1.getBasePrice()).thenReturn(100.5D);
		orderEntryModel2 = spy(new AbstractOrderEntryModel());
		when(orderEntryModel2.getBasePrice()).thenReturn(200D);

		when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntryModel1,orderEntryModel2));

		returnEntryModel1 = spy(new RefundEntryModel());
		returnEntryModel1.setExpectedQuantity(1L);
		returnEntryModel1.setAmount(new BigDecimal(100.5D));
		when(returnEntryModel1.getExpectedQuantity()).thenReturn(1L);
		when(returnEntryModel1.getOrderEntry()).thenReturn(orderEntryModel1);

		returnEntryModel2 = spy(new RefundEntryModel());
		returnEntryModel2.setExpectedQuantity(2L);
		returnEntryModel2.setAmount(new BigDecimal(200D));
		when(returnEntryModel2.getExpectedQuantity()).thenReturn(1L);
		when(returnEntryModel2.getOrderEntry()).thenReturn(orderEntryModel2);

		returnRequest = new ReturnRequestModel();
		returnRequest.setOrder(orderModel);
		returnRequest.setRefundDeliveryCost(Boolean.FALSE);
		returnRequest.setRMA("RETURN_REQUEST1");

		when(returnEntryModel1.getReturnRequest()).thenReturn(returnRequest);
		when(returnEntryModel2.getReturnRequest()).thenReturn(returnRequest);

		returnRequest.setReturnEntries(Arrays.asList(returnEntryModel1,returnEntryModel2));
		when(returnService.getReturnRequests(returnRequest.getOrder().getCode())).thenReturn(Arrays.asList(returnRequest));

		returnWithShippingCost = new ReturnRequestModel();
		returnWithShippingCost.setOrder(orderModel);
		returnWithShippingCost.setRefundDeliveryCost(Boolean.TRUE);
		returnWithShippingCost.setRMA("RETURN_REQUEST2");

		returnWithShippingCost.setReturnEntries(Arrays.asList(returnEntryModel1,returnEntryModel2));
		when(returnService.getReturnRequests(returnWithShippingCost.getOrder().getCode())).thenReturn(Arrays.asList(returnWithShippingCost));

		returnProcessModel = new ReturnProcessModel();
		returnProcessModel.setReturnRequest(returnRequest);

		doNothing().when(modelService).save(any());
	}

	@Test
	public void shouldCalculateRefAmountWhenShipCost_NotRequested() throws Exception
	{
		BigDecimal refundAmount = new BigDecimal("300.50");
		refundAmount = refundAmount.setScale(5, BigDecimal.ROUND_HALF_DOWN);

		assertEquals(refundAmount, refundAmountCalculationService.getOriginalRefundAmount(returnRequest));
	}

	@Test
	public void shouldCalculateRefAmountWhenShipCost_Requested() throws Exception
	{
		BigDecimal refundAmount = new BigDecimal("310.70");
		refundAmount = refundAmount.setScale(5, BigDecimal.ROUND_HALF_DOWN);

		assertEquals(refundAmount, refundAmountCalculationService.getOriginalRefundAmount(returnWithShippingCost));
	}

	@Test
	public void shouldNotIncludeShippingCostWhenAlreadyRefunded() throws Exception
	{
		BigDecimal refundAmount = new BigDecimal("300.50");
		refundAmount = refundAmount.setScale(5, BigDecimal.ROUND_HALF_DOWN);

		assertEquals(refundAmount, refundAmountCalculationService.getCustomRefundAmount(returnRequest));
	}

	@Test
	public void shouldCalculateZeroBasePriceOrderEntry() throws Exception
	{
		BigDecimal refundAmount = new BigDecimal("300.50");
		refundAmount = refundAmount.setScale(5, BigDecimal.ROUND_HALF_DOWN);

		assertEquals(refundAmount, refundAmountCalculationService.getOriginalRefundAmount(returnRequest));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCalculateNullReturn() throws Exception
	{
		refundAmountCalculationService.getCustomRefundAmount(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCalculateNullRefundEntry() throws Exception
	{
		returnRequest.setReturnEntries(null);
		refundAmountCalculationService.getCustomRefundAmount(returnRequest);
	}

}
