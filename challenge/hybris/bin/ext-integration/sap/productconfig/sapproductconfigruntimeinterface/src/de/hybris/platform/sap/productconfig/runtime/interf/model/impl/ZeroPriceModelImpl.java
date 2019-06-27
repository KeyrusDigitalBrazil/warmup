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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;


/**
 * Immutable Object
 *
 */
public final class ZeroPriceModelImpl extends PriceModelImpl
{

	private static final String ZERO_PRICE_MODEL_IMPL_IS_IMMUTABLE = "ZeroPriceModelImpl is immutable";

	@Override
	public void setCurrency(final String currency)
	{
		throw new IllegalArgumentException(ZERO_PRICE_MODEL_IMPL_IS_IMMUTABLE);
	}

	@Override
	public void setPriceValue(final BigDecimal priceValue)
	{
		throw new IllegalArgumentException(ZERO_PRICE_MODEL_IMPL_IS_IMMUTABLE);
	}

	@Override
	public void setObsoletePriceValue(final BigDecimal priceValue)
	{
		throw new IllegalArgumentException(ZERO_PRICE_MODEL_IMPL_IS_IMMUTABLE);
	}

	@Override
	public String getCurrency()
	{
		return "";
	}

	@Override
	public BigDecimal getPriceValue()
	{
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getObsoletePriceValue()
	{
		return BigDecimal.ZERO;
	}

	@Override
	public PriceModel clone()
	{
		//We explicitly want the same instance when cloning this one, therefore hiding sonar check
		return this;
	}
}
