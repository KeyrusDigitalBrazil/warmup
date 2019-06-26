/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;

import java.util.Collection;
import java.util.List;


/**
 * Interface to manage configurations of single type.
 */
public interface ProductConfigurationHandler
{
	/**
	 * Create default set of {@code AbstractOrderEntryProductInfoModel} for given configurable product.
	 *
	 * @param productSettings
	 *           default configuration settings
	 * @return list of {@code AbstractOrderEntryProductInfoModel} filled with default values.
	 */
	List<AbstractOrderEntryProductInfoModel> createProductInfo(AbstractConfiguratorSettingModel productSettings);

	/**
	 * Converts a set of {@code ProductConfigurationItem} into a list of {@code AbstractOrderEntryProductInfoModel}.
	 *
	 * @param items
	 *           configurator setting DTOs
	 * @param entry
	 *           entry the settings will be added to (for reference)
	 * @return particular specialization of product info model, set up from {@code items}
	 */
	List<AbstractOrderEntryProductInfoModel> convert(Collection<ProductConfigurationItem> items, AbstractOrderEntryModel entry);
}
