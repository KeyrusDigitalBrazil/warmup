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
package de.hybris.platform.sap.core.jco.mock;

import de.hybris.platform.sap.core.jco.rec.JCoRecException;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * Class for mocking JCo result structures based on a file.
 */
public interface JCoMockRepository
{
	/**
	 * Returns a JCoRecord (ie. JCoTable or JCoStructure) by it's key.
	 * 
	 * @param key
	 *           for this JCoRecord.
	 * @return Returns the Record with the given key.
	 * @throws JCoRecException
	 *            if there is no Record available with the given key.
	 */
	public JCoRecord getRecord(final String key) throws JCoRecException;

	/**
	 * Returns a JCoStructure (JCoRecord) by it's key.
	 * 
	 * @param key
	 *           the key for this JCoStructure.
	 * @return Returns the JCoStructure with the given key.
	 * @throws JCoRecException
	 *            if the Record with this key is not a JCoStructure or there is no JCoStructure with this key.
	 */
	public JCoStructure getStructure(final String key) throws JCoRecException;

	/**
	 * Returns a JCoTable by it's key.
	 * 
	 * @param key
	 *           for this JCoTable.
	 * @return Returns the JCoTable with the given key.
	 * @throws JCoRecException
	 *            if the Record with this key is not a JCoTable or there is no JCoTable with this key.
	 */
	public JCoTable getTable(final String key) throws JCoRecException;






}
