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
public class ServiceInvalidException extends AbstractCisClientException
{
	private static final long serialVersionUID = -8031191096903241146L;

	/**
	 * Instantiates a new ServiceInvalidException.
	 * 
	 * @param serviceExceptionDetails a list of details for this exception
	 */
	public ServiceInvalidException(final List<ServiceExceptionDetail> serviceExceptionDetails)
	{
		super(serviceExceptionDetails);
	}

	/**
	 * Instantiates a new ServiceInvalidException.
	 * 
	 * @param function a function which isn't valid for this service
	 */
	public ServiceInvalidException(final String function)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NOT_VALID, function));
	}
}
