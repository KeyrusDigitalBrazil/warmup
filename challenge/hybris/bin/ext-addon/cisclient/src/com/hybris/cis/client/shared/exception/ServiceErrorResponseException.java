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
package com.hybris.cis.client.shared.exception;


import com.hybris.cis.client.shared.exception.codes.ServiceExceptionDetail;
import com.hybris.cis.client.shared.exception.codes.UnknownServiceExceptionDetail;

import java.util.List;


/**
 * Exception thrown when the 3rd party returns an error.
 */
public class ServiceErrorResponseException extends AbstractServiceErrorException
{

	private static final long serialVersionUID = -6980142120552997382L;

	/**
	 * Instantiates a new cis service exception with an {@link UnknownServiceExceptionDetail}.
	 * 
	 * @param errorMsg The error message
	 */
	public ServiceErrorResponseException(final String errorMsg)
	{
		super(new UnknownServiceExceptionDetail(errorMsg));
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode An error code
	 */
	public ServiceErrorResponseException(final ServiceExceptionDetail errorCode)
	{
		super(errorCode);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode an error code
	 * @param cause the root cause
	 */
	public ServiceErrorResponseException(final ServiceExceptionDetail errorCode, final Throwable cause)
	{
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 */
	public ServiceErrorResponseException(final List<ServiceExceptionDetail> errorCodes)
	{
		super(errorCodes);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 * @param cause the root cause
	 */
	public ServiceErrorResponseException(final List<ServiceExceptionDetail> errorCodes, final Throwable cause)
	{
		super(errorCodes, cause);
	}
}
