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
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.warehousing.cancellation.impl.DefaultConsignmentCancellationService;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation to provide business logic for {@link de.hybris.platform.sap.saporderexchangeoms.cancellation.SapConsignmentCancellationService}
 */
public class SapOmsConsignmentCancellationService extends DefaultConsignmentCancellationService implements
		SapConsignmentCancellationService
{

	@Override
	public void processSapConsignmentCancellation(final OrderCancelResponse orderCancelResponse)
	{

		final Map<AbstractOrderEntryModel, Long> orderCancelEntriesCompleted = new HashMap<AbstractOrderEntryModel, Long>();

		for (final OrderCancelEntry orderCancelEntry : orderCancelResponse.getEntriesToCancel())
		{
			Long alreadyCancelledQty = Long.valueOf(0L);

			for (final Map.Entry<AbstractOrderEntryModel, Long> entry : orderCancelEntriesCompleted.entrySet())
			{
				if (entry.getKey().equals(orderCancelEntry.getOrderEntry()))
				{
					alreadyCancelledQty = Long.valueOf(alreadyCancelledQty.longValue() + entry.getValue().longValue());
				}
			}

			if (orderCancelEntry.getCancelQuantity() > alreadyCancelledQty.longValue())
			{
				updateConsigmentStatus(orderCancelResponse, orderCancelEntriesCompleted, orderCancelEntry);
			}
		}

	}

	private void updateConsigmentStatus(OrderCancelResponse orderCancelResponse, Map<AbstractOrderEntryModel, Long> orderCancelEntriesCompleted, OrderCancelEntry orderCancelEntry) {
		for (final ConsignmentEntryModel consignmentEntry : orderCancelEntry.getOrderEntry().getConsignmentEntries())
        {
            if (!consignmentEntry.getConsignment().getStatus().equals(ConsignmentStatus.CANCELLING))
            {
                consignmentEntry.getConsignment().setStatus(ConsignmentStatus.CANCELLING);
                getModelService().save(consignmentEntry.getConsignment());
                orderCancelEntriesCompleted.putAll(cancelConsignment(consignmentEntry.getConsignment(), orderCancelResponse));
            }
        }
	}

}
