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
package de.hybris.platform.sap.sappricing.services;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;

/**
 *	Sap Pricing Catalog service.
 */
public interface SapPricingCatalogService extends PriceService
{
	/**
	 * Method to get price information for products 
	 * 
	 * @param models List<ProductModel>
	 * @return List<PriceInformation>
	 */
	public List<PriceInformation> getPriceInformationForProducts(List<ProductModel> models);

}
