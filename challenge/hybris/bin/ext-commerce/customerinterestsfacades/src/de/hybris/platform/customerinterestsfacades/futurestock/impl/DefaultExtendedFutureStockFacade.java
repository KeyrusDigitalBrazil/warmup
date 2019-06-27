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
package de.hybris.platform.customerinterestsfacades.futurestock.impl;

import de.hybris.platform.acceleratorfacades.futurestock.impl.DefaultFutureStockFacade;
import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerinterestsfacades.futurestock.ExtendedFutureStockFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.assertj.core.util.Lists;


/**
 * Default implementation for {@link ExtendedFutureStockFacade}
 */
public class DefaultExtendedFutureStockFacade extends DefaultFutureStockFacade implements ExtendedFutureStockFacade
{
	@Override
	public List<FutureStockData> getFutureAvailability(final ProductModel productModel)
	{
		final List<ProductModel> products = Lists.newArrayList(productModel);
		final Map<String, Map<Date, Integer>> productsMap = getFutureStockService().getFutureAvailability(products);

		if (Objects.isNull(productsMap) || productsMap.isEmpty())
		{
			return new ArrayList();
		}

		final Map<String, List<FutureStockData>> result = new HashMap<>();
		mapMap2MapList(products, result, productsMap);
		return result.get(productModel.getCode());
	}
}
