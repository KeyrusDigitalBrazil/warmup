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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.dao.AlipayPaymentTransactionDao;
import de.hybris.platform.chinesepspalipayservices.dao.AlipayPaymentTransactionEntryDao;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayErrorInfo;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayNotification;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;
import de.hybris.platform.chinesepspalipayservices.enums.AlipayPayMethod;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@IntegrationTest
public class DefaultAlipayPaymentTransactionStrategyTest extends ServicelayerTransactionalTest
{
	final String TEST_PAYREQUEST_URL = "test/payrequest/url";

	@Resource(name = "alipayPaymentTransactionStrategy")
	private DefaultAlipayPaymentTransactionStrategy defaultAlipayPaymentTransactionStrategy;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Mock
	private KeyGenerator paymentTransactionKeyGenerator;

	@Mock
	private AlipayPaymentTransactionDao alipayPaymentTransactionDao;

	@Mock
	private AlipayPaymentTransactionEntryDao alipayPaymentTransactionEntryDao;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		given(paymentTransactionKeyGenerator.generate()).willReturn("00000002");
	}

	@Test
	public void testCreateForNewRequest()
	{
		final OrderModel order = generateOrder();
		final DefaultAlipayPaymentTransactionStrategy spy = Mockito.spy(defaultAlipayPaymentTransactionStrategy);
		spy.setPaymentTransactionKeyGenerator(paymentTransactionKeyGenerator);

		spy.createForNewRequest(order, TEST_PAYREQUEST_URL);
     
		final AlipayPaymentTransactionModel transaction = (AlipayPaymentTransactionModel) order.getPaymentTransactions().get(0);
		assertNotNull(transaction);
		assertEquals("00000001", transaction.getCode());
		assertEquals("00000001", transaction.getRequestId());
		assertEquals(TEST_PAYREQUEST_URL, transaction.getPaymentUrl());
		assertEquals(AlipayPayMethod.DIRECTPAY, transaction.getPayMethod());
		assertEquals(PaymentConstants.Basic.PAYMENT_PROVIDER, transaction.getPaymentProvider());

		final AlipayPaymentTransactionEntryModel entry = (AlipayPaymentTransactionEntryModel) transaction.getEntries().get(0);
		assertNotNull(entry);
		assertEquals("USD", entry.getCurrency().getIsocode());
		assertEquals(PaymentTransactionType.REQUEST, entry.getType());
		assertEquals("00000001", entry.getRequestId());
		assertEquals(TransactionStatus.ACCEPTED.name(), entry.getTransactionStatus());
		assertEquals(TransactionStatusDetails.SUCCESFULL.name(), entry.getTransactionStatusDetails());
		assertEquals("00000002", entry.getCode());
	}

	@Test
	public void testUpdateForRefundRequest()
	{
		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.CAPTURE, TransactionStatus.ACCEPTED);
		final AlipayPaymentTransactionModel baseTransaction = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions()
				.get(0);

		final AlipayRefundRequestData alipayRefundRequestData = genereateRefundRequestData();
		final DefaultAlipayPaymentTransactionStrategy spy = Mockito.spy(defaultAlipayPaymentTransactionStrategy);
		spy.setPaymentTransactionKeyGenerator(paymentTransactionKeyGenerator);
		Mockito.doReturn(Optional.of(baseTransaction)).when(spy)
				.getPaymentTransactionWithCaptureEntry(Mockito.any(), Mockito.any());

		spy.updateTransactionForRefundRequest(orderModel, alipayRefundRequestData);

		final Optional<AlipayPaymentTransactionEntryModel> alipayPaymentTransactionEntryModel = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionEntry(orderModel, TransactionStatus.REVIEW, PaymentTransactionType.REFUND_STANDALONE);
		assertTrue(alipayPaymentTransactionEntryModel.isPresent());
		final AlipayPaymentTransactionEntryModel result = alipayPaymentTransactionEntryModel.get();
		assertEquals("USD", result.getCurrency().getIsocode());
		assertEquals(PaymentTransactionType.REFUND_STANDALONE, result.getType());
		assertEquals(baseTransaction, result.getPaymentTransaction());
		assertEquals("000001", result.getRequestId());
		assertEquals(TransactionStatus.REVIEW.name(), result.getTransactionStatus());
		assertEquals(TransactionStatusDetails.SUCCESFULL.name() + "; Refund request: 201601011234",
				result.getTransactionStatusDetails());
		assertEquals("00000002", result.getCode());
	}

	@Test
	public void testUpdateForRefundNotification()
	{
		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.CAPTURE, TransactionStatus.ACCEPTED);

		final DefaultAlipayPaymentTransactionStrategy spy = Mockito.spy(defaultAlipayPaymentTransactionStrategy);
		spy.setPaymentTransactionKeyGenerator(paymentTransactionKeyGenerator);

		final List<AlipayRefundData> alipayRefundData = new ArrayList<>();

		final AlipayRefundData refundData = new AlipayRefundData();
		refundData.setAlipayCode("123456");
		refundData.setBatchNo("20060702001");
		refundData.setPayerRefundAmount(80);
		refundData.setPayerRefundStatus("SUCCESS");
		refundData.setSellerEmail("jax_chuanhang@alipay.com");
		refundData.setSellerId("2088101003147483");
		refundData.setSellerRefundAmount(0.01);
		refundData.setSellerRefundStatus("SUCCESS");

		alipayRefundData.add(refundData);

		final AlipayPaymentTransactionModel transaction = new AlipayPaymentTransactionModel();
		transaction.setAlipayCode("123456");
		transaction.setRequestId("000001");
		transaction.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
		transaction.setOrder(orderModel);

		modelService.save(transaction);

		final AlipayPaymentTransactionEntryModel entry = new AlipayPaymentTransactionEntryModel();
		entry.setPaymentTransaction(transaction);
		entry.setType(PaymentTransactionType.REFUND_STANDALONE);
		entry.setTransactionStatus(TransactionStatus.REVIEW.name());
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()) + "11");

		modelService.save(entry);
		modelService.refresh(transaction);

		final Map<OrderModel, Boolean> refundStatus = spy.updateForRefundNotification(alipayRefundData);

		final Optional<AlipayPaymentTransactionEntryModel> refundEntryOptional = spy.getPaymentTransactionEntry(orderModel,
				TransactionStatus.ACCEPTED, PaymentTransactionType.REFUND_STANDALONE);
		assertTrue(refundEntryOptional.isPresent());
		final AlipayPaymentTransactionEntryModel refundEntry = refundEntryOptional.get();

		assertEquals(PaymentTransactionType.REFUND_STANDALONE, refundEntry.getType());
		assertEquals(transaction, refundEntry.getPaymentTransaction());
		assertEquals(TransactionStatus.ACCEPTED.name(), refundEntry.getTransactionStatus());
		assertEquals("SUCCESS" + "; Refund Batch No: " + "20060702001", refundEntry.getTransactionStatusDetails());
		assertEquals(String.valueOf(paymentTransactionKeyGenerator.generate()) + "11", refundEntry.getCode());

		assertEquals(refundStatus.size(), 1);
		assertTrue(refundStatus.get(orderModel));
	}

	@Test
	public void testGetPaymentTransactionWithCaptureEntrySuccessfully()
	{
		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.CAPTURE, TransactionStatus.ACCEPTED);
		final AlipayPaymentTransactionModel baseTransaction = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions()
				.get(0);

		final AlipayPaymentTransactionEntryModel entry = new AlipayPaymentTransactionEntryModel();
		entry.setPaymentTransaction(baseTransaction);
		entry.setType(PaymentTransactionType.CAPTURE);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()) + "22");
		modelService.save(entry);
		modelService.refresh(baseTransaction);

		final Optional<AlipayPaymentTransactionModel> result = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(orderModel, TransactionStatus.ACCEPTED);

		assertTrue(result.isPresent());
		final AlipayPaymentTransactionModel resultTransaction = result.get();
		assertEquals(baseTransaction, resultTransaction);
		assertEquals("10000", resultTransaction.getAlipayCode());
		assertEquals("000001", resultTransaction.getRequestId());
	}

	@Test
	public void testGetPaymentTransactionWithCaptureEntryFailed()
	{
		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.CAPTURE, TransactionStatus.ACCEPTED);
		final AlipayPaymentTransactionModel baseTransaction = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions()
				.get(0);
		final AlipayPaymentTransactionEntryModel entry = new AlipayPaymentTransactionEntryModel();
		entry.setPaymentTransaction(baseTransaction);
		entry.setType(PaymentTransactionType.PARTIAL_CAPTURE);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()) + "33");
		modelService.save(entry);
		modelService.refresh(baseTransaction);

		final Optional<AlipayPaymentTransactionModel> result = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(orderModel, TransactionStatus.ACCEPTED);

		assertFalse(result.isPresent());
	}

	@Test
	public void testCreateTransacionForNewRequest()
	{
		final OrderModel emptyOrder = generateOrder();
		modelService.save(emptyOrder);

		final String requestUrl = "https://mapi.alipay.com/gateway.do?";

		defaultAlipayPaymentTransactionStrategy.createTransacionForNewRequest(emptyOrder, requestUrl);

		final List<PaymentTransactionModel> transactionModels = emptyOrder.getPaymentTransactions();
		assertEquals(1, transactionModels.size());
		final AlipayPaymentTransactionModel transaction = (AlipayPaymentTransactionModel) transactionModels.get(0);
		assertEquals("00000001", transaction.getCode());
		assertEquals("https://mapi.alipay.com/gateway.do?", transaction.getPaymentUrl());
		assertEquals(AlipayPayMethod.DIRECTPAY, transaction.getPayMethod());
		assertEquals("00000001", transaction.getRequestId());
		assertEquals(PaymentConstants.Basic.PAYMENT_PROVIDER, transaction.getPaymentProvider());
	}

	@Test
	public void testSaveForStatusCheck()
	{
		final AlipayRawPaymentStatus alipayRawPaymentStatus = new AlipayRawPaymentStatus();
		alipayRawPaymentStatus.setTradeNo("87914321574713");
		alipayRawPaymentStatus.setTradeStatus("TRADE_SUCCESS");
		alipayRawPaymentStatus.setTotalFee(1.5);
		alipayRawPaymentStatus.setBuyerEmail("jax_chuanhang@alipay.com");
		alipayRawPaymentStatus.setBuyerId("2264872159712354");
		alipayRawPaymentStatus.setToSellerFee(1.5);

		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.REQUEST, TransactionStatus.ACCEPTED);
		final AlipayPaymentTransactionModel baseTransaction = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions()
				.get(0);

		final DefaultAlipayPaymentTransactionStrategy spy = Mockito.spy(defaultAlipayPaymentTransactionStrategy);
		spy.setPaymentTransactionKeyGenerator(paymentTransactionKeyGenerator);

		Mockito
				.doReturn(baseTransaction)
				.when(spy)
				.getPaymentTransactionToUpdate(Matchers.eq(orderModel), Matchers.eq(TransactionStatus.ACCEPTED), Matchers.anyString());

		spy.saveForStatusCheck(orderModel, alipayRawPaymentStatus);

		assertEquals(1, orderModel.getPaymentTransactions().size());
		assertEquals(baseTransaction, orderModel.getPaymentTransactions().get(0));
		assertEquals("87914321574713", baseTransaction.getAlipayCode());

		final Optional<AlipayPaymentTransactionEntryModel> option = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionEntry(orderModel, TransactionStatus.ACCEPTED, PaymentTransactionType.CAPTURE);
		assertTrue(option.isPresent());
		final AlipayPaymentTransactionEntryModel captureEntry = option.get();
		assertEquals(1.5, captureEntry.getAmount().doubleValue(), 0.001);
		assertEquals("jax_chuanhang@alipay.com", captureEntry.getPayerEmail());
		assertEquals("2264872159712354", captureEntry.getPayerId());
	}

	@Test
	public void testUpdateForCancelPayment()
	{
		final OrderModel orderModel = generateOrder();
		final AlipayPaymentTransactionModel baseTransaction = generateAlipayPaymentTransaction();
		final AlipayPaymentTransactionEntryModel baseEntry = generateAlipayPaymentTransactionEntry(PaymentTransactionType.CAPTURE,
				TransactionStatus.ACCEPTED, String.valueOf(paymentTransactionKeyGenerator.generate()) + "44");
		baseEntry.setPaymentTransaction(baseTransaction);
		final List<PaymentTransactionModel> transactions = new ArrayList<>();
		transactions.add(baseTransaction);
		orderModel.setPaymentTransactions(transactions);
		final AlipayRawCancelPaymentResult alipayRawCancelPaymentResult = new AlipayRawCancelPaymentResult();
		alipayRawCancelPaymentResult.setIsSuccess("T");

		defaultAlipayPaymentTransactionStrategy.updateForCancelPayment(orderModel, alipayRawCancelPaymentResult);

		final Optional<AlipayPaymentTransactionEntryModel> option = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionEntry(orderModel, TransactionStatus.ACCEPTED, PaymentTransactionType.CANCEL);

		assertTrue(option.isPresent());
	}

	@Test
	public void testUpdateForNotification()
	{
		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.REQUEST, TransactionStatus.ACCEPTED);
		final AlipayRawDirectPayNotification directPayNotifyResponseData = new AlipayRawDirectPayNotification();
		directPayNotifyResponseData.setTradeNo("8125764794235471");
		directPayNotifyResponseData.setTradeStatus("TRADE_FINISHED");
		directPayNotifyResponseData.setUseCoupon("T");
		directPayNotifyResponseData.setBuyerEmail("test@gmail.com");
		directPayNotifyResponseData.setBuyerId("2897165714527821");
		directPayNotifyResponseData.setTotalFee(2.0);

		final DefaultAlipayPaymentTransactionStrategy spy = Mockito.spy(defaultAlipayPaymentTransactionStrategy);
		spy.setPaymentTransactionKeyGenerator(paymentTransactionKeyGenerator);

		final AlipayPaymentTransactionModel transaction = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions()
				.get(0);
		Mockito.doReturn(transaction).when(spy)
				.getPaymentTransactionToUpdate(Matchers.eq(orderModel), Matchers.any(TransactionStatus.class), Matchers.anyString());

		spy.updateForNotification(orderModel, directPayNotifyResponseData);

		final Optional<AlipayPaymentTransactionEntryModel> option = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionEntry(orderModel, TransactionStatus.FINISHED, PaymentTransactionType.CAPTURE);
		assertTrue(option.isPresent());
		assertEquals("8125764794235471", transaction.getAlipayCode());
		final AlipayPaymentTransactionEntryModel entry = option.get();
		assertTrue(entry.getCouponUsed());
		assertEquals("test@gmail.com", entry.getPayerEmail());
		assertEquals("2897165714527821", entry.getPayerId());
		assertEquals(2.0, entry.getAdjustedAmount().doubleValue(), 0.01);
		assertEquals("Trade Status:TRADE_FINISHED", entry.getTransactionStatusDetails());
	}

	@Test
	public void testUpdateForError()
	{
		final OrderModel orderModel = generateOrder();
		addPaymentTransaction(orderModel, PaymentTransactionType.REQUEST, TransactionStatus.ACCEPTED);
		final AlipayRawDirectPayErrorInfo alipayRawDirectPayErrorInfo = new AlipayRawDirectPayErrorInfo();
		alipayRawDirectPayErrorInfo.setErrorCode("ILLEGAL_FEE_PARAM");
		alipayRawDirectPayErrorInfo.setBuyerEmail("test@gmail.com");
		alipayRawDirectPayErrorInfo.setBuyerId("2897165714527821");
		final AlipayPaymentTransactionModel transaction = (AlipayPaymentTransactionModel) orderModel.getPaymentTransactions()
				.get(0);
		defaultAlipayPaymentTransactionStrategy.setAlipayPaymentTransactionDao(alipayPaymentTransactionDao);
		Mockito.when(alipayPaymentTransactionDao.findTransactionByLatestRequestEntry(orderModel, true)).thenReturn(transaction);

		defaultAlipayPaymentTransactionStrategy.updateForError(orderModel, alipayRawDirectPayErrorInfo);

		final Optional<AlipayPaymentTransactionEntryModel> option = defaultAlipayPaymentTransactionStrategy
				.getPaymentTransactionEntry(orderModel, TransactionStatus.ERROR, PaymentTransactionType.CAPTURE);
		assertTrue(option.isPresent());

		final AlipayPaymentTransactionEntryModel entry = option.get();
		assertEquals("Error CodeILLEGAL_FEE_PARAM", entry.getTransactionStatusDetails());
		assertEquals("test@gmail.com", entry.getPayerEmail());
		assertEquals("2897165714527821", entry.getPayerId());
		assertEquals(transaction, entry.getPaymentTransaction());
	}

	private OrderModel generateOrder()
	{
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("USD");
		final OrderModel order = new OrderModel();
		order.setCode("00000001");
		order.setTotalPrice(1.5);
		order.setCurrency(currency);
		order.setDate(new Date());
		order.setUser(userService.getCurrentUser());
		return order;
	}

	private AlipayPaymentTransactionModel generateAlipayPaymentTransaction()
	{
		final AlipayPaymentTransactionModel transaction = new AlipayPaymentTransactionModel();
		transaction.setPaymentUrl("testRequestUrl");
		transaction.setPayMethod(AlipayPayMethod.DIRECTPAY);
		transaction.setPaymentProvider(PaymentConstants.Basic.PAYMENT_PROVIDER);
		transaction.setAlipayCode("10000");
		transaction.setRequestId("000001");
		return transaction;
	}

	private AlipayPaymentTransactionEntryModel generateAlipayPaymentTransactionEntry(final PaymentTransactionType type,
			final TransactionStatus status, final String code)
	{
		final AlipayPaymentTransactionEntryModel entry = new AlipayPaymentTransactionEntryModel();
		entry.setType(type);
		entry.setTransactionStatus(status.name());
		entry.setCode(code);
		return entry;
	}

	private AlipayRefundRequestData genereateRefundRequestData()
	{
		final AlipayRefundRequestData alipayRefundRequestData = new AlipayRefundRequestData();
		alipayRefundRequestData.setBatchNo("201601011234");
		return alipayRefundRequestData;
	}

	private void addPaymentTransaction(final OrderModel orderModel, final PaymentTransactionType type,
			final TransactionStatus status)
	{
		final AlipayPaymentTransactionModel baseTransaction = generateAlipayPaymentTransaction();
		final SecureRandom rn = new SecureRandom();
		final AlipayPaymentTransactionEntryModel baseEntry = generateAlipayPaymentTransactionEntry(type, status,
				String.valueOf(paymentTransactionKeyGenerator.generate()) + rn.nextInt(1000));
		baseEntry.setPaymentTransaction(baseTransaction);
		final List<PaymentTransactionModel> transactions = new ArrayList<>();
		transactions.add(baseTransaction);
		orderModel.setPaymentTransactions(transactions);
	}
}
