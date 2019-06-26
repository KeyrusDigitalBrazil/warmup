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
package de.hybris.platform.sap.core.jco.rec.version000.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.RecorderUtils;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.impl.JCoRecFunctionDecorator;
import de.hybris.platform.sap.core.jco.rec.version000.JCoRecXMLParserException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * This represents the repository while it is held in memory.
 */
public class JCoRecRepository implements RepositoryPlayback
{

	/**
	 * Logger.
	 */
	private static final Logger LOG = Logger.getLogger(JCoRecRepository.class.getName());

	private final File repoFile;

	private final JCoRecMode mode;

	/** counts the number of uses of the respective function. From functionName to number of uses. */
	private final Map<String, Integer> executionCounter;

	/** Function template repository. */
	private final JCoCustomRepository metaDataRepository;
	/** All executed functions. Identified by FunctionName+::+numberOfExecutions. */
	private final Map<String, JCoRecFunctionDecorator> functions;
	private final Map<String, JCoRecord> records;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *           the repository-file from which the data will be loaded.
	 */
	public JCoRecRepository(final File file)
	{
		functions = new HashMap<String, JCoRecFunctionDecorator>();
		records = new HashMap<String, JCoRecord>();
		executionCounter = new HashMap<String, Integer>();

		repoFile = file;
		this.metaDataRepository = JCo.createCustomRepository(getRepositoryKey());

		// JCoRecRepository will be used for playback only
		this.mode = JCoRecMode.PLAYBACK;
	}

	/**
	 * The name of the repository-file is the key for the repository.
	 * 
	 * @return Returns the name of the repository-file.
	 */
	private String getRepositoryKey()
	{
		return repoFile.getName();
	}

	/**
	 * Getter for the current {@link #metaDataRepository}.
	 * 
	 * @return Returns the current {@link #metaDataRepository}
	 */
	public JCoCustomRepository getMetaDataRepository()
	{
		return metaDataRepository;
	}

	/**
	 * Adds the {@code function} with given counter to the local {@link #metaDataRepository}.
	 * 
	 * @param function
	 *           the new function to add.
	 * @param counter
	 *           parameter for the new {@link JCoRecFunctionDecorator}.
	 * @return Returns the previous mapping for the {@link JCoRecFunctionDecorator#getFunctionKey()
	 *         function.getFunctionKey()} or {@code null} if there was no mapping.
	 */
	protected JCoRecFunctionDecorator put(JCoFunction function, final int counter)
	{
	    JCoFunction newFunction=function;
		if (function instanceof JCoRecFunctionDecorator)
		{
		    newFunction = ((JCoRecFunctionDecorator) function).getDecoratedFunction();
		}
		final JCoRecFunctionDecorator toPut = new JCoRecFunctionDecorator(newFunction, counter, mode);

		this.metaDataRepository.addFunctionTemplateToCache(toPut.getFunctionTemplate());
		return functions.put(toPut.getFunctionKey(), toPut);
	}


	/**
	 * Loads the saves repository and all its contents from the repository file specified in
	 * {@code JCoRecManagedConnectionFactory.getKey()}.
	 * 
	 * @param input
	 *           the location of the source repository file.
	 */
	public void parseRepositoryFile(final File input)
	{
		String filePath = "";
		try
		{
			filePath = input.getCanonicalPath();
			if (!input.exists())
			{
				throw new JCoRecRuntimeException("Cannot find recording file " + filePath);
			}

			new JCoRecDefaultXMLParser().parse(this, input);
		}
		catch (final JCoRecXMLParserException e)
		{
			throw new JCoRecRuntimeException("Error parsing " + filePath, e);
		}
		catch (final IOException e)
		{
			throw new JCoRecRuntimeException("IOException during reading of canonical path.", e);
		}
	}

	/**
	 * Increases the execution-counter for the function with the given {@code functionName}.
	 * 
	 * @param functionName
	 *           the name of the function for which the counter should be increased.
	 * @return Returns the value of the counter for this function after increasing it.
	 */
	public int increaseCounter(final String functionName)
	{
		Integer count = executionCounter.get(functionName);
		if (count != null)
		{
			count++;
		}
		else
		{
			count = Integer.valueOf(1);
		}

		executionCounter.put(functionName, count);
		return count;
	}

	/**
	 * Returns a JCoStructure (JCoRecord) by it's key.
	 * 
	 * @param key
	 *           the key for this JCoStructure.
	 * @return Returns the JCoStructure with the given key.
	 * @throws JCoRecException
	 *            if the Record with this key is not a JCoStructure or there is no JCoStructure with this key.
	 */
	public JCoStructure getStructure(final String key) throws JCoRecException
	{
		try
		{
			return (JCoStructure) this.getRecord(key);
		}
		catch (final ClassCastException e)
		{
			LOG.error(e);
			throw new JCoRecException("Record with key " + key + " is not a JCoStructure", new ClassCastException());
		}
	}

	/**
	 * Returns a JCoTable by it's key.
	 * 
	 * @param key
	 *           for this JCoTable.
	 * @return Returns the JCoTable with the given key.
	 * @throws JCoRecException
	 *            if the Record with this key is not a JCoTable or there is no JCoTable with this key.
	 */
	public JCoTable getTable(final String key) throws JCoRecException
	{
		try
		{
			final JCoTable table = (JCoTable) this.getRecord(key);
			table.firstRow();
			return table;
		}
		catch (final ClassCastException e)
		{
			LOG.error(e);
			throw new JCoRecException("Record with key " + key + " is not a JCoTable", new ClassCastException());
		}
	}

	/**
	 * Puts {@code newRecord} with the {@code recordKey} into the map {@link #records}.
	 * 
	 * @param recordKey
	 *           the identifier of the {@code newRecord}.
	 * @param record
	 *           the new value for the map {@link #records}.
	 */
	public void put(final String recordKey, final JCoRecord record)
	{
		records.put(recordKey, record);
	}

	@Override
	public JCoFunction getFunction(final String functionName) throws JCoRecException
	{
		final int counter = increaseCounter(functionName);
		final String functionKey = RecorderUtils.getFunctionKey(functionName, counter);
		final JCoRecFunctionDecorator function = functions.get(functionKey);
		if (function == null)
		{
			throw new JCoRecException("Function " + functionKey + " not found in repository!");
		}
		return function;
	}

	@Override
	public JCoRecord getRecord(final String recordName) throws JCoRecException
	{
		final JCoRecord record = records.get(recordName);
		if (record == null)
		{
			throw new JCoRecException("JCoRecord with key " + recordName + " is not available");
		}
		return records.get(recordName);
	}

	@Override
	public String toString()
	{
		return super.toString() + " with key " + getRepositoryKey();
	}
}
