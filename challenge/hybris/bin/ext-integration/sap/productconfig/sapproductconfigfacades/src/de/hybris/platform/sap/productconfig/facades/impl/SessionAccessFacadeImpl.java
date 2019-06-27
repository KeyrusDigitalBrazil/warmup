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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SessionAccessFacade}
 */
public class SessionAccessFacadeImpl implements SessionAccessFacade
{

	private SessionAccessService sessionAccessService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationModelCacheStrategy configModelCacheStrategy;


	/**
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#setConfigIdForCartEntry(String, String)} instead
	 */
	@Deprecated
	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartEntryKey, configId);
	}

	/**
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getConfigIdForCartEntry(String)} instead
	 */
	@Deprecated
	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		return getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntryKey);
	}

	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return sessionAccessService.getUiStatusForCartEntry(cartEntryKey);
	}

	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		sessionAccessService.setUiStatusForCartEntry(cartEntryKey, uiStatus);

	}

	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		sessionAccessService.setUiStatusForProduct(productKey, uiStatus);

	}

	@Override
	public <T> T getUiStatusForProduct(final String productKey)
	{
		return sessionAccessService.getUiStatusForProduct(productKey);
	}

	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		sessionAccessService.removeUiStatusForCartEntry(cartEntryKey);
	}

	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		sessionAccessService.removeUiStatusForProduct(productKey);
	}

	/**
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getCartEntryForConfigId(String)} instead
	 */
	@Deprecated
	@Override
	public String getCartEntryForConfigId(final String configId)
	{
		return getAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId);
	}

	/**
	 * @deprecated since 18.08.0 - do not link cart entry and product diretly. always link and read via aconfig id.
	 */
	@Deprecated
	@Override
	public void setCartEntryForProduct(final String productKey, final String cartEntryKey)
	{
		//not supported / required anymore
	}

	/**
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getConfigIdForCartEntry(String)} and then
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getCartEntryForConfigId(String) } instead
	 */
	@Deprecated
	@Override
	public String getCartEntryForProduct(final String productKey)
	{
		return getSessionAccessService().getCartEntryForProduct(productKey);
	}

	/**
	 * @deprecated since 18.08.0 - do not link cart entry and product diretly. always link and read via aconfig id.
	 */
	@Deprecated
	@Override
	public void removeCartEntryForProduct(final String productKey)
	{
		getSessionAccessService().removeCartEntryForProduct(productKey);
	}

	/**
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#removeConfigIdForCartEntry(String)} instead
	 */
	@Deprecated
	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		getAbstractOrderEntryLinkStrategy().removeConfigIdForCartEntry(cartEntryKey);
	}

	/**
	 * @deprecated since 18.11.0 - call {@link SessionService#getCurrentSession()#getSessionId()} instead
	 */
	@Deprecated
	@Override
	public String getSessionId()
	{
		return sessionAccessService.getSessionId();
	}

	/**
	 * @deprecated since 18.08.0 - call {@link ConfigurationModelCacheStrategy#getConfigurationModelEngineState(String)}
	 *             instead
	 */
	@Deprecated
	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return getConfigModelCacheStrategy().getConfigurationModelEngineState(configId);
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

	/**
	 * @param sessionAccessService
	 *           injects the underlying session access service
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}


	protected SessionAccessService getSessionAccessService()
	{
		return this.sessionAccessService;
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
}
