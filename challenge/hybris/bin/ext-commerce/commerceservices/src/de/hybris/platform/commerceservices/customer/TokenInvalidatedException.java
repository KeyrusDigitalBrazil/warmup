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
package de.hybris.platform.commerceservices.customer;

/**
 * Exception is thrown if the user tries to use the same token multiple times.
 */
public class TokenInvalidatedException extends Exception
{

	/**
	 * Default Constructor
	 */
	public TokenInvalidatedException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TokenInvalidatedException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public TokenInvalidatedException(final String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public TokenInvalidatedException(final Throwable cause)
	{
		super(cause);
	}

}
