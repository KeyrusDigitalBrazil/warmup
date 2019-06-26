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

import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.impl.executors.WarehouseResponseExecutor;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.warehousing.cancellation.ConsignmentCancellationService;
import de.hybris.platform.warehousing.cancellation.OmsOrderCancelService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class SapOmsWarehouseResponseExecutor extends WarehouseResponseExecutor
{
	private OmsOrderCancelService omsOrderCancelService;
	private ConsignmentCancellationService consignmentCancellationService;
	private SapConsignmentCancellationService sapConsignmentCancellationService;

	@Override
	public void processCancelResponse(final OrderCancelResponse orderCancelResponse,
			final OrderCancelRecordEntryModel cancelRequestRecordEntry) throws OrderCancelException
	{
		// Cancel first the unallocated quantities if existing
		final List<OrderCancelEntry> allocatedEntries = getOmsOrderCancelService().processOrderCancel(cancelRequestRecordEntry);
		final OrderCancelResponse updatedOrderCancelResponse = new OrderCancelResponse(orderCancelResponse.getOrder(),
				allocatedEntries);

		// Cancel order entries
		super.processCancelResponse(updatedOrderCancelResponse, cancelRequestRecordEntry);

		// Then process the cancellation of the consignments
		getSapConsignmentCancellationService().processSapConsignmentCancellation(orderCancelResponse);
	}

	protected SapConsignmentCancellationService getSapConsignmentCancellationService()
	{
		return sapConsignmentCancellationService;
	}

	@Required
	public void setSapConsignmentCancellationService(final SapConsignmentCancellationService sapConsignmentCancellationService)
	{
		this.sapConsignmentCancellationService = sapConsignmentCancellationService;
	}

	protected OmsOrderCancelService getOmsOrderCancelService()
	{
		return omsOrderCancelService;
	}

	@Required
	public void setOmsOrderCancelService(final OmsOrderCancelService omsOrderCancelService)
	{
		this.omsOrderCancelService = omsOrderCancelService;
	}

	protected ConsignmentCancellationService getConsignmentCancellationService()
	{
		return consignmentCancellationService;
	}

	@Required
	public void setConsignmentCancellationService(final ConsignmentCancellationService consignmentCancellationService)
	{
		this.consignmentCancellationService = consignmentCancellationService;
	}

}
