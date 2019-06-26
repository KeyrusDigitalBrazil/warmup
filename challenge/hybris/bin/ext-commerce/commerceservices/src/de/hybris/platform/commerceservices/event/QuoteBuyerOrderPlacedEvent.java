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
package de.hybris.platform.commerceservices.event;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * Event to indicate that buyer placed a quote order
 */
public class QuoteBuyerOrderPlacedEvent extends AbstractEvent
{
	private final QuoteModel quote;
	private final OrderModel order;

	/**
	 * Default Constructor
	 *
	 * @param order
	 * @param quote
	 */
	public QuoteBuyerOrderPlacedEvent(final OrderModel order, final QuoteModel quote)
	{
		this.order = order;
		this.quote = quote;
	}

	/**
	 * @return the quote
	 */
	public QuoteModel getQuote()
	{
		return quote;
	}

	/**
	 * @return the order
	 */
	public OrderModel getOrder()
	{
		return order;
	}

}
