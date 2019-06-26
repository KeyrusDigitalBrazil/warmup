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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;


/**
 * Populates a price summary
 *
 */
public class PriceSummaryPopulator implements Populator<PriceSummaryModel, PricingData>
{
	private ConfigPricing configPricing;

	@Override
	public void populate(final PriceSummaryModel source, final PricingData target)
	{
		fillPricingData(source, target);
	}


	/**
	 * @param source
	 * @param target
	 */
	protected void fillPricingData(final PriceSummaryModel source, final PricingData target)
	{
		if (source != null)
		{
			target.setBasePrice(getPriceData(source.getBasePrice()));
			target.setCurrentTotal(getPriceData(source.getCurrentTotalPrice()));
			target.setCurrentTotalSavings(getPriceData(source.getCurrentTotalSavings()));
			target.setSelectedOptions(getPriceData(source.getSelectedOptionsPrice()));
		}
	}


	/**
	 * @param priceModel
	 */
	protected PriceData getPriceData(final PriceModel priceModel)
	{
		return configPricing.getPriceData(priceModel);
	}



	/**
	 * @return the configPricing
	 */
	public ConfigPricing getConfigPricing()
	{
		return configPricing;
	}


	/**
	 * @param configPricing
	 *           the configPricing to set
	 */
	public void setConfigPricing(final ConfigPricing configPricing)
	{
		this.configPricing = configPricing;
	}

}
