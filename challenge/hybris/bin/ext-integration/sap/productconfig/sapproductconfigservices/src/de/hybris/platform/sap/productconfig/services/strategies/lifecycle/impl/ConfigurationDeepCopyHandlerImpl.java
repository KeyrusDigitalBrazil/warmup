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

import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ConfigurationDeepCopyHandlerImpl implements ConfigurationDeepCopyHandler
{
	private ProductConfigurationService configurationService;

	private static final Logger LOG = Logger.getLogger(ConfigurationDeepCopyHandlerImpl.class);

	@Override
	public String deepCopyConfiguration(final String configId, final String productCode, final String externalConfiguration,
			final boolean force, final ProductConfigurationRelatedObjectType targetObjectType)
	{
		// ignoring force flag is intended by this implementation
		String newConfigId;
		String externalConfigNotNull = externalConfiguration;
		if (null == externalConfigNotNull)
		{
			externalConfigNotNull = getConfigurationService().retrieveExternalConfiguration(configId);
		}
		String productCodeNotNull = productCode;
		if (null == productCodeNotNull)
		{
			final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);
			productCodeNotNull = configModel.getRootInstance().getName();
		}
		final ConfigurationRetrievalOptions retrievalOptions = prepareRetrievalOptions(targetObjectType);
		final ConfigModel newConfiguration = getConfigurationService()
				.createConfigurationFromExternal(new KBKeyImpl(productCodeNotNull), externalConfigNotNull, null, retrievalOptions);
		newConfigId = newConfiguration.getId();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deep copied configuration '" + configId + "' into new configuration '" + newConfigId + "'");
		}
		return newConfigId;
	}

	protected ConfigurationRetrievalOptions prepareRetrievalOptions(final ProductConfigurationRelatedObjectType targetObjectType)
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setRelatedObjectType(targetObjectType);
		return options;
	}


	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
