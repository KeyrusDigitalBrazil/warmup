/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.events;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 *
 */
public class SapCpiQuoteOrderPlacedEvent extends AbstractEvent
{
	private final QuoteModel quote;
	private final OrderModel order;

	/**
	 * Default Constructor
	 *
	 * @param order
	 * @param quote
	 */
	public SapCpiQuoteOrderPlacedEvent(final OrderModel order, final QuoteModel quote)
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
