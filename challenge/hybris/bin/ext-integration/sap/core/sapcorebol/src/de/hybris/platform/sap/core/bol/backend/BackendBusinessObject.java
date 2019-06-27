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
package de.hybris.platform.sap.core.bol.backend;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Interface for backend business objects.
 */
public interface BackendBusinessObject
{
	/**
	 * This method is called by the SessionObjectManager to initialize object after all properties have been injected.
	 * 
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public void initBackendObject() throws BackendException;

	/**
	 * This method is called by the Spring framework before the object gets invalidated. It can be used to lean up
	 * resources allocated by the backend object.
	 */
	public void destroyBackendObject();

}
