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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.SessionServiceAware;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationLifecycleStrategy}. It uses the hybris session to store any data
 * and hence delegates to the {@link SessionAccessService}.
 */
public class DefaultConfigurationLifecycleStrategyImpl extends SessionServiceAware implements ConfigurationLifecycleStrategy
{

	private ProviderFactory providerFactory;


	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		return getConfigurationProvider().createDefaultConfiguration(kbKey);
	}

	@Override
	public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		return getConfigurationProvider().updateConfiguration(model);
	}

	@Override
	public void updateUserLinkToConfiguration(final String userSessionId)
	{
		// empty - not needed
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveConfigurationModel(configId);
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId, final ConfigurationRetrievalOptions options)
			throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveConfigurationModel(configId, options);
	}

	@Override
	public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveExternalConfiguration(configId);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		return getConfigurationProvider().createConfigurationFromExternalSource(extConfig);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		return getConfigurationProvider().createConfigurationFromExternalSource(kbKey, extConfig);
	}

	@Override
	public void releaseSession(final String configId)
	{
		getConfigurationProvider().releaseSession(configId);
	}

	@Override
	public void releaseExpiredSessions(final String sessionId)
	{
		// empty - not needed
	}

	@Override
	public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		return getConfigurationProvider().retrieveConfigurationFromVariant(baseProductCode, variantProductCode);
	}

	@Override
	public boolean isConfigForCurrentUser(final String configId)
	{
		// any config in a different session cannot be accessed and the process will fail during update/release
		return true;
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return getProviderFactory().getConfigurationProvider();
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

	@Override
	public boolean isConfigKnown(final String configId)
	{
		// can only handle/access of own session by design
		return true;
	}
}
