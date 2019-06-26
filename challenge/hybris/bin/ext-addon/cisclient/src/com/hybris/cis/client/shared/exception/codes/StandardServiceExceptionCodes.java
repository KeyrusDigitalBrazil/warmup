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
package com.hybris.cis.client.shared.exception.codes;

/**
 * Shared standard error codes.
 */
public enum StandardServiceExceptionCodes implements StandardServiceExceptionCode
{
	/**
	 * Unknown error.
	 */
	UNKNOWN(0, "An unknown error occured"),
	/**
	 * Not found error.
	 */
	NOT_FOUND(404, "Resource not found"),
	/**
	 * Authorization error.
	 */
	NOT_AUTHORIZED(403, "The user is not authorized to use the service or resource"),
	/**
	 * Internal server error.
	 */
	INTERNAL_SERVER_ERROR(500, "Internal server error"),
    /**
     * Internal server error.
     */
    NO_SUCH_SERVICE(500, "No such service"),
    /**
     * Internal server error.
     */
    NO_CONFIGURATION_FOUND(500, "No cis configuration found"),
	/**
	 * Not implemented error.
	 */
	NOT_IMPLEMENTED(501, "The service or function is not implemented"),
	/**
	 * Not available error.
	 */
	NOT_AVAILABLE(503, "The service is currently not available"),
	/**
	 * Time out error.
	 */
	TIMEOUT(504, "A service timeout occured"),
	/**
	 * Invalid field or missing field error.
	 */
	INVALID_OR_MISSING_FIELD(1000, "Invalid or missing field"),
	/**
	 * "The service id is invalid for the type of service requested".
	 */
	NOT_VALID(1001, "The service id is invalid for the type of service requested"),
	/**
	 * "The service configuration is missing a required value".
	 */
	INCOMPLETE_SERVICE_CONFIGURATION(1002, "The service configuration is missing a required value"),
	/**
	 * "The 3rd party service returned an error".
	 */
	ERROR_RESPONSE_FROM_SERVICE(1003, "The 3rd party service returned an error"),
	/**
	 * "Unreadable response came from the 3rd party".
	 */
	UNREADABLE_RESPONSE(1004, "Unreadable response came from the 3rd party"),
    /**
     * "Unreadable request was send to CIS".
     */
    UNREADABLE_REQUEST(1005, "Unreadable request was send to CIS");

	private final int code;
	private final String message;

	private StandardServiceExceptionCodes(final int code, final String message)
	{
		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode()
	{
		return this.code;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

	@Override
	public String toString()
	{
		return this.code + " - " + this.message;
	}


	public static StandardServiceExceptionCode fromCode(final String c)
	{
		for (final StandardServiceExceptionCode standardServiceExceptionCode : StandardServiceExceptionCodes.values())
		{
			if (String.valueOf(standardServiceExceptionCode.getCode()).equalsIgnoreCase(c))
			{
				return standardServiceExceptionCode;
			}
		}
		throw new IllegalArgumentException("No enum value found for: " + c);
	}
}
