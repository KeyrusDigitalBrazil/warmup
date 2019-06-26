/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sapquoteintegration.outbound.hook;

import org.apache.log4j.Logger;

import com.sap.hybris.sapquoteintegration.events.SapCpiQuoteOrderPlacedEvent;

import de.hybris.platform.commerceservices.order.hook.impl.CommercePlaceQuoteOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class SapCpiQuoteOrderMethodHook extends CommercePlaceQuoteOrderMethodHook
{

	private static final Logger LOG = Logger.getLogger(SapCpiQuoteOrderMethodHook.class);

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter commerceCheckoutParameter,
			final CommerceOrderResult commerceOrderResult)
	{
		final OrderModel order = commerceOrderResult.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);

		// Set quote state for quote order
		final QuoteModel quote= order.getQuoteReference();
		if (quote!= null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Quote Order has been placed. Quote Code : [%s] , Order Code : [%s]", quote.getCode(),
						order.getCode()));
			}
			final SapCpiQuoteOrderPlacedEvent quoteBuyerOrderPlacedEvent = new SapCpiQuoteOrderPlacedEvent(order, quote);
			getEventService().publishEvent(quoteBuyerOrderPlacedEvent);
		}
	}
}