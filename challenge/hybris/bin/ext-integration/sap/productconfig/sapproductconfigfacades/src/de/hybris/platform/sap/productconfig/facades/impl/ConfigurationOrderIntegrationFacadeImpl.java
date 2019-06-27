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

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationAbstractOrderIntegrationHelper;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.strategies.impl.ProductConfigurationCartEntryValidationStrategyImpl;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Default implementation of the {@link ConfigurationOrderIntegrationFacade}
 */
public class ConfigurationOrderIntegrationFacadeImpl implements ConfigurationOrderIntegrationFacade
{

	/**
	 * CartModification status code indicating the the KB version isn't known (anymore) to the configuration engine.
	 *
	 * @see CartModificationData#getStatusCode()
	 */
	public static final String KB_NOT_VALID = ProductConfigurationCartEntryValidationStrategyImpl.KB_NOT_VALID;
	private CustomerAccountService customerAccountService;
	private UserService userService;
	private BaseStoreService baseStoreService;

	private ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper;



	@Override
	public ConfigurationOverviewData getConfiguration(final String code, final int entryNumber)
	{
		final OrderModel order = findOrderModel(code);
		return getConfigurationAbstractOrderIntegrationHelper().retrieveConfigurationOverviewData(order, entryNumber);
	}

	@Override
	public boolean isReorderable(final String orderCode)
	{
		final OrderModel order = findOrderModel(orderCode);
		return configurationAbstractOrderIntegrationHelper.isReorderable(order);
	}

	protected OrderModel findOrderModel(final String code)
	{
		final BaseStoreModel store = getBaseStoreService().getCurrentBaseStore();
		return getCustomerAccountService().getOrderForCode(code, store);
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected ConfigurationAbstractOrderIntegrationHelper getConfigurationAbstractOrderIntegrationHelper()
	{
		return configurationAbstractOrderIntegrationHelper;
	}

	/**
	 * @param configurationAbstractOrderIntegrationHelper
	 */
	public void setConfigurationAbstractOrderIntegrationHelper(
			final ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper)
	{
		this.configurationAbstractOrderIntegrationHelper = configurationAbstractOrderIntegrationHelper;
	}

}
