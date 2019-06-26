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

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Strategy for synchronizing the contents of a {@link CartModel} to a {@link QuoteModel} associated with it.
 */
public interface UpdateQuoteFromCartStrategy
{

	/**
	 * Updates the {@link QuoteModel} associated with the given {@link CartModel} with the contents of the cart.
	 * Implementations have to handle cases where no quote is associated with the cart by e.g. throwing a runtime
	 * exception.
	 *
	 * @param cart
	 *           a {@link CartModel} associated with a {@link QuoteModel}.
	 * @return the updated {@link QuoteModel}
	 */
	QuoteModel updateQuoteFromCart(final CartModel cart);
}
