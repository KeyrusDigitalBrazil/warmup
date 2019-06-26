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
package de.hybris.platform.ordermanagementfacades.payment.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Ordermanagement populator for converting {@link PaymentTransactionEntryData}
 */
public class PaymentTransactionEntryReversePopulator
		implements Populator<PaymentTransactionEntryData, PaymentTransactionEntryModel>
{

	private CommonI18NService commonI18NService;

	@Override
	public void populate(final PaymentTransactionEntryData source, final PaymentTransactionEntryModel target)
	{
		if (source != null && target != null)
		{
			target.setAmount(source.getAmount());
			target.setCode(source.getCode());
			target.setCurrency(getCommonI18NService().getCurrency(source.getCurrencyIsocode()));
			target.setRequestId(source.getRequestId());
			target.setRequestToken(source.getRequestToken());
			target.setSubscriptionID(source.getSubscriptionID());
			target.setTime(source.getTime());
			target.setTransactionStatus(source.getTransactionStatus());
			target.setTransactionStatusDetails(source.getTransactionStatusDetails());
			target.setType(source.getType());
			target.setVersionID(source.getVersionID());
		}

	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
