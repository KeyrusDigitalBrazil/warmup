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
 * Indicates that the call to an external service has timed out.
 * 
 */
public class ServiceTimeoutException extends AbstractServiceErrorException
{
	private static final long serialVersionUID = -620153758083015833L;

	public ServiceTimeoutException(final String message)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.TIMEOUT, message));
	}

	public ServiceTimeoutException(final Throwable cause)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.TIMEOUT, cause.getMessage()), cause);
	}

	public ServiceTimeoutException(final List<ServiceExceptionDetail> serviceExceptionDetail)
	{
		super(serviceExceptionDetail);
	}
}
