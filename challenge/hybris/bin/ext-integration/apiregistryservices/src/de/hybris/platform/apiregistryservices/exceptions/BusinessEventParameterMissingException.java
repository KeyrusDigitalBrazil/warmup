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

package de.hybris.platform.apiregistryservices.exceptions;


/**
 * Thrown when business event parameter is missing for business process actions.
 */
public class BusinessEventParameterMissingException extends Exception
{
	public BusinessEventParameterMissingException(final String message)
	{
		super(message);
	}

	public BusinessEventParameterMissingException(final String message, final Throwable t)
	{
		super(message, t);
	}
}
