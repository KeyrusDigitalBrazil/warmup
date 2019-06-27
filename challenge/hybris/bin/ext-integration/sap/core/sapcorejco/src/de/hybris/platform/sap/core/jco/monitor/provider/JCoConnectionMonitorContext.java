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

/**
 * Interface to get JCo connection monitor context data.
 */

public interface JCoConnectionMonitorContext {

    /**
     * Returns current JCo connection data.
     * 
     * @return JCo connection data list
     */
    @java.lang.SuppressWarnings("squid:S1452")
    public List<? extends JCoConnectionData> getJCoConnectionData();

    /**
     * Returns the time stamp of the snapshot.
     * 
     * @return snapshot time stamp
     */
    public long getSnapshotTimestamp();

}
