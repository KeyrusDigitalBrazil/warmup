/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteEntryMapperService;


/**
 *
 */
public class DefaultSapCpiQuoteEntryMapperService
		implements SapCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCpiOutboundQuoteItemModel>
{

	@Override
	public void map(final AbstractOrderEntryModel quoteEntry, final SAPCpiOutboundQuoteItemModel sapCpiQuoteItem)
	{
		mapQuoteEntries(quoteEntry, sapCpiQuoteItem);
	}


	/**
	 *
	 */
	private void mapQuoteEntries(final AbstractOrderEntryModel quoteEntry, final SAPCpiOutboundQuoteItemModel sapCpiQuoteItem)
	{

		sapCpiQuoteItem.setEntryNumber(quoteEntry.getEntryNumber().toString());
		sapCpiQuoteItem.setProductCode(quoteEntry.getProduct().getCode());
		sapCpiQuoteItem.setProductName(quoteEntry.getProduct().getName());
		sapCpiQuoteItem.setQuantity(quoteEntry.getQuantity().toString());

	}


}
