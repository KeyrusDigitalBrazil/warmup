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

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


public class OrderPendingApprovalEvent extends AbstractEvent
{
	private final OrderProcessModel process;

	public OrderPendingApprovalEvent(final OrderProcessModel process)
	{
		this.process = process;
	}

	public OrderProcessModel getProcess()
	{
		return process;
	}
}
