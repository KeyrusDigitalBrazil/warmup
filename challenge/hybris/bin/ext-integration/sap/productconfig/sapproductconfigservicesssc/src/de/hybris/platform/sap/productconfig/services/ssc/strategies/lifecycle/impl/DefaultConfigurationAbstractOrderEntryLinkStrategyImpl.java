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

import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.SessionServiceAware;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;


/**
 * Default implementation of the {@link ConfigurationAbstractOrderEntryLinkStrategy}. It uses the hybris session to
 * store any data and hence delegates to the {@link SessionAccessService}.
 */
public class DefaultConfigurationAbstractOrderEntryLinkStrategyImpl extends SessionServiceAware
		implements ConfigurationAbstractOrderEntryLinkStrategy
{
	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		getSessionAccessService().setConfigIdForCartEntry(cartEntryKey, configId);
	}

	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		return getSessionAccessService().getConfigIdForCartEntry(cartEntryKey);
	}

	@Override
	public String getCartEntryForConfigId(final String configId)
	{
		return getSessionAccessService().getCartEntryForConfigId(configId);
	}

	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		getSessionAccessService().removeConfigIdForCartEntry(cartEntryKey);
	}


	@Override
	public void removeSessionArtifactsForCartEntry(final String cartEntryId)
	{
		getSessionAccessService().removeSessionArtifactsForCartEntry(cartEntryId);
	}

	@Override
	public void setDraftConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		getSessionAccessService().setDraftConfigIdForCartEntry(cartEntryKey, configId);
	}

	@Override
	public void removeDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		getSessionAccessService().removeDraftConfigIdForCartEntry(cartEntryKey);
	}

	@Override
	public String getDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		return getSessionAccessService().getDraftConfigIdForCartEntry(cartEntryKey);
	}

	@Override
	public String getCartEntryForDraftConfigId(final String configId)
	{
		return getSessionAccessService().getCartEntryForDraftConfigId(configId);
	}

	@Override
	public boolean isDocumentRelated(final String configId)
	{
		return null != getSessionAccessService().getCartEntryForConfigId(configId)
				|| null != getSessionAccessService().getCartEntryForDraftConfigId(configId);
	}
}
