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
package de.hybris.platform.apiregistryservices.interceptors;

import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import org.apache.commons.lang.StringUtils;


/**
 * RemoveInterceptor for Exposed Destination.
 * Prevents that exposed destinations which are already registered in a target system can be deleted.
 */
public class RemoveExposedDestinationInterceptor implements RemoveInterceptor<ExposedDestinationModel>
{
	@Override
	public void onRemove(final ExposedDestinationModel exposedDestination, final InterceptorContext ctx)
			throws InterceptorException
	{
		if (StringUtils.isNotEmpty(exposedDestination.getTargetId()))
		{
			throw new InterceptorException(
					String.format("Cannot delete Registered Exposed Destination with id: [{%s}]", exposedDestination.getId()));
		}
	}
}
