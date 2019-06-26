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
package de.hybris.platform.commerceservices.order.hook.impl;

import de.hybris.platform.commerceservices.event.QuoteBuyerOrderPlacedEvent;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Hook for updating quote's state if the placed order was based on a quote
 */
public class CommercePlaceQuoteOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private static final Logger LOG = Logger.getLogger(CommercePlaceQuoteOrderMethodHook.class);
	private EventService eventService;

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter commerceCheckoutParameter,
			final CommerceOrderResult commerceOrderResult)
	{
		final OrderModel order = commerceOrderResult.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);

		// Set quote state for quote order
		final QuoteModel quoteModel = order.getQuoteReference();
		if (quoteModel != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Quote Order has been placed. Quote Code : [%s] , Order Code : [%s]", quoteModel.getCode(),
						order.getCode()));
			}
			final QuoteBuyerOrderPlacedEvent quoteBuyerOrderPlacedEvent = new QuoteBuyerOrderPlacedEvent(order, quoteModel);
			getEventService().publishEvent(quoteBuyerOrderPlacedEvent);
		}
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter commerceCheckoutParameter)
	{
		// not implemented
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter commerceCheckoutParameter,
			final CommerceOrderResult commerceOrderResult)
	{
		// not implemented
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
