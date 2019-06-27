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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

/**
 * Container for the different top-level prices of the configuration
 */
public class PriceSummaryModel
{
	private PriceModel currentTotalPrice;
	private PriceModel currentTotalSavings;
	private PriceModel basePrice;
	private PriceModel selectedOptionsPrice;

	/**
	 * @return total price of the configurable product
	 */
	public PriceModel getCurrentTotalPrice()
	{
		return currentTotalPrice;
	}

	/**
	 * @param currentTotalPrice
	 *           total price of the configurable product
	 */
	public void setCurrentTotalPrice(final PriceModel currentTotalPrice)
	{
		this.currentTotalPrice = currentTotalPrice;
	}

	/**
	 * @return base price of the configutable product, so excluding any surcharges for selected options.
	 */
	public PriceModel getBasePrice()
	{
		return basePrice;
	}

	/**
	 * @param basePrice
	 *           base price of the configutable product, so excluding any surcharges for selected options.
	 */
	public void setBasePrice(final PriceModel basePrice)
	{
		this.basePrice = basePrice;
	}

	/**
	 * @return options price, sum of all surcharges for selected options.
	 */
	public PriceModel getSelectedOptionsPrice()
	{
		return selectedOptionsPrice;
	}

	/**
	 * @param selectedOptionsPrice
	 *           options price, sum of all surcharges for selected options.
	 */
	public void setSelectedOptionsPrice(final PriceModel selectedOptionsPrice)
	{
		this.selectedOptionsPrice = selectedOptionsPrice;
	}

	/**
	 * @return all savaings cummulated
	 */
	public PriceModel getCurrentTotalSavings()
	{
		return currentTotalSavings;
	}

	/**
	 * @param currentTotalSavings
	 */
	public void setCurrentTotalSavings(final PriceModel currentTotalSavings)
	{
		this.currentTotalSavings = currentTotalSavings;
	}
}
