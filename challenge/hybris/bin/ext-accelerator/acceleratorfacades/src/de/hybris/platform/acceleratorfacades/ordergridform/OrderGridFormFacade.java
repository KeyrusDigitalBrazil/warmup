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
package de.hybris.platform.acceleratorfacades.ordergridform;


import de.hybris.platform.acceleratorfacades.product.data.ReadOnlyOrderGridData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Interface that can be used/implemented to populate readonly order grid for multi-d products.
 */
public interface OrderGridFormFacade
{
	/**
	 * Populates the readonly order grid
	 *
	 * @param orderEntryDataList
	 * @return map containing grouped ReadOnlyOrderGridDatas based on the category
	 */
	Map<String, ReadOnlyOrderGridData> getReadOnlyOrderGrid(final List<OrderEntryData> orderEntryDataList);

	/**
	 * Populates the read-only order grid for the specified multi-d product, the collection of product options, and if
	 * the product is contained in the entries of the specified AbstractOrder.
	 *
	 * @param productCode
	 * @param productOptions
	 *           a collection of ProductOption used to retrieve the product.
	 * @param abstractOrderData
	 * @return map containing grouped ReadOnlyOrderGridDatas based on the category
	 */
	Map<String, ReadOnlyOrderGridData> getReadOnlyOrderGridForProductInOrder(final String productCode,
			Collection<ProductOption> productOptions, final AbstractOrderData abstractOrderData);
}
