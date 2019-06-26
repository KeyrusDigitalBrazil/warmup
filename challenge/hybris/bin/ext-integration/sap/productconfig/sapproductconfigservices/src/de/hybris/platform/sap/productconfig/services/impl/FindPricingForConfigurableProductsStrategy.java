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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.FindPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.util.PriceValue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * CPQ specific sub-class of the {@link FindPricingWithCurrentPriceFactoryStrategy}. This class ensures, that the base
 * price of any configurable cart entry is kept, in case the cart is reclaculated.
 */
public class FindPricingForConfigurableProductsStrategy extends FindPricingWithCurrentPriceFactoryStrategy
{

	private static final Logger LOG = Logger.getLogger(FindPricingForConfigurableProductsStrategy.class);
	private transient CPQConfigurableChecker cpqConfigurableChecker;

	@Override
	public PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		final PriceValue basePrice;
		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(entry.getProduct()))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Keeping old base price for configurable product " + entry.getProduct().getCode());
			}

			final AbstractOrderModel order = entry.getOrder();
			basePrice = new PriceValue(order.getCurrency().getIsocode(), entry.getBasePrice().doubleValue(),
					order.getNet().booleanValue());

		}
		else
		{
			basePrice = super.findBasePrice(entry);
		}
		return basePrice;
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
}
