/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 *
 */
package de.hybris.platform.ordermanagementfacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * Ordermanagement Populator for the product data with the most basic product data
 */
public class OrdermanagementProductBasicPopulator extends ProductBasicPopulator
{
	@Override
	public void populate(final ProductModel productModel, final ProductData productData)
	{
		if(productModel != null && productData != null)
		{
			productData.setName((String) getProductAttribute(productModel, ProductModel.NAME));
			productData.setManufacturer((String) getProductAttribute(productModel, ProductModel.MANUFACTURERNAME));
			productData.setCode(productModel.getCode());
			productData.setAverageRating(productModel.getAverageRating());
			productData.setPurchasable(productModel.getVariantType() == null && isApproved(productModel));
		}
	}
}
