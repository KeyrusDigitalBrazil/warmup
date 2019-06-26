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
package de.hybris.platform.stocknotificationfacades;

import de.hybris.platform.commercefacades.product.data.ProductData;


/**
 * interface of the Stock Notification Facade
 */
public interface StockNotificationFacade
{
	/**
	 * check if current product is watching.
	 *
	 * @param product
	 *           ProductData of the product
	 * @return true the product is watching.
	 */
	boolean isWatchingProduct(ProductData product);

}
