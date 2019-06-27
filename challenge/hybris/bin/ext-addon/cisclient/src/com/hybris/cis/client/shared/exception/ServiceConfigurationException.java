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
 * Indicates that the service configuration is not valid.
 */
public class ServiceConfigurationException extends AbstractCisClientException
{

	private static final long serialVersionUID = -2303120384153187659L;


	/**
	 * Instantiates a new ServiceConfigurationException.
	 * 
	 * @param serviceExceptionDetails a list of exception details explaining this exception
	 */
	public ServiceConfigurationException(final List<ServiceExceptionDetail> serviceExceptionDetails)
	{
		super(serviceExceptionDetails);
	}


	/**
	 * Instantiates a new ServiceConfigurationException.
	 * 
	 * @param configurationValue a configuration value which wasn't completed
	 */
	public ServiceConfigurationException(final String configurationValue)
	{
		super(new ServiceExceptionDetail(StandardServiceExceptionCodes.INCOMPLETE_SERVICE_CONFIGURATION, configurationValue));
	}

}
