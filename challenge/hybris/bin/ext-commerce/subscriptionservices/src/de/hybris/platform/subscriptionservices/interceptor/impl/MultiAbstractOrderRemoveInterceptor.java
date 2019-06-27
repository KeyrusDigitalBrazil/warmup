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

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.fest.util.Collections;

import javax.annotation.Nonnull;


/**
 * If a master {@link AbstractOrderModel} is deleted all dependent child {@link AbstractOrderModel}s are also removed.
 */
public class MultiAbstractOrderRemoveInterceptor implements RemoveInterceptor
{

	@Override
	public void onRemove(@Nonnull final Object model, @Nonnull final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model instanceof AbstractOrderModel)
		{
			final AbstractOrderModel order = (AbstractOrderModel) model;

			if (!Collections.isEmpty(order.getChildren()))
			{
				for (final AbstractOrderModel childOrder : order.getChildren())
				{
					ctx.getModelService().remove(childOrder);
				}
			}
		}
	}

}
