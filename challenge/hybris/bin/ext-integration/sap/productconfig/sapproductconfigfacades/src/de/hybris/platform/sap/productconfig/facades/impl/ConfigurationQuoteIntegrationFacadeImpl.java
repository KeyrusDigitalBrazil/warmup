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

import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationAbstractOrderIntegrationHelper;
import de.hybris.platform.sap.productconfig.facades.ConfigurationQuoteIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationQuoteIntegrationFacade}
 */
public class ConfigurationQuoteIntegrationFacadeImpl implements ConfigurationQuoteIntegrationFacade
{

	private QuoteService quoteService;
	private BaseStoreService baseStoreService;
	private UserService userService;
	private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
	private ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper;


	/**
	 * Makes sure that a runtime configuration for this quote entry exists. Either an existing configuration session is
	 * returned, or if no session exists, yet, a new configuration session is created from the external configuration
	 * attached to the quotation item.
	 */
	@Override
	public ConfigurationOverviewData getConfiguration(final String code, final int entryNumber)
	{
		final QuoteModel quote = findQuote(code);
		return configurationAbstractOrderIntegrationHelper.retrieveConfigurationOverviewData(quote, entryNumber);
	}

	protected QuoteModel findQuote(final String code)
	{
		final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(code);
		if (null == quote)
		{
			throw new IllegalArgumentException("Could not find quote with code '" + code + "'");
		}
		return quote;
	}

	protected QuoteService getQuoteService()
	{
		return quoteService;
	}

	/**
	 * @param quoteService
	 */
	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected QuoteUserIdentificationStrategy getQuoteUserIdentificationStrategy()
	{
		return quoteUserIdentificationStrategy;
	}

	/**
	 * @param quoteUserIdentificationStrategy
	 */
	@Required
	public void setQuoteUserIdentificationStrategy(final QuoteUserIdentificationStrategy quoteUserIdentificationStrategy)
	{
		this.quoteUserIdentificationStrategy = quoteUserIdentificationStrategy;
	}

	protected ConfigurationAbstractOrderIntegrationHelper getConfigurationAbstractOrderIntegrationHelper()
	{
		return configurationAbstractOrderIntegrationHelper;
	}

	/**
	 * @param configurationAbstractOrderIntegrationHelper
	 */
	@Required
	public void setConfigurationAbstractOrderIntegrationHelper(
			final ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper)
	{
		this.configurationAbstractOrderIntegrationHelper = configurationAbstractOrderIntegrationHelper;
	}
}
