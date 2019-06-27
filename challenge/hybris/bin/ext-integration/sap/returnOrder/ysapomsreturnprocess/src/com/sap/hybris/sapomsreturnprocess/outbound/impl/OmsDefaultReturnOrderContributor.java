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
package com.sap.hybris.sapomsreturnprocess.outbound.impl;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.hybris.returnsexchange.outbound.impl.DefaultReturnOrderContributor;


/**
 *
 */
public class OmsDefaultReturnOrderContributor extends DefaultReturnOrderContributor
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
	public List<Map<String, Object>> createRows(final ReturnRequestModel returnRequest)
	{

		final List<Map<String, Object>> rows = super.createRows(returnRequest);
		return enhanceRowForOmsReturn(returnRequest, rows);


	}

	/**
	 * @param returnRequest
	 *
	 */
	private List<Map<String, Object>> enhanceRowForOmsReturn(final ReturnRequestModel returnRequest,
			final List<Map<String, Object>> rows)
	{
		// There is only one row on order level
		final Map<String, Object> row = rows.get(0);
		final SAPSalesOrganizationModel sapSalesOrganization = returnRequest.getSapSalesOrganization();
		if (sapSalesOrganization != null)
		{
			row.put(OrderCsvColumns.LOGICAL_SYSTEM, returnRequest.getSapLogicalSystem());
			row.put(OrderCsvColumns.SALES_ORGANIZATION, sapSalesOrganization.getSalesOrganization());
			row.put(OrderCsvColumns.DISTRIBUTION_CHANNEL, sapSalesOrganization.getDistributionChannel());
			row.put(OrderCsvColumns.DIVISION, sapSalesOrganization.getDivision());
		}
		return rows;


	}

}
