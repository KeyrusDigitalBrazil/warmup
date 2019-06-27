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
package de.hybris.platform.marketplaceservices.product;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Service with VendorUser related methods
 */
public interface MarketplaceProductService
{
	/**
	 * Get all product by vendor
	 *
	 * @param vendorCode
	 *           code of vendor
	 * @return list of products belong to given vendor
	 */
	List<ProductModel> getAllProductByVendor(String vendorCode);
}
