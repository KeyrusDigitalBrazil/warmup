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

import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationAccessControlService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ProductConfigurationAccessControlService}
 */
public class ProductConfigurationAccessControlServiceImpl implements ProductConfigurationAccessControlService
{
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;

	@Override
	public boolean isUpdateAllowed(final String configId)
	{
		return !isRelatedToNonDraftDocument(configId) && getConfigurationLifecycleStrategy().isConfigForCurrentUser(configId);
	}

	@Override
	public boolean isReadAllowed(final String configId)
	{
		if (getConfigurationLifecycleStrategy().isConfigKnown(configId))
		{
			final boolean isDocumentRelated = getConfigurationAbstractOrderEntryLinkStrategy().isDocumentRelated(configId);
			final boolean isUserRelated = getConfigurationLifecycleStrategy().isConfigForCurrentUser(configId);
			return isUserRelated || !isDocumentRelated;
		}
		else
		{
			// can happen if the config is created and sent from external system
			return true;
		}
	}


	@Override
	public boolean isReleaseAllowed(final String configId)
	{
		return getConfigurationLifecycleStrategy().isConfigForCurrentUser(configId);
	}

	protected boolean isRelatedToNonDraftDocument(final String configId)
	{
		return getConfigurationAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId) != null;
	}

	protected ConfigurationProductLinkStrategy getConfigurationProductLinkStrategy()
	{
		return configurationProductLinkStrategy;
	}

	@Required
	public void setConfigurationProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getConfigurationAbstractOrderEntryLinkStrategy()
	{
		return this.configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setConfigurationAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
	{
		return configurationLifecycleStrategy;
	}

	@Required
	public void setConfigurationLifecycleStrategy(final ConfigurationLifecycleStrategy configurationLifecycleStrategy)
	{
		this.configurationLifecycleStrategy = configurationLifecycleStrategy;
	}
}
