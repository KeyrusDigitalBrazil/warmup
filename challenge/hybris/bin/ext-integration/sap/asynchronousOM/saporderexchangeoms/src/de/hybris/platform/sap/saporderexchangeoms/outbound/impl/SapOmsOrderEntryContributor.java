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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderEntryContributor;
import de.hybris.platform.sap.saporderexchangeoms.outbound.SapVendorService;


/**
 * DefaultSapOmsOrderEntryContributor
 *
 */
public class SapOmsOrderEntryContributor extends DefaultOrderEntryContributor
{
	private final static Logger LOG = Logger.getLogger(SapOmsOrderEntryContributor.class);
	
	private SapVendorService sapVendorService;

	@Override
	public Set<String> getColumns()
	{
		final Set<String> columns = super.getColumns();
		columns.addAll(Arrays.asList(OrderEntryCsvColumns.WAREHOUSE, OrderEntryCsvColumns.EXPECTED_SHIPPING_DATE,OrderEntryCsvColumns.ITEM_CATEGORY));
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{

		final List<Map<String, Object>> result = new ArrayList<>();

		for (final ConsignmentModel consignment : order.getConsignments())
		{
			for (final ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries())
			{
				final Map<String, Object> row = new HashMap<>();

				row.put(OrderCsvColumns.ORDER_ID, order.getCode());
				row.put(OrderEntryCsvColumns.ENTRY_NUMBER, Integer.valueOf(consignmentEntry.getSapOrderEntryRowNumber() - 1));
				row.put(OrderEntryCsvColumns.QUANTITY, consignmentEntry.getQuantity());
				row.put(OrderEntryCsvColumns.PRODUCT_CODE, consignmentEntry.getOrderEntry().getProduct().getCode());
				row.put(OrderEntryCsvColumns.WAREHOUSE, consignmentEntry.getConsignment().getWarehouse().getCode());
				row.put(OrderEntryCsvColumns.EXPECTED_SHIPPING_DATE, consignmentEntry.getConsignment().getShippingDate());
				row.put(OrderEntryCsvColumns.EXTERNAL_PRODUCT_CONFIGURATION,
						getProductConfigurationData(consignmentEntry.getOrderEntry()));

				final UnitModel unit = consignmentEntry.getOrderEntry().getUnit();
				if (unit != null)
				{
					row.put(OrderEntryCsvColumns.ENTRY_UNIT_CODE, unit.getCode());
				}
				else
				{
					LOG.warn(String.format("Could not determine unit code for product %s as entry %d of order %s", consignmentEntry
							.getOrderEntry().getProduct().getCode(), consignmentEntry.getOrderEntry().getEntryNumber(), order.getCode()));
				}

				String shortText = determineItemShortText(consignmentEntry.getOrderEntry(), order.getLanguage().getIsocode());

				shortText = setShortText(order, consignmentEntry, shortText);

				row.put(OrderEntryCsvColumns.PRODUCT_NAME, shortText);
				
				if (getSapVendorService().isVendorExternal(consignmentEntry.getConsignment().getWarehouse().getVendor().getCode())){
					row.put(OrderEntryCsvColumns.ITEM_CATEGORY, getSapVendorService().getVendorItemCategory());
				}
				getBatchIdAttributes().forEach(row::putIfAbsent);
				row.put("dh_batchId", order.getCode());

				result.add(row);

			}
		}

		return result;

	}

	private String setShortText(OrderModel order, ConsignmentEntryModel consignmentEntry, String shortText) {
		if (shortText.isEmpty())
        {
            final List<LanguageModel> fallbackLanguages = order.getLanguage().getFallbackLanguages();
            if (!fallbackLanguages.isEmpty())
            {
                shortText = determineItemShortText(consignmentEntry.getOrderEntry(), fallbackLanguages.get(0).getIsocode());
            }
        }
		return shortText;
	}


	protected SapVendorService getSapVendorService() {
		return sapVendorService;
	}

	@Required
	public void setSapVendorService(SapVendorService sapVendorService) {
		this.sapVendorService = sapVendorService;
	}

}
