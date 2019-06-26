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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Strategy to calculate cart.
 */
public interface CommerceCartCalculationStrategy
{
	/**
	 * @deprecated Since 5.2. Use
	 *             {@link #calculateCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 */
	@Deprecated
	boolean calculateCart(final CartModel cartModel);

	/**
	 * @deprecated Since 5.2. Use
	 *             {@link #recalculateCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 */
	@Deprecated
	boolean recalculateCart(final CartModel cartModel);

	/**
	 * Calculate cart.
	 *
	 * @param parameter
	 *           the parameter
	 * @return true, if successful
	 */
	boolean calculateCart(final CommerceCartParameter parameter);

	/**
	 * Recalculate cart.
	 *
	 * @param parameter
	 *           the parameter
	 * @return true, if successful
	 */
	boolean recalculateCart(final CommerceCartParameter parameter);


}
