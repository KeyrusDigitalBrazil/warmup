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
import de.hybris.platform.subscriptionservices.model.ChargeEntryModel;

import javax.annotation.Nonnull;


/**
 * Interceptor to validate ChargeEntryModel.
 * <ul>
 * <li>the {@link ChargeEntryModel}'s parent objects are marked as modified
 * <li>the price of the {@link ChargeEntryModel} is not negative
 * </ul>
 */
public class ChargeEntryValidateInterceptor extends AbstractParentChildValidateInterceptor
{
	@Override
	protected void doValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model instanceof ChargeEntryModel)
		{
			final ChargeEntryModel chargeEntry = (ChargeEntryModel) model;
			if (chargeEntry.getPrice() < 0.0D)
			{
				throw new InterceptorException("The price must not be negative");
			}
		}
	}

}
