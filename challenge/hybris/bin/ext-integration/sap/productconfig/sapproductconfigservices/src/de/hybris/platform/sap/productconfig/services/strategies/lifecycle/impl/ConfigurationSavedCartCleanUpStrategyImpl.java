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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationSavedCartCleanUpStrategy;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Takes care of releasing session artifacts for product configuration, such as product link to the product
 * configuration
 */
public class ConfigurationSavedCartCleanUpStrategyImpl implements ConfigurationSavedCartCleanUpStrategy
{

	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	private CartService cartService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;


	/**
	 * @param configurationLifecycleStrategy
	 *           the configurationLifecycleStrategy to set
	 */
	@Required
	public void setConfigurationLifecycleStrategy(final ConfigurationLifecycleStrategy configurationLifecycleStrategy)
	{
		this.configurationLifecycleStrategy = configurationLifecycleStrategy;
	}


	/**
	 * @param configurationAbstractOrderEntryLinkStrategy
	 *           the configurationAbstractOrderEntryLinkStrategy to set
	 */
	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}


	@Override
	public void cleanUpCart()
	{
		retrieveSessionCartEntries().stream().filter(this::isConfigurableProduct).forEach(this::cleanUpCartEntry);
	}


	protected boolean isConfigurableProduct(final AbstractOrderEntryModel entry)
	{
		return getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(entry.getProduct());
	}


	protected List<AbstractOrderEntryModel> retrieveSessionCartEntries()
	{
		if (!getCartService().hasSessionCart())
		{
			return Collections.emptyList();
		}

		return getCartService().getSessionCart().getEntries();
	}

	protected void cleanUpCartEntry(final AbstractOrderEntryModel entry)
	{
		//cleanup product link if connected to cart entry
		final String cartEntryKey = entry.getPk().toString();
		final String productCode = entry.getProduct().getCode();
		final String configIdForCartEntry = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntryKey);
		Preconditions.checkNotNull(configIdForCartEntry,
				"In context of this method we expect a configuration attached to cart entry");
		final String configIdForProduct = getProductLinkStrategy().getConfigIdForProduct(productCode);
		if (configIdForCartEntry.equals(configIdForProduct))
		{
			getProductLinkStrategy().removeConfigIdForProduct(productCode);
		}
		//cleanup draft
		final String draftConfigIdForCartEntry = getAbstractOrderEntryLinkStrategy().getDraftConfigIdForCartEntry(cartEntryKey);
		if (draftConfigIdForCartEntry != null)
		{
			getConfigurationLifecycleStrategy().releaseSession(draftConfigIdForCartEntry);
		}

	}


	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	/**
	 * Set helper, to check if the related product is CPQ configurable
	 *
	 * @param cpqConfigurableChecker
	 *           configurator checker
	 */
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	public ConfigurationProductLinkStrategy getProductLinkStrategy()
	{
		return configurationProductLinkStrategy;
	}

	@Required
	public void setProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;
	}

	public CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return this.configurationAbstractOrderEntryLinkStrategy;
	}


	protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
	{
		return this.configurationLifecycleStrategy;
	}


}
