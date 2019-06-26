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
package de.hybris.platform.commerceservices.strategies;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;


public interface ProductReferenceTargetStrategy
{
	/**
	 * Gets the target product for the given product reference.
	 * 
	 * Should return either the a proposed target product or null if this strategy cannot offer a better target than the
	 * default.
	 * 
	 * @param sourceProduct
	 *           the source product
	 * @param reference
	 *           the product reference
	 * @return the target product
	 */
	ProductModel getTarget(ProductModel sourceProduct, ProductReferenceModel reference);
}
