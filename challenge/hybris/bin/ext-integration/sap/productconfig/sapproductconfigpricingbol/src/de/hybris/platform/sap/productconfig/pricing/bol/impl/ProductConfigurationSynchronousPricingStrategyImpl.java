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
package de.hybris.platform.sap.productconfig.pricing.bol.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPricingStrategyImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Configuration order integration for synchronous pricing
 */
public class ProductConfigurationSynchronousPricingStrategyImpl extends ProductConfigurationPricingStrategyImpl
		implements ProductConfigurationPricingStrategy
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationSynchronousPricingStrategyImpl.class);

	private SapPricingEnablementService sapPricingEnablementService;

	@Override
	public boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter)
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			return updateCartEntryPricesSynchronous(entry, calculateCart, passedParameter);
		}
		return super.updateCartEntryPrices(entry, calculateCart, passedParameter);
	}

	protected boolean updateCartEntryPricesSynchronous(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter)
	{
		if (calculateCart)
		{
			try
			{
				if (passedParameter == null)
				{
					final CommerceCartParameter parameter = getParametersForCartUpdate(entry);
					getCommerceCartService().recalculateCart(parameter);
				}
				else
				{
					getCommerceCartService().recalculateCart(passedParameter);
				}
				return true;
			}
			catch (final CalculationException e)
			{
				LOG.error("Price could not be updated", e);
			}

		}
		return false;
	}

	@Override
	public boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry)
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			// nothing to do here as cart calculation and recalculation are equivalent for synchronous pricing and read new prices from backend
			return false;
		}
		return super.updateCartEntryBasePrice(entry);
	}

	@Override
	public boolean isCartPricingErrorPresent(final ConfigModel configModel)
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			return false;
		}
		return super.isCartPricingErrorPresent(configModel);
	}

	protected SapPricingEnablementService getSapPricingEnablementService()
	{
		return sapPricingEnablementService;
	}

	/**
	 * @param sapPricingEnablementService
	 *           the sapPricingEnablementService to set
	 */
	@Required
	public void setSapPricingEnablementService(final SapPricingEnablementService sapPricingEnablementService)
	{
		this.sapPricingEnablementService = sapPricingEnablementService;
	}
}