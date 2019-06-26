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
package de.hybris.platform.sap.c4c.quote.order.hook.impl;

import org.apache.log4j.Logger;

import de.hybris.platform.commerceservices.order.hook.impl.CommercePlaceQuoteOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.events.C4CQuoteBuyerOrderPlacedEvent;
import de.hybris.platform.servicelayer.util.ServicesUtil;


public class C4CPlaceQuoteOrderMethodHook extends CommercePlaceQuoteOrderMethodHook
{

	private static final Logger LOG = Logger.getLogger(C4CPlaceQuoteOrderMethodHook.class);

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
			final C4CQuoteBuyerOrderPlacedEvent quoteBuyerOrderPlacedEvent = new C4CQuoteBuyerOrderPlacedEvent(order, quoteModel);
			getEventService().publishEvent(quoteBuyerOrderPlacedEvent);
		}
	}
}
