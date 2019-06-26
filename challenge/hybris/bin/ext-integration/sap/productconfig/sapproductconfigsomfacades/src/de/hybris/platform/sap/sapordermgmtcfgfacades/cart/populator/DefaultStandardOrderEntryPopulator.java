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
package de.hybris.platform.sap.sapordermgmtcfgfacades.cart.populator;

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;

import org.springframework.beans.factory.annotation.Required;



/**
 * Populates SAP specific attributes we need for the back end downtime scenario
 *
 */
public class DefaultStandardOrderEntryPopulator extends OrderEntryPopulator
{

	private ProductConfigurationSomService productConfigurationService;

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		populateCFGAttributes(source, target);
	}

	/**
	 * Populates configurable attribute and handle from the hybris persistence key
	 *
	 * @param source
	 *           Model
	 * @param target
	 *           DAO for cart entry
	 */
	void populateCFGAttributes(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		final String externalConfiguration = source.getExternalConfiguration();
		final ProductModel product = source.getProduct();
		//Product variants: In this case we must not try to retrieve a configuration model,
		//therefore we need to check the product master as well

		if (product.getSapConfigurable() != null && product.getSapConfigurable().booleanValue() && externalConfiguration != null)
		{
			final boolean configurable = !externalConfiguration.isEmpty();
			target.setConfigurationAttached(configurable);
			if (configurable && (!isConfigurationSessionAvailable(source.getPk().toString())))
			{
				final ConfigModel configModel = productConfigurationService.getConfigModel(source.getProduct().getCode(),
						source.getExternalConfiguration());
				productConfigurationService.setIntoSession(source.getPk().toString(), configModel.getId());
			}
		}
		final PK pk = source.getPk();
		if (pk != null)
		{
			target.setItemPK(pk.toString());
		}
	}


	/**
	 * @param configurationContainer
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationSomService configurationContainer)
	{
		this.productConfigurationService = configurationContainer;

	}

	/**
	 * @return Product Configuration Service
	 */
	public ProductConfigurationSomService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * Returns true if a item with the itemKey has is found in the session
	 *
	 * @param itemKey
	 * @return true if the itemKey is found in the session
	 */
	protected boolean isConfigurationSessionAvailable(final String itemKey)
	{
		return getProductConfigurationService().isInSession(itemKey);
	}

}
