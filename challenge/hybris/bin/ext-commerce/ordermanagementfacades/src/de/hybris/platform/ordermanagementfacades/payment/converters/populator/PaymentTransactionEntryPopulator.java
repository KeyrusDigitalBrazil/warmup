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
package de.hybris.platform.ordermanagementfacades.payment.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Ordermanagement populator for converting {@link PaymentTransactionEntryModel}
 */
public class PaymentTransactionEntryPopulator implements Populator<PaymentTransactionEntryModel, PaymentTransactionEntryData>
{
	@Override
	public void populate(final PaymentTransactionEntryModel source, final PaymentTransactionEntryData target)
			throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setAmount(source.getAmount());
			target.setCurrencyIsocode(source.getCurrency().getIsocode());
			target.setTime(source.getTime());
			target.setTransactionStatus(source.getTransactionStatus());
			target.setTransactionStatusDetails(source.getTransactionStatusDetails());
			target.setType(source.getType());
		}
	}
}
