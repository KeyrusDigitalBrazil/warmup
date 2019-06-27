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
package de.hybris.platform.sap.c4c.quote.inbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundOrderHelper;


/**
 * Helper class for creating C4CQuoteBuyerOrderPlacedEvent after receiving confirmation for order.
 */
public class DefaultInboundQuoteOrderedHelper extends DefaultDataHubInboundOrderHelper
{

	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{
		super.processOrderConfirmationFromHub(orderNumber);

		final OrderModel order = readOrder(orderNumber);
		if (order != null && order.getQuoteReference() != null && order.getQuoteReference().getCode() != null)
		{
			final String eventName = C4cquoteConstants.ERP_ORDERCONFIRMATION_EVENT_FOR_C4C_QUOTE + order.getQuoteReference().getCode();
			getBusinessProcessService().triggerEvent(eventName);
		}
	}

}
