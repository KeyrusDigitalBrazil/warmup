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
 * Indicates that the requested service or function is not supported.
 * 
 */
public class ServiceNotImplementedException extends AbstractCisClientException
{
	private static final long serialVersionUID = -8031191096903241146L;

	/**
	 * Instantiates a new ServiceNotImplementedException.
	 * 
	 * @param serviceExceptionDetails a list of exception details explaining this exception
	 */
	public ServiceNotImplementedException(final List<ServiceExceptionDetail> serviceExceptionDetails)
	{
		super(serviceExceptionDetails);
	}

	/**
	 * Instantiates a new ServiceNotImplementedException.
	 * 
	 * @param function a method which is not (yet) implemented
	 */
	public ServiceNotImplementedException(final String function)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NOT_IMPLEMENTED, function));
	}
}
