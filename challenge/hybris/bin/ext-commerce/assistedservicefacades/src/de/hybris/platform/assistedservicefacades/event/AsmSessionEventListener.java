/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedservicefacades.event;

import de.hybris.platform.assistedserviceservices.events.CustomerSupportEventService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.ticketsystem.events.SessionEvent;

import org.apache.log4j.Logger;


public class AsmSessionEventListener extends AbstractEventListener<SessionEvent>
{
	protected static final Logger logger = Logger.getLogger(AsmSessionEventListener.class); //NOSONAR

	private CustomerSupportEventService customerSupportEventService;

	@Override
	protected void onEvent(final SessionEvent sessionEventData)
	{
		if (null != sessionEventData && null != sessionEventData.getEventType())
		{
			customerSupportEventService.registerSessionEvent(sessionEventData);
		}
	}

	public CustomerSupportEventService getCustomerSupportEventService()
	{
		return customerSupportEventService;
	}

	public void setCustomerSupportEventService(final CustomerSupportEventService customerSupportEventService)
	{
		this.customerSupportEventService = customerSupportEventService;
	}
}

