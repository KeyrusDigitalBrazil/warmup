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
package de.hybris.platform.sap.c4c.quote.inbound;

import de.hybris.platform.core.model.order.QuoteModel;
/**
 * Helper Class for updating the quote version in inbound scenarios
 */
public interface InboundQuoteVersionControlHelper {

	/**
	 * Get quote for code
	 * @param code
	 * @return quote
	 */
	QuoteModel getQuoteforCode(String code);
	/**
	 * Get updated version for given quote
	 * @param quote
	 * @return
	 */
	Integer getUpdatedVersionNumber(QuoteModel quote);
}
