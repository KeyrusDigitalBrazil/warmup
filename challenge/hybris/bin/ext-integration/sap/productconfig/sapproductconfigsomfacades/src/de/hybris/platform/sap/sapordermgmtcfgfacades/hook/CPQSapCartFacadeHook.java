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
package de.hybris.platform.sap.sapordermgmtcfgfacades.hook;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtb2bfacades.hook.SapCartFacadeHook;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Release session before cart entry update
 */
public class CPQSapCartFacadeHook implements SapCartFacadeHook
{

	private ProductConfigurationSomService productConfigurationSomService;
	private ProductConfigurationService productConfigurationService;

	@Override
	public void beforeCartEntryUpdate(final long quantity, final long entryNumber, final List<OrderEntryData> entries)
	{
		String itemKey = "";
		for (final OrderEntryData entry : entries)
		{
			if (entry.getEntryNumber().longValue() == entryNumber)
			{
				itemKey = entry.getItemPK();
			}
		}
		if (quantity == 0)
		{
			final String configId = getProductConfigurationSomService().getGetConfigId(itemKey);
			if (configId != null)
			{
				getProductConfigurationService().releaseSession(configId);
			}
		}

	}

	protected ProductConfigurationSomService getProductConfigurationSomService()
	{
		return productConfigurationSomService;
	}

	/**
	 * @param productConfigurationSomService
	 *           the productConfigurationSomService to set
	 */
	@Required
	public void setProductConfigurationSomService(final ProductConfigurationSomService productConfigurationSomService)
	{
		this.productConfigurationSomService = productConfigurationSomService;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

}
