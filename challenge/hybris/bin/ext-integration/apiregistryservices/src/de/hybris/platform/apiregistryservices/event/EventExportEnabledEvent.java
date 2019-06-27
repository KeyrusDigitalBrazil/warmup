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

import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * Event triggering event export enabling
 */
public class EventExportEnabledEvent extends AbstractEvent implements ClusterAwareEvent
{
	private List<Integer> targetNodeIds;

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return CollectionUtils.isEmpty(targetNodeIds) || targetNodeIds.contains(targetNodeId);
	}

	public List<Integer> getTargetNodeIds()
	{
		return targetNodeIds;
	}

	public void setTargetNodeIds(final List<Integer> targetNodeIds)
	{
		this.targetNodeIds = targetNodeIds;
	}
}
