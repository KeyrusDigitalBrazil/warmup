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
package de.hybris.platform.cmsfacades.synchronization;

/**
 * Exception used when the synchronization is already in progress
 */
public class SynchronizationInProgressException extends RuntimeException
{
	private static final long serialVersionUID = -8051464932011941508L;

	public SynchronizationInProgressException()
	{
	}

	public SynchronizationInProgressException(final String message)
	{
		super(message);
	}

	public SynchronizationInProgressException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
