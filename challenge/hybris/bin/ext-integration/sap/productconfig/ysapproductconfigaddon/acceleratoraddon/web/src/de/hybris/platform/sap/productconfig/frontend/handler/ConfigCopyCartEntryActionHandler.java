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
package de.hybris.platform.sap.productconfig.frontend.handler;

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Action Handler to Copy a CartItem including configuration
 */
public class ConfigCopyCartEntryActionHandler implements CartEntryActionHandler
{
	private static final Logger LOG = Logger.getLogger(ConfigCopyCartEntryActionHandler.class);

	static final String SUCCESS_KEY = "sapproductconfig.copy.success.message.key";
	static final String ERROR_KEY = "sapproductconfig.copy.error.message.key";
	private ConfigurationCartIntegrationFacade configCartFacade;
	private CartFacade cartFacade;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationCopyStrategy configurationCopyStrategy;

	private CPQConfigurableChecker cpqConfigurableChecker;


	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * @param cartFacade
	 */
	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected ConfigurationCartIntegrationFacade getConfigCartFacade()
	{
		return configCartFacade;
	}

	/**
	 * @param configCartFacade
	 */
	@Required
	public void setConfigCartFacade(final ConfigurationCartIntegrationFacade configCartFacade)
	{
		this.configCartFacade = configCartFacade;
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

	@Override
	public Optional<String> handleAction(final List<Long> entryNumbers) throws CartEntryActionException
	{
		for (final Long entryNumber : entryNumbers)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Copying cart item entry '" + entryNumber + "' including configuration");
			}
			final OrderEntryData entryToCopy = getEntryByEntryNumber(entryNumber.longValue());
			final List<ConfigurationInfoData> infos = entryToCopy.getConfigurationInfos();
			final boolean copyDirect = isDefaultConfig(infos);
			try
			{
				copyAndAddToCart(entryToCopy, copyDirect);
			}
			catch (final CommerceCartModificationException e)
			{
				LOG.error("Copying cart item entry '" + entryNumber + "' including configuration failed due to an exception", e);
				throw new CartEntryActionException("Copy Configuration Failed due: " + e.getMessage(), e);
			}
		}
		return Optional.empty();
	}

	protected void copyAndAddToCart(final OrderEntryData entryToCopy, final boolean copyDirect)
			throws CommerceCartModificationException
	{
		if (copyDirect)
		{
			LOG.debug("Original item has still default configuration, just adding same item diretly again to cart via cart facade");
			cartFacade.addToCart(entryToCopy.getProduct().getCode(), 1L);
		}
		else
		{
			LOG.debug("Original item has still modified configuration, coping configuration and adding item via cpq facade");
			final ConfigurationData configData = copyConfiguration(entryToCopy);
			getConfigCartFacade().addConfigurationToCart(configData);
		}
	}

	protected boolean isDefaultConfig(final List<ConfigurationInfoData> infos)
	{
		return infos.isEmpty() || (infos.size() == 1 && infos.get(0).getConfigurationLabel() == null);
	}

	protected ConfigurationData copyConfiguration(final OrderEntryData entryToCopy)
	{
		final String configId = getConfigIdByOrderEntry(entryToCopy);
		final String pCode = entryToCopy.getProduct().getCode();
		final String newConfigId = getConfigurationCopyStrategy().deepCopyConfiguration(configId, pCode, null, true);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"copied configuration session with id '" + configId + "' and created new session with id '" + newConfigId + "'");
		}
		final ConfigurationData configData = new ConfigurationData();
		configData.setConfigId(newConfigId);
		final KBKeyData key = new KBKeyData();
		key.setProductCode(pCode);
		configData.setKbKey(key);
		return configData;
	}

	protected String getConfigIdByOrderEntry(final OrderEntryData entryToCopy)
	{
		return getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(entryToCopy.getItemPK());
	}

	protected OrderEntryData getEntryByEntryNumber(final long entryNumber)
	{
		final List<OrderEntryData> entries = getCartFacade().getSessionCart().getEntries();
		for (final OrderEntryData entry : entries)
		{
			if (entry.getEntryNumber().longValue() == entryNumber)
			{
				return entry;
			}
		}

		return null;
	}

	@Override
	public String getSuccessMessageKey()
	{
		return SUCCESS_KEY;
	}

	@Override
	public String getErrorMessageKey()
	{
		return ERROR_KEY;
	}

	@Override
	public boolean supports(final CartEntryModel cartEntry)
	{
		final boolean configurable = getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(cartEntry.getProduct());
		if (LOG.isDebugEnabled())
		{
			if (configurable)
			{
				LOG.debug(
						"Showing copy action for cart entry " + cartEntry.getEntryNumber() + " because it is a configurable product");
			}
			else
			{
				LOG.debug("Hiding copy action for cart entry " + cartEntry.getEntryNumber()
						+ " because it is not a configurable product");
			}
		}
		return configurable;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return cpqConfigurableChecker;
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

	protected ConfigurationCopyStrategy getConfigurationCopyStrategy()
	{
		return configurationCopyStrategy;
	}

	@Required
	public void setConfigurationCopyStrategy(final ConfigurationCopyStrategy configurationCopyStrategy)
	{
		this.configurationCopyStrategy = configurationCopyStrategy;
	}
}
