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
package de.hybris.platform.sap.saporderexchangeoms.outbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderCancelRequestContributor;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;


/**
 * Builds the row map for the CSV files for the SAP OMS order cancel request
 */
public class SapOmsOrderCancelRequestContributor extends DefaultOrderCancelRequestContributor
{
	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	@Override
	public Set<String> getColumns()
	{
		final Set<String> columns = super.getColumns();
		columns.addAll(Arrays.asList(OrderCsvColumns.LOGICAL_SYSTEM));
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderCancelRecordEntryModel orderCancelRequest)
	{

		final OrderModel order = orderCancelRequest.getModificationRecord().getOrder();
		final List<Map<String, Object>> result = new ArrayList<>();

		order.getSapOrders()
				.stream()
				.forEach(
						sapOrder -> sapOrder
								.getConsignments()
								.stream()
								.forEach(consignment -> {
									// Read customizing data from the base store configuration
										final SAPPlantLogSysOrgModel saplogSysSalesOrg = getSapPlantLogSysOrgService()
												.getSapPlantLogSysOrgForPlant(order.getStore(), consignment.getWarehouse().getCode());

										consignment
												.getConsignmentEntries()
												.stream()
												.forEach(
														consignmentEntry -> addRow(consignmentEntry, saplogSysSalesOrg, orderCancelRequest,
																result));
									}));

		return result;

	}


	/**
	 * @param consignmentEntry
	 * @param orderCancelRequest
	 * @param result
	 */
	protected void addRow(final ConsignmentEntryModel consignmentEntry, final SAPPlantLogSysOrgModel saplogSysSalesOrg,//
			final OrderCancelRecordEntryModel orderCancelRequest, final List<Map<String, Object>> result)
	{
		final Map<String, Object> row = new HashMap<>();

		row.put(OrderCsvColumns.ORDER_ID, consignmentEntry.getConsignment().getSapOrder().getCode());
		row.put(OrderEntryCsvColumns.REJECTION_REASON, orderCancelRequest.getCancelReason().toString());

		row.put(OrderEntryCsvColumns.ENTRY_NUMBER, Integer.valueOf(consignmentEntry.getSapOrderEntryRowNumber() - 1));
		row.put(OrderEntryCsvColumns.PRODUCT_CODE, consignmentEntry.getOrderEntry().getProduct().getCode());

		row.put(OrderCsvColumns.LOGICAL_SYSTEM, saplogSysSalesOrg.getLogSys().getSapLogicalSystemName());

		result.add(row);
	}

	protected SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	@Required
	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}


}
