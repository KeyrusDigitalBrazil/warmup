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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.warehousing.util.builder.CurrencyModelBuilder;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;


public class Currencies extends AbstractItems<CurrencyModel>
{
	public static final String ISOCODE_USD = "USD";
	public static final String SYMBOL_DOLLAR = "$";

	public static final String ISOCODE_EUR = "EUR";
	public static final String SYMBOL_EURO = "€";

	private CurrencyDao currencyDao;

	public CurrencyModel AmericanDollar()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCurrencyDao().findCurrenciesByCode(ISOCODE_USD), 
				() -> CurrencyModelBuilder.aModel() 
						.withIsoCode(ISOCODE_USD) 
						.withName("US Dollar", Locale.ENGLISH) 
						.withActive(Boolean.TRUE) 
						.withConversion(Double.valueOf(1)) 
						.withDigits(Integer.valueOf(2)) 
						.withSymbol(SYMBOL_DOLLAR) 
						.build());
	}

	public CurrencyModel Euro()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCurrencyDao().findCurrenciesByCode(ISOCODE_EUR),
				() -> CurrencyModelBuilder.aModel()
						.withIsoCode(ISOCODE_EUR)
						.withName("Euro", Locale.ENGLISH)
						.withActive(Boolean.TRUE)
						.withConversion(Double.valueOf(1))
						.withDigits(Integer.valueOf(2))
						.withSymbol(SYMBOL_EURO)
						.build());
	}

	public CurrencyDao getCurrencyDao()
	{
		return currencyDao;
	}

	@Required
	public void setCurrencyDao(final CurrencyDao currencyDao)
	{
		this.currencyDao = currencyDao;
	}
}
