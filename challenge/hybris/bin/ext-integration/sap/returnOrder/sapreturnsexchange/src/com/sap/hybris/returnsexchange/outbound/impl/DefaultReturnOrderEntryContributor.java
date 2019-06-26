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

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;


public class DefaultReturnOrderEntryContributor implements RawItemContributor<ReturnRequestModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultReturnOrderEntryContributor.class);

	@Override
	public Set<String> getColumns()
	{
		return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, OrderEntryCsvColumns.ENTRY_NUMBER,
				OrderEntryCsvColumns.QUANTITY, OrderEntryCsvColumns.REJECTION_REASON, OrderEntryCsvColumns.NAMED_DELIVERY_DATE,
				OrderEntryCsvColumns.ENTRY_UNIT_CODE, OrderEntryCsvColumns.PRODUCT_CODE, ReturnOrderEntryCsvColumns.WAREHOUSE));
	}

	@Override
	public List<Map<String, Object>> createRows(final ReturnRequestModel returnRequest)
	{
		final List<ReturnEntryModel> entries = returnRequest.getReturnEntries();
		final List<Map<String, Object>> result = new ArrayList<>();

		for (final ReturnEntryModel returnEntry : entries)
		{
			result.add(createReturnEntryRow(returnEntry));
		}
		return result;
	}

	protected String determineItemShortText(final AbstractOrderEntryModel item, final String language)
	{
		final String shortText = item.getProduct().getName(new java.util.Locale(language));
		return shortText == null ? "" : shortText;
	}

	protected Map<String, Object> createReturnEntryRow(final ReturnEntryModel returnEntry)
	{
		final AbstractOrderEntryModel returnItem = returnEntry.getOrderEntry();
		final Map<String, Object> row = new HashMap<>();

		row.put(OrderCsvColumns.ORDER_ID, returnEntry.getReturnRequest().getCode());
		row.put(OrderEntryCsvColumns.ENTRY_NUMBER, returnItem.getEntryNumber());
		row.put(OrderEntryCsvColumns.QUANTITY, returnEntry.getExpectedQuantity());
		row.put(OrderEntryCsvColumns.PRODUCT_CODE, returnItem.getProduct().getCode());
		row.put(OrderEntryCsvColumns.REJECTION_REASON, returnEntry.getOrderEntry().getOrder().getStore().getSAPConfiguration().getReturnOrderReason());
		final UnitModel unit = returnEntry.getOrderEntry().getProduct().getUnit();
		if (unit != null)
		{
			row.put(OrderEntryCsvColumns.ENTRY_UNIT_CODE, unit.getCode());
		}
		else
		{
			LOG.warn("Could not determine unit code for product " + returnItem.getProduct().getCode() + "as entry "
					+ returnItem.getEntryNumber() + "of order " + returnEntry.getReturnRequest().getOrder().getCode());
		}
		String language = returnEntry.getReturnRequest().getOrder().getLanguage().getIsocode();
		String shortText = determineItemShortText(returnItem, language);

		if (shortText.isEmpty())
		{
			final List<LanguageModel> fallbackLanguages = returnEntry.getReturnRequest().getOrder().getLanguage()
					.getFallbackLanguages();
			if (!fallbackLanguages.isEmpty())
			{
				language = fallbackLanguages.get(0).getIsocode();
				shortText = determineItemShortText(returnItem, language);
			}
		}
		row.put(OrderEntryCsvColumns.PRODUCT_NAME, shortText);

		row.put(ReturnOrderEntryCsvColumns.WAREHOUSE, fetchWarehouseCode(returnEntry));

		return row;
	}

	private String fetchWarehouseCode(final ReturnEntryModel returnEntry)
	{

		final Set<ConsignmentEntryModel> consignmentSet = returnEntry.getOrderEntry().getConsignmentEntries();
		final Iterator<ConsignmentEntryModel> itr = consignmentSet.iterator();
		return itr.next().getConsignment().getWarehouse().getCode();
	}
}