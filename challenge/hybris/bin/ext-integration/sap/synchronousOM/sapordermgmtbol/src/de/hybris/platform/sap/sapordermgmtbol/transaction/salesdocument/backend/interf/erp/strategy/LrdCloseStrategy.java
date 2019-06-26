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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Closes a LO-API session in the back end
 * 
 */
public interface LrdCloseStrategy
{

	/**
	 * Closes a LO-API session to release the attached resources (reservations...)
	 * 
	 * @param connection
	 * @throws BackendException
	 *            Issue when releasing LO-API (e.g. still loaded)
	 */
	void close(JCoConnection connection) throws BackendException;

}
