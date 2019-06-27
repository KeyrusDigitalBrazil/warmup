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
package de.hybris.platform.sap.core.jco.monitor.provider;

import java.util.List;

import com.sap.conn.jco.monitor.JCoConnectionData;
import com.sap.conn.jco.monitor.JCoConnectionMonitor;


/**
 * Provides the local JCo connection monitor context.
 */
public class JCoConnectionMonitorLocalContext implements JCoConnectionMonitorContext
{

	@Override
	public List<? extends JCoConnectionData> getJCoConnectionData()
	{
		return JCoConnectionMonitor.getConnectionsData();
	}

	@Override
	public long getSnapshotTimestamp()
	{
		return System.currentTimeMillis();
	}

}
