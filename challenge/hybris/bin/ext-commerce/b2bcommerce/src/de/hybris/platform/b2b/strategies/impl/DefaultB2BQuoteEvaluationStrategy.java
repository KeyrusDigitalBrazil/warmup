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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BQuoteLimitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.QuoteEvaluationStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;


/**
 * @deprecated Since 6.3.
 */
@Deprecated
public class DefaultB2BQuoteEvaluationStrategy implements QuoteEvaluationStrategy
{
	private String quoteLimitCurrency;
	private BigDecimal quoteLimit;

	private CommonI18NService commonI18NService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private B2BCurrencyConversionService b2bCurrencyConversionService;


	@Override
	public boolean isQuoteAllowed(final AbstractOrderModel source)
	{
		BigDecimal applicableQuoteLimitAmount = null;
		CurrencyModel applicableQuoteLimitCurrency = null;

		final B2BQuoteLimitModel quoteLimitAssignedToUnit = findQuoteLimitAssignedToUnit(source.getUnit());

		if (quoteLimitAssignedToUnit == null)
		{
			applicableQuoteLimitAmount = quoteLimit;
			applicableQuoteLimitCurrency = getCommonI18NService().getCurrency(quoteLimitCurrency);
		}
		else
		{
			applicableQuoteLimitAmount = quoteLimitAssignedToUnit.getAmount();
			applicableQuoteLimitCurrency = quoteLimitAssignedToUnit.getCurrency();
		}

		final BigDecimal orderTotalConvertedToLimitCurrency = BigDecimal.valueOf(getB2bCurrencyConversionService().convertAmount(
				source.getTotalPrice(), source.getCurrency(), applicableQuoteLimitCurrency));

		return orderTotalConvertedToLimitCurrency.compareTo(applicableQuoteLimitAmount) >= 0;
	}

	protected B2BQuoteLimitModel findQuoteLimitAssignedToUnit(final B2BUnitModel unit)
	{
		B2BUnitModel currentUnit = unit;
		B2BQuoteLimitModel quoteLimit = null;

		while (currentUnit != null)
		{
			quoteLimit = currentUnit.getQuoteLimit();
			if (quoteLimit != null)
			{
				break;
			}
			currentUnit = getB2bUnitService().getParent(currentUnit);
		}
		return quoteLimit;

	}

	/**
	 * @param b2bCurrencyConversionService
	 *           the b2bCurrencyConversionService to set
	 */
	@Required
	public void setB2bCurrencyConversionService(final B2BCurrencyConversionService b2bCurrencyConversionService)
	{
		this.b2bCurrencyConversionService = b2bCurrencyConversionService;
	}


	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @return the b2bCurrencyConversionService
	 */
	protected B2BCurrencyConversionService getB2bCurrencyConversionService()
	{
		return b2bCurrencyConversionService;
	}


	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	/**
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}



	public String getQuoteLimitCurrency()
	{
		return quoteLimitCurrency;
	}

	/**
	 * The ISOCode of the currency of the quoteLimit against which orders are calculated.
	 */
	@Required
	public void setQuoteLimitCurrency(final String quoteLimitCurrency)
	{
		this.quoteLimitCurrency = quoteLimitCurrency;
	}

	public BigDecimal getQuoteLimit()
	{
		return quoteLimit;
	}

	/**
	 * Given orders placed above this value in the given currency, Users shall be able to request quotes
	 */
	@Required
	public void setQuoteLimit(final BigDecimal quoteLimit)
	{
		this.quoteLimit = quoteLimit;
	}
}
