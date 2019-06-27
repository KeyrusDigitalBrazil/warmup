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
package de.hybris.platform.sap.productconfig.services;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;

import java.util.Map;
import java.util.Set;


/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface SessionAccessService
{
	/**
	 * cache key of product configuration cache container
	 */
	String PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER = "productconfigSessionAttributeContainer";

	/**
	 * returns the unique session id
	 *
	 * @return session id
	 *
	 * @deprecated since 18.11.0 - call {@link SessionService#getCurrentSession()#getSessionId()}
	 */
	@Deprecated
	String getSessionId();

	/**
	 * Stores configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Retrieves config identifier from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ID of a runtime configuration object
	 */
	String getConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ui status for cart entry
	 */
	<T> T getUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param productKey
	 *           Product key
	 * @return ui status for product
	 */
	<T> T getUiStatusForProduct(String productKey);

	/**
	 * Stores object for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param uiStatus
	 *           ui status for cart entry
	 */
	void setUiStatusForCartEntry(String cartEntryKey, Object uiStatus);

	/**
	 * Stores object for a product key into the session
	 *
	 * @param productKey
	 *           Product key
	 * @param uiStatus
	 *           ui status for product
	 */
	void setUiStatusForProduct(String productKey, Object uiStatus);

	/**
	 * Removes object for a cart entry
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 */
	void removeUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Removes object for a product
	 *
	 * @param productKey
	 *           Product key
	 */
	void removeUiStatusForProduct(String productKey);



	/**
	 * Retrieves cart entry key belonging to a specific config ID
	 *
	 * @param configId
	 *           id of the configuration
	 * @return String representation of the cart entry primary key
	 */
	String getCartEntryForConfigId(String configId);

	/**
	 * Stores cart entry in session per product key
	 *
	 * @param productKey
	 *           product key
	 * @param cartEntryId
	 *           String representation of the cart entry primary key
	 * @deprecated since 18.08.0 - only link cart entries to configs and configs to products, no direct linking.
	 */
	@Deprecated
	void setCartEntryForProduct(String productKey, String cartEntryId);

	/**
	 * Retrieves cart entry key per product
	 *
	 * @param productKey
	 *           product key
	 * @return String representation of the cart entry primary key
	 *
	 * @deprecated since 18.08.0 - only link cart entries to configs and configs to products, no direct linking.
	 *
	 */
	@Deprecated
	String getCartEntryForProduct(String productKey);

	/**
	 * Removes cart entry key for product
	 *
	 * @param productKey
	 *           product key
	 * @deprecated since 18.08.0 - only link cart entries to configs and configs to products, no direct linking.
	 */
	@Deprecated
	void removeCartEntryForProduct(String productKey);

	/**
	 * Removes config ID for cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 */
	void removeConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Removes all session artifacts belonging to a cart entry
	 *
	 * @param cartEntryId
	 *           cart entry key
	 * @param productKey
	 *           product key
	 */
	void removeSessionArtifactsForCartEntry(String cartEntryId);

	/**
	 * @return Map of names from the hybris classification system
	 *
	 * @deprecated call {@link ProductConfigurationCacheAccessService#getCachedNameMap()} instead
	 * @since 18.11.0
	 */
	@Deprecated
	Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap();

	/**
	 * @return Set of indexed properties
	 * @deprecated since 18.11.0 - no longer cached
	 */
	@Deprecated
	Set<String> getSolrIndexedProperties();

	/**
	 * Stores set of indexed properties
	 *
	 * @param solrTypes
	 *           solr types to be stored
	 * @deprecated since 18.11.0 - no longer cached
	 */
	@Deprecated
	void setSolrIndexedProperties(Set<String> solrTypes);

	/**
	 * get the configuration provider for this session
	 *
	 * @return Configuration provider
	 * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	ConfigurationProvider getConfigurationProvider();

	/**
	 * cache the pricing provider in this session
	 *
	 * @param provider
	 *           provider to cache
	 * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	void setPricingProvider(PricingProvider provider);

	/**
	 * get the pricing provider for this session
	 *
	 * @return Configuration provider
	 * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	PricingProvider getPricingProvider();

	/**
	 * cache the pricing provider in this session
	 *
	 * @param provider
	 *           provider to cache
	 * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	void setConfigurationProvider(ConfigurationProvider provider);



	/**
	 * Retrieves the configuration model engine state
	 *
	 * @param configId
	 *           id of the configuration
	 * @return Configuration model
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#getConfigurationModelEngineState(String)} instead
	 */
	@Deprecated
	ConfigModel getConfigurationModelEngineState(String configId);

	/**
	 * Puts the given config model into the engine state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param configModel
	 *           model to cache
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#setConfigurationModelEngineState(String)} instead
	 */
	@Deprecated
	void setConfigurationModelEngineState(String configId, ConfigModel configModel);


	/**
	 * Clears the read cache for the configuration engine state and price summary states for the whole user session
	 *
	 * @deprecated since 18.11.0 - this method is obsolete because the key under which the configuration engine state and
	 *             price summary states have been saved consists of configuration id an user session id
	 */
	@Deprecated
	void removeConfigAttributeStates();

	/**
	 * Retrieves the price summary for a given runtime configuration, specified via its runtime id
	 *
	 * @param configId
	 *           id of the configuration
	 * @return price summary model
	 *
	 * @deprecated since 18.11.0 - call {@link ProductConfigurationCacheAccessService#getPriceSummaryState(String)}
	 *             instead
	 */
	@Deprecated
	PriceSummaryModel getPriceSummaryState(String configId);

	/**
	 * Puts the given price summary model into the price summary model state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param priceSummaryModel
	 *           model to cache
	 *
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#setPriceSummaryState(String, PriceSummaryModel )}
	 *             instead
	 */
	@Deprecated
	void setPriceSummaryState(String configId, PriceSummaryModel priceSummaryModel);

	/**
	 * Puts the given analytics provider into the cached
	 *
	 * @param analyticsProvider
	 * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	void setAnalyticsProvider(AnalyticsProvider analyticsProvider);

	/**
	 * Removes the given configuration engine state and price summary model from read cache for engine state
	 *
	 * @param configId
	 *           unique config id
	 * @deprecated since 18.11.0 - call {@link ProductConfigurationCacheAccessService#removeConfigAttributeState(String)}
	 *             instead
	 */
	@Deprecated
	void removeConfigAttributeState(String configId);

	/**
	 * Retrieves an analytics provider from the cached map
	 *
	 * @return return the cached analytics provider
	 * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	AnalyticsProvider getAnalyticsProvider();

	/**
	 * Sets analytic data into the cached map
	 *
	 * @param configId
	 *           id of the configuration
	 * @param analyticsDocument
	 *           analytics document to be stored
	 *
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#setAnalyticData(String, AnalyticsDocument)} instead
	 */
	@Deprecated
	void setAnalyticData(String configId, AnalyticsDocument analyticsDocument);

	/**
	 * Retrieves analytic data from the cached map
	 *
	 * @param configId
	 *           id of the configuration
	 * @return anlytics document
	 *
	 * @deprecated since 18.11.0 - call {@link ProductConfigurationCacheAccessService#getAnalyticData(String)} instead
	 */
	@Deprecated
	AnalyticsDocument getAnalyticData(String configId);

	/**
	 * Purges the entire session (with regards to CPQ artifacts)
	 */
	void purge();

	/**
	 * Get the runtime configuration currently associated with the given product
	 *
	 * @param productCode
	 *           code of product, for which the link to the runtime configuration should be returned
	 * @return runtime configuration id that is currently linked to the given product
	 */
	String getConfigIdForProduct(String productCode);

	/**
	 * Get the product for the given runtime configuration id
	 *
	 * @param configId
	 *           configuration id
	 * @return product code if link is present, otherwise null
	 */
	String getProductForConfigId(String configId);

	/**
	 * Links a product code with a given runtime configuration
	 *
	 * @param productCode
	 *           code of product, for which the link to the runtime configuration should be created
	 * @param configId
	 *           runtime configuration id
	 */
	void setConfigIdForProduct(String productCode, String configId);

	/**
	 * Removes the link between product code and runtime configuration
	 *
	 * @param productCode
	 *           code of product, for which the link to the runtime configuration should be deleted
	 */
	void removeConfigIdForProduct(String productCode);

	/**
	 * get cart entry linked to the given draft configuration
	 *
	 * @param configId
	 *           runtime configuration id
	 * @return cartItemHandle
	 */
	String getCartEntryForDraftConfigId(String configId);

	/**
	 * gets the config id linked as draft to the given cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 * @return config Id
	 */
	String getDraftConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Stores darfat configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setDraftConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Removes draft config ID for cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 */
	void removeDraftConfigIdForCartEntry(String cartEntryKey);

}
