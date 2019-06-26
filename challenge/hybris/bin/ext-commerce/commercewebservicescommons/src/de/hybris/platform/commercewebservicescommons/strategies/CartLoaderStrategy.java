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
package de.hybris.platform.commercewebservicescommons.strategies;

/**
 * Strategy loading user cart for current session
 */
public interface CartLoaderStrategy
{
	/**
	 * Loads cart for current session
	 *
	 * @param cartId
	 *           Cart identifier (can be guid or code)
	 */
	void loadCart(final String cartId);

	/**
	 * Loads cart for current session
	 *
	 * @param cartId
	 *           Cart identifier (can be guid or code)
	 * @param refresh
	 *           Define if cart should be refreshed (recalculated). Refreshing cart can change it.
	 *
	 */
	default void loadCart(final String cartId, final boolean refresh)
	{
		loadCart(cartId);
	}
}
