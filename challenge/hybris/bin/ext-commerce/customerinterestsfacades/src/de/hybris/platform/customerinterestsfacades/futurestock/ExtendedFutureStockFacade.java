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
package de.hybris.platform.customerinterestsfacades.futurestock;

import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Facade for 'Future Stock Management'.
 */
public interface ExtendedFutureStockFacade
{

	/**
	 * Gets the future product availability for the specified product, for each future date.
	 *
	 * @param productModel
	 *           the product model
	 * @return A list of quantity ordered by date. If there is no availability for this product in the future, an empty
	 *         list is returned.
	 */
	List<FutureStockData> getFutureAvailability(final ProductModel productModel);
}
