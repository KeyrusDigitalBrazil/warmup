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
 * Thrown when there is a problem while calling any of the SmarteditConfigurationFacade methods
 */
public class SmarteditConfigurationException extends RuntimeException
{
	private static final long serialVersionUID = -3178966706411273787L;

	public SmarteditConfigurationException(final String message)
	{
		super(message);
	}

	public SmarteditConfigurationException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public SmarteditConfigurationException(final Throwable cause)
	{
		super(cause);
	}
}
