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
import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCodes;

import java.util.List;


/**
 * Indicates that the user is not authorized to access the service.
 * 
 */
public class ServiceNotAuthorizedException extends AbstractCisClientException
{
	private static final long serialVersionUID = 5519943302279364465L;

	/**
	 * Instantiates a new ServiceNotAuthorizedException.
	 * 
	 * @param serviceExceptionDetails a list of exception details explaining this exception
	 */
	public ServiceNotAuthorizedException(final List<ServiceExceptionDetail> serviceExceptionDetails)
	{
		super(serviceExceptionDetails);
	}

	/**
	 * Instantiates a new ServiceNotAuthorizedException.
	 * 
	 * @param serviceId a service id which is not allowed to be called
	 */
	public ServiceNotAuthorizedException(final String serviceId)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NOT_AUTHORIZED, serviceId));
	}

	/**
	 * Instantiates a new ServiceNotAuthorizedException.
	 * 
	 * @param serviceId a service Id which is not allowed to be called
	 * @param cause a throwable with a cause for the exception
	 */
	public ServiceNotAuthorizedException(final String serviceId, final Throwable cause)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NOT_AUTHORIZED, serviceId), cause);
	}

}
