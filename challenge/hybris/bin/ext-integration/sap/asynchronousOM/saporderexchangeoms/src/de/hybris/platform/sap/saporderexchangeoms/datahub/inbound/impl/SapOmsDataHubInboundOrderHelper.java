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
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl;


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundOrderHelper;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.constants.SapOmsOrderExchangeConstants;


/**
 * Default data-hub inbound helper for OMS order related notifications with multiple back-ends
 */
public class SapOmsDataHubInboundOrderHelper extends DefaultDataHubInboundOrderHelper
{

	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{

		final SAPOrderModel sapOrder = readSapOrder(orderNumber);
		sapOrder.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);
		getModelService().save(sapOrder);

		final String event = new StringBuilder()//
				.append(DataHubInboundConstants.ERP_ORDER_CONFIRMATION_EVENT)//
				.append(sapOrder.getOrder().getCode())//
				.toString();

		getBusinessProcessService().triggerEvent(event);

	}

	@Override
	public void cancelOrder(final String orderInformation, final String orderCode) throws ImpExException
	{
		final SAPOrderModel sapOrder = readSapOrder(orderCode);
		sapOrder.setSapOrderStatus(SAPOrderStatus.CANCELLED_FROM_ERP);
		getModelService().save(sapOrder);


		sapOrder.getOrder().getConsignments().stream().forEach(

				consignment -> consignment.getSapConsignmentProcesses().stream().forEach(process -> {

					final String eventId = new StringBuilder()//
							.append(process.getCode())//
							.append(DataHubInboundConstants.UNDERSCORE)//
							.append(SapOmsOrderExchangeConstants.CONSIGNMENT_ACTION_EVENT_NAME)//
							.toString();

					final BusinessProcessEvent event = BusinessProcessEvent.builder(eventId)
							.withChoice(SapOmsOrderExchangeConstants.CONSIGNMENT_PROCESS_CANCELLED).build();


					getBusinessProcessService().triggerEvent(event);

				})

				);

		processOrderCancellationFromDatahub(sapOrder.getOrder(), orderInformation);


	}

	protected void processOrderCancellationFromDatahub(final OrderModel order, final String reason) throws ImpExException
	{
		try
		{
			getSapOrderCancelService().cancelOrder(order, reason);
		}
		catch (final OrderCancelException e)
		{
			throw new ImpExException(e);
		}
	}

}
