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
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderContributor;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;




/**
 * Builds the Row map for the CSV files for the OMS Order with multiple back-ends
 */
public class SapOmsOrderContributor extends DefaultOrderContributor
{

	@Override
	public Set<String> getColumns()
	{
		final Set<String> columns = super.getColumns();
		columns.addAll(Arrays.asList(OrderCsvColumns.LOGICAL_SYSTEM, OrderCsvColumns.SALES_ORGANIZATION,
				OrderCsvColumns.DISTRIBUTION_CHANNEL, OrderCsvColumns.DIVISION));
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{

		final Map<String, Object> row = new HashMap<>();

		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(OrderCsvColumns.DATE, order.getDate());
		row.put(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		row.put(OrderCsvColumns.BASE_STORE, order.getStore().getUid());

		final DeliveryModeModel deliveryMode = order.getDeliveryMode();
		row.put(OrderCsvColumns.DELIVERY_MODE, deliveryMode != null ? deliveryMode.getCode() : "");

		final SAPSalesOrganizationModel sapSalesOrganization = order.getSapSalesOrganization();
		row.put(OrderCsvColumns.LOGICAL_SYSTEM, order.getSapLogicalSystem());
		row.put(OrderCsvColumns.SALES_ORGANIZATION, sapSalesOrganization.getSalesOrganization());
		row.put(OrderCsvColumns.DISTRIBUTION_CHANNEL, sapSalesOrganization.getDistributionChannel());
		row.put(OrderCsvColumns.DIVISION, sapSalesOrganization.getDivision());

		getBatchIdAttributes().forEach(row::putIfAbsent);
		row.put("dh_batchId", order.getCode());

		return Arrays.asList(row);

	}

}
