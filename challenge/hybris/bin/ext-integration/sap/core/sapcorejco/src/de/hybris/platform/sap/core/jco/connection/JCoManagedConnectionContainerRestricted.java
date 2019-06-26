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
 * Restricted interface for internal use to react on JCoException.JCO_ERROR_COMMUNICATION.
 */
public interface JCoManagedConnectionContainerRestricted
{

	/**
	 * Removes the Stateful connection from managedConnection container.
	 * 
	 * @param connection
	 *           JCo Connection
	 */
	public void removeConnection(final JCoConnection connection);
}
