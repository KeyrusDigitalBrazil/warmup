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


import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCodes;
import com.hybris.cis.client.shared.exception.codes.ServiceExceptionDetail;

import java.util.List;


/**
 * Exception thrown when the 3rd party service returns a not expected response format (xml parsing issues, or not
 * expected values) or when there is some IO issue.
 */
public class UnreadableServiceResponseException extends AbstractServiceErrorException
{

	private static final long serialVersionUID = -6980142120552997382L;

	/**
	 * Instantiates a new cis service exception with an {@link UnreadableServiceResponseException}.
	 * 
	 * @param errorMsg The error message
	 */
	public UnreadableServiceResponseException(final String errorMsg)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.UNREADABLE_RESPONSE, errorMsg));
	}

	/**
	 * Instantiates a new {@link UnreadableServiceResponseException}.
	 * 
	 * @param throwable a throwable detailing the exception
	 */
	public UnreadableServiceResponseException(final Throwable throwable)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.UNREADABLE_RESPONSE), throwable);
	}

	/**
	 * Instantiates a new {@link UnreadableServiceResponseException}.
	 * 
	 * @param errorCode an service exception detail with an specific error code
	 */
	public UnreadableServiceResponseException(final ServiceExceptionDetail errorCode)
	{
		super(errorCode);
	}

	/**
	 * Instantiates a new {@link UnreadableServiceResponseException}.
	 * 
	 * @param errorCode an service exception detail with an specific error code
	 * @param e an exception to pass in
	 * 
	 */
	public UnreadableServiceResponseException(final ServiceExceptionDetail errorCode, final Exception e)
	{
		super(errorCode, e);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 */
	public UnreadableServiceResponseException(final List<ServiceExceptionDetail> errorCodes)
	{
		super(errorCodes);
	}

}
