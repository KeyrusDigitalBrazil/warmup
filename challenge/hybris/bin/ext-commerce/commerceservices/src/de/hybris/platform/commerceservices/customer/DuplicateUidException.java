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
 * Exception is thrown when an attempt to store an UID that is already assigned
 */
public class DuplicateUidException extends Exception
{

	/**
	 * Default constructor
	 */
	public DuplicateUidException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateUidException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DuplicateUidException(final String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public DuplicateUidException(final Throwable cause)
	{
		super(cause);
	}

}
