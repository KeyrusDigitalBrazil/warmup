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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;


/**
 * Retrieves hybris data relevant for the configuration and pricing engine.
 */
public interface PricingConfigurationParameter
{
	/**
	 * Retrieves the value of the flag for activating of pricing on the product configuration page. If inactive, no pricing
	 * information is shown.
	 *
	 * @return the value of the flag for activating of pricing
	 */
	boolean isPricingSupported();

	/**
	 * Retrieves the sales organization.
	 *
	 * @return the sales organization
	 */
	String getSalesOrganization();

	/**
	 * Retrieves the distribution channel used for condition determination.
	 *
	 * @return the the distribution channel
	 */
	String getDistributionChannelForConditions();

	/**
	 * Retrieves the division used for condition determination.
	 *
	 * @return the the division
	 */
	String getDivisionForConditions();

	/**
	 * Retrieves the SAP currency code for a given <code>CurrencyModel</code>.
	 *
	 * @param currencyModel
	 *           currency model
	 * @return the SAP currency code
	 */
	String retrieveCurrencySapCode(CurrencyModel currencyModel);

	/**
	 * Retrieves the SAP unit of measure code for a given <code>UnitModel</code>.
	 *
	 * @param unitModel
	 *           unit model
	 * @return the SAP unit of measure code
	 */
	String retrieveUnitSapCode(UnitModel unitModel);

	/**
	 * Retrieves the value of the flag for activating of displaying of the base price and selected options on the product
	 * configuration page. If inactive, no base price and selected options are shown.
	 *
	 * @return the value of the flag for activating of displaying of the base price and selected options
	 */
	boolean showBasePriceAndSelectedOptions();

	/**
	 * Retrieves the value of the flag for activating of displaying of the delta prices for values. If inactive, absolute
	 * value prices are shown.
	 *
	 *
	 * @return the value of the flag for activating of displaying of the delta prices for values
	 */
	boolean showDeltaPrices();

	/**
	 * Retrieves the ISO currency code for a given <code>CurrencyModel</code>.
	 *
	 * @param currencyModel
	 *           currency model
	 * @return the ISO currency code
	 */
	String retrieveCurrencyIsoCode(CurrencyModel currencyModel);

	/**
	 * Retrieves the ISO unit of measure code for a given <code>UnitModel</code>.
	 *
	 * @param unitModel
	 *           unit model
	 * @return the ISO unit of measure code
	 */
	String retrieveUnitIsoCode(UnitModel unitModel);

}
