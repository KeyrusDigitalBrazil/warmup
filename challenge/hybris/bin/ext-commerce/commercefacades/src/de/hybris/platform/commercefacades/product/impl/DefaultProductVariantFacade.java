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
package de.hybris.platform.commercefacades.product.impl;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Iterables;


/**
 * Extension of {@link DefaultProductFacade} that works with a product's variants.
 */
public class DefaultProductVariantFacade extends DefaultProductFacade<ProductModel>
{
	/**
	 * @deprecated Since 6.2.
	 */
	@Deprecated
	@Override
	public ProductData getProductForOptions(final ProductModel productModel, final Collection<ProductOption> options)
	{
		if (CollectionUtils.isNotEmpty(options) && options.contains(ProductOption.VARIANT_FIRST_VARIANT)
				&& CollectionUtils.isNotEmpty(productModel.getVariants()))
		{
			final ProductModel firstVariant = Iterables.get(productModel.getVariants(), 0);
			return super.getProductForOptions(firstVariant, options);
		}
		else
		{
			return super.getProductForOptions(productModel, options);
		}
	}

}
