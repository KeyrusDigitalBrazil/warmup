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
package de.hybris.platform.cmsfacades.exception;

/**
 * Exception thrown when attempting to user cannot access an attribute
 */
public class AttributePermissionException extends RuntimeException
{
	private static final long serialVersionUID = 8033677933215967847L;

	public AttributePermissionException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public AttributePermissionException(final String message)
	{
		super(message);
	}

	public AttributePermissionException(final Throwable cause)
	{
		super(cause);
	}

}
