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


public class OrderRejectedEvent extends AbstractEvent
{
	private OrderModel order;
	private PrincipalModel approver;

	public OrderRejectedEvent(final OrderModel order, final PrincipalModel approver)
	{
		this.order = order;
		this.approver = approver;
	}

	public OrderModel getOrder()
	{
		return order;
	}

	public void setOrder(final OrderModel order)
	{
		this.order = order;
	}

	public PrincipalModel getApprover()
	{
		return approver;
	}

	public void setApprover(final PrincipalModel approver)
	{
		this.approver = approver;
	}
}
