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

/**
 * Destination Not Found Exception
 */
package de.hybris.platform.apiregistryservices.exceptions;

public class DestinationNotFoundException extends ApiRegistrationException
{
	public DestinationNotFoundException(final String message)
	{
		super(message);
	}

	public DestinationNotFoundException(final String message, final Throwable t)
	{
		super(message, t);
	}
}
