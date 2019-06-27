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

import java.util.ArrayList;
import java.util.List;




/**
 * Indicates that the request has invalid or missing elements that prevents the request to be fulfilled.
 * 
 */
public class InvalidOrMissingFieldException extends ServicePreconditionFailedException
{
	private static final long serialVersionUID = -8031191096903241146L;

	/**
	 * @param field name of the missing field
	 */
	public InvalidOrMissingFieldException(final String field)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.INVALID_OR_MISSING_FIELD, field));
	}



    public InvalidOrMissingFieldException(List<ServiceExceptionDetail> details)
    {
        super(details);
    }

	private static List<ServiceExceptionDetail> toServiceExceptionDetails(final List<String> fields)
	{
		final List<ServiceExceptionDetail> codes = new ArrayList<ServiceExceptionDetail>(fields.size());

		for (final String field : fields)
		{
			codes.add(new ServiceExceptionDetail(StandardServiceExceptionCodes.INVALID_OR_MISSING_FIELD, field));
		}

		return codes;
	}
}
