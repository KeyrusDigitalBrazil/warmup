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
import de.hybris.platform.servicelayer.event.PublishEventContext;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


public class InvalidateCharonCacheEvent extends AbstractEvent implements ClusterAwareEvent
{
	private String cacheKey;

	public InvalidateCharonCacheEvent(final String cacheKey)
	{
		this.cacheKey = cacheKey;
	}

	@Override
	public boolean canPublish(final PublishEventContext publishEventContext)
	{
		return true;
	}

	public String getCacheKey()
	{
		return cacheKey;
	}

	protected void setCacheKey(final String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
}