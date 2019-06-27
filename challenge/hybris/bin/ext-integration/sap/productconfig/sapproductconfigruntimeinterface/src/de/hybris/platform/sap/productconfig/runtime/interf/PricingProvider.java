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

import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;


/**
 * Provides access to configuration pricing information from the configuration in an asynchronous manner in relation to
 * the configuration process.
 */
public interface PricingProvider
{
	/**
	 * Retrieves if present current total price, base price and selected options price
	 *
	 * @param configId
	 *           runtime id of the configuration
	 * @return map of current total price, base price and selected options price
	 * @throws PricingEngineException
	 * @deprecated since 18.11.0 - use {@link #getPriceSummary(String, ConfigurationRetrievalOptions)}
	 */
	@Deprecated
	PriceSummaryModel getPriceSummary(final String configId) throws PricingEngineException;

	/**
	 * Retrieves if present current total price, base price and selected options price
	 *
	 * @param configId
	 *           runtime id of the configuration
	 * @param options
	 *           configuration retrieval options
	 * @return map of current total price, base price and selected options price
	 * @throws PricingEngineException
	 * @throws ConfigurationEngineException
	 */
	PriceSummaryModel getPriceSummary(final String configId, ConfigurationRetrievalOptions options)
			throws PricingEngineException, ConfigurationEngineException;

	/**
	 * Fills absolute value prices or delta prices for all passed PriceValueUpdateModels dependent on setting in
	 * backoffice
	 *
	 * @param updateModels
	 *           each list entry represents a characteristic for which absolute value prices or delta prices are filled
	 * @param kbId
	 *           KnowledgeBase id belonging to the runtime configuration for which absolute value prices or delta price
	 *           are calculated
	 * @throws PricingEngineException
	 */
	void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId) throws PricingEngineException;

	/**
	 * Fills value prices into the config model
	 *
	 * @param configModel
	 *           runtime representation of the configuration
	 * @throws PricingEngineException
	 */
	void fillValuePrices(final ConfigModel configModel) throws PricingEngineException;

	/**
	 * Fills absolute value prices or delta prices for all passed PriceValueUpdateModels dependent on setting in
	 * backoffice
	 *
	 * @param updateModels
	 *           each list entry represents a characteristic for which absolute value prices or delta prices are filled
	 * @param kbId
	 *           KnowledgeBase id belonging to the runtime configuration for which absolute value prices or delta price
	 *           are calculated
	 * @param options
	 *           options to consider during price retrival/calculation, such as external requested conditions
	 * @throws PricingEngineException
	 */
	default void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId,
			final ConfigurationRetrievalOptions options) throws PricingEngineException
	{
		fillValuePrices(updateModels, kbId);
	}

	/**
	 * Fills value prices into the config model
	 *
	 * @param configModel
	 *           runtime representation of the configuration
	 * @param options
	 *           options to consider during price retrival/calculation, such as external requested conditions
	 * @throws PricingEngineException
	 */
	default void fillValuePrices(final ConfigModel configModel, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		fillValuePrices(configModel);
	}

	/**
	 * Indicates whether the pricing provider is active
	 *
	 * @return true if pricing provider is active in the implementation
	 */
	boolean isActive();
}
