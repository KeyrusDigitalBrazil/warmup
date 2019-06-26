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

import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderCancelResponseExecutor;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;

/**
 * Concrete implementation to provide SAP OMS integration to DefaultOrderCancelService
 */
public class SapOmsOrderCancelCallbackService extends DefaultOrderCancelService
{

	@Override
	public void onOrderCancelResponse(final OrderCancelResponse cancelResponse) throws OrderCancelException
	{

		final OrderCancelRecordModel ocrm = getCancelRecordForOrder(cancelResponse.getOrder());

		if (ocrm.getModificationRecordEntries().stream()
				.allMatch(omrem -> omrem.getStatus().equals(OrderModificationEntryStatus.SUCCESSFULL)))
		{
			return;
		}

		if (ocrm.isInProgress())
		{
			OrderModificationRecordEntryModel pendingRecord = null;
			for (final OrderModificationRecordEntryModel omrem : ocrm.getModificationRecordEntries())
			{
				if (OrderModificationEntryStatus.INPROGRESS != omrem.getStatus())
				{
					continue;
				}
				if (pendingRecord != null)
				{
					throw new IllegalStateException("more than one pending cancel requests for given order found");
				}
				pendingRecord = omrem;
			}

			if (pendingRecord == null)
			{
				throw new IllegalArgumentException("No pending cancel requests for given order found");
			}

			final OrderCancelState currentCancelState = this.getStateMappingStrategy().getOrderCancelState(pendingRecord
					.getModificationRecord().getOrder());

			final OrderCancelResponseExecutor ocre = this.getResponseExecutorsMap().get(currentCancelState);


			if (ocre == null)
			{
				throw new IllegalStateException("Cannot find response executor for cancel state: "
						+ currentCancelState.name());
			}

			ocre.processCancelResponse(cancelResponse, (OrderCancelRecordEntryModel) pendingRecord);
		}
		else
		{
			throw new IllegalArgumentException("No pending cancel requests for given order found");
		}
	}

}
