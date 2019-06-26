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
package de.hybris.platform.commerceservices.order.strategies;


import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Strategy that will be used by business process to check a quote can be auto approved or require seller approval
 * action
 */
public interface QuoteSellerApproverAutoApprovalStrategy
{
	/**
	 * Check whether the quote's sub total amount is within the configured quote auto approval threshold.
	 *
	 * @param quoteModel
	 *           the quote to inspect
	 * @return true if the quote is within the auto approval threshold, false otherwise
	 */
	boolean shouldAutoApproveQuote(QuoteModel quoteModel);
}
