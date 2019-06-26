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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.strategies.intf.ProductConfigurationCartEntryValidationStrategy;

import org.apache.log4j.Logger;


/**
 * B2C cart validation strategy. Adds a check for the configuration attached to a cart entry
 */
public class ProductConfigurationCartValidationStrategyImpl extends DefaultCartValidationStrategy
{

	private ProductConfigurationCartEntryValidationStrategy productConfigurationCartEntryValidationStrategy;
	private static final Logger LOG = Logger.getLogger(ProductConfigurationCartValidationStrategyImpl.class);

	/**
	 * @return the productConfigurationCartEntryValidationStrategy
	 */
	public ProductConfigurationCartEntryValidationStrategy getProductConfigurationCartEntryValidationStrategy()
	{
		return productConfigurationCartEntryValidationStrategy;
	}

	/**
	 * @param productConfigurationCartEntryValidationStrategy
	 *           the productConfigurationCartEntryValidationStrategy to set
	 */
	public void setProductConfigurationCartEntryValidationStrategy(
			final ProductConfigurationCartEntryValidationStrategy productConfigurationCartEntryValidationStrategy)
	{
		this.productConfigurationCartEntryValidationStrategy = productConfigurationCartEntryValidationStrategy;
	}

	@Override
	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final CartEntryModel cartEntryModel)
	{

		CommerceCartModification modification = super.validateCartEntry(cartModel, cartEntryModel);
		if (modification.getStatusCode().equals(CommerceCartModificationStatus.SUCCESS))
		{
			final CommerceCartModification configurationModification = getProductConfigurationCartEntryValidationStrategy()
					.validateConfiguration(cartEntryModel);
			if (configurationModification != null)
			{
				modification = configurationModification;
			}

		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Validate cart entry for product '" + modification.getEntry().getProduct().getCode() + "' with status '"
					+ modification.getStatusCode() + "'");
		}

		return modification;
	}



}
