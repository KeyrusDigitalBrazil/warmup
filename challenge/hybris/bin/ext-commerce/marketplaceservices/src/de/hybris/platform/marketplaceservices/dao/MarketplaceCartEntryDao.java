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
package de.hybris.platform.marketplaceservices.dao;

import de.hybris.platform.commerceservices.order.dao.CartEntryDao;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;


/**
 * Dao to find order related data
 */
public interface MarketplaceCartEntryDao extends CartEntryDao
{
	/**
	 * Find entries in given cart that include unsaleable product
	 *
	 * @param cart
	 *           CartModel
	 * @return list of entries in given cart
	 */
	List<CartEntryModel> findUnSaleableCartEntries(CartModel cart);
}
