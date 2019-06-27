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
package de.hybris.platform.b2bacceleratorservices.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.impl.DefaultGenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * JUnit test suite for {@link DefaultB2BCommerceCheckoutService}
 */
@UnitTest
public class DefaultB2BCommerceCheckoutServiceTest
{
	public static final String TEST_CUSTOMER_UID = "TestCustomerUID";
	public static final double totalAmount = 99.99d;
	private DefaultB2BCommerceCheckoutService commerceCheckoutService;
	private CartModel cartModel;
	private CustomerModel customerModel;
	private InvoicePaymentInfoModel invoicePaymentInfoModel;
	private CurrencyModel currencyModel;
	private PaymentTransactionEntryModel transactionEntryModel;
	private PaymentTransactionModel paymentTransactionModel;

	@Mock
	private ModelService modelService;
	@Mock
	private PaymentService paymentService;
	@Mock
	private Configuration configuration;


	@InjectMocks
	@Spy
	private final DefaultGenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy = new DefaultGenerateMerchantTransactionCodeStrategy(); // The variable is used in the test.


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		commerceCheckoutService = new DefaultB2BCommerceCheckoutService();
		commerceCheckoutService.setModelService(modelService);
		commerceCheckoutService.setPaymentService(paymentService);
		commerceCheckoutService.setGenerateMerchantTransactionCodeStrategy(generateMerchantTransactionCodeStrategy);

		cartModel = mock(CartModel.class);
		customerModel = mock(CustomerModel.class);
		invoicePaymentInfoModel = mock(InvoicePaymentInfoModel.class);
		currencyModel = mock(CurrencyModel.class);
		transactionEntryModel = new PaymentTransactionEntryModel();
		paymentTransactionModel = new PaymentTransactionModel();

		given(cartModel.getUser()).willReturn(customerModel);
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(customerModel.getUid()).willReturn(TEST_CUSTOMER_UID);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(totalAmount));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAuthorizeInvoicePaymentNullCart()
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(null);

		commerceCheckoutService.authorizePayment(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAuthorizeInvoicePaymentNullPaymentInfo()
	{
		given(cartModel.getPaymentInfo()).willReturn(null);
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		commerceCheckoutService.authorizePayment(parameter);
	}

	@Test
	public void shouldAuthorizeInvoicePayment()
	{
		given(cartModel.getPaymentInfo()).willReturn(invoicePaymentInfoModel);
		given(cartModel.getCalculated()).willReturn(Boolean.TRUE);
		given(modelService.create(PaymentTransactionModel.class)).willReturn(paymentTransactionModel);
		given(modelService.create(PaymentTransactionEntryModel.class)).willReturn(transactionEntryModel);

		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		final PaymentTransactionEntryModel authResultEntry = commerceCheckoutService.authorizePayment(parameter);

		Assert.assertNotNull(authResultEntry);
		final BigDecimal amount = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_EVEN);
		Assert.assertEquals(amount, authResultEntry.getAmount());
		Assert.assertEquals(currencyModel, authResultEntry.getCurrency());
		Assert.assertEquals(PaymentTransactionType.AUTHORIZATION, authResultEntry.getType());
		Assert.assertNotNull(authResultEntry.getTime());
		Assert.assertEquals(TransactionStatus.ACCEPTED.name(), authResultEntry.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL.toString(), authResultEntry.getTransactionStatusDetails());
		final PaymentTransactionModel authResultTransaction = authResultEntry.getPaymentTransaction();
		Assert.assertEquals(paymentTransactionModel, authResultTransaction);
		Assert.assertNotNull(authResultTransaction.getCode());
		Assert.assertEquals(amount, authResultTransaction.getPlannedAmount());
		Assert.assertEquals(cartModel, authResultTransaction.getOrder());
		Assert.assertEquals(invoicePaymentInfoModel, authResultTransaction.getInfo());
		verify(modelService, times(1)).saveAll(cartModel, paymentTransactionModel, transactionEntryModel);
	}
}
