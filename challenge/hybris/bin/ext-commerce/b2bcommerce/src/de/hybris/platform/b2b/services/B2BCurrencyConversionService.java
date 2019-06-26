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
package de.hybris.platform.b2b.services;

import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.Locale;


/**
 * A service around {@link CurrencyModel}
 * 
 * @spring.bean b2bCurrencyConversionService
 */
public interface B2BCurrencyConversionService
{

	/**
	 * Used to convert amount from one currency to another
	 * 
	 * @param amount
	 *           the dollar amount to convert
	 * @param sourceCurrency
	 *           the current currency type
	 * @param targetCurrency
	 *           the currency in which to convert to
	 * @return converted amount
	 */
	public Double convertAmount(Double amount, CurrencyModel sourceCurrency, CurrencyModel targetCurrency);

	/**
	 * Formats the currency amount for the correct locale
	 * 
	 * @param locale
	 *           the local in which to format for
	 * @param currency
	 *           the currency type
	 * @param amount
	 *           the dollar amount
	 * @return the String representation of the currency correctly formatted
	 */
	public String formatCurrencyAmount(Locale locale, CurrencyModel currency, double amount);

}
