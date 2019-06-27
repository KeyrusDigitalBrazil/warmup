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
package de.hybris.platform.orderprocessing.events;

import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


public class SendOrderPartiallyCanceledMessageEvent extends AbstractEvent
{
	private static final long serialVersionUID = 1L;

	private final OrderModificationProcessModel process;

	public SendOrderPartiallyCanceledMessageEvent(final OrderModificationProcessModel process)
	{
		this.process = process;
	}

	public OrderModificationProcessModel getProcess()
	{
		return process;
	}

}
