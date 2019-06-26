/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.cmsfacades.util.builder.CurrencyModelBuilder;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;


public class CurrencyModelMother extends AbstractModelMother<CurrencyModel>
{

	public static final String CODE_USD = "USD";
	private static final String SYMBOL_USD = "$";

	private CurrencyDao currencyDao;

	protected CurrencyModel defaultCurrency()
	{
		return CurrencyModelBuilder.aModel().withActive(Boolean.TRUE).build();
	}

	public CurrencyModel createUSDollar()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCurrencyDao().findCurrenciesByCode(CODE_USD),
				() -> CurrencyModelBuilder.fromModel(defaultCurrency()).withIsocode(CODE_USD).withSymbol(SYMBOL_USD).build());
	}

	public CurrencyDao getCurrencyDao()
	{
		return currencyDao;
	}

	public void setCurrencyDao(final CurrencyDao currencyDao)
	{
		this.currencyDao = currencyDao;
	}


}
