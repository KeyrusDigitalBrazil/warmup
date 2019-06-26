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

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.CPQConfiguratorSettingsModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * CPQ implementaion of the {@link ProductConfigurationHandler}.<br>
 * ensures that the {@link ConfiguratorType#CPQCONFIGURATOR} is set for the product infos of cpq-configurable products
 */
public class CPQConfigurationHandler implements ProductConfigurationHandler
{
	private ProductConfigurationService productConfigurationService;

	@Override
	public List<AbstractOrderEntryProductInfoModel> createProductInfo(final AbstractConfiguratorSettingModel productSettings)
	{
		if (productSettings instanceof CPQConfiguratorSettingsModel)
		{
			final CPQOrderEntryProductInfoModel result = new CPQOrderEntryProductInfoModel();
			result.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
			return Collections.singletonList(result);
		}
		else
		{
			throw new IllegalArgumentException("Argument must be a type of CPQConfiguratorSettingsModel");
		}
	}

	@Override
	public List<AbstractOrderEntryProductInfoModel> convert(final Collection<ProductConfigurationItem> items,
			final AbstractOrderEntryModel entry)
	{
		return Collections.emptyList();
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}
}
