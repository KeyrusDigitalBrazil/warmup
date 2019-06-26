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

/**
 * Exception for quote that does not meet threshold.
 *
 */
public class QuoteUnderThresholdException extends RuntimeException
{
	private static final String EXCEPTION_MSG_FORMAT = "Quote with code [%s] and version [%s] does not meet the threshold.";

	public QuoteUnderThresholdException(final String quoteCode, final Integer quoteVersion, final Throwable cause)
	{
		super(String.format(EXCEPTION_MSG_FORMAT, quoteCode, quoteVersion), cause);
	}

	public QuoteUnderThresholdException(final String quoteCode, final Integer quoteVersion)
	{
		super(String.format(EXCEPTION_MSG_FORMAT, quoteCode, quoteVersion));
	}

	public QuoteUnderThresholdException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public QuoteUnderThresholdException(final String message)
	{
		super(message);
	}

}
