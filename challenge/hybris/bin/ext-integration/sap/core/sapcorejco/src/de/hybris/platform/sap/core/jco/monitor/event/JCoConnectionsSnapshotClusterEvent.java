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
package de.hybris.platform.sap.core.jco.monitor.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * Initiates the creation of a JCo connections snapshot on each cluster.
 */
public class JCoConnectionsSnapshotClusterEvent extends AbstractEvent implements ClusterAwareEvent
{

	private static final long serialVersionUID = -5580091916886609299L;
	private final String snapshotUuid;

	/**
	 * Standard constructor.
	 * 
	 * @param snapshotUuid
	 *           snapshot uuid
	 */
	public JCoConnectionsSnapshotClusterEvent(final String snapshotUuid)
	{
		super();
		this.snapshotUuid = snapshotUuid;

	}

	/**
	 * Returns the snapshot uuid.
	 * 
	 * @return the snapshotGuid
	 */
	public String getSnapshotUuid()
	{
		return snapshotUuid;
	}

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return true;
	}

}
