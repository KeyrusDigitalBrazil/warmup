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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPartnerContributor;
import de.hybris.platform.sap.saporderexchangeoms.outbound.SapVendorService;

public class SapOmsVendorContributor extends DefaultPartnerContributor {

	public static final String VENDOR_CODE = "LF";
	private SapVendorService sapVendorService;
	
	@Override
	public Set<String> getColumns()
	{
		final Set<String> columns = super.getColumns();
		columns.addAll(Arrays.asList(OrderEntryCsvColumns.ENTRY_NUMBER));
		return columns;
	}
	
	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<Map<String, Object>> result = super.createRows(order);
		
		enhanceWithVendorRows(order, result);
		
		return result;

	}

	protected void enhanceWithVendorRows(final OrderModel order,
			final List<Map<String, Object>> result) {
		result.stream().forEach(row -> row.put(OrderEntryCsvColumns.ENTRY_NUMBER, "-1"));
		order.getConsignments().stream().forEach(consignment -> {
			
			if (consignment.getWarehouse() != null 
					&& consignment.getWarehouse().getVendor() != null 
						&& sapVendorService.isVendorExternal(consignment.getWarehouse().getVendor().getCode())) {
				
						consignment.getConsignmentEntries().stream().forEach(entry-> {
						final Map<String, Object> row = new HashMap<>();
						row.put(OrderCsvColumns.ORDER_ID, order.getCode());
						row.put(OrderEntryCsvColumns.ENTRY_NUMBER, Integer.valueOf(entry.getSapOrderEntryRowNumber() - 1));
						row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, VENDOR_CODE);
						row.put(PartnerCsvColumns.PARTNER_CODE, consignment.getWarehouse().getVendor().getCode());

						getBatchIdAttributes().forEach(row::putIfAbsent);
						row.put("dh_batchId", order.getCode());

						result.add(row);
					
					});
				};
				
			});
	}

	protected SapVendorService getSapVendorService() {
		return sapVendorService;
	}

	@Required
	public void setSapVendorService(SapVendorService sapVendorService) {
		this.sapVendorService = sapVendorService;
	}
	
}
