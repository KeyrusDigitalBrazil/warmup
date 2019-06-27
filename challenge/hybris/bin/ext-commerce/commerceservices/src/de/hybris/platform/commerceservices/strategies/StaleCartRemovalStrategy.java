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
package de.hybris.platform.commerceservices.strategies;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * 
 * Strategy to decide which carts to remove.
 * 
 */
public interface StaleCartRemovalStrategy
{
	/**
	 * @deprecated Since 5.2. Use {@link #removeStaleCarts(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)} instead.
	 * @param currentCart
	 * @param baseSite
	 * @param user
	 */
	@Deprecated
	void removeStaleCarts(CartModel currentCart, BaseSiteModel baseSite, UserModel user);

	void removeStaleCarts(final CommerceCartParameter parameters);
}
