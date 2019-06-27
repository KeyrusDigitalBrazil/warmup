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

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for pricing for configurable products
 */
public class ProductConfigurationPricingStrategyImpl implements ProductConfigurationPricingStrategy
{
	private ProductConfigurationService configurationService;
	private PricingService pricingService;
	private CommerceCartService commerceCartService;
	private ModelService modelService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	private static final Logger LOG = Logger.getLogger(ProductConfigurationPricingStrategyImpl.class);

	@Override
	public boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry)
	{
		final String pk = entry.getPk().toString();
		String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(pk);
		if (null == configId)
		{
			configId = getAbstractOrderEntryLinkStrategy().getDraftConfigIdForCartEntry(pk);
		}
		final PriceModel currentTotalPrice = retrieveCurrentTotalPrice(configId);
		boolean cartEntryUpdated = false;
		if (currentTotalPrice != null && currentTotalPrice.hasValidPrice())
		{
			final Double newPrice = Double.valueOf(currentTotalPrice.getPriceValue().doubleValue());
			if (hasBasePriceChanged(entry, newPrice))
			{
				entry.setBasePrice(newPrice);
				LOG.debug("Base price: " + entry.getBasePrice() + " is set for the cart entry with pk: " + entry.getPk());
				cartEntryUpdated = true;
			}
		}
		return cartEntryUpdated;
	}

	protected boolean hasBasePriceChanged(final AbstractOrderEntryModel entry, final Double newPrice)
	{
		return !newPrice.equals(entry.getBasePrice());
	}

	@Override
	public boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter)
	{
		if (updateCartEntryBasePrice(entry))
		{
			//We need to persist both entities before cart calculation, otherwise
			//total calculation does not work (subsequent save calls restore the old state
			//because unsaved changes are present)
			getModelService().save(entry);
			getModelService().save(entry.getOrder());
			if (calculateCart)
			{
				if (passedParameter == null)
				{
					final CommerceCartParameter parameter = getParametersForCartUpdate(entry);
					getCommerceCartService().calculateCart(parameter);
				}
				else
				{
					getCommerceCartService().calculateCart(passedParameter);
				}
			}
			return true;
		}
		return false;
	}

	protected CommerceCartParameter getParametersForCartUpdate(final AbstractOrderEntryModel entry)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart((CartModel) entry.getOrder());
		final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(entry.getPk().toString());
		parameter.setConfigId(configId);
		return parameter;
	}

	protected PriceModel retrieveCurrentTotalPrice(final String configId)
	{
		if (getPricingService().isActive())
		{
			final PriceSummaryModel priceSummary = getPricingService().getPriceSummary(configId);
			if (priceSummary == null)
			{
				return null;
			}
			return priceSummary.getCurrentTotalPrice();
		}
		else
		{
			return getConfigurationService().retrieveConfigurationModel(configId).getCurrentTotalPrice();
		}
	}

	@Override
	public boolean isCartPricingErrorPresent(final ConfigModel configModel)
	{
		return configModel.hasPricingError();
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected PricingService getPricingService()
	{
		return pricingService;
	}

	/**
	 * @param pricingService
	 *           the pricingService to set
	 */
	public void setPricingService(final PricingService pricingService)
	{
		this.pricingService = pricingService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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

}
