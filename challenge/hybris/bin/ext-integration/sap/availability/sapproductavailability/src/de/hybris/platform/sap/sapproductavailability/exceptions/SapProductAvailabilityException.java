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
package de.hybris.platform.sap.sapproductavailability.exceptions;

/**
 * 
 */
public class SapProductAvailabilityException extends RuntimeException
{
	
	private static final long serialVersionUID = 7320793872171187671L;

	public SapProductAvailabilityException(final String message)
	{
		super(message);
	}

	public SapProductAvailabilityException(final String message, final Throwable throwable)
	{
		super(message, throwable);
	}

}
