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
package de.hybris.platform.chinesepaymentservices.checkout.strategies.impl;

import de.hybris.platform.chinesepaymentservices.checkout.strategies.ChinesePaymentServicesStrategy;
import de.hybris.platform.chinesepaymentservices.payment.ChinesePaymentService;
import de.hybris.platform.core.Registry;

import org.springframework.context.ApplicationContext;


public class DefaultChinesePaymentServicesStrategy implements ChinesePaymentServicesStrategy
{
	protected ApplicationContext getApplicationContext()
	{
		return Registry.getApplicationContext();
	}

	@Override
	public ChinesePaymentService getPaymentService(final String paymentService)
	{
		return (ChinesePaymentService) getApplicationContext().getBean(paymentService);
	}

}
