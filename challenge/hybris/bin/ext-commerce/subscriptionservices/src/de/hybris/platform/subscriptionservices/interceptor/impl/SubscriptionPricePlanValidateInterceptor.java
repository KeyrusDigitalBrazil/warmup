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
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import javax.annotation.Nonnull;


/**
 * Interceptor to validate SubscriptionPricePlanModel.
 * <ul>
 * <li>the {@link SubscriptionPricePlanModel}'s parent objects are marked as modified
 * </ul>
 */
public class SubscriptionPricePlanValidateInterceptor extends AbstractParentChildValidateInterceptor
{

	@Override
	public void doValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx) throws InterceptorException
	{
		// nothing is validated
	}

}
