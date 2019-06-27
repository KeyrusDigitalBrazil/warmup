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
package de.hybris.platform.sap.productconfig.runtime.interf.services;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Container for session attibutes used in product configuration
 */
public class ProductConfigSessionAttributeContainer implements Serializable
{
	private static final long serialVersionUID = 1L;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, String> cartEntryConfigurations;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, String> cartEntryDraftConfigurations;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, Object> cartEntryUiStatuses;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, Object> productUiStatuses;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, ClassificationSystemCPQAttributesContainer> classificationSystemCPQAttributes;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient ConfigurationProvider configurationProvider;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, ConfigModel> configurationModelEngineStates;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient PricingProvider pricingProvider;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, PriceSummaryModel> priceSummaryStates;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient AnalyticsProvider analyticsProvider;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, AnalyticsDocument> analyticData;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, String> productConfigurations;

	private final Set<String> indexedProperties = Collections.synchronizedSet(new HashSet<String>());

	/**
	 * DefaultConstructor
	 */
	public ProductConfigSessionAttributeContainer()
	{
		super();
		initTransientMaps();
	}

	/**
	 * @return the cartEntryConfigurations
	 */
	public Map<String, String> getCartEntryConfigurations()
	{
		return cartEntryConfigurations;
	}


	/**
	 * @return the cartEntryDraftConfigurations
	 */
	public Map<String, String> getCartEntryDraftConfigurations()
	{
		return cartEntryDraftConfigurations;
	}


	/**
	 * @param cartEntryConfigurations
	 *           the cartEntryConfigurations to set
	 */
	public void setCartEntryConfigurations(final Map<String, String> cartEntryConfigurations)
	{
		this.cartEntryConfigurations.clear();
		this.cartEntryConfigurations.putAll(cartEntryConfigurations);
	}


	/**
	 * @param cartEntryDraftConfigurations
	 *           the cartEntryConfigurations to set
	 */
	public void setCartEntryDraftConfigurations(final Map<String, String> cartEntryDraftConfigurations)
	{
		this.cartEntryDraftConfigurations.clear();
		this.cartEntryDraftConfigurations.putAll(cartEntryDraftConfigurations);
	}


	/**
	 * @return the cartEntryUiStatuses
	 */
	public Map<String, Object> getCartEntryUiStatuses()
	{
		return cartEntryUiStatuses;
	}

	/**
	 * @param cartEntryUiStatuses
	 *           the cartEntryUiStatuses to set
	 */
	public void setCartEntryUiStatuses(final Map<String, Object> cartEntryUiStatuses)
	{
		this.cartEntryUiStatuses.clear();
		this.cartEntryUiStatuses.putAll(cartEntryUiStatuses);
	}

	/**
	 * @return the productUiStatuses
	 */
	public Map<String, Object> getProductUiStatuses()
	{
		return productUiStatuses;
	}

	/**
	 * @param productUiStatuses
	 *           the productUiStatuses to set
	 */
	public void setProductUiStatuses(final Map<String, Object> productUiStatuses)
	{
		this.productUiStatuses.clear();
		this.productUiStatuses.putAll(productUiStatuses);
	}

	/**
	 * @return the indexedProperties
	 */
	public Set<String> getIndexedProperties()
	{
		return Optional.ofNullable(indexedProperties).map(Set::stream).orElseGet(Stream::empty).collect(Collectors.toSet());
	}

	/**
	 * @param indexedProperties
	 *           the indexedProperties to set
	 */
	public void setIndexedProperties(final Set<String> indexedProperties)
	{
		this.indexedProperties.clear();
		this.indexedProperties.addAll(indexedProperties);
	}

	/**
	 * @return the classificationSystemCPQAttributes
	 */
	public Map<String, ClassificationSystemCPQAttributesContainer> getClassificationSystemCPQAttributes()
	{
		return classificationSystemCPQAttributes;
	}

	/**
	 * @return the configurationProvider
	 */
	public ConfigurationProvider getConfigurationProvider()
	{
		return configurationProvider;
	}

	/**
	 * @param configurationProvider
	 *           the configurationProvider to set
	 */
	public void setConfigurationProvider(final ConfigurationProvider configurationProvider)
	{
		this.configurationProvider = configurationProvider;
	}

	/**
	 * @return the configurationModelEngineStates
	 */
	public Map<String, ConfigModel> getConfigurationModelEngineStates()
	{
		return configurationModelEngineStates;
	}

	/**
	 * @return the priceSummaryStates
	 */
	public Map<String, PriceSummaryModel> getPriceSummaryStates()
	{
		return priceSummaryStates;
	}

	/**
	 * @param priceSummaryStates
	 *           the priceSummaryStates to set
	 */
	public void setPriceSummaryStates(final Map<String, PriceSummaryModel> priceSummaryStates)
	{
		this.priceSummaryStates.clear();
		this.priceSummaryStates.putAll(priceSummaryStates);
	}

	private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		initTransientMaps();
	}

	private void initTransientMaps()
	{
		cartEntryConfigurations = Collections.synchronizedMap(new HashMap<String, String>());
		cartEntryDraftConfigurations = Collections.synchronizedMap(new HashMap<String, String>());

		cartEntryUiStatuses = Collections.synchronizedMap(new HashMap<String, Object>());
		productUiStatuses = Collections.synchronizedMap(new HashMap<String, Object>());
		classificationSystemCPQAttributes = new ConcurrentHashMap<>(512);
		configurationModelEngineStates = Collections.synchronizedMap(new HashMap<String, ConfigModel>());
		priceSummaryStates = Collections.synchronizedMap(new HashMap<String, PriceSummaryModel>());
		analyticData = Collections.synchronizedMap(new HashMap<String, AnalyticsDocument>());
		productConfigurations = Collections.synchronizedMap(new HashMap<String, String>());
	}

	/**
	 * @param provider
	 *           - PricingProvider to set
	 */
	public void setPricingProvider(final PricingProvider provider)
	{
		this.pricingProvider = provider;

	}

	/**
	 * @return PricingProvider saved in container
	 */
	public PricingProvider getPricingProvider()
	{
		return this.pricingProvider;
	}

	/**
	 * Sets an analytics provider
	 *
	 * @param analyticsProvider
	 */
	public void setAnalyticsProvider(final AnalyticsProvider analyticsProvider)
	{
		this.analyticsProvider = analyticsProvider;
	}

	/**
	 * Retrieves an analytics provider
	 *
	 * @return analyticsProvider
	 */
	public AnalyticsProvider getAnalyticsProvider()
	{
		return analyticsProvider;
	}

	/**
	 * Sets analytics document into the analytic data map
	 *
	 * @param configId
	 * @param analyticsDocument
	 */
	public void setAnalyticData(final String configId, final AnalyticsDocument analyticsDocument)
	{
		analyticData.put(configId, analyticsDocument);
	}

	/**
	 * Retrieves analytics document from the analytic data map by config id
	 *
	 * @param configId
	 * @return analytics document
	 */
	public AnalyticsDocument getAnalyticData(final String configId)
	{
		return analyticData.get(configId);
	}

	/**
	 * @return the analytic states
	 */
	public Map<String, AnalyticsDocument> getAnalyticDataStates()
	{
		return analyticData;
	}


	/**
	 * @return product to configuration link cache
	 */
	public Map<String, String> getProductConfigurations()
	{
		return productConfigurations;
	}

	/**
	 * @param productConfigurations
	 */
	public void setProductConfigurations(final Map<String, String> productConfigurations)
	{
		this.productConfigurations = productConfigurations;
	}
}
