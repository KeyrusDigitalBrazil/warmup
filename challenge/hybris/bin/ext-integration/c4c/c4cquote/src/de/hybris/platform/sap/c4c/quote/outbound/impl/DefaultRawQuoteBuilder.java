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
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.orderexchange.outbound.impl.AbstractRawItemBuilder;

import org.apache.log4j.Logger;


/**
 * Quote raw item builder delegating the creation of the individual lines of the raw item to the registered instances of
 * {@link RawItemContributor}. The results are merged into one list. Fields not provided by all contributors are
 * defaulted to ""
 */
public class DefaultRawQuoteBuilder extends AbstractRawItemBuilder<QuoteModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultRawQuoteBuilder.class);

	@Override
	protected Logger getLogger()
	{
		return LOG;
	}

}
