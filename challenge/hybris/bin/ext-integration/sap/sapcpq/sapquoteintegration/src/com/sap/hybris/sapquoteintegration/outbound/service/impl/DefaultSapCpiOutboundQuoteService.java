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

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService;

import rx.Observable;


/**
 *
 */
public class DefaultSapCpiOutboundQuoteService implements SapCpiOutboundQuoteService
{

	// Quote Outbound
	private static final String OUTBOUND_QUOTE_OBJECT = "OutboundQuote";
	private static final String OUTBOUND_QUOTE_DESTINATION = "scpiQuoteDestination";
	
	// Quote Status Outbound
	private static final String OUTBOUND_QUOTE_STATUS_OBJECT = "OutboundQuoteStatus";
	private static final String OUTBOUND_QUOTE_STATUS_DESTINATION = "scpiQuoteStatusDestination";

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendQuote(final SAPCpiOutboundQuoteModel sapCpiOutboundQuoteModel)
	{
		return getOutboundServiceFacade().send(sapCpiOutboundQuoteModel, OUTBOUND_QUOTE_OBJECT, OUTBOUND_QUOTE_DESTINATION);
	}

	@Override
	public Observable<ResponseEntity<Map>> sendQuoteStatus(SAPCpiOutboundQuoteStatusModel sapCpiOutboundQuoteStatusModel) 
	{
		return getOutboundServiceFacade().send(sapCpiOutboundQuoteStatusModel, OUTBOUND_QUOTE_STATUS_OBJECT, OUTBOUND_QUOTE_STATUS_DESTINATION);
	}
	
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	@Required
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}
