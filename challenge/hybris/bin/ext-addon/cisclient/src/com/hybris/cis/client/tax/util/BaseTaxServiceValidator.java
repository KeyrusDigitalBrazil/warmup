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
package com.hybris.cis.client.tax.util;

import com.hybris.cis.api.executor.ServiceMethodRequest;
import com.hybris.cis.api.executor.ServiceMethodValidator;
import com.hybris.cis.client.shared.exception.ServicePreconditionFailedException;
import com.hybris.cis.client.shared.exception.codes.ServiceExceptionDetail;
import com.hybris.cis.client.shared.models.CisAddressType;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.exception.TaxServiceExceptionCodes;


/**
 * A minimal validator to check whether the ship to address is set on the order.
 */
public class BaseTaxServiceValidator implements ServiceMethodValidator<CisOrder>
{
	@Override
	public void validateRequest(final ServiceMethodRequest<CisOrder> request) throws ServicePreconditionFailedException
	{
		if (request.getModel().getAddressByType(CisAddressType.SHIP_TO) == null)
		{
			throw new ServicePreconditionFailedException(new ServiceExceptionDetail(TaxServiceExceptionCodes.SHIP_TO_MISSING));
		}
	}

}
