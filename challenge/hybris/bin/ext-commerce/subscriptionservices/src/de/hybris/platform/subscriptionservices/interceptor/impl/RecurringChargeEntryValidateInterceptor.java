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
package de.hybris.platform.subscriptionservices.interceptor.impl;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;

import javax.annotation.Nonnull;
import javax.annotation.Resource;


public class RecurringChargeEntryValidateInterceptor implements ValidateInterceptor
{

	@Resource
	ModelService modelService;

	@Override
	public synchronized void onValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model instanceof RecurringChargeEntryModel)
		{
			final RecurringChargeEntryModel toValidate = (RecurringChargeEntryModel) model;

			final int cycleStart = toValidate.getCycleStart() == null ? Integer.MAX_VALUE : toValidate.getCycleStart();
			final int cycleEnd = (toValidate.getCycleEnd() == null || toValidate.getCycleEnd() == -1) ? Integer.MAX_VALUE
					: toValidate.getCycleEnd();

			if (cycleStart > cycleEnd)
			{
				throw new InterceptorException("Cycle end must be greater than or equal to cycle start", this);
			}
		}
	}

}
