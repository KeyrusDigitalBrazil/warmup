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
package de.hybris.platform.sap.c4c.quote.decorators;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.util.CSVCellDecorator;
/**
 * Decorator class to Set the GUID of the Quote from previous Quote version
 */
public class QuoteGuidCellDecorator implements CSVCellDecorator {

	private static final Logger LOG = LoggerFactory.getLogger(QuoteGuidCellDecorator.class);

	private InboundQuoteHelper inboundQuoteHelper = (InboundQuoteHelper) Registry.getApplicationContext()
			.getBean("inboundQuoteHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine) {
		LOG.debug("Decorating the quote GUID field");
		String guid = null;
		final String quoteId = impexLine.get(Integer.valueOf(position));
		if (StringUtils.isNotEmpty(quoteId)) {
			guid = getInboundQuoteHelper().getGuid(quoteId);
		}
		return guid;
	}

	public InboundQuoteHelper getInboundQuoteHelper() {
		return inboundQuoteHelper;
	}

	public void setInboundQuoteHelper(InboundQuoteHelper inboundQuoteHelper) {
		this.inboundQuoteHelper = inboundQuoteHelper;
	}
}
