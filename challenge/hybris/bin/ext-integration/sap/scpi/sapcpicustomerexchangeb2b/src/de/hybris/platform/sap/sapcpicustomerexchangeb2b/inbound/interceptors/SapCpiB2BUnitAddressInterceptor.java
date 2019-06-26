/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.inbound.interceptors;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import com.sap.hybris.sapcustomerb2b.inbound.DefaultSAPCustomerAddressConsistencyInterceptor;


public class SapCpiB2BUnitAddressInterceptor extends DefaultSAPCustomerAddressConsistencyInterceptor
{
	@Override
	public void onPrepare(Object model, InterceptorContext context) throws InterceptorException
	{
		// Disable the interceptor logic form the sapcustomerb2b extension
		return;
	}

}
