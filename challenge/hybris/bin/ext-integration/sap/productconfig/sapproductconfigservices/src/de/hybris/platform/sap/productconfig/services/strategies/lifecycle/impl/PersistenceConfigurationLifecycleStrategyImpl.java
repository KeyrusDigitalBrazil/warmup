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

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class PersistenceConfigurationLifecycleStrategyImpl implements ConfigurationLifecycleStrategy
{
	private static final Logger LOG = Logger.getLogger(PersistenceConfigurationLifecycleStrategyImpl.class);

	private ProviderFactory providerFactory;
	private ModelService modelService;
	private UserService userService;
	private SessionService sessionService;
	private ProductConfigurationPersistenceService persistenceService;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		final ConfigModel config = getConfigurationProvider().createDefaultConfiguration(kbKey);
		persistNewConfiguration(config);
		return config;
	}

	protected void persistNewConfiguration(final ConfigModel config)
	{
		final String configid = config.getId();
		final ProductConfigurationModel configModel = getModelService().create(ProductConfigurationModel.class);
		configModel.setConfigurationId(configid);
		configModel.setVersion(config.getVersion());
		configModel.setUser(getUserService().getCurrentUser());
		configModel.setProduct(Collections.emptyList());
		final KBKey kbKey = config.getKbKey();
		configModel.setKbName(kbKey.getKbName());
		configModel.setKbLogsys(kbKey.getKbLogsys());
		configModel.setKbVersion(kbKey.getKbVersion());
		if (persistSessionId())
		{
			final String sessionId = getSessionService().getCurrentSession().getSessionId();
			configModel.setUserSessionId(sessionId);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Persisting new configuration with id '" + configid + "' for user session '" + sessionId + "'");
			}
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Persisting new configuration with id '" + configid
						+ "' for a session that does not want to bind configurations");
			}
		}
		getModelService().save(configModel);
	}

	@Override
	public void updateUserLinkToConfiguration(final String userSessionId)
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		if (!getUserService().isAnonymousUser(currentUser))
		{
			final List<ProductConfigurationModel> configModelsInSession = getPersistenceService().getByUserSessionId(userSessionId);

			for (final ProductConfigurationModel configModel : configModelsInSession)
			{
				if (isProductLinkedToProductConfiguration(configModel))
				{
					removeDuplicateProductLink(configModel);
				}
				configModel.setUser(currentUser);
				getModelService().save(configModel);
			}
		}
	}

	protected void removeDuplicateProductLink(final ProductConfigurationModel actualConfigModel)
	{

		final String productCode = getProductLinkedToProductConfiguration(actualConfigModel);
		final ProductConfigurationModel previousConfigModel = getPersistenceService().getByProductCodeAndUser(productCode,
				getUserService().getCurrentUser());
		if (null != previousConfigModel)
		{
			final String previousConfigId = previousConfigModel.getConfigurationId();
			if (null == getPersistenceService().getOrderEntryByConfigId(previousConfigId, false))
			{
				releaseSession(previousConfigId);
			}
			else
			{
				LOG.error(String.format(
						"A configuration from a previous session is linked to product and order entry, which is inavlid. Removing product link for product '%s' to config '%s'",
						productCode, previousConfigId));
				previousConfigModel.setProduct(null);
				getModelService().save(previousConfigModel);
			}
		}
	}

	protected boolean isProductLinkedToProductConfiguration(final ProductConfigurationModel configModel)
	{
		return CollectionUtils.isNotEmpty(configModel.getProduct());
	}

	protected String getProductLinkedToProductConfiguration(final ProductConfigurationModel configModel)
	{
		return isProductLinkedToProductConfiguration(configModel) ? configModel.getProduct().iterator().next().getCode() : null;
	}

	@Override
	public void releaseExpiredSessions(final String userSessionId)
	{
		if (getUserService().isAnonymousUser(getUserService().getCurrentUser()))
		{
			final List<ProductConfigurationModel> configModels = getPersistenceService().getByUserSessionId(userSessionId);
			for (final ProductConfigurationModel configModel : configModels)
			{
				if (getPersistenceService().getAllOrderEntriesByConfigId(configModel.getConfigurationId()).isEmpty())
				{
					releaseSession(configModel.getConfigurationId());
				}
			}
		}
	}

	@Override
	public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		final ProductConfigurationModel persistenceModel = getPersistenceService().getByConfigId(model.getId());
		final String currentVersion = persistenceModel.getVersion();
		updateETag(currentVersion, model, persistenceModel);
		final String updatedVersion = getConfigurationProvider().changeConfiguration(model);
		updateETag(updatedVersion, model, persistenceModel);
		return !updatedVersion.equals(currentVersion);

	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
	{
		final ConfigModel configModel = getConfigurationProvider().retrieveConfigurationModel(configId);
		updateETagFromModel(configModel);
		return configModel;
	}


	@Override
	public ConfigModel retrieveConfigurationModel(final String configId, final ConfigurationRetrievalOptions options)
			throws ConfigurationEngineException
	{
		final ConfigModel configModel = getConfigurationProvider().retrieveConfigurationModel(configId, options);
		updateETagFromModel(configModel);
		return configModel;
	}


	@Override
	public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveExternalConfiguration(configId);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(extConfig);
		persistNewConfiguration(config);
		return config;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(kbKey, extConfig);
		persistNewConfiguration(config);
		return config;
	}

	@Override
	public void releaseSession(final String configId)
	{
		final ProductConfigurationModel configModel = getPersistenceService().getByConfigId(configId);
		final String currentVersion = configModel.getVersion();
		releaseSessionSafely(configId, currentVersion);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Releasing configuration with id '" + configModel.getConfigurationId());
		}
		getModelService().remove(configModel);
	}

	protected void releaseSessionSafely(final String configId, final String currentVersion)
	{
		try
		{
			getConfigurationProvider().releaseSession(configId, currentVersion);
		}
		catch (final IllegalStateException ex)
		{
			if (ex.getCause() instanceof ConfigurationNotFoundException)
			{
				LOG.warn(String.format("Configuration '%s' not found, has it already been deleted?", configId), ex);
			}
			else
			{
				throw ex;
			}
		}
	}

	@Override
	public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		final ConfigModel config = getConfigurationProvider().retrieveConfigurationFromVariant(baseProductCode, variantProductCode);
		persistNewConfiguration(config);
		return config;
	}

	@Override
	public boolean isConfigForCurrentUser(final String configId)
	{
		final ProductConfigurationModel configPersistenceModel = getPersistenceService().getByConfigId(configId);
		final UserModel currentUser = getUserService().getCurrentUser();
		final boolean isSameSession = isSameSession(configPersistenceModel);
		final boolean isSameUser = isSameUser(configPersistenceModel, currentUser);
		final boolean isAdminUser = isAdminUser(currentUser);
		return isSameUser || isSameSession || isAdminUser;
	}

	protected boolean isSameSession(final ProductConfigurationModel productConfiguration)
	{
		return getSessionService().getCurrentSession().getSessionId().equals(productConfiguration.getUserSessionId());
	}

	protected boolean isSameUser(final ProductConfigurationModel productConfiguration, final UserModel currentUser)
	{
		return currentUser.equals(productConfiguration.getUser());
	}

	protected boolean isAdminUser(final UserModel currentUser)
	{
		return getUserService().isAdmin(currentUser);
	}

	protected void updateETagFromModel(final ConfigModel configModel)
	{
		final ProductConfigurationModel persistenceModel = getPersistenceService().getByConfigId(configModel.getId());
		updateETag(configModel.getVersion(), configModel, persistenceModel);
	}

	protected void updateETag(final String newVersion, final ConfigModel configModel,
			final ProductConfigurationModel persistenceModel)
	{
		if (!persistenceModel.getVersion().equals(newVersion))
		{
			persistenceModel.setVersion(newVersion);
			getModelService().save(persistenceModel);
		}
		configModel.setVersion(newVersion);
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return getProviderFactory().getConfigurationProvider();
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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

	protected boolean persistSessionId()
	{
		boolean persistSessionId = true;
		final Object attribute = getSessionService()
				.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS);
		if (attribute instanceof Boolean)
		{
			persistSessionId = !((Boolean) attribute).booleanValue();
		}
		return persistSessionId;

	}

	@Override
	public boolean isConfigKnown(final String configId)
	{
		return null != getPersistenceService().getByConfigId(configId, true);
	}

}
