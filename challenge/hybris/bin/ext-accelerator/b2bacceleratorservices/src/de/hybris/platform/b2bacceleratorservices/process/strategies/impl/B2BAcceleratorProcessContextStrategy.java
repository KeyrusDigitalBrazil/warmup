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
package de.hybris.platform.b2bacceleratorservices.process.strategies.impl;

import java.util.Optional;

import de.hybris.platform.acceleratorservices.process.strategies.impl.AbstractOrderProcessContextStrategy;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Strategy to impersonate site and initialize session context from an instance of ReplenishmentProcessModel.
 */
public class B2BAcceleratorProcessContextStrategy extends AbstractOrderProcessContextStrategy
{
	@Override
	protected Optional<CartModel> getOrderModel(final BusinessProcessModel businessProcessModel)
	{
		return Optional.of(businessProcessModel).filter(businessProcess -> businessProcess instanceof ReplenishmentProcessModel)
				.map(businessProcess -> ((ReplenishmentProcessModel) businessProcess).getCartToOrderCronJob())
				.map(CartToOrderCronJobModel::getCart);
	}
}
