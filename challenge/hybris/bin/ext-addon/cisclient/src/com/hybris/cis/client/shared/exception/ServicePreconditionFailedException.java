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
 * A precondition failed which prevented it from fulfilling the request. This client exception is thrown if this
 * condition prevents us from calling the 3rd party service.
 * 
 */
public class ServicePreconditionFailedException extends AbstractCisClientException
{
	private static final long serialVersionUID = 8971841288042730453L;

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode An error code
	 */
	public ServicePreconditionFailedException(final ServiceExceptionDetail errorCode)
	{
		super(errorCode);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode an error code
	 * @param cause the root cause
	 */
	public ServicePreconditionFailedException(final ServiceExceptionDetail errorCode, final Throwable cause)
	{
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 */
	public ServicePreconditionFailedException(final List<ServiceExceptionDetail> errorCodes)
	{
		super(errorCodes);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 * @param cause the root cause
	 */
	public ServicePreconditionFailedException(final List<ServiceExceptionDetail> errorCodes, final Throwable cause)
	{
		super(errorCodes, cause);
	}

}
