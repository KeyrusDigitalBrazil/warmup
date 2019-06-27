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
package de.hybris.platform.consignmenttrackingservices.interceptor;

import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;


/**
 * An implementation of {@link PrepareInterceptor}
 */
public class ConsignmentPrepareInterceptor implements PrepareInterceptor<ConsignmentModel>
{

	@Override
	public void onPrepare(ConsignmentModel consignment, InterceptorContext ctx) throws InterceptorException
	{
		final CarrierModel carrier = consignment.getCarrierDetails();
		final String carrierCode = carrier == null ? null : carrier.getCode();
		consignment.setCarrier(carrierCode);
	}

}
