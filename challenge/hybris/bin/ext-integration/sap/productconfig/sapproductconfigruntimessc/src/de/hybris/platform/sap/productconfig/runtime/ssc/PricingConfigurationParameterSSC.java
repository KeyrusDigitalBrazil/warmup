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
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;


/**
 * Retrieves hybris and SSC specific data relevant for the configuration and pricing engine.
 */
public interface PricingConfigurationParameterSSC extends PricingConfigurationParameter
{

	/**
	 * Retrieves the pricing procedure used for pricing.
	 *
	 * @return the pricing procedure
	 */
	String getPricingProcedure();

	/**
	 * Retrieves the target for the base price. This is the purpose assigned to the condition function relevant for
	 * determining the base price of configurable products
	 *
	 * @return the target for the base price
	 */
	String getTargetForBasePrice();

	/**
	 * Retrieves the target for the option price. This is the purpose assigned to the condition function relevant for
	 * determining the total of the price-relevant options selected.
	 *
	 * @return the target for the option price
	 */
	String getTargetForSelectedOptions();

}
