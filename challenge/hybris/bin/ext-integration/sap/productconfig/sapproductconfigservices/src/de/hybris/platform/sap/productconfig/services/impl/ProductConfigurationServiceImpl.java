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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationAccessControlService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.fest.util.Collections;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationService}.<br>
 * This implementation will synchronize access to the {@link ConfigurationProvider}, so that it is guaranteed that only
 * exactly one thread will access the configuration provider for a given configuration session. Furthermore a simple
 * session based read cache ensures that subsequent calls to read the same configuration result only into exactly one
 * read request to the configuration engine.
 *
 * @see ProductConfigurationServiceImpl#setMaxLocksPerMap(int)
 * @see SessionAccessServiceImpl#setMaxCachedConfigsInSession(int)
 */
public class ProductConfigurationServiceImpl implements ProductConfigurationService
{
	public static final String NOT_ALLOWED_TO_UPDATE_CONFIGURATION = "Not allowed to update configuration";
	public static final String NOT_ALLOWED_TO_READ_CONFIGURATION = "Not allowed to read configuration";
	public static final String NOT_ALLOWED_TO_RELEASE_CONFIGURATION = "Not allowed to release configuration";
	protected static final String DEBUG_CONFIG_WITH_ID = "Config with id '";
	static final Object PROVIDER_LOCK = new Object();

	private static final Logger LOG = Logger.getLogger(ProductConfigurationServiceImpl.class);

	private static int maxLocksPerMap = 1024;
	private static Map<String, Object> locks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));
	private static Map<String, Object> oldLocks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));

	private ProviderFactory providerFactory;
	private ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService;
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;

	private ConfigurationLifecycleStrategy configLifecycleStrategy;
	private ConfigurationModelCacheStrategy configModelCacheStrategy;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private TrackingRecorder recorder;
	private ProductConfigurationAccessControlService productConfigurationAccessControlService;
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;
	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationProductUtil configurationProductUtil;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		// no need to synchronize create, because config session (identified by
		// the config ID)
		// is only exposed once the object has been created
		final ConfigModel config = getConfigLifecycleStrategy().createDefaultConfiguration(kbKey);
		recorder.recordCreateConfiguration(config, kbKey);

		return afterDefaultConfigCreated(config);

	}

	@Override
	public void updateConfiguration(final ConfigModel model)
	{
		checkUpdateAllowed(model);

		final String id = model.getId();
		final Object lock = ProductConfigurationServiceImpl.getLock(id);
		synchronized (lock)
		{

			try
			{
				final boolean updateExecuted = getConfigLifecycleStrategy().updateConfiguration(model);
				if (updateExecuted)
				{
					recorder.recordUpdateConfiguration(model);
					if (LOG.isDebugEnabled())
					{
						LOG.debug(DEBUG_CONFIG_WITH_ID + model.getId() + "' updated, removing it from cache");
					}
					removeConfigAttributesFromCache(id);
				}
			}
			catch (final ConfigurationEngineException ex)
			{
				cleanUpAfterEngineError(id);
				throw new IllegalStateException("Updating configuration failed", ex);
			}
		}
	}

	protected void checkUpdateAllowed(final ConfigModel model)
	{
		if (!getProductConfigurationAccessControlService().isUpdateAllowed(model.getId()))
		{
			throw new ProductConfigurationAccessException(NOT_ALLOWED_TO_UPDATE_CONFIGURATION);
		}
	}


	protected void checkReadAllowed(final String configId)
	{
		if (!getProductConfigurationAccessControlService().isReadAllowed(configId))
		{
			throw new ProductConfigurationAccessException(NOT_ALLOWED_TO_READ_CONFIGURATION);
		}
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		checkReadAllowed(configId);
		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = getConfigModelCacheStrategy().getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{

				cachedModel = retrieveConfigurationModelFromConfigurationEngine(configId, retrieveCorrectPricingDate(configId));
				cacheConfig(cachedModel);
				recorder.recordConfigurationStatus(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	protected boolean isRelatedObjectReadOnly(final String configId, final ConfigurationRetrievalOptions retrievalOptions)
	{
		boolean required = false;

		final ProductConfigurationRelatedObjectType assignedTo = retrieveRelatedObjectType(configId, retrievalOptions);

		if (assignedTo == ProductConfigurationRelatedObjectType.ORDER_ENTRY
				|| assignedTo == ProductConfigurationRelatedObjectType.QUOTE_ENTRY
				|| assignedTo == ProductConfigurationRelatedObjectType.SAVEDCART_ENTRY)
		{
			required = true;
		}
		return required;
	}

	protected ProductConfigurationRelatedObjectType retrieveRelatedObjectType(final String configId,
			final ConfigurationRetrievalOptions retrievalOptions)
	{
		final ProductConfigurationRelatedObjectType assignedTo;
		if (retrievalOptions != null && retrievalOptions.getRelatedObjectType() != null)
		{
			assignedTo = retrievalOptions.getRelatedObjectType();
		}
		else
		{
			assignedTo = getAssignmentResolverStrategy().retrieveRelatedObjectType(configId);
		}
		return assignedTo;
	}

	protected ConfigurationRetrievalOptions retrieveCorrectPricingDate(final String configId)
	{
		if (isRelatedObjectReadOnly(configId, null))
		{
			final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
			//use past pricing date
			options.setPricingDate(getAssignmentResolverStrategy().retrieveCreationDateForRelatedEntry(configId));
			return options;
		}
		//use current pricing date
		return null;
	}

	protected ConfigModel retrieveConfigurationModelFromConfigurationEngine(final String configId,
			final ConfigurationRetrievalOptions options)
	{
		try
		{
			ConfigModel configModel;
			if (options == null)
			{
				configModel = getConfigLifecycleStrategy().retrieveConfigurationModel(configId);
			}
			else
			{
				configModel = getConfigLifecycleStrategy().retrieveConfigurationModel(configId, options);
			}

			//this is needed as AssignmentResolverStrategy accesses the cache
			cacheConfig(configModel);
			final String productCode = getAssignmentResolverStrategy().retrieveRelatedProductCode(configId);

			if (productCode != null)
			{
				updateKbKeyWithProductCode(configModel, productCode);
			}

			return configModel;
		}
		catch (final ConfigurationEngineException ex)
		{
			cleanUpAfterEngineError(configId);
			throw new IllegalStateException("Retrieving configuration failed", ex);
		}
	}

	protected void updateKbKeyForVariants(final ConfigModel configModel, final String baseProductCode, final String variantCode)
	{
		updateKbKeyWithProductCode(configModel, isChangeableVariant(variantCode) ? variantCode : baseProductCode);
	}

	protected boolean isChangeableVariant(final String variantCode)
	{
		final ProductModel variantModel = getConfigurationProductUtil().getProductForCurrentCatalog(variantCode);
		return variantModel != null && getCpqConfigurableChecker().isCPQChangeableVariantProduct(variantModel);
	}

	protected void updateKbKeyWithProductCode(final ConfigModel configModel, final String productCode)
	{
		final KBKey oldKey = configModel.getKbKey();
		configModel.setKbKey(
				new KBKeyImpl(productCode, oldKey.getKbName(), oldKey.getKbLogsys(), oldKey.getKbVersion(), oldKey.getDate()));
	}

	protected void cleanUpAfterEngineError(final String configId)
	{
		getConfigModelCacheStrategy().purge();
		removeConfigAttributesFromCache(configId);
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		checkReadAllowed(configId);
		final Object lock = getLock(configId);
		synchronized (lock)
		{
			try
			{
				return getConfigLifecycleStrategy().retrieveExternalConfiguration(configId);
			}
			catch (final ConfigurationEngineException e)
			{
				cleanUpAfterEngineError(configId);
				throw new IllegalStateException("Retrieving external configuration failed", e);
			}
		}
	}

	/**
	 * @param providerFactory
	 *           inject factory to access the providers
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	/**
	 * A configuration provider lock ensures, that there are no concurrent requests send to the configuration engine for
	 * the same configuration session.<br>
	 * We might not always get informed when a configuration session is released, hence we do not rely on this. Instead
	 * we just keep a maximum number of locks and release the oldest locks, when there are to many. The maximum number
	 * can be configured by this setter. <br>
	 * A look can be re-created in case it had already been deleted. The number should be high enough, so that locks do
	 * not get deleted while some concurrent threads are still using the lock, as this could cause concurrency issue.
	 * <b>The maximum number heavily depends on the number of concurrent threads expected.</b> Default is 1024.
	 *
	 * @param maxLocksPerMap
	 *           sets the maximum number of Configuration Provider Locks kept.
	 */
	public static void setMaxLocksPerMap(final int maxLocksPerMap)
	{
		ProductConfigurationServiceImpl.maxLocksPerMap = maxLocksPerMap;
	}

	protected static int getMaxLocksPerMap()
	{
		return ProductConfigurationServiceImpl.maxLocksPerMap;
	}

	protected static Object getLock(final String configId)
	{
		synchronized (PROVIDER_LOCK)
		{

			Object lock = locks.get(configId);
			if (lock == null)
			{
				lock = oldLocks.get(configId);
				if (lock == null)
				{
					ensureThatLockMapIsNotTooBig();
					lock = new Object();
					locks.put(configId, lock);
				}
			}
			return lock;
		}
	}

	protected static void ensureThatLockMapIsNotTooBig()
	{
		if (locks.size() >= maxLocksPerMap)
		{
			oldLocks.clear();
			oldLocks = locks;
			// avoid rehashing, create with sufficient capacity
			locks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));
		}
	}

	protected ConfigModel afterDefaultConfigCreated(final ConfigModel config)
	{
		cacheConfig(config);
		return config;
	}

	protected ConfigModel afterConfigCreated(final ConfigModel config, final ConfigurationRetrievalOptions retrievalOptions)
	{
		cacheConfig(config);
		return config;
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration, null);
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration, cartEntryKey, null);
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey, final ConfigurationRetrievalOptions retrievalOptions)
	{

		final ConfigModel config = getConfigLifecycleStrategy().createConfigurationFromExternalSource(kbKey, externalConfiguration);
		updateKbKeyWithProductCode(config, kbKey.getProductCode());
		recorder.recordCreateConfigurationFromExternalSource(config);
		if (null != cartEntryKey)
		{
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartEntryKey, config.getId());
		}

		return afterConfigCreated(config, retrievalOptions);
	}


	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel config = getConfigLifecycleStrategy().createConfigurationFromExternalSource(extConfig);
		recorder.recordCreateConfigurationFromExternalSource(config);

		return afterConfigCreated(config, null);
	}

	@Override
	public void releaseSession(final String configId)
	{
		releaseSession(configId, false);
	}

	@Override
	public void releaseSession(final String configId, final boolean keepModel)
	{
		checkReleaseAllowed(configId);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Releasing config session with id " + configId);
		}

		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			getConfigLifecycleStrategy().releaseSession(configId);
			if (!keepModel)
			{
				removeConfigAttributesFromCache(configId);
			}

			synchronized (PROVIDER_LOCK)
			{
				locks.remove(configId);
				oldLocks.remove(configId);
			}
		}
	}

	protected void checkReleaseAllowed(final String configId)
	{
		if (!getProductConfigurationAccessControlService().isReleaseAllowed(configId))
		{
			throw new ProductConfigurationAccessException(NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		}
	}

	protected void removeConfigAttributesFromCache(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing config with id '" + configId + "' from cache");
		}

		getConfigModelCacheStrategy().removeConfigAttributeState(configId);
	}

	/**
	 * @deprecated since 18.11.0 - call {@link removeConfigAttributesFromCache(String)} instead
	 */
	@Deprecated
	protected void removeConfigAttributesSessionFromCache(final String configId)
	{
		removeConfigAttributesFromCache(configId);
	}

	protected void cacheConfig(final ConfigModel config)
	{
		getConfigModelCacheStrategy().setConfigurationModelEngineState(config.getId(), config);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(DEBUG_CONFIG_WITH_ID + config.getId() + "' read frist time, caching it for further access");
		}
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	protected ConfigurationLifecycleStrategy getConfigLifecycleStrategy()
	{
		return configLifecycleStrategy;
	}

	@Required
	public void setConfigLifecycleStrategy(final ConfigurationLifecycleStrategy configLifecycleStrategy)
	{
		this.configLifecycleStrategy = configLifecycleStrategy;
	}

	protected ConfigurationModelCacheStrategy getConfigModelCacheStrategy()
	{
		return configModelCacheStrategy;
	}

	@Required
	public void setConfigModelCacheStrategy(final ConfigurationModelCacheStrategy configModelCacheStrategy)
	{
		this.configModelCacheStrategy = configModelCacheStrategy;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	@Override
	public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
	{
		final ConfigModel configurationModel = retrieveConfigurationModel(configId);

		return countNumberOfIncompleteCstics(configurationModel.getRootInstance())
				+ countNumberOfSolvableConflicts(configurationModel);

	}


	protected int countNumberOfIncompleteCstics(final InstanceModel rootInstance)
	{

		int numberOfErrors = 0;
		for (final InstanceModel subInstace : rootInstance.getSubInstances())
		{
			numberOfErrors += countNumberOfIncompleteCstics(subInstace);
		}
		for (final CsticModel cstic : rootInstance.getCstics())
		{
			if (cstic.isRequired() && !cstic.isComplete())
			{
				numberOfErrors++;
				if (!cstic.isVisible())
				{
					LOG.warn("The mandatory CSTIC '" + cstic.getName() + "' is not complete, which is not visible to the user.");
				}
				else if (LOG.isDebugEnabled())
				{
					LOG.debug("Mandatory Cstic missing: " + cstic.getName());
				}
			}
		}
		return numberOfErrors;

	}

	protected int countNumberOfNotConsistentCstics(final InstanceModel instance)
	{
		int result = (int) instance.getCstics().stream().filter(cstic -> !cstic.isConsistent()).count();

		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			result += countNumberOfNotConsistentCstics(subInstance);
		}

		return result;
	}

	protected int countNumberOfSolvableConflicts(final ConfigModel configModel)
	{
		int result = 0;
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		if (!Collections.isEmpty(solvableConflicts))
		{
			return solvableConflicts.size();
		}

		if (!configModel.isConsistent())
		{
			result = countNumberOfNotConsistentCstics(configModel.getRootInstance());
		}

		return result;
	}

	@Override
	public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
	{
		if (getProviderFactory().getConfigurationProvider().isConfigureVariantSupported())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("create variant configuration for base product " + baseProductCode + " of product variant "
						+ variantProductCode);
			}
			final ConfigModel configModel = getConfigLifecycleStrategy().retrieveConfigurationFromVariant(baseProductCode,
					variantProductCode);
			updateKbKeyForVariants(configModel, baseProductCode, variantProductCode);
			recorder.recordCreateConfigurationForVariant(configModel, baseProductCode, variantProductCode);

			if (isChangeableVariant(variantProductCode))
			{
				return afterDefaultConfigCreated(configModel);
			}
			else
			{
				return afterConfigCreated(configModel, null);
			}
		}
		else
		{
			throw new IllegalStateException(
					"The active configuration provider does not support the configuration of a product variant");
		}
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           inject the CPQ tracking recorder for tracking CPQ events
	 */
	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	@Override
	public boolean hasKbForDate(final String productCode, final Date kbDate)
	{
		return getConfigurationProvider().isKbForDateExists(productCode, kbDate);
	}


	@Override
	public boolean isKbVersionValid(final KBKey kbKey)
	{
		return getConfigurationProvider().isKbVersionValid(kbKey);
	}

	/**
	 * @deprecated since 18.08
	 */
	@Override
	@Deprecated
	public boolean hasKbForVersion(final KBKey kbKey, final String externalConfig)
	{
		return getConfigurationProvider().isKbVersionExists(kbKey, externalConfig);
	}

	protected boolean isConfigureVariantSupported()
	{
		return getConfigurationProvider().isConfigureVariantSupported();
	}

	protected ProductConfigurationOrderIntegrationService getConfigurationPricingOrderIntegrationService()
	{
		return configurationPricingOrderIntegrationService;
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return getProviderFactory().getConfigurationProvider();
	}

	/**
	 * @param configurationPricingOrderIntegrationService
	 *           the configurationPricingOrderIntegrationService to set
	 */
	@Required
	public void setConfigurationPricingOrderIntegrationService(
			final ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService)
	{
		this.configurationPricingOrderIntegrationService = configurationPricingOrderIntegrationService;
	}

	@Override
	public int getTotalNumberOfIssues(final ConfigModel configModel)
	{
		return countNumberOfIncompleteCstics(configModel.getRootInstance()) + countNumberOfSolvableConflicts(configModel);
	}

	protected ProductConfigurationPricingStrategy getProductConfigurationPricingStrategy()
	{
		return productConfigurationPricingStrategy;
	}

	/**
	 * @param productConfigurationPricingStrategy
	 *           the productConfigurationPricingStrategy to set
	 */
	@Required
	public void setProductConfigurationPricingStrategy(
			final ProductConfigurationPricingStrategy productConfigurationPricingStrategy)
	{
		this.productConfigurationPricingStrategy = productConfigurationPricingStrategy;
	}

	@Override
	public KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		return getConfigurationProvider().extractKbKey(productCode, externalConfig);
	}

	/**
	 * @param productConfigurationAccessControlService
	 */
	@Required
	public void setProductConfigurationAccessControlService(
			final ProductConfigurationAccessControlService productConfigurationAccessControlService)
	{
		this.productConfigurationAccessControlService = productConfigurationAccessControlService;

	}

	protected ProductConfigurationAccessControlService getProductConfigurationAccessControlService()
	{
		return productConfigurationAccessControlService;
	}


	protected ConfigurationAssignmentResolverStrategy getAssignmentResolverStrategy()
	{
		return assignmentResolverStrategy;
	}

	@Required
	public void setAssignmentResolverStrategy(final ConfigurationAssignmentResolverStrategy assignmentResolverStrategy)
	{
		this.assignmentResolverStrategy = assignmentResolverStrategy;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	protected ConfigurationProductUtil getConfigurationProductUtil()
	{
		return configurationProductUtil;
	}

	@Required
	public void setConfigurationProductUtil(final ConfigurationProductUtil configurationProductUtil)
	{
		this.configurationProductUtil = configurationProductUtil;
	}
}
