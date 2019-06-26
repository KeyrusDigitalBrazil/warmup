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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Provides configuration info for variant products
 */
public interface VariantConfigurationInfoProvider
{
	/**
	 * Retrieves configuration infos for a variant product
	 * 
	 * @param product
	 *           product model
	 * @return list of configuration infos
	 */
	List<ConfigurationInfoData> retrieveVariantConfigurationInfo(final ProductModel product);
}
