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
 * Indicates that the external service responded with an invalid or unexpected result.
 * 
 */
public abstract class AbstractServiceErrorException extends AbstractCisServiceException
{
	private static final long serialVersionUID = -30883185866709408L;

	/**
	 * Instantiates a new cis service exception with an {@link UnknownServiceExceptionDetail}.
	 * 
	 * @param errorMsg The error message
	 */
	public AbstractServiceErrorException(final String errorMsg)
	{
		super(new UnknownServiceExceptionDetail(errorMsg));
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode An error code
	 */
	public AbstractServiceErrorException(final ServiceExceptionDetail errorCode)
	{
		super(errorCode);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode an error code
	 * @param cause the root cause
	 */
	public AbstractServiceErrorException(final ServiceExceptionDetail errorCode, final Throwable cause)
	{
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 */
	public AbstractServiceErrorException(final List<ServiceExceptionDetail> errorCodes)
	{
		super(errorCodes);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 * @param cause the root cause
	 */
	public AbstractServiceErrorException(final List<ServiceExceptionDetail> errorCodes, final Throwable cause)
	{
		super(errorCodes, cause);
	}
}
