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
package de.hybris.platform.commerceservices.order.exceptions;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.core.enums.QuoteState;


/**
 * Exception thrown when submit action cannot be performed for a quote. Encapsulates basic quote information.
 */
public class IllegalQuoteSubmitException extends IllegalQuoteStateException
{
	public IllegalQuoteSubmitException(final String quoteCode, final QuoteState quoteState, final Integer quoteVersion)
	{
		super(QuoteAction.SUBMIT, quoteCode, quoteState, quoteVersion);
	}

	public IllegalQuoteSubmitException(final String quoteCode, final QuoteState quoteState, final Integer quoteVersion,
			final Throwable cause)
	{
		super(QuoteAction.SUBMIT, quoteCode, quoteState, quoteVersion, cause);
	}

	public IllegalQuoteSubmitException(final String quoteCode, final QuoteState quoteState, final Integer quoteVersion,
			final String message)
	{
		super(QuoteAction.SUBMIT, quoteCode, quoteState, quoteVersion, message);
	}

	public IllegalQuoteSubmitException(final String quoteCode, final QuoteState quoteState, final Integer quoteVersion,
			final String message, final Throwable cause)
	{
		super(QuoteAction.SUBMIT, quoteCode, quoteState, quoteVersion, message, cause);
	}
}
