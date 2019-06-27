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
package com.hybris.cis.client.tax.util;

import java.math.BigDecimal;
import java.math.MathContext;


/**
 * A simple tool for calculating tax values based on a fixed percentage.
 * 
 */
public class TaxPercentageCalculator
{
	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

	private BigDecimal netValue;

	private BigDecimal grossValue;

	private BigDecimal taxValue;

	private BigDecimal taxPercentage;

	private MathContext maCtx = MathContext.DECIMAL128;

	private Integer roundToScale = new Integer(2);

	public TaxPercentageCalculator(final BigDecimal taxPercentage)
	{
		if (taxPercentage == null)
		{
			throw new IllegalArgumentException("vat percentage must not be null!");
		}
		this.taxPercentage = taxPercentage;
	}

	public TaxPercentageCalculator(final BigDecimal netValue, final BigDecimal grossValue)
	{
		if (netValue == null)
		{
			throw new IllegalArgumentException("net value must not be null!");
		}
		if (grossValue == null)
		{
			throw new IllegalArgumentException("gross value must not be null!");
		}

		this.netValue = netValue;
		this.grossValue = grossValue;
	}


	/**
	 * Calculates {@link #netValue} or {@link #grossValue} depending whether one of these values is null.
	 */
	@SuppressWarnings("boxing")
	public TaxPercentageCalculator calculate()
	{
		if (taxPercentage == null)
		{
			taxPercentage = getTaxPercentage(grossValue, netValue);
			if (roundToScale != null)
			{
				taxPercentage = taxPercentage.setScale(roundToScale.intValue(), BigDecimal.ROUND_HALF_EVEN);
			}
			taxValue = grossValue.subtract(netValue);
		}
		else
		{

			if (grossValue != null)
			{
				if (taxPercentage.compareTo(BigDecimal.ZERO) <= 0)
				{
					netValue = grossValue;
					taxValue = BigDecimal.ZERO;
				}
				else
				{

					// 100 / ( 100 + [VAT Rate] ) * [Final Price] = [Pre-VAT Price]
					final BigDecimal val1 = taxPercentage.add(ONE_HUNDRED);
					final BigDecimal val2 = ONE_HUNDRED.divide(val1, maCtx);
					netValue = val2.multiply(grossValue);
					taxValue = grossValue.subtract(netValue);
				}

				// round if preferred
				if (roundToScale != null)
				{
					netValue = netValue.setScale(roundToScale.intValue(), BigDecimal.ROUND_HALF_EVEN);
					taxValue = taxValue.setScale(roundToScale.intValue(), BigDecimal.ROUND_HALF_EVEN);
				}
			}
			else if (netValue != null)
			{
				if (taxPercentage.compareTo(BigDecimal.ZERO) <= 0)
				{
					grossValue = netValue;
					taxValue = BigDecimal.ZERO;
				}
				else
				{
					// ( ( [VAT Rate] + 100 ) / 100 ) * [Original Price] = [Total Price Including VAT]
					BigDecimal val = taxPercentage.add(ONE_HUNDRED);
					val = val.divide(ONE_HUNDRED, maCtx);
					grossValue = val.multiply(netValue);
					taxValue = grossValue.subtract(netValue);
				}

				// round if preferred
				if (roundToScale != null)
				{
					grossValue = grossValue.setScale(roundToScale.intValue(), BigDecimal.ROUND_HALF_EVEN);
					taxValue = taxValue.setScale(roundToScale.intValue(), BigDecimal.ROUND_HALF_EVEN);
				}
			}
		}

		return this;
	}

	/**
	 * Returns {@link BigDecimal#ZERO} if <code>gross</code> is less than or equal to zero or null or if <code>net</code>
	 * is is less than or
	 * equal to zero or null. In all other cases ((<code>net</code> divided by <code>gross</code>) multiplied by 100) is
	 * returned.
	 * 
	 * @param total
	 * @return The tax percentage
	 */
	protected BigDecimal getTaxPercentage(final BigDecimal total, final BigDecimal part)
	{
		if ((total == null) || (BigDecimal.ZERO.compareTo(total) != -1))
		{
			return BigDecimal.ZERO;
		}

		if ((part == null) || (BigDecimal.ZERO.compareTo(part) != -1))
		{
			return BigDecimal.ZERO;
		}

		BigDecimal percentage = BigDecimal.ZERO;
		if (BigDecimal.ZERO.compareTo(total) == -1 && BigDecimal.ZERO.compareTo(part) == -1)
		{
			percentage = total.divide(part, maCtx).multiply(ONE_HUNDRED).subtract(ONE_HUNDRED);

			if (roundToScale != null)
			{
				percentage = percentage.setScale(roundToScale.intValue(), BigDecimal.ROUND_HALF_EVEN);
			}
		}

		return percentage;
	}

	public BigDecimal getNetValue()
	{
		return netValue;
	}

	public TaxPercentageCalculator setNetValue(final BigDecimal netValue)
	{
		this.netValue = netValue;
		return this;
	}

	public BigDecimal getGrossValue()
	{
		return grossValue;
	}

	public TaxPercentageCalculator setGrossValue(final BigDecimal grossValue)
	{
		this.grossValue = grossValue;
		return this;
	}

	public BigDecimal getTaxValue()
	{
		return taxValue;
	}

	public void setTaxValue(final BigDecimal vatValue)
	{
		this.taxValue = vatValue;
	}

	public BigDecimal getTaxPercentage()
	{
		return taxPercentage;
	}

	public void setTaxPercentage(final BigDecimal taxPercentage)
	{
		this.taxPercentage = taxPercentage;
	}

	public MathContext getMaCtx()
	{
		return maCtx;
	}

	public void setMaCtx(final MathContext maCtx)
	{
		this.maCtx = maCtx;
	}

	public Integer getRoundToScale()
	{
		return roundToScale;
	}

	public TaxPercentageCalculator setRoundToScale(final Integer roundToScale)
	{
		this.roundToScale = roundToScale;
		return this;
	}
}
