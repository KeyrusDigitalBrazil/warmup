/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ysapdpordermanagement.actions.returns;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentCaptureException;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRefundException;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;
import de.hybris.platform.ysapdpordermanagement.actions.order.payment.SapDigitalPaymentTakePaymentAction;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapDigitalPaymentCaptureRefundActionTest
{
	
	ReturnProcessModel returnProcessModel;
	OrderModel orderModel;
	PaymentTransactionModel paymentTransaction;
	PaymentTransactionEntryModel paymentTransactionEntry;
	BigDecimal customRefundAmount;
	List<PaymentTransactionModel> paymentTransactions;
	
	ReturnRequestModel returnRequest;
	
	@InjectMocks
	SapDigitalPaymentCaptureRefundAction action = new SapDigitalPaymentCaptureRefundAction();
	
	@Mock
	SapDigitalPaymentService sapDigitalPaymentService;
	
	@Mock
	RefundAmountCalculationService refundAmountCalculationService;
	
	@Mock
	ModelService modelService;
	
	
	@Before
	public void setup() throws SapDigitalPaymentRefundException
	{
		returnRequest = new ReturnRequestModel();
		paymentTransactionEntry = spy(new PaymentTransactionEntryModel());

		paymentTransaction = new PaymentTransactionModel();
		paymentTransaction.setPaymentProvider("PaymentProvider");
		paymentTransactions =  new ArrayList<>();
		paymentTransactions.add(paymentTransaction);
		
		orderModel = new OrderModel();
		orderModel.setPaymentTransactions(paymentTransactions);
		returnRequest.setOrder(orderModel);
		
		final List<ReturnEntryModel> returnEntryList = new ArrayList<>();
		ReturnEntryModel returnEntry = new ReturnEntryModel();
		returnEntryList.add(returnEntry);
		
		returnRequest.setReturnEntries(returnEntryList);

		returnProcessModel = new ReturnProcessModel();
		returnProcessModel.setReturnRequest(returnRequest);

		when(refundAmountCalculationService.getCustomRefundAmount(returnRequest)).thenReturn(new BigDecimal(1000));
		when(refundAmountCalculationService.getOriginalRefundAmount(returnRequest)).thenReturn(new BigDecimal(1000));
		
		when(sapDigitalPaymentService.refund(eq(paymentTransaction), any(BigDecimal.class))).thenReturn(paymentTransactionEntry);
		when(sapDigitalPaymentService.isSapDigitalPaymentTransaction(eq(paymentTransaction))).thenReturn(Boolean.TRUE);
		
	}
	
	@Test
	public void failRefundForEmptyTransaction() throws RetryLaterException, Exception
	{
		returnRequest.getOrder().setPaymentTransactions(Collections.EMPTY_LIST);
		final Transition transition = action.executeAction(returnProcessModel);
		assertTrue(AbstractSimpleDecisionAction.Transition.NOK.toString().equals(transition.toString()));
		assertTrue(returnRequest.getStatus().toString().equals(ReturnStatus.PAYMENT_REVERSAL_FAILED.toString()));
		returnRequest.getReturnEntries().stream().forEach(e -> assertTrue(e.getStatus().toString().equals(ReturnStatus.PAYMENT_REVERSAL_FAILED.toString())));
	}
	
	@Test
	public void successRefundForForAcceptTransaction() throws RetryLaterException, Exception
	{
		
		orderModel.setPaymentTransactions(paymentTransactions);
		returnRequest.setOrder(orderModel);
		when(paymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
		final Transition transition = action.executeAction(returnProcessModel);
		assertTrue(AbstractSimpleDecisionAction.Transition.OK.toString().equals(transition.toString()));
		assertTrue(returnRequest.getStatus().toString().equals(ReturnStatus.PAYMENT_REVERSED.toString()));
		returnRequest.getReturnEntries().stream().forEach(e -> assertTrue(e.getStatus().toString().equals(ReturnStatus.PAYMENT_REVERSED.toString())));
	}
	
	@Test
	public void failRefundForForErrorTransaction() throws RetryLaterException, Exception
	{
		
		orderModel.setPaymentTransactions(paymentTransactions);
		returnRequest.setOrder(orderModel);
		when(paymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ERROR.name());
		final Transition transition = action.executeAction(returnProcessModel);
		assertTrue(AbstractSimpleDecisionAction.Transition.NOK.toString().equals(transition.toString()));
		assertTrue(returnRequest.getStatus().toString().equals(ReturnStatus.PAYMENT_REVERSAL_FAILED.toString()));
		returnRequest.getReturnEntries().stream().forEach(e -> assertTrue(e.getStatus().toString().equals(ReturnStatus.PAYMENT_REVERSAL_FAILED.toString())));
	}
	
	
	
	
	
	
	
	
	
	
	

}
