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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.product.ProductService;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;

/**
 * Predicate to test if the product code exists.
 * <p>
 * Returns <tt>TRUE</tt> if the given product code exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class ProductCodeExistsPredicate implements Predicate<String>
{
	private ProductService productService;

	@Override
	@SuppressWarnings("squid:S1166")
	public boolean test(String productCode)
	{
		try
		{
			getProductService().getProductForCode(productCode);
			return true;
		}
		catch (RuntimeException e)
		{
			return false;
		}
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(ProductService productService)
	{
		this.productService = productService;
	}
}
