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

import de.hybris.platform.sap.core.jco.exceptions.BackendException;



/**
 * Interface for marking connections as stateful.
 */
public interface JCoStateful
{
	/**
	 * Gets called if connection should be destroyed.
	 * 
	 * @throws BackendException
	 *            in case of failure.
	 */
	public void destroy() throws BackendException;

}
