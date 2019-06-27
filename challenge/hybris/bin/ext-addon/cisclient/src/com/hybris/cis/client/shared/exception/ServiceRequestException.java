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

import java.util.List;


/**
 * The 3rd party returned an error due to invalid or missing arguments.
 */
public class ServiceRequestException extends AbstractCisClientException
{
	private static final long serialVersionUID = 5739425421118011132L;

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode An error code
	 */
	public ServiceRequestException(final ServiceExceptionDetail errorCode)
	{
		super(errorCode);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode an error code
	 * @param cause the root cause
	 */
	public ServiceRequestException(final ServiceExceptionDetail errorCode, final Throwable cause)
	{
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 */
	public ServiceRequestException(final List<ServiceExceptionDetail> errorCodes)
	{
		super(errorCodes);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 * @param cause the root cause
	 */
	public ServiceRequestException(final List<ServiceExceptionDetail> errorCodes, final Throwable cause)
	{
		super(errorCodes, cause);
	}

}
