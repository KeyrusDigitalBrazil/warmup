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
package de.hybris.platform.sap.orderexchange.datahub.inbound.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.sap.orderexchange.cancellation.SapOrderCancelService;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;

/**
 * Default Data Hub Inbound helper for order related notifications
 */
public class DefaultDataHubInboundOrderHelper extends AbstractDataHubInboundHelper implements DataHubInboundOrderHelper
{
	private SapOrderCancelService sapOrderCancelService;

	@SuppressWarnings("javadoc")
	public SapOrderCancelService getSapOrderCancelService()
	{
		return sapOrderCancelService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setSapOrderCancelService(final SapOrderCancelService sapOrderCancelService)
	{
		this.sapOrderCancelService = sapOrderCancelService;
	}

	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{
		final String eventName = DataHubInboundConstants.ERP_ORDER_CONFIRMATION_EVENT + orderNumber;
		getBusinessProcessService().triggerEvent(eventName);
	}

	protected void processOrderCancellationFromHub(final String orderNumber, final String reason) throws OrderCancelException
	{
		final OrderModel order = readOrder(orderNumber);
		sapOrderCancelService.cancelOrder(order, reason);
	}

	@Override
	public void cancelOrder(final String orderInformation, final String orderCode) throws ImpExException
	{
		try
		{
			processOrderCancellationFromHub(orderCode, orderInformation);
		}
		catch (final OrderCancelException e)
		{
			throw new ImpExException(e);
		}
	}
}
