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
package de.hybris.platform.b2bacceleratorfacades.customer.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Exception is thrown when there is attempt to change customer password but it does not match the validation regex.
 */
public class InvalidPasswordException extends SystemException
{

	public InvalidPasswordException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public InvalidPasswordException(final String message)
	{
		super(message);
	}

	public InvalidPasswordException(final Throwable cause)
	{
		super(cause);
	}

}
