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
package de.hybris.platform.sap.core.jco.connection;

/**
 * Interface for Managed Connection Factory.
 */
public interface JCoManagedConnectionFactory
{

	/**
	 * Returns a managed connection by a given connection name.
	 * 
	 * @param connectionName
	 *           name of the connection
	 * @param callerId
	 *           id for identifying the caller used for debugging
	 * @return managed connection instance
	 */
	public JCoConnection getManagedConnection(final String connectionName, final String callerId);

	/**
	 * Returns a managed connection by a given connection name.
	 * 
	 * @param connectionName
	 *           name of the connection
	 * @param callerId
	 *           id for identifying the caller used for debugging
	 * @param destinationName
	 *           name of the destination to be used
	 * @return managed connection instance
	 */
	public JCoConnection getManagedConnection(final String connectionName, final String callerId, final String destinationName);

}
