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
package com.sap.hybris.sapquoteintegration.inbound;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteMapperService;


public class SapCpiInboundQuotePersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiInboundQuotePersistenceHook.class);
	private List<InboundQuoteHelper> sapInboundQuoteHelpers;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		LOG.info("Entering SapCpiInboundQuotePersistenceHook#execute");
		if (item instanceof QuoteModel)
		{
			QuoteModel sapQuoteModel = (QuoteModel) item;
			for (InboundQuoteHelper inboundQuoteHelper : sapInboundQuoteHelpers) {
				sapQuoteModel = inboundQuoteHelper.processInboundQuote(sapQuoteModel);
			}
			LOG.info("Exiting SapCpiInboundQuotePersistenceHook#execute");
			return Optional.of(sapQuoteModel);
		}
		LOG.info("Exiting SapCpiInboundQuotePersistenceHook#execute");
		return Optional.of(item);
	}

	public List<InboundQuoteHelper> getSapInboundQuoteHelpers() {
		return sapInboundQuoteHelpers;
	}

	public void setSapInboundQuoteHelpers(List<InboundQuoteHelper> sapInboundQuoteHelpers) {
		this.sapInboundQuoteHelpers = sapInboundQuoteHelpers;
	}


}
