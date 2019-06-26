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

import de.hybris.platform.orderprocessing.model.OrderProcessModel;


/**
 * Event representing a failure to capture payment
 */
public class PaymentFailedEvent extends OrderProcessingEvent
{
	private static final long serialVersionUID = -4143696687348230520L;

	public PaymentFailedEvent(final OrderProcessModel process)
	{
		super(process);
	}
}
