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
package de.hybris.platform.integration.cis.payment.strategies.impl;

import java.util.Optional;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;

import de.hybris.platform.core.Registry;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.service.CisClientPaymentService;


/**
 * Strategy for retrieving the post URL for payment forms using CIS web service.
 */
public class CisPaymentFormActionUrlStrategy implements PaymentFormActionUrlStrategy
{
	private CisClientPaymentService cisClientPaymentService;
	private PaymentFormActionUrlStrategy paymentFormActionUrlStrategy;

	@Override
	public String getHopRequestUrl()
	{
		return paymentFormActionUrlStrategy.getHopRequestUrl();
	}

	@Override
	public String getSopRequestUrl(final String clientRef)
	{
		Optional<String> url = getCisClientPaymentService().pspUrl(clientRef, Registry.getCurrentTenant().getTenantID()).header("location");
		return url.isPresent() ? url.get() : null;
	}

	protected CisClientPaymentService getCisClientPaymentService()
	{
		return cisClientPaymentService;
	}

	@Required
	public void setCisClientPaymentService(final CisClientPaymentService cisClientPaymentService)
	{
		this.cisClientPaymentService = cisClientPaymentService;
	}

	protected PaymentFormActionUrlStrategy getPaymentFormActionUrlStrategy()
	{
		return paymentFormActionUrlStrategy;
	}

	@Required
	public void setPaymentFormActionUrlStrategy(final PaymentFormActionUrlStrategy paymentFormActionUrlStrategy)
	{
		this.paymentFormActionUrlStrategy = paymentFormActionUrlStrategy;
	}
}
