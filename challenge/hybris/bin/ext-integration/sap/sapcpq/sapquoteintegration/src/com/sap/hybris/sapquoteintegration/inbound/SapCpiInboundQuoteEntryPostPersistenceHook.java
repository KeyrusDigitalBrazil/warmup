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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteEntryHelper;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;


public class SapCpiInboundQuoteEntryPostPersistenceHook implements PostPersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiInboundQuoteEntryPostPersistenceHook.class);
	private DefaultInboundQuoteEntryHelper sapInboundQuoteEntryHelper;

	@Override
	public void execute(ItemModel item)
	{
		LOG.info("Entering SapCpiInboundQuoteEntryPOSTPersistenceHook#execute");
		if (item instanceof QuoteEntryModel)
		{
			QuoteEntryModel sapQuoteEntryModel = (QuoteEntryModel) item;
			sapInboundQuoteEntryHelper.calculateQuote(sapQuoteEntryModel);
		}
		LOG.info("Exiting SapCpiInboundQuoteEntryPOSTPersistenceHook#execute");
		
	}

	public DefaultInboundQuoteEntryHelper getSapInboundQuoteEntryHelper() {
		return sapInboundQuoteEntryHelper;
	}

	public void setSapInboundQuoteEntryHelper(DefaultInboundQuoteEntryHelper sapInboundQuoteEntryHelper) {
		this.sapInboundQuoteEntryHelper = sapInboundQuoteEntryHelper;
	}


}