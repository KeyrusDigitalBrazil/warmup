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
 * Container for managed JCO connections. <br>
 */
public interface JCoManagedConnectionContainer
{

	/**
	 * Getter for managed connection. <br>
	 * Throws BackendRuntimeException in case of managed connection with given name cannot be constructed.
	 * 
	 * @param connectionName
	 *           name of the connection.
	 * @return managed connection.
	 */
	public JCoConnection getManagedConnection(String connectionName);

	/**
	 * Getter for managed connection. <br>
	 * Throws BackendRuntimeException in case of managed connection with given name cannot be constructed.
	 * 
	 * @param connectionName
	 *           name of the connection.
	 * @param destinationName
	 *           name of the destination to be used
	 * @return managed connection.
	 */
	public JCoConnection getManagedConnection(String connectionName, final String destinationName);

	/**
	 * Getter for managed connection. <br>
	 * Throws BackendRuntimeException in case of managed connection with given name cannot be constructed.
	 * 
	 * @param connectionName
	 *           name of the connection.
	 * @param destinationName
	 *           name of the destination to be used
	 * @param scopeId
	 *           scope id which is needed to identify different connections (sessions) to the same destination
	 * @return managed connection.
	 */
	public JCoConnection getManagedConnection(String connectionName, final String destinationName, String scopeId);

}
