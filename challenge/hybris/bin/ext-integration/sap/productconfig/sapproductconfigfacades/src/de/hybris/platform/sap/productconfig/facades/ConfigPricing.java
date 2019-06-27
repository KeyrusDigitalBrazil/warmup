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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.impl.NoConfigPrice;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;


/**
 * Factory for pricing data for product configuration.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface ConfigPricing
{
	/**
	 * Value-Object to model the case, when no price information is available
	 */
	PriceData NO_PRICE = new NoConfigPrice();

	/**
	 * Factory method to extract pricing data from the given product configuration model
	 *
	 * @param model
	 *           product configuration model
	 * @return pricing data
	 */
	PricingData getPricingData(ConfigModel model);

	/**
	 * @param priceModel
	 * @return price DTO
	 */
	PriceData getPriceData(PriceModel priceModel);

	/**
	 * @param priceModel
	 * @return price DTO
	 */
	PriceData getObsoletePriceData(PriceModel priceModel);

	/**
	 * @param priceDataFactory
	 */
	void setPriceDataFactory(PriceDataFactory priceDataFactory);
}
