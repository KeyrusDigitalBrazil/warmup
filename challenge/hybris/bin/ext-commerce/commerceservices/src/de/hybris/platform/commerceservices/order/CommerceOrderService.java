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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Service interface that provides an API for commerce orders.
 */
public interface CommerceOrderService
{
	/**
	 * If the quote is associated with an order then the associated order will be returned, else null is returned
	 *
	 * @param quoteModel
	 *           the quote model
	 * @return the associated order model if any
	 * @throws IllegalArgumentException
	 *            if quote is null
	 */
	OrderModel getOrderForQuote(QuoteModel quoteModel);
}
