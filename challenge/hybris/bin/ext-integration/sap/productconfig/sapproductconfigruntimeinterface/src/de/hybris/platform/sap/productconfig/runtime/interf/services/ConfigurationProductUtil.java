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
package de.hybris.platform.sap.productconfig.runtime.interf.services;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * Utility service for non-configurable aspects of products
 */
public interface ConfigurationProductUtil
{

	/**
	 * Retrieves a product for the currently active catalog version
	 *
	 * @param productCode
	 *           product code
	 * @return product model
	 */
	ProductModel getProductForCurrentCatalog(String productCode);
}
