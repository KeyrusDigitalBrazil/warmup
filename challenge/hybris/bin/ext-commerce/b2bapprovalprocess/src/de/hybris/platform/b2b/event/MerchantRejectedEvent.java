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
package de.hybris.platform.b2b.event;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


public class MerchantRejectedEvent extends AbstractEvent
{
	private OrderModel order;
	private PrincipalModel manager;

	public MerchantRejectedEvent(final OrderModel order, final PrincipalModel manager)
	{
		this.order = order;
		this.manager = manager;
	}

	public OrderModel getOrder()
	{
		return order;
	}

	public void setOrder(final OrderModel order)
	{
		this.order = order;
	}

	public PrincipalModel getManager()
	{
		return manager;
	}

	public void setManager(final PrincipalModel manager)
	{
		this.manager = manager;
	}
}
