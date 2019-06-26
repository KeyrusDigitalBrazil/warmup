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
package de.hybris.platform.sap.productconfig.b2bservices.strategies.impl;

import de.hybris.platform.b2bacceleratorservices.strategies.impl.DefaultB2BCartValidationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.strategies.intf.ProductConfigurationCartEntryValidationStrategy;

import org.apache.log4j.Logger;


/**
 * CPQ default implementation of B2B cart validation
 */
public class ProductConfigurationB2BCartValidationStrategyImpl extends DefaultB2BCartValidationStrategy
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationB2BCartValidationStrategyImpl.class);

	private ProductConfigurationCartEntryValidationStrategy productConfigurationCartEntryValidationStrategy;

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
			if (null != configurationModification)
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
