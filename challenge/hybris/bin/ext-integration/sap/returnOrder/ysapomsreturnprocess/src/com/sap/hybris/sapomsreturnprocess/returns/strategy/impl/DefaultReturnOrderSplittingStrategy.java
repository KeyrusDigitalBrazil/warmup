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
package com.sap.hybris.sapomsreturnprocess.returns.strategy.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sap.hybris.sapomsreturnprocess.returns.strategy.ReturnOrderStartegy;


public class DefaultReturnOrderSplittingStrategy implements ReturnOrderStartegy
{
	@Override
	public void splitOrder(final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnOrderEntryConsignmentListMap)
	{
		for (final Map.Entry<ReturnEntryModel, List<ConsignmentEntryModel>> entry : returnOrderEntryConsignmentListMap.entrySet())
		{
			final Long expectedQuantity = entry.getKey().getExpectedQuantity();
			List<ConsignmentEntryModel> consignmentList = entry.getValue();

			consignmentList = distributeQuantityInMultipleConsignments(expectedQuantity, consignmentList);

			setRefundAmountForEachConsignmentEntry(entry, consignmentList);
			entry.setValue(consignmentList);
			
		}
	}

	/**
	 *
	 *This method calculates the refund amount to be sent to each backend. 
	 *This is calculated by multiplying the total refund amount of each 
	 *refund entry with the ratio of the return quantity for each consignment
	 *entry to that of total quantity to be returned in the return request. 
	 *
	 */
	protected void setRefundAmountForEachConsignmentEntry(final Map.Entry<ReturnEntryModel, List<ConsignmentEntryModel>> entry,
			final List<ConsignmentEntryModel> consignmentList)
	{
		final RefundEntryModel refundEntry = (RefundEntryModel) entry.getKey();
		for (final ConsignmentEntryModel consignmentEntry : consignmentList)
		{
			final BigDecimal ratioOfQtyForConsignmentEntryToTotalReturnQty = BigDecimal.valueOf(consignmentEntry.getReturnQuantity().floatValue()
					/ refundEntry.getExpectedQuantity().longValue());
			consignmentEntry.setAmount(refundEntry.getAmount().multiply(ratioOfQtyForConsignmentEntryToTotalReturnQty).setScale(2, BigDecimal.ROUND_HALF_UP));
		}
	}

	/**
	 *
	 */
	private List<ConsignmentEntryModel> distributeQuantityInMultipleConsignments(final Long expectedQuantity,
			final List<ConsignmentEntryModel> consignmentList)
	{
		final List<ConsignmentEntryModel> consignmentListTemp = new ArrayList<>();

		Long totalReturnQuantity = Long.valueOf(0L);

		for (int i = 0; i <= consignmentList.size() - 1; i++)
		{
			final long returnableQuantity = consignmentList.get(i).getQuantityShipped()
					- consignmentList.get(i).getQuantityReturnedUptil();
			if ((expectedQuantity - totalReturnQuantity) <= returnableQuantity)
			{
				consignmentListTemp.add(consignmentList.get(i));
				consignmentList.get(i).setQuantityReturnedUptil(
						expectedQuantity - totalReturnQuantity + consignmentList.get(i).getQuantityReturnedUptil());
				consignmentList.get(i).setReturnQuantity(expectedQuantity - totalReturnQuantity);

				break;
			}
			else
			{

				if (returnableQuantity > 0)
				{
					consignmentListTemp.add(consignmentList.get(i));
					totalReturnQuantity = totalReturnQuantity + returnableQuantity;
					consignmentList.get(i).setQuantityReturnedUptil(consignmentList.get(i).getQuantityShipped());
					consignmentList.get(i).setReturnQuantity(returnableQuantity);
				}
			}
		}
		return consignmentListTemp;
	}
}
