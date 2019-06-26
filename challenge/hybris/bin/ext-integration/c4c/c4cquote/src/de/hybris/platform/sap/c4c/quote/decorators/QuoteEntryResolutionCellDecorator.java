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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;
import de.hybris.platform.util.CSVCellDecorator;

public class QuoteEntryResolutionCellDecorator implements CSVCellDecorator {

	private static final Logger LOG = LoggerFactory.getLogger(QuoteEntryResolutionCellDecorator.class);
	private InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper = (InboundQuoteVersionControlHelper) Registry.getApplicationContext().getBean("inboundQuoteVersionControlHelper");

	private InboundQuoteHelper inboundQuoteHelper = (InboundQuoteHelper) Registry.getApplicationContext().getBean("inboundQuoteHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine) {
		LOG.info("Decorating order entry information from canonical into target models.");
		final String orderEntryInfo = impexLine.get(Integer.valueOf(position));
		String result = "";
		if (orderEntryInfo != null && !orderEntryInfo.equals(C4cquoteConstants.IGNORE)) {
			final List<String> orderEntryData = Arrays.asList(StringUtils.split(orderEntryInfo, '|'));
			final String quoteId = orderEntryData.get(0);
			final String entryNumber = getInboundQuoteHelper().convertEntryNumber(orderEntryData.get(1));
			Integer entry=Integer.parseInt(entryNumber);
			QuoteModel quote = getInboundQuoteVersionControlHelper().getQuoteforCode(quoteId);
			if (quote != null) {
				result=quote.getEntries().get(entry).getPk().toString();
			} else {
				LOG.info("No quote exist in system with quoteId= ",quoteId);
			}
		}
		return result;
	}

	public InboundQuoteVersionControlHelper getInboundQuoteVersionControlHelper() {
		return inboundQuoteVersionControlHelper;
	}

	public void setInboundQuoteVersionControlHelper(InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper) {
		this.inboundQuoteVersionControlHelper = inboundQuoteVersionControlHelper;
	}

	public InboundQuoteHelper getInboundQuoteHelper() {
		return inboundQuoteHelper;
	}

	public void setInboundQuoteHelper(InboundQuoteHelper inboundQuoteHelper) {
		this.inboundQuoteHelper = inboundQuoteHelper;
	}

}
