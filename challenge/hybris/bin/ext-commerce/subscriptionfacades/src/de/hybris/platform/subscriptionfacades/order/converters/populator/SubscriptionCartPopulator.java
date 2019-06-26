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
package de.hybris.platform.subscriptionfacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Nonnull;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Subscription cart populator.
 */
public class SubscriptionCartPopulator extends AbstractSubscriptionOrderPopulator<CartModel, CartData>
{
	// Concrete implementation of the SubscriptionCartPopulator that should be used for further customizations

	@Override
	public void populate(@Nonnull final CartModel source,
	                     @Nonnull final CartData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);

		if (source.getBillingTime() == null)
		{
			// compatibility mode: do not perform the subscription specific populator tasks
			return;
		}

		super.populate(source, target);
		target.setTotalUnitCount(calcTotalUnitCount(source));
	}

}
