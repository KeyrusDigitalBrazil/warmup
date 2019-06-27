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
package de.hybris.platform.sap.productconfig.services.cache;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.Map;


/**
 * Accessing the cache to set, read and remove the cached data
 */
public interface ProductConfigurationCacheAccessService
{

	/**
	 * Sets analytic data into the cached map
	 *
	 * @param configId
	 *           id of the configuration
	 * @param analyticsDocument
	 *           analytics document to be stored
	 */
	void setAnalyticData(String configId, AnalyticsDocument analyticsDocument);

	/**
	 * Retrieves analytic data from the cached map
	 *
	 * @param configId
	 *           id of the configuration
	 * @return anlytics document
	 */
	AnalyticsDocument getAnalyticData(String configId);

	/**
	 * Retrieves the price summary for a given runtime configuration, specified via its runtime id
	 *
	 * @param configId
	 *           id of the configuration
	 * @return price summary model
	 */
	PriceSummaryModel getPriceSummaryState(String configId);

	/**
	 * Puts the given price summary model into the price summary model state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param priceSummaryModel
	 *           model to cache
	 */
	void setPriceSummaryState(String configId, PriceSummaryModel priceSummaryModel);

	/**
	 * Retrieves the configuration model engine state
	 *
	 * @param configId
	 *           id of the configuration
	 * @return Configuration model
	 */
	ConfigModel getConfigurationModelEngineState(String configId);

	/**
	 * Puts the given config model into the engine state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param configModel
	 *           model to cache
	 */
	void setConfigurationModelEngineState(String configId, ConfigModel configModel);


	/**
	 * Removes cached config, prices and analytics data from caches
	 *
	 * @param configId
	 *           associated configuration runtime id
	 */
	void removeConfigAttributeState(final String configId);


	/**
	 * Retrieves a map of names from the Hybris classification system
	 *
	 * @param productCode The productCode for the classification system to retrieve
	 * @return Map of names from the Hybris classification system
	 */
	Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(String productCode);

}
