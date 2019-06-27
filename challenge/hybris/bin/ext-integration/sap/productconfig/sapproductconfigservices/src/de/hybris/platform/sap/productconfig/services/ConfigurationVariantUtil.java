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
package de.hybris.platform.sap.productconfig.services;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * Utility service for variants of configurable products
 */
public interface ConfigurationVariantUtil
{
	/**
	 * Determines whether a product acts as a base product for variant products.
	 *
	 * @param productModel
	 *           productmodel which is tested for variants
	 * @return true if productmodel has variants
	 */
	boolean isCPQBaseProduct(final ProductModel productModel);

	/**
	 * Checks whether a product is a Variant
	 *
	 * @param productModel
	 *           productmodel which is tested for being a variant
	 * @return true if productmodel represents a variant
	 */
	boolean isCPQVariantProduct(ProductModel productModel);

	/**
	 * returns the base product code for a CPQ variant. call
	 * {@link ConfigurationVariantUtil#isCPQVariantProduct(ProductModel)} to check whether the given product is a
	 * CPQVaraint at all!
	 *
	 * @param variantProductModel
	 *           variant
	 * @return code of the base product of this variant
	 */
	String getBaseProductCode(ProductModel variantProductModel);

	/**
	 * Checks whether a product is a Changeable Variant
	 *
	 * @param productModel
	 *           productmodel which is tested for being a changeable variant
	 * @return true if productmodel represents a changeable variant
	 */
	boolean isCPQChangeableVariantProduct(ProductModel productModel);

	/**
	 * Checks whether a product is a Not Changeable Variant
	 *
	 * @param productModel
	 *           productmodel which is tested for being a not changeable variant
	 * @return true if productmodel represents a not changeable variant
	 */
	boolean isCPQNotChangeableVariantProduct(ProductModel productModel);
}
