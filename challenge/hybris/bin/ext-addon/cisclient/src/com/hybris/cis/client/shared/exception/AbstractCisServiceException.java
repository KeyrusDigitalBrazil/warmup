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


import com.hybris.cis.api.service.CisService;
import com.hybris.cis.client.shared.exception.codes.ServiceExceptionDetail;

import java.util.Collections;
import java.util.List;



/**
 * Super class for all service related exceptions.
 * 
 */
public abstract class AbstractCisServiceException extends RuntimeException
{

	public static final long serialVersionUID = 783332842364593436L;

	private String vendorId;
	private String serviceId;

	private final List<ServiceExceptionDetail> errorCodes;


	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode An error code
	 */
	public AbstractCisServiceException(final ServiceExceptionDetail errorCode)
	{
		this(Collections.singletonList(errorCode));
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCode an error code
	 * @param cause the root cause
	 */
	public AbstractCisServiceException(final ServiceExceptionDetail errorCode, final Throwable cause)
	{
		this(Collections.singletonList(errorCode), cause);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 */
	public AbstractCisServiceException(final List<ServiceExceptionDetail> errorCodes)
	{
		super(formatErrors(errorCodes));
		this.errorCodes = errorCodes;
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param errorCodes One or more error codes
	 * @param cause the root cause
	 */
	public AbstractCisServiceException(final List<ServiceExceptionDetail> errorCodes, final Throwable cause)
	{
		super(formatErrors(errorCodes), cause);
		this.errorCodes = errorCodes;
	}

	private static String formatErrors(final List<ServiceExceptionDetail> errorCodes)
	{
		final StringBuilder message = new StringBuilder();
		for (final ServiceExceptionDetail error : errorCodes)
		{
			message.append(error.getMessage()).append("\n");
		}
		return message.substring(0, message.length() - 1);
	}

	public void setService(final CisService service)
	{
		this.serviceId = service.getType().toString();
		this.vendorId = service.getId();
	}

	public String getVendorId()
	{
		return this.vendorId;
	}

	public String getServiceId()
	{
		return this.serviceId;
	}

	public List<ServiceExceptionDetail> getErrorCodes()
	{
		return this.errorCodes;
	}
}
