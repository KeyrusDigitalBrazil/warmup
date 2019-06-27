/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.monitoring;

/**
 * An IntegrationKeyExtractionException represents an exception that occurs while extracting the integration key value from a response
 */
public class IntegrationKeyExtractionException extends RuntimeException
{
	private static final String MSG = "An exception has occurred while extracting the integration key value from the response";
	
	public IntegrationKeyExtractionException(final Throwable throwable)
	{
		super(MSG, throwable);
	}
}
