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

import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;

import java.math.BigDecimal;


/**
 * Default implementation of the {@link VariantConditionModel}
 */
public class VariantConditionModelImpl extends BaseModelImpl implements VariantConditionModel
{
	private String key;
	private BigDecimal factor;

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setKey(final String key)
	{
		this.key = key;
	}

	@Override
	public BigDecimal getFactor()
	{
		return factor;
	}

	@Override
	public void setFactor(final BigDecimal factor)
	{
		this.factor = factor;
	}

	@Override
	public String toString()
	{
		return "VariantConditionModelImpl [key=" + key + ", factor=" + factor + "]";
	}

	@Override
	public VariantConditionModel copy()
	{
		final VariantConditionModel copy = new VariantConditionModelImpl();
		copy.setKey(this.getKey());
		copy.setFactor(this.getFactor());
		return copy;
	}

}
