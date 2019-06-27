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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Builds the Row map for the CSV files for the Cancel Request
 */
public class DefaultOrderCancelRequestContributor implements RawItemContributor<OrderCancelRecordEntryModel>
{

	private Map<String, String> batchIdAttributes;
	
	@Override
	public Set<String> getColumns()
	{
		Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, OrderEntryCsvColumns.ENTRY_NUMBER,
				OrderEntryCsvColumns.REJECTION_REASON, OrderEntryCsvColumns.PRODUCT_CODE));
		columns.addAll(getBatchIdAttributes().keySet());
		
		return columns;
	}
	
	public Map<String, String> getBatchIdAttributes() {
		return batchIdAttributes;
	}

	@Required
	public void setBatchIdAttributes(Map<String, String> batchIdAttributes) {
		this.batchIdAttributes = batchIdAttributes;
	}

	

	@Override
	public List<Map<String, Object>> createRows(final OrderCancelRecordEntryModel orderCancelRequest)
	{
		final OrderModel order = orderCancelRequest.getModificationRecord().getOrder();
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		final CancelReason cancelReason = orderCancelRequest.getCancelReason();

		final List<Map<String, Object>> result = new ArrayList<>();


		for (final AbstractOrderEntryModel entry : entries)
		{
			final Map<String, Object> row = new HashMap<>();
			row.put(OrderCsvColumns.ORDER_ID, order.getCode());
			row.put(OrderEntryCsvColumns.ENTRY_NUMBER, entry.getEntryNumber());
			row.put(OrderEntryCsvColumns.REJECTION_REASON, cancelReason.toString());
			row.put(OrderEntryCsvColumns.PRODUCT_CODE, entry.getProduct().getCode());
			getBatchIdAttributes().forEach(row::putIfAbsent);
			row.put("dh_batchId", order.getCode());

			result.add(row);
		}

		return result;
	}


}
