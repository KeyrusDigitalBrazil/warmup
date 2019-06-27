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
package com.sap.hybris.sapquoteintegration.outbound.service;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 *
 */
public interface SapCpiOutboundQuoteConversionService
{

	SAPCpiOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel);
	
	SAPCpiOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(final QuoteModel quoteModel);

}
