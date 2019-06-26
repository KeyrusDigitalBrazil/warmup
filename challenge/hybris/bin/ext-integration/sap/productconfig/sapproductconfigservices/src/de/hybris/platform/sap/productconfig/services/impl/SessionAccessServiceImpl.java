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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link SessionAccessService}
 */
public class SessionAccessServiceImpl implements SessionAccessService
{
	private int maxCachedConfigMapSize = 20;
	private Set<String> cachedConfigIds = Collections.synchronizedSet(new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1)));
	private Set<String> oldCachedConfigIds = Collections.synchronizedSet(new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1)));

	private static final String TRACE_MESSAGE_FOR_CART_ENTRY = "for cart entry: ";
	private static final String TRACE_MESSAGE_FOR_PRODUCT = "for product: ";
	private static final Logger LOG = Logger.getLogger(SessionAccessServiceImpl.class);
	private SessionService sessionService;

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @deprecated since 18.11.0 - call {@link SessionService#getCurrentSession()#getSessionId()}
	 */
	@Deprecated
	@Override
	public String getSessionId()
	{
		return getSessionService().getCurrentSession().getSessionId();
	}

	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put config ID " + configId + " into session for cart entry: " + cartEntryKey);
		}

		final String other = getCartEntryForConfigId(configId);
		if (other != null)
		{
			removeConfigIdForCartEntry(other);
		}
		getCartEntryConfigCache().put(cartEntryKey, configId);
	}

	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		final Map<String, String> sessionConfigCartEntryCache = retrieveSessionAttributeContainer().getCartEntryConfigurations();
		final String configId = sessionConfigCartEntryCache.get(cartEntryKey);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get config ID " + configId + " from session for cart entry: " + cartEntryKey);
		}

		return configId;
	}

	@Override
	public String getDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		final Map<String, String> sessionDraftConfigCartEntryCache = retrieveSessionAttributeContainer()
				.getCartEntryDraftConfigurations();
		final String configId = sessionDraftConfigCartEntryCache.get(cartEntryKey);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get draft config ID " + configId + " from session for cart entry: " + cartEntryKey);
		}

		return configId;
	}


	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return getUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}


	/**
	 * Retrieves UiStatus from session
	 *
	 * @param key
	 *           Key of object in map
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 * @return UiStatus
	 */
	protected <T> T getUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		final Map<String, Object> sessionUiStatusCache;
		if (forCart)
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}
		final Object uiStatus = sessionUiStatusCache.get(key);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get UiStatus " + uiStatus + " from session " + traceMessage + key);
		}

		return (T) uiStatus;
	}


	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		setUiStatusIntoSession(cartEntryKey, uiStatus, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	@Override
	public Object getUiStatusForProduct(final String productKey)
	{
		return getUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}


	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		setUiStatusIntoSession(productKey, uiStatus, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	/**
	 * Puts UiStatus object into session
	 *
	 * @param key
	 *           Key for object
	 * @param uiStatus
	 *           The object we want to store in session
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void setUiStatusIntoSession(final String key, final Object uiStatus, final boolean forCart,
			final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put UiStatus " + uiStatus + " into session " + traceMessage + key);
		}

		final Map<String, Object> sessionUiStatusEntryCache;
		if (forCart)
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		sessionUiStatusEntryCache.put(key, uiStatus);
	}


	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		removeUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	/**
	 * Removes UiStatus object from session
	 *
	 * @param key
	 *           Key for object
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void removeUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove UiStatus from session " + traceMessage + key);
		}

		final Map<String, Object> uiStatusMap;
		if (forCart)
		{
			uiStatusMap = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			uiStatusMap = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		if (!MapUtils.isEmpty(uiStatusMap))
		{
			uiStatusMap.remove(key);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Map does not exist in session");
			}
		}
	}

	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		removeUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	@Override
	public String getCartEntryForConfigId(final String configId)
	{
		final Map<String, String> sessionCartEntryConfigurations = retrieveSessionAttributeContainer().getCartEntryConfigurations();

		final List<String> matches = findConfigIdInMap(configId, sessionCartEntryConfigurations);

		if (matches.size() > 1)
		{
			throw new IllegalStateException("Multiple matches for configuration: " + configId);
		}
		if (!matches.isEmpty())
		{
			final String cartEntryKey = matches.get(0);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Get cart entry key " + cartEntryKey + " from session for config ID" + configId);
			}
			return cartEntryKey;
		}

		return null;
	}

	@Override
	public String getCartEntryForDraftConfigId(final String configId)
	{
		final Map<String, String> sessionCartEntryDraftConfigurations = retrieveSessionAttributeContainer()
				.getCartEntryDraftConfigurations();

		final List<String> matches = findConfigIdInMap(configId, sessionCartEntryDraftConfigurations);

		if (matches.size() > 1)
		{
			throw new IllegalStateException("Multiple matches for draft configuration: " + configId);
		}
		if (!matches.isEmpty())
		{
			final String cartEntryKey = matches.get(0);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Get cart entry key " + cartEntryKey + " from session for draft config ID" + configId);
			}
			return cartEntryKey;
		}

		return null;
	}


	protected List<String> findConfigIdInMap(final String configId, final Map<String, String> sessionCartEntryConfigurations)
	{
		final List<String> matches = sessionCartEntryConfigurations.entrySet().stream()//
				.filter(entry -> entry.getValue() != null ? entry.getValue().equals(configId) : (configId == null))//
				.map(entry -> entry.getKey())//
				.collect(Collectors.toList());
		return matches;
	}


	@Override
	public void removeSessionArtifactsForCartEntry(final String cartEntryId)
	{
		// consider draft as well
		final String configId = getConfigIdForCartEntry(cartEntryId);
		final String draftConfigId = getDraftConfigIdForCartEntry(cartEntryId);

		//remove configuration ID if needed
		removeConfigIdForCartEntry(cartEntryId);

		//remove draft configuration ID if needed
		removeDraftConfigIdForCartEntry(cartEntryId);

		//remove UI status attached to cart entry
		removeUiStatusForCartEntry(cartEntryId);

		//check if configuration & draft configuration are maintained at product level also
		removeProductRelatedSessionArtifacts(configId);
		removeProductRelatedSessionArtifacts(draftConfigId);
	}


	protected void removeProductRelatedSessionArtifacts(final String configId)
	{
		if (null != configId)
		{
			String productKey = null;
			for (final Entry<String, String> entry : retrieveSessionAttributeContainer().getProductConfigurations().entrySet())
			{
				// consider draft as well
				if (configId.equals(entry.getValue()))
				{
					productKey = entry.getKey();
					break;
				}
			}
			if (null != productKey)
			{
				removeUiStatusForProduct(productKey);
				removeConfigIdForProduct(productKey);
			}
		}
	}


	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove config ID for cart entry: " + cartEntryKey);
		}

		getCartEntryConfigCache().remove(cartEntryKey);

	}

	/**
	 * @return Map: Configuration ID's for cart entry
	 */
	protected Map<String, String> getCartEntryConfigCache()
	{
		return retrieveSessionAttributeContainer().getCartEntryConfigurations();
	}

	protected Map<String, String> getCartEntryDraftConfigCache()
	{
		return retrieveSessionAttributeContainer().getCartEntryDraftConfigurations();
	}

	/**
	 * @deprecated call {@link ProductConfigurationCacheAccessService#getCachedNameMap()} instead
	 * @since 18.11.0
	 */
	@Deprecated
	@Override
	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap()
	{
		return retrieveSessionAttributeContainer().getClassificationSystemCPQAttributes();
	}

	/**
	 * @deprecated since 18.11.0 - no longer used
	 */
	@Override
	@Deprecated
	public Set<String> getSolrIndexedProperties()
	{
		return retrieveSessionAttributeContainer().getIndexedProperties();
	}

	/**
	 * @deprecated since 18.11.0 - no longer used
	 */
	@Override
	@Deprecated
	public void setSolrIndexedProperties(final Set<String> solrTypes)
	{
		retrieveSessionAttributeContainer().setIndexedProperties(solrTypes);
	}

	/**
	 * * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		return retrieveSessionAttributeContainer().getConfigurationProvider();
	}

	/**
	 * * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	@Override
	public void setConfigurationProvider(final ConfigurationProvider provider)
	{
		retrieveSessionAttributeContainer().setConfigurationProvider(provider);
	}

	/**
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#getConfigurationModelEngineState(String)} instead
	 */
	@Deprecated
	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder();
			String sessionId = null;
			if (getSessionService() != null && getSessionService().getCurrentSession() != null)
			{
				sessionId = getSessionService().getCurrentSession().getSessionId();
			}
			debugOutput.append("getConfigurationModelEngineState, configuration ID ").append(configId)
					.append(" is bound to session ").append(sessionId);
			LOG.debug(debugOutput);
		}
		return retrieveSessionAttributeContainer().getConfigurationModelEngineStates().get(configId);
	}

	/**
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#setConfigurationModelEngineState(String, ConfigModel)}
	 *             instead
	 */
	@Deprecated
	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder();
			String sessionId = null;
			if (getSessionService() != null && getSessionService().getCurrentSession() != null)
			{
				sessionId = getSessionService().getCurrentSession().getSessionId();
			}
			String rootProductId = null;
			if (configModel != null && configModel.getRootInstance() != null)
			{
				rootProductId = configModel.getRootInstance().getName();
			}
			debugOutput.append("setConfigurationModelEngineState, configuration ID ").append(configId)
					.append(" is bound to session ").append(sessionId).append(" and belongs to root product ").append(rootProductId);
			LOG.debug(debugOutput);
		}
		ensureThatNotToManyConfigsAreCachedInSession();
		cachedConfigIds.add(configId);
		retrieveSessionAttributeContainer().getConfigurationModelEngineStates().put(configId, configModel);
	}

	/**
	 * @deprecated since 18.11.0 - this method is obsolete because the key under which the configuration engine state and
	 *             price summary states have been saved consists of configuration id an user session id
	 */
	@Deprecated
	@Override
	public void removeConfigAttributeStates()
	{
		final ProductConfigSessionAttributeContainer container = retrieveSessionAttributeContainer(false);
		if (container != null)
		{
			LOG.debug("Cleaning product config engine state read cache");
			container.getConfigurationModelEngineStates().clear();
			container.getPriceSummaryStates().clear();
		}
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer()
	{
		return retrieveSessionAttributeContainer(true);
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer(final boolean createLazy)
	{

		synchronized (getSessionService().getCurrentSession())
		{
			ProductConfigSessionAttributeContainer attributeContainer = getSessionService()
					.getAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
			if (attributeContainer == null && createLazy)
			{
				attributeContainer = new ProductConfigSessionAttributeContainer();
				getSessionService().setAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, attributeContainer);
			}
			return attributeContainer;
		}
	}

	/**
	 * * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	@Override
	public void setPricingProvider(final PricingProvider provider)
	{
		retrieveSessionAttributeContainer().setPricingProvider(provider);

	}


	/**
	 * * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	@Override
	public PricingProvider getPricingProvider()
	{
		return retrieveSessionAttributeContainer().getPricingProvider();
	}

	/**
	 * @deprecated since 18.11.0 - call {@link ProductConfigurationCacheAccessService#getPriceSummaryState(String)}
	 *             instead
	 */
	@Deprecated
	@Override
	public PriceSummaryModel getPriceSummaryState(final String configId)
	{
		return retrieveSessionAttributeContainer().getPriceSummaryStates().get(configId);
	}

	/**
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#setPriceSummaryState(String, PriceSummaryModel)} instead
	 */
	@Deprecated
	@Override
	public void setPriceSummaryState(final String configId, final PriceSummaryModel priceSummaryModel)
	{
		retrieveSessionAttributeContainer().getPriceSummaryStates().put(configId, priceSummaryModel);
	}

	/**
	 * @deprecated since 18.11.0 - call {@link ProductConfigurationCacheAccessService#removeConfigAttributeState(String)}
	 *             instead
	 */
	@Deprecated
	@Override
	public void removeConfigAttributeState(final String configId)
	{
		final ProductConfigSessionAttributeContainer container = retrieveSessionAttributeContainer();
		container.getConfigurationModelEngineStates().remove(configId);
		container.getPriceSummaryStates().remove(configId);
		container.getAnalyticDataStates().remove(configId);
		cachedConfigIds.remove(configId);
	}

	/**
	 * * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	@Override
	public void setAnalyticsProvider(final AnalyticsProvider analyticsProvider)
	{
		retrieveSessionAttributeContainer().setAnalyticsProvider(analyticsProvider);
	}

	/**
	 * * @deprecated since 18.11.0 - caching decision is moved to {@link ProviderFactory}
	 */
	@Deprecated
	@Override
	public AnalyticsProvider getAnalyticsProvider()
	{
		return retrieveSessionAttributeContainer().getAnalyticsProvider();
	}

	/**
	 * @deprecated since 18.11.0 - call
	 *             {@link ProductConfigurationCacheAccessService#setAnalyticData(String, AnalyticsDocument)} instead
	 */
	@Deprecated
	@Override
	public void setAnalyticData(final String configId, final AnalyticsDocument analyticsDocument)
	{
		retrieveSessionAttributeContainer().setAnalyticData(configId, analyticsDocument);

	}

	/**
	 * @deprecated since 18.11.0 - call {@link ProductConfigurationCacheAccessService#getAnalyticData(String)} instead
	 */
	@Deprecated
	@Override
	public AnalyticsDocument getAnalyticData(final String configId)
	{
		return retrieveSessionAttributeContainer().getAnalyticData(configId);
	}

	@Override
	public void purge()
	{
		getSessionService().setAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	protected int getMaxCachedConfigsInSession()
	{
		return maxCachedConfigMapSize * 2;
	}

	/**
	 * Re-reading a configuration from the configuration engine can be expensive, especially for large configurations.
	 * This is only necessary when the configuration was updated since the last time being read. To make life for callers
	 * easier, this implementation features a simple read-cache for configurations based on the user session. So any
	 * calls to read configuration will always result in a cache hit until the configuration is updated.
	 *
	 * @param maxCachedConfigsInSession
	 *           set the maximum number of configs to be cached in the session. Default is 10.
	 */
	public void setMaxCachedConfigsInSession(final int maxCachedConfigsInSession)
	{
		this.maxCachedConfigMapSize = maxCachedConfigsInSession / 2;
	}

	protected void ensureThatNotToManyConfigsAreCachedInSession()
	{
		if (cachedConfigIds.size() >= maxCachedConfigMapSize)
		{
			for (final String configId : oldCachedConfigIds)
			{
				// clear old configs from session cache
				removeConfigAttributesFromSessionCache(configId);
			}
			oldCachedConfigIds = cachedConfigIds;
			// avoid rehashing, create with sufficient capacity
			cachedConfigIds = Collections.synchronizedSet(new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1)));
		}
	}

	protected void removeConfigAttributesFromSessionCache(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing config with id '" + configId + "' from cache");
		}

		removeConfigAttributeState(configId);
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}


	@Override
	public String getConfigIdForProduct(final String productCode)
	{
		final Map<String, String> sessionProductConfigurationsCache = retrieveSessionAttributeContainer()
				.getProductConfigurations();

		final String configId = sessionProductConfigurationsCache.get(productCode);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get config ID " + configId + " from session for product.");
		}

		return configId;
	}


	@Override
	public void setConfigIdForProduct(final String productCode, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put cartEntryId " + configId + " into session for product: " + configId);
		}
		retrieveSessionAttributeContainer().getProductConfigurations().put(productCode, configId);
	}

	@Override
	public void removeConfigIdForProduct(final String pCode)
	{
		retrieveSessionAttributeContainer().getProductConfigurations().remove(pCode);
	}


	/**
	 * @deprecated since 18.08.0 - only link cart entries to configs and configs to products, no direct linking.
	 */
	@Override
	@Deprecated
	public void setCartEntryForProduct(final String productKey, final String cartEntryId)
	{
		//not supported anymore
	}

	/**
	 * @deprecated since 18.08.0 - only link cart entries to configs and configs to products, no direct linking.
	 */
	@Override
	@Deprecated
	public String getCartEntryForProduct(final String productKey)
	{
		return getCartEntryForConfigId(getConfigIdForProduct(productKey));
	}

	/**
	 * @deprecated since 18.08.0 - only link cart entries to configs and configs to products, no direct linking.
	 */
	@Override
	@Deprecated
	public void removeCartEntryForProduct(final String productKey)
	{
		removeConfigIdForCartEntry(getCartEntryForProduct(productKey));
	}


	@Override
	public void setDraftConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put draft config ID " + configId + " into session for cart entry: " + cartEntryKey);
		}
		getCartEntryDraftConfigCache().put(cartEntryKey, configId);

	}


	@Override
	public void removeDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove draft config ID for cart entry: " + cartEntryKey);
		}

		getCartEntryDraftConfigCache().remove(cartEntryKey);
	}

	@Override
	public String getProductForConfigId(final String configId)
	{
		if (configId != null)
		{
			final Map<String, String> sessionProductConfigurationsCache = retrieveSessionAttributeContainer()
					.getProductConfigurations();
			final Optional<Entry<String, String>> productConfigIdPair = sessionProductConfigurationsCache.entrySet().stream()
					.filter(element -> configId.equals(element.getValue())).findFirst();
			if (productConfigIdPair.isPresent())
			{
				return productConfigIdPair.get().getKey();
			}
		}
		return null;
	}

}
