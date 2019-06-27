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
package de.hybris.platform.smarteditwebservices.configuration;


/**
 * Thrown when a configuration was not found
 */
public class SmarteditConfigurationNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 6032378484891557906L;

	public SmarteditConfigurationNotFoundException(final String message)
	{
		super(message);
	}

	public SmarteditConfigurationNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public SmarteditConfigurationNotFoundException(final Throwable cause)
	{
		super(cause);
	}
}
