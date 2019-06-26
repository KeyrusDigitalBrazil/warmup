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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DefaultReturnOrderPartnerContributor implements RawItemContributor<ReturnRequestModel>
{

	private B2CCustomerHelper b2CCustomerHelper;


	@Override
	public Set<String> getColumns()
	{
		return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, PartnerCsvColumns.PARTNER_ROLE_CODE,
				PartnerCsvColumns.PARTNER_CODE, PartnerCsvColumns.DOCUMENT_ADDRESS_ID, PartnerCsvColumns.FIRST_NAME,
				PartnerCsvColumns.LAST_NAME, PartnerCsvColumns.STREET, PartnerCsvColumns.CITY, PartnerCsvColumns.TEL_NUMBER,
				PartnerCsvColumns.HOUSE_NUMBER, PartnerCsvColumns.POSTAL_CODE, PartnerCsvColumns.REGION_ISO_CODE,
				PartnerCsvColumns.COUNTRY_ISO_CODE, PartnerCsvColumns.EMAIL, PartnerCsvColumns.LANGUAGE_ISO_CODE,
				PartnerCsvColumns.MIDDLE_NAME, PartnerCsvColumns.MIDDLE_NAME2, PartnerCsvColumns.DISTRICT, PartnerCsvColumns.BUILDING,
				PartnerCsvColumns.APPARTMENT, PartnerCsvColumns.POBOX, PartnerCsvColumns.FAX, PartnerCsvColumns.TITLE,
				OrderEntryCsvColumns.ENTRY_NUMBER));
	}

	@Override
	public List<Map<String, Object>> createRows(final ReturnRequestModel model)
	{
		final List<Map<String, Object>> rows = createSoldtoPartyRows(model.getOrder());
		final List<ReturnEntryModel> returnEntries = model.getReturnEntries();
		for (final Map<String, Object> row : rows)
		{
			row.put(OrderCsvColumns.ORDER_ID, model.getCode());
			row.put(OrderEntryCsvColumns.ENTRY_NUMBER, returnEntries.get(0).getOrderEntry().getEntryNumber());

		}
		return rows;
	}

	public List<Map<String, Object>> createSoldtoPartyRows(final OrderModel order)
	{
		final List<Map<String, Object>> result = new ArrayList<>(3);
		final Map<String, Object> row = new HashMap<String, Object>();
		final String b2cCustomer = b2CCustomerHelper.determineB2CCustomer(order);
		final String sapcommonCustomer = b2cCustomer != null ? b2cCustomer
				: order.getStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.SOLD_TO.getCode());
		row.put(PartnerCsvColumns.PARTNER_CODE, sapcommonCustomer);
		result.add(row);
		return result;
	}


	@SuppressWarnings("javadoc")
	public B2CCustomerHelper getB2CCustomerHelper()
	{
		return b2CCustomerHelper;
	}

	@SuppressWarnings("javadoc")
	public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper)
	{
		b2CCustomerHelper = b2cCustomerHelper;
	}


}
