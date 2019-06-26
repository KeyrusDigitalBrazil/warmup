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
package de.hybris.platform.sap.saporderexchangeomsb2b.outbound.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchangeb2b.outbound.impl.DefaultB2BOrderContributor;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;

public class SapOmsB2BOrderContributor extends DefaultB2BOrderContributor {

	@Override
	public Set<String> getColumns() {
		
		final Set<String> columns = super.getColumns();
		
		columns.addAll(Arrays.asList(OrderCsvColumns.LOGICAL_SYSTEM, OrderCsvColumns.SALES_ORGANIZATION,
				OrderCsvColumns.DISTRIBUTION_CHANNEL, OrderCsvColumns.DIVISION));
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order) {
		final List<Map<String, Object>> rows = super.createRows(order);
		return enhanceRowsByOmsB2BFields(order, rows);
	}

	protected List<Map<String, Object>> enhanceRowsByOmsB2BFields(final OrderModel order, final List<Map<String, Object>> rows) {

		final Map<String, Object> row = rows.get(0);
		final SAPSalesOrganizationModel sapSalesOrganization = order.getSapSalesOrganization();
		
		if (sapSalesOrganization != null) {
			
			row.put(OrderCsvColumns.LOGICAL_SYSTEM, order.getSapLogicalSystem());
			row.put(OrderCsvColumns.SALES_ORGANIZATION, sapSalesOrganization.getSalesOrganization());
			row.put(OrderCsvColumns.DISTRIBUTION_CHANNEL, sapSalesOrganization.getDistributionChannel());
			row.put(OrderCsvColumns.DIVISION, sapSalesOrganization.getDivision());
			
		}

		return rows;
	}

}
