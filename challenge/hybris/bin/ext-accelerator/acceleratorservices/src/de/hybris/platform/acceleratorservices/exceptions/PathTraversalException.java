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
package de.hybris.platform.acceleratorservices.exceptions;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Thrown when path traversal is somehow violated.
 */
public class PathTraversalException extends SystemException

{
	/**
	 * Constructs the exception with given message.
	 *
	 * @param message
	 *           a message
	 */
	public PathTraversalException(final String message)
	{
		super(message);
	}

	/**
	 * Constructs the exception with given message and a cause.
	 *
	 * @param message
	 *           the entity message
	 * @param cause
	 *           the cause
	 */
	public PathTraversalException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}