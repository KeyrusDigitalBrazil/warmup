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
package com.sap.hybris.returnsexchange.outbound.impl;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;


public class DefaultPrecedingDocContributor implements RawItemContributor<ReturnRequestModel>
{

	@Override
	public Set<String> getColumns()
	{
		return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, ReturnOrderEntryCsvColumns.PRECEDING_DOCUMENT_ID));
	}

	@Override
	public List<Map<String, Object>> createRows(final ReturnRequestModel model)
	{
		final List<Map<String, Object>> result = new ArrayList<>();
		final Map<String, Object> row = new HashMap<>();
		row.put(OrderCsvColumns.ORDER_ID, model.getCode());
		row.put(ReturnOrderEntryCsvColumns.PRECEDING_DOCUMENT_ID, model.getOrder().getCode());
		result.add(row);
		return result;
	}

}
