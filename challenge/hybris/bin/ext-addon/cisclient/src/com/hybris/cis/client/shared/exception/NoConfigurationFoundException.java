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


public class NoConfigurationFoundException extends AbstractCisServiceException {

    private static final long serialVersionUID = 8384998861225823347L;

    public NoConfigurationFoundException(ServiceExceptionDetail serviceExceptionDetail)
    {
        super(serviceExceptionDetail);
    }

    public NoConfigurationFoundException(final List<ServiceExceptionDetail> serviceExceptionDetails)
    {
        super(serviceExceptionDetails);
    }

    public NoConfigurationFoundException(final Throwable cause)
    {
        super(new ServiceExceptionDetail(StandardServiceExceptionCodes.NO_CONFIGURATION_FOUND, cause.getMessage()), cause);
    }
}
