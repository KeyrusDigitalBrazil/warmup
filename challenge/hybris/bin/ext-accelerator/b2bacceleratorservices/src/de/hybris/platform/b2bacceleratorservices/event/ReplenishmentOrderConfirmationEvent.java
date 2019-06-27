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
package de.hybris.platform.b2bacceleratorservices.event;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class ReplenishmentOrderConfirmationEvent extends AbstractEvent
{
	private final OrderModel order;

	public ReplenishmentOrderConfirmationEvent(final OrderModel order)
	{
		this.order = order;
	}

	public OrderModel getOrder()
	{
		return order;
	}
}
