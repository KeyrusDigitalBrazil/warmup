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
package de.hybris.platform.acceleratorservices.dataimport.batch.stock;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.stock.StockService;


/**
 * Adapter to translate a stock import row into a service call to {@link StockService}.
 */
public interface StockImportAdapter
{
	/**
	 * Import a stock value by calling the {@link StockService}.
	 * 
	 * @param cellValue
	 * @param product
	 * @throws IllegalArgumentException
	 *            if the cellValue is empty, null or invalid or the product is null
	 */
	void performImport(String cellValue, Item product);

}
