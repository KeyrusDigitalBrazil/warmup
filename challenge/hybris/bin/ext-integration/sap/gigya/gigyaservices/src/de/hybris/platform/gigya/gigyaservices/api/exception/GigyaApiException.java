/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaservices.api.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Gigya API exception class
 */
public class GigyaApiException extends SystemException
{

	private final int gigyaErrorCode;

	public GigyaApiException(final String message)
	{
		super(message);
		this.gigyaErrorCode = -1;
	}

	public GigyaApiException(final String message, final Throwable cause)
	{
		super(message, cause);
		this.gigyaErrorCode = -1;
	}

	public GigyaApiException(final String message, final int gigyaErrorCode)
	{
		super(message);
		this.gigyaErrorCode = gigyaErrorCode;
	}

	public GigyaApiException(final Throwable cause, final int gigyaErrorCode)
	{
		super(cause);
		this.gigyaErrorCode = gigyaErrorCode;
	}

	public GigyaApiException(final String message, final int gigyaErrorCode, final Throwable cause)
	{
		super(message, cause);
		this.gigyaErrorCode = gigyaErrorCode;
	}

	public int getGigyaErrorCode()
	{
		return gigyaErrorCode;
	}

}
