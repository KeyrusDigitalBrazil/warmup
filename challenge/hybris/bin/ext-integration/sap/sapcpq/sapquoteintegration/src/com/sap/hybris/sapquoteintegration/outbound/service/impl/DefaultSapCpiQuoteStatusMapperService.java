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

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteStatusMapperService;

import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 *
 */
public class DefaultSapCpiQuoteStatusMapperService
		implements SapCpiQuoteStatusMapperService<QuoteModel, SAPCpiOutboundQuoteStatusModel> {

	@Override
	public void map(QuoteModel quote, SAPCpiOutboundQuoteStatusModel scpiQuoteStatus) {
		mapQuoteStatus(quote, scpiQuoteStatus);
	}

	private void mapQuoteStatus(QuoteModel quote, SAPCpiOutboundQuoteStatusModel scpiQuoteStatus) {

		scpiQuoteStatus.setExternalQuoteId(quote.getExternalQuoteId());
		scpiQuoteStatus.setQuoteId(quote.getCode());
		scpiQuoteStatus.setStatus(quote.getState().toString());
		if (QuoteState.BUYER_ORDERED.equals(quote.getState())) {
			scpiQuoteStatus.setOrderId(quote.getOrderCode());
		}
	}

}
