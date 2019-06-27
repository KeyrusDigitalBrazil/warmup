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
package de.hybris.platform.acceleratorwebservicesaddon.exceptions;

/**
 * Thrown when location was not found while setting user location
 */

public class NoLocationFoundException extends Exception
{
	/**
	 * @param location
	 */
	public NoLocationFoundException(final String location)
	{
		super("Location: " + location + " could not be found");
	}

	public NoLocationFoundException(final String location, final Throwable rootCause)
	{
		super("Location: " + location + " could not be found", rootCause);
	}
}
