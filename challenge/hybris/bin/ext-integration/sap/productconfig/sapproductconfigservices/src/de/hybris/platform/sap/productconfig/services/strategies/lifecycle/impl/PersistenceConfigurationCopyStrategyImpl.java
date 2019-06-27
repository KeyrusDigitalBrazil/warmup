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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDependencyHandler;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class PersistenceConfigurationCopyStrategyImpl implements ConfigurationCopyStrategy
{
	private static final Logger LOG = Logger.getLogger(PersistenceConfigurationCopyStrategyImpl.class);
	private ConfigurationDeepCopyHandler configDeepCopyHandler;
	private ProductConfigurationPersistenceService persistenceService;
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private ConfigurationDependencyHandler configurationDependencyHandler;
	private ProductConfigurationCacheAccessService cacheAccessService;

	@Override
	public String deepCopyConfiguration(final String configId, final String productCode, final String externalConfiguration,
			final boolean force)
	{
		return getConfigDeepCopyHandler().deepCopyConfiguration(configId, productCode, externalConfiguration, force, null);
	}

	@Override
	public void finalizeClone(final AbstractOrderModel source, final AbstractOrderModel target)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("Abstract Order to finalize clone process must not be null");
		}

		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		if (currentBaseSite == null)
		{
			LOG.info(
					"Injecting BaseSite ro enable cloning of configuration. Consider to fix the process, so that a base site is always available.");
			replaceBaseSiteIfPossible(source);
		}
		final UserModel userToRestore = updateCurrentUserIfRequired(source);

		final Map<String, String> oldConfigId2newConfigIdMap = new HashMap();
		final ProductConfigurationRelatedObjectType targetObjectType = getAssignmentResolverStrategy()
				.retrieveRelatedObjectType(target);
		source.getEntries().stream().forEach(entry -> finalizeCloneEntry(entry, oldConfigId2newConfigIdMap, targetObjectType));
		final List<AbstractOrderEntryModel> entries = target.getEntries();
		if (entries != null)
		{
			entries.stream().forEach(cloneEntry -> replaceProductConfiguration(cloneEntry, oldConfigId2newConfigIdMap));
		}

		if (target instanceof QuoteModel)
		{
			copyProductConfigurationDependentObjects(oldConfigId2newConfigIdMap);
		}

		// restore session user
		getUserService().setCurrentUser(userToRestore);
	}

	protected void copyProductConfigurationDependentObjects(final Map<String, String> oldConfigId2newConfigIdMap)
	{
		for (final Map.Entry<String, String> entry : oldConfigId2newConfigIdMap.entrySet())
		{
			final String sourceConfigId = entry.getKey();
			final String targetConfigId = entry.getValue();
			getConfigurationDependencyHandler().copyProductConfigurationDependency(sourceConfigId, targetConfigId);
			getCacheAccessService().removeConfigAttributeState(targetConfigId);
		}
	}

	protected UserModel updateCurrentUserIfRequired(final AbstractOrderModel source)
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		if (getUserService().isAnonymousUser(currentUser))
		{
			// this can happen in case documents are cloned asynchronously without user context using the task engine
			final UserModel sourceDocumentUser = source.getUser();
			// set the document user as current user, so the clone of configs will not fail due to missing read permissions
			// this also ensures that the new config is associated with the correct user
			userService.setCurrentUser(sourceDocumentUser);
			LOG.info(
					"Injecting document user as current user to enable cloning of configuration. Consider to fix the process, so that the current user is the document userr or an admin");
		}
		return currentUser;
	}

	protected void finalizeCloneEntry(final AbstractOrderEntryModel entry, final Map<String, String> oldConfigId2newConfigIdMap,
			final ProductConfigurationRelatedObjectType targetObjectType)
	{
		final ProductConfigurationModel configModel = entry.getProductConfiguration();
		if (configModel != null)
		{
			final String configId = configModel.getConfigurationId();
			if (!StringUtils.isEmpty(configId))
			{

				final String newConfigId = getConfigDeepCopyHandler().deepCopyConfiguration(configId, entry.getProduct().getCode(),
						null, false, targetObjectType);
				oldConfigId2newConfigIdMap.put(configId, newConfigId);
			}
		}
	}

	protected void replaceProductConfiguration(final AbstractOrderEntryModel cloneEntry,
			final Map<String, String> oldConfigId2newConfigIdMap)
	{
		final ProductConfigurationModel oldConfigModel = cloneEntry.getProductConfiguration();
		if (oldConfigModel != null)
		{
			final String oldConfigId = oldConfigModel.getConfigurationId();
			if (!StringUtils.isEmpty(oldConfigId))
			{
				final String newConfigId = oldConfigId2newConfigIdMap.get(oldConfigId);
				final ProductConfigurationModel configurationModel = getPersistenceService().getByConfigId(newConfigId);
				cloneEntry.setProductConfiguration(configurationModel);
			}
		}
	}

	protected boolean replaceBaseSiteIfPossible(final AbstractOrderModel model)
	{
		boolean replaced = false;
		final BaseSiteModel baseSite = model.getSite();
		if (baseSite != null)
		{
			getBaseSiteService().setCurrentBaseSite(baseSite, true);
			replaced = true;
		}
		return replaced;
	}

	protected ConfigurationDeepCopyHandler getConfigDeepCopyHandler()
	{
		return configDeepCopyHandler;
	}

	@Required
	public void setConfigDeepCopyHandler(final ConfigurationDeepCopyHandler configDeepCopyHandler)
	{
		this.configDeepCopyHandler = configDeepCopyHandler;
	}

	protected ProductConfigurationPersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	@Required
	public void setPersistenceService(final ProductConfigurationPersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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

	protected ConfigurationDependencyHandler getConfigurationDependencyHandler()
	{
		return configurationDependencyHandler;
	}

	@Required
	public void setConfigurationDependencyHandler(final ConfigurationDependencyHandler configurationDependencyHandler)
	{
		this.configurationDependencyHandler = configurationDependencyHandler;
	}

	protected ProductConfigurationCacheAccessService getCacheAccessService()
	{
		return cacheAccessService;
	}

	@Required
	public void setCacheAccessService(final ProductConfigurationCacheAccessService cacheAccessService)
	{
		this.cacheAccessService = cacheAccessService;
	}

}
