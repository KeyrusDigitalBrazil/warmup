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


import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

/**
 * A strategy for validating the cart
 */
public interface CartValidationStrategy
{
	/**
	 * @deprecated Since 5.2. Use {@link #validateCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)} instead.
	 */
	@Deprecated
	List<CommerceCartModification> validateCart(CartModel cartModel);

	List<CommerceCartModification> validateCart(final CommerceCartParameter parameter);

}
