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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementaion of the {@link ConfigPricing}.
 */
public class ConfigPricingImpl implements ConfigPricing
{
	private PriceDataFactory priceDataFactory;

	/**
	 * @param priceDataFactory
	 *           injects the hybris pricing data factory
	 */
	@Override
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	@Override
	public PricingData getPricingData(final ConfigModel model)
	{
		final PricingData pricingData = new PricingData();

		final PriceData basePrice;
		final PriceData selectedOptions;
		final PriceData currentTotalSavings;
		final PriceData currentTotal;

		final PriceModel basePriceModel = model.getBasePrice();
		basePrice = getPriceData(basePriceModel);

		final PriceModel selectedOptionsPriceModel = model.getSelectedOptionsPrice();
		selectedOptions = getPriceData(selectedOptionsPriceModel);

		final PriceModel currentTotalPriceModel = model.getCurrentTotalPrice();
		currentTotal = getPriceData(currentTotalPriceModel);

		final PriceModel currentTotalSavingsModel = model.getCurrentTotalSavings();
		currentTotalSavings = getPriceData(currentTotalSavingsModel);

		pricingData.setBasePrice(basePrice);
		pricingData.setSelectedOptions(selectedOptions);
		pricingData.setCurrentTotal(currentTotal);
		pricingData.setCurrentTotalSavings(currentTotalSavings);

		return pricingData;
	}

	@Override
	public PriceData getPriceData(final PriceModel priceModel)
	{
		final PriceData priceData;
		if (priceModel == null || !priceModel.hasValidPrice())
		{
			priceData = ConfigPricing.NO_PRICE;
		}
		else
		{
			priceData = priceDataFactory.create(PriceDataType.BUY, priceModel.getPriceValue(), priceModel.getCurrency());
		}
		return priceData;
	}

	@Override
	public PriceData getObsoletePriceData(final PriceModel priceModel)
	{
		final PriceData priceData;
		if (priceModel == null || !priceModel.hasValidPrice() || priceModel.getObsoletePriceValue() == null)
		{
			priceData = null;
		}
		else
		{
			priceData = priceDataFactory.create(PriceDataType.BUY, priceModel.getObsoletePriceValue(), priceModel.getCurrency());
		}
		return priceData;
	}
}
