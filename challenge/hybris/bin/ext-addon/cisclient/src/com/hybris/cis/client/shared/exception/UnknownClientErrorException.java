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


/**
 * This Exception is to be used when an unexpected / unreadable exception from the client was thrown.
 */
public class UnknownClientErrorException extends AbstractCisClientException
{
	private static final long serialVersionUID = -981349022802378073L;

	public UnknownClientErrorException(final Exception e)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.UNKNOWN, e.getClass().getName() + " : " + e.getMessage()));
	}

	public UnknownClientErrorException(final String e)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.UNKNOWN, e));
	}

}
