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
 * A generic exception if a resource wasn't found (will be translated to a 404).
 */
public class NotFoundException extends AbstractCisClientException
{
	private static final long serialVersionUID = 687700316762411995L;

	public NotFoundException(final List<ServiceExceptionDetail> serviceExceptionDetails)
	{
		super(serviceExceptionDetails);
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param resourceId The id of the resource which couldn't be found.
	 */
	public NotFoundException(final String resourceId)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NOT_FOUND, resourceId));
	}

	/**
	 * Instantiates a new cis service exception.
	 * 
	 * @param resourceId The id of the resource which couldn't be found.
	 * @param cause The root cause
	 */
	public NotFoundException(final String resourceId, final Throwable cause)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NOT_FOUND, resourceId), cause);
	}

    /**
     * New exception based on error code and reason code
     * @param shortDescription
     * @param reasonCode
     */
    public NotFoundException (final String shortDescription, final String reasonCode)
    {
        this(shortDescription + " (reasonCode=" + reasonCode + ")");
    }

    /**
     * New exception based on properties from an external exception or message
     * @param id  short description
     * @param sourceSimpleClassName name of the original message or exception
     * @param requestId   request ID associated with the error
     * @param reasonCode   reason code associated with the error
     */
    public NotFoundException( String id, String sourceSimpleClassName, String requestId, String reasonCode)
    {
            this(id + " (request type=" + sourceSimpleClassName + ", requestId="
        + requestId + ", profileId=" + id + ", reasonCode=" + reasonCode + ")");
    }


}
