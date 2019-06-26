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
package de.hybris.platform.odata2services.odata.persistence;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

/**
 * Exception to throw for Internal processing Exceptions.
 * Will result in HttpStatus 500
 */
public class InternalProcessingException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.INTERNAL_SERVER_ERROR;
	private static final String DEFAULT_ERROR_CODE = "internal_error";
	private static final String DEFAULT_MESSAGE = "There was an error encountered during the processing of the " +
			"integration object. The detailed cause of this error is visible in the log.";

	/**
	 * Constructor to create InternalProcessingException
	 *
	 * @param message error message
	 */
	public InternalProcessingException(final String message)
	{
		super(message, STATUS_CODE, DEFAULT_ERROR_CODE, "");
	}

	/**
	 * Constructor to create InternalProcessingException
	 *
	 * @param message error message
	 * @param integrationKey key value for the item
	 */
	public InternalProcessingException(final String message, final String integrationKey)
	{
		super(message, STATUS_CODE, DEFAULT_ERROR_CODE, integrationKey);
	}

	/**
	 * Constructor to create InternalProcessingException
	 *
	 * @param e exception to get Message from
	 * @param integrationKey key value for the item
	 */
	public InternalProcessingException(final Throwable e, final String integrationKey)
	{
		super(STATUS_CODE, DEFAULT_ERROR_CODE, e, integrationKey);
	}

	/**
	 * Constructor to create InternalProcessingException
	 *
	 * @param e exception to get Message from
	 */
	public InternalProcessingException(final Throwable e)
	{
		super(DEFAULT_MESSAGE, STATUS_CODE, DEFAULT_ERROR_CODE, e);
	}

	/**
	 * Constructor to create InternalProcessingException
	 *
	 * @param errorCode error code
	 * @param message error message
	 * @param e exception to get Message from
	 */
	public InternalProcessingException(final String errorCode, final String message, final Throwable e)
	{
		super(message, STATUS_CODE, errorCode, e);
	}
}
