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
package com.sap.hybris.returnsexchangeb2b.outbound.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;

import com.sap.hybris.returnsexchange.outbound.impl.DefaultReturnOrderPartnerContributor;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BReturnOrderPartnerContributor extends DefaultReturnOrderPartnerContributor

{   
	
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
		

	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	@Override
	public List<Map<String, Object>> createRows(final ReturnRequestModel model)
	{
		OrderModel order=model.getOrder();
		return isB2BOrder(order) ? createB2BRows(order,model) : super.createRows(model);
		
	}



	protected List<Map<String, Object>> createB2BRows(final OrderModel order,final ReturnRequestModel model)
	{ 
		final Map<String, Object> row1 = createPartnerRow(order, PartnerRoles.SOLD_TO, soldToFromOrder(order));
		final List<Map<String, Object>> rows=Arrays.asList(row1);
		final List<ReturnEntryModel> returnEntries = model.getReturnEntries();
		for (final Map<String, Object> row : rows)
		{
			row.put(OrderCsvColumns.ORDER_ID, model.getCode());
			row.put(OrderEntryCsvColumns.ENTRY_NUMBER, returnEntries.get(0).getOrderEntry().getEntryNumber());
		}
		
		return rows;
	}

	
    protected Map<String, Object> createPartnerRow(final OrderModel order, final PartnerRoles partnerRole, final String partnerId) {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
        return row;
    }

	protected boolean isB2BOrder(final OrderModel orderModel)
	{
		return orderModel.getSite().getChannel() == SiteChannel.B2B;
	}

	protected String soldToFromOrder(final OrderModel order)
	{
		final CompanyModel rootUnit = getB2bUnitService().getRootUnit(order.getUnit());
		return rootUnit.getUid();
	}

}
