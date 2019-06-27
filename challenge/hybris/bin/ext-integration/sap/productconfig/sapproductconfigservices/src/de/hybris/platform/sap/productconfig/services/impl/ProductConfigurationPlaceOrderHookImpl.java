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

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Releases configuration sessions on placing an order
 *
 */
public class ProductConfigurationPlaceOrderHookImpl implements CommercePlaceOrderMethodHook
{

	private static final Logger LOG = Logger.getLogger(ProductConfigurationPlaceOrderHookImpl.class);

	private ProductConfigurationService productConfigurationService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;


	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{

		if (LOG.isDebugEnabled())
		{
			traceCPQAspectsAfterPlaceOrder(orderModel.getOrder());
		}

		for (final AbstractOrderEntryModel cartEntry : parameter.getCart().getEntries())
		{
			final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntry.getPk().toString());
			if (configId != null && (!configId.isEmpty()))
			{
				getConfigurationAbstractOrderIntegrationStrategy().finalizeCartEntry(cartEntry);
			}
		}
		orderModel.getOrder().getEntries().stream().filter(entry -> hasConfigurationAttached(entry))
				.forEach(entry -> prepareForOrderReplication(entry));
	}

	protected void prepareForOrderReplication(final AbstractOrderEntryModel entry)
	{
		getConfigurationAbstractOrderIntegrationStrategy().prepareForOrderReplication(entry);
	}

	protected void traceCPQAspectsAfterPlaceOrder(final AbstractOrderModel orderModel)
	{
		LOG.debug("After place order, target document has code: " + orderModel.getCode());
		orderModel.getEntries().stream().forEach(entry -> traceCPQAspectsAfterPlaceOrder(entry));
	}

	protected void traceCPQAspectsAfterPlaceOrder(final AbstractOrderEntryModel entry)
	{
		LOG.debug("Product configuration: " + entry.getProductConfiguration() + " for entry " + entry.getPk());
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		// Nothing done here

	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		// Nothing done here
	}

	/**
	 * @return product configuration service
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return this.productConfigurationService;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	protected boolean hasConfigurationAttached(final AbstractOrderEntryModel cartEntry)
	{
		final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntry.getPk().toString());
		return StringUtils.isNotEmpty(configId);
	}

	/**
	 * @param configurationAbstractOrderIntegrationStrategy
	 */
	@Required
	public void setConfigurationAbstractOrderIntegrationStrategy(
			final ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy)
	{
		this.configurationAbstractOrderIntegrationStrategy = configurationAbstractOrderIntegrationStrategy;
	}

	protected ConfigurationAbstractOrderIntegrationStrategy getConfigurationAbstractOrderIntegrationStrategy()
	{
		return configurationAbstractOrderIntegrationStrategy;
	}

}
