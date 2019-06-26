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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.order.strategies.ordercloning.impl.DefaultCloneAbstractOrderStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * CPQ specific default implementation of {@link CloneAbstractOrderStrategy}. Adds configuration session management on
 * top of the default implementation {@link DefaultCloneAbstractOrderStrategy}
 */
public class ProductConfigCloneAbstractOrderStrategyImpl implements CloneAbstractOrderStrategy
{

	private static final Logger LOG = Logger.getLogger(ProductConfigCloneAbstractOrderStrategyImpl.class);

	private DefaultCloneAbstractOrderStrategy defaultCloneAbstractOrderStrategy;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationCopyStrategy configCopyStrategy;

	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;

	@Override
	public <T extends AbstractOrderModel> T clone(final ComposedTypeModel orderType, final ComposedTypeModel entryType,
			final AbstractOrderModel original, final String code, final Class abstractOrderClassResult,
			final Class abstractOrderEntryClassResult)
	{
		final T clone = defaultCloneAbstractOrderStrategy.clone(orderType, entryType, original, code, abstractOrderClassResult,
				abstractOrderEntryClassResult);
		//Whenever a cart is cloned into a quote: release all configuration sessions attached to the cart
		if (isCleanUpNeeded(original, abstractOrderClassResult))
		{
			cleanUp(original);
		}
		// Execute additional steps to finalize clone process whenever:
		//  - a cart, quote or an order is cloned into a cart
		//  - a quote is cloned into a quote
		if (isFinalizeCloneNeeded(original, abstractOrderClassResult))
		{
			getConfigCopyStrategy().finalizeClone(original, clone);
		}
		if (LOG.isDebugEnabled())
		{
			traceCPQAspects(clone);
		}
		return clone;
	}

	protected boolean isFinalizeCloneNeeded(final AbstractOrderModel original, final Class abstractOrderClassResult)
	{
		return isQuoteOrOrderOrCartToCartCloneProcess(original, abstractOrderClassResult)
				|| isQuoteToQuoteCloneProcess(original, abstractOrderClassResult);
	}

	protected boolean isQuoteOrOrderOrCartToCartCloneProcess(final AbstractOrderModel original,
			final Class abstractOrderClassResult)
	{
		return (CartModel.class.isAssignableFrom(abstractOrderClassResult)
				&& (original instanceof QuoteModel || original instanceof OrderModel || original instanceof CartModel));
	}

	protected boolean isQuoteToQuoteCloneProcess(final AbstractOrderModel original, final Class abstractOrderClassResult)
	{
		return (QuoteModel.class.isAssignableFrom(abstractOrderClassResult) && original instanceof QuoteModel);
	}

	protected void traceCPQAspects(final AbstractOrderModel orderModel)
	{
		LOG.debug("After clone, target document has code: " + orderModel.getCode());
		orderModel.getEntries().stream().forEach(entry -> traceCPQAspects(entry));
	}

	protected void traceCPQAspects(final AbstractOrderEntryModel entry)
	{
		LOG.debug("Product configuration: " + entry.getProductConfiguration() + " for entry " + entry.getPk());
	}

	protected boolean isCleanUpNeeded(final AbstractOrderModel original, final Class abstractOrderClassResult)
	{
		return QuoteModel.class.isAssignableFrom(abstractOrderClassResult) && original instanceof CartModel;
	}

	protected boolean isCleanUpNeeded(final AbstractOrderModel original)
	{
		return original instanceof CartModel;
	}

	protected void cleanUp(final AbstractOrderModel original)
	{
		if (original == null)
		{
			throw new IllegalArgumentException("Abstract Order to clean up must not be null");
		}
		original.getEntries().stream().forEach(entry -> cleanUpEntry(entry));
	}

	protected void cleanUpEntry(final AbstractOrderEntryModel entry)
	{
		final PK pk = entry.getPk();
		if (pk != null)
		{
			final String cartEntryKey = pk.toString();
			final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntryKey);
			if (!StringUtils.isEmpty(configId))
			{
				getConfigurationAbstractOrderIntegrationStrategy().finalizeCartEntry(entry);
			}
		}
	}

	@Override
	public <T extends AbstractOrderEntryModel> Collection<T> cloneEntries(final ComposedTypeModel entriesType,
			final AbstractOrderModel original)
	{
		//Whenever a cart is cloned into a quote: release all configuration sessions attached to the cart
		if (isCleanUpNeeded(original))
		{
			cleanUp(original);
		}
		return getDefaultCloneAbstractOrderStrategy().cloneEntries(entriesType, original);
	}

	/**
	 * @return the defaultCloneAbstractOrderStrategy
	 */
	protected DefaultCloneAbstractOrderStrategy getDefaultCloneAbstractOrderStrategy()
	{
		return defaultCloneAbstractOrderStrategy;
	}

	@Required
	public void setDefaultCloneAbstractOrderStrategy(final DefaultCloneAbstractOrderStrategy defaultCloneAbstractOrderStrategy)
	{
		this.defaultCloneAbstractOrderStrategy = defaultCloneAbstractOrderStrategy;

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

	protected ConfigurationCopyStrategy getConfigCopyStrategy()
	{
		return configCopyStrategy;
	}

	@Required
	public void setConfigCopyStrategy(final ConfigurationCopyStrategy configCopyStrategy)
	{
		this.configCopyStrategy = configCopyStrategy;
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
