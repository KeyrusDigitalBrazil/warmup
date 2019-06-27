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
package de.hybris.platform.selectivecartfacades.strategies;

import de.hybris.platform.commercefacades.order.data.CartData;


/**
 * Orders cart entries when displaying the cart page
 */
public interface CartEntriesOrderingStrategy
{

	/**
	 * Orders cart entries
	 *
	 * @param cartData
	 *           the cart data with entries to be sorted
	 * @return the cart data with correct ordering
	 */
	CartData ordering(CartData cartData);
}
