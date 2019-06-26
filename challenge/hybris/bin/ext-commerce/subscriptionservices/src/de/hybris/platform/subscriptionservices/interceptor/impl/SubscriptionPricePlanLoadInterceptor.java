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
import de.hybris.platform.servicelayer.interceptor.LoadInterceptor;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.impl.RecurringChargeEntryModelSortService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Nonnull;


/**
 * {@link LoadInterceptor} implementation for instances of {@link SubscriptionPricePlanModel}. Ensures that:<br>
 * <ul>
 * <li>recurring charge entries are sorted by their periods</li>
 * </ul>
 */
public class SubscriptionPricePlanLoadInterceptor implements LoadInterceptor
{
	@Autowired
	@Qualifier("recurringChargeEntryModelSortService")
	private RecurringChargeEntryModelSortService sortService;

	@Override
	public void onLoad(@Nonnull final Object model, @Nonnull final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model instanceof SubscriptionPricePlanModel)
		{
			final SubscriptionPricePlanModel pricePlan = (SubscriptionPricePlanModel) model;

			// sort recurring charges
			pricePlan.setRecurringChargeEntries(sortService.sort(pricePlan.getRecurringChargeEntries()));
		}
	}

}
