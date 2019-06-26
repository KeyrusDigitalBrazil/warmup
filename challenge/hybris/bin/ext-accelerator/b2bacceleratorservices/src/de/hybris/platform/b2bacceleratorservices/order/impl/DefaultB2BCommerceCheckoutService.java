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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;


/**
 * Service for b2b checkout and place order functionality
 */
public class DefaultB2BCommerceCheckoutService extends DefaultCommerceCheckoutService
{
	private GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy;

	/**
	 * Authorizes the total amount of the cart
	 *
	 * @param parameter    A parameter object holding the cart, security code, payment provider and optionaly authorization amount.
	 * @return the payment A payment transaction entry
	 */
	@Override
	public PaymentTransactionEntryModel authorizePayment(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(cartModel.getPaymentInfo(), "Payment information on cart cannot be null");

		if (cartModel.getPaymentInfo() instanceof InvoicePaymentInfoModel)
		{
			// create payment transaction
			final PaymentTransactionModel transaction = getModelService().create(PaymentTransactionModel.class);
			final BigDecimal amount = calculateAuthAmount(cartModel);
			transaction.setCode(getGenerateMerchantTransactionCodeStrategy().generateCode(cartModel));
			transaction.setPlannedAmount(amount);
			transaction.setOrder(cartModel);
			transaction.setInfo(cartModel.getPaymentInfo());
			final PaymentTransactionType paymentTransactionType = PaymentTransactionType.AUTHORIZATION;
			final String newEntryCode = getPaymentService().getNewPaymentTransactionEntryCode(transaction, paymentTransactionType);

			// create payment transaction entry. Always successful for account payment
			final PaymentTransactionEntryModel entry = getModelService().create(PaymentTransactionEntryModel.class);
			entry.setAmount(amount);
			entry.setCurrency(cartModel.getCurrency());
			entry.setType(PaymentTransactionType.AUTHORIZATION);
			entry.setTime(new Date());
			entry.setPaymentTransaction(transaction);
			entry.setTransactionStatus(TransactionStatus.ACCEPTED.toString());
			entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.toString());
			entry.setCode(newEntryCode);
			getModelService().saveAll(cartModel, transaction, entry);

			return entry;
		}
		return super.authorizePayment(parameter);
	}

	protected GenerateMerchantTransactionCodeStrategy getGenerateMerchantTransactionCodeStrategy()
	{
		return generateMerchantTransactionCodeStrategy;
	}

	@Required
	public void setGenerateMerchantTransactionCodeStrategy(
			final GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy)
	{
		this.generateMerchantTransactionCodeStrategy = generateMerchantTransactionCodeStrategy;
	}
}
