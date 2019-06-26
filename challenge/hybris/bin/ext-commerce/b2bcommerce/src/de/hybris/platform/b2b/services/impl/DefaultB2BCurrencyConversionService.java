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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link B2BCurrencyConversionService}
 * 
 * @spring.bean b2bCurrencyConversionService
 */
public class DefaultB2BCurrencyConversionService extends AbstractBusinessService implements B2BCurrencyConversionService
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BCurrencyConversionService.class);

	@Override
	public Double convertAmount(final Double amount, final CurrencyModel sourceCurrency, final CurrencyModel targetCurrency)
	{
		return Double.valueOf(amount.doubleValue() * targetCurrency.getConversion().doubleValue()
				/ sourceCurrency.getConversion().doubleValue());
	}

	@Override
	public String formatCurrencyAmount(final Locale locale, final CurrencyModel currency, final double amount)
	{
		if (currency != null && locale != null)
		{
			// Lookup the number formatter for the locale
			final NumberFormat localizedNumberFormat = NumberFormat.getCurrencyInstance(locale);

			// Lookup the java currency object for the currency code (must be ISO 4217)
			final String currencyIsoCode = currency.getIsocode();
			final java.util.Currency javaCurrency = java.util.Currency.getInstance(currencyIsoCode);

			try
			{
				localizedNumberFormat.setCurrency(javaCurrency);
			}
			catch (final IllegalArgumentException |  NullPointerException e)
			{
				if (LOG.isInfoEnabled())
				{
					LOG.info("formatCurrencyAmount failed to lookup java.util.Currency from [" + currencyIsoCode
							+ "] ensure this is an ISO 4217 code and is supported by the java runtime.", e);
				}
			}

			// Format the amount
			final String result = localizedNumberFormat.format(amount);

			// Print out debug to see what is happening on javelin's system
			if (LOG.isDebugEnabled())
			{
				LOG.debug("formatCurrencyAmount locale=[" + locale + "] currency=[" + currency + "] amount=[" + amount
						+ "] currencyIsoCode=[" + currencyIsoCode + "] javaCurrency=[" + javaCurrency + "] result=[" + result + "]");
			}

			return result;
		}
		return "";
	}
}
