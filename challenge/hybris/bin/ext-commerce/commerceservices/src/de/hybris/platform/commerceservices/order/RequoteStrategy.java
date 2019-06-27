/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Strategy for re-quote an inactive quote
 */
public interface RequoteStrategy
{
	/**
	 * Re-quote an existing quote to get a new quote
	 * <p>
	 * fields in the new created quoteModel are cleared including: name, description, expire time, cart comments, line
	 * item comments, cartReference, assignee and generatedNotifications
	 * </p>
	 * <p>
	 * the new quote's version will be set to 1; and state is set to CREATE
	 * </p>
	 *
	 * @param quote
	 *           quoteModel which will be re-quoted
	 * @return new quoteModel
	 */
	QuoteModel requote(final QuoteModel quote);
}
