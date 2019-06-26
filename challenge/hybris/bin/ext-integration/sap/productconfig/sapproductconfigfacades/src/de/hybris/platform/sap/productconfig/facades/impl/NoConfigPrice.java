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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;

import java.math.BigDecimal;


/**
 * Immutable sub-class of the the {@link PriceData} class.<br>
 * It models the case, when there is not price data avaiavble.
 */
public class NoConfigPrice extends PriceData
{
	private static final String EMPTY = "";
	private static final String NO_VALUE = "-";
	private static final String CLASS_IS_IMMUTABLE_MSG = "NoConfigPrice is immutable";

	@Override
	public void setCurrencyIso(final String currencyIso)
	{
		throw new IllegalArgumentException(CLASS_IS_IMMUTABLE_MSG);
	}

	@Override
	public String getCurrencyIso()
	{
		return EMPTY;
	}

	@Override
	public void setPriceType(final PriceDataType priceType)
	{
		throw new IllegalArgumentException(CLASS_IS_IMMUTABLE_MSG);
	}

	@Override
	public PriceDataType getPriceType()
	{
		return PriceDataType.BUY;
	}

	@Override
	public void setValue(final BigDecimal value)
	{
		throw new IllegalArgumentException(CLASS_IS_IMMUTABLE_MSG);
	}

	@Override
	public BigDecimal getValue()
	{
		return BigDecimal.ZERO;
	}

	@Override
	public void setMaxQuantity(final Long maxQuantity)
	{
		throw new IllegalArgumentException(CLASS_IS_IMMUTABLE_MSG);
	}

	@Override
	public Long getMaxQuantity()
	{
		return Long.valueOf(0);
	}

	@Override
	public void setMinQuantity(final Long minQuantity)
	{
		throw new IllegalArgumentException(CLASS_IS_IMMUTABLE_MSG);
	}

	@Override
	public Long getMinQuantity()
	{
		return Long.valueOf(0);
	}

	@Override
	public void setFormattedValue(final String formattedValue)
	{
		throw new IllegalArgumentException(CLASS_IS_IMMUTABLE_MSG);
	}

	@Override
	public String getFormattedValue()
	{
		return NO_VALUE;
	}


}
