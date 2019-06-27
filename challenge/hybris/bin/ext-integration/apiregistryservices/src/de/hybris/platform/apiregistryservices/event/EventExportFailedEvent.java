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
package de.hybris.platform.apiregistryservices.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * Event export failed cluster event
 */
public class EventExportFailedEvent extends AbstractEvent implements ClusterAwareEvent
{

	private String message;
	private Integer retry;

	public EventExportFailedEvent(final String message, final Integer retry)
	{
		this.message = message;
		this.retry = retry;
	}

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return true;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}

	public Integer getRetry()
	{
		return retry;
	}

	public void setRetry(final Integer retry)
	{
		this.retry = retry;
	}
}
