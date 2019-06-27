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
package de.hybris.platform.sap.sapinvoicebol.backend.impl;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoicebol.backend.SapInvoiceBackend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoTable;


/**
 *
 */
public class SapInvoiceBackendImpl extends BackendBusinessObjectBaseJCo implements SapInvoiceBackend
{
	static final private Log4JWrapper logger = Log4JWrapper.getInstance(SapInvoiceBackendImpl.class.getName());


	private String jcoFunction = null;

	/**
	 * @return the jcoFunction
	 */
	public String getJcoFunction()
	{
		return jcoFunction;
	}

	/**
	 * @param jcoFunction
	 *           the jcoFunction to set
	 */
	public void setJcoFunction(final String jcoFunction)
	{
		this.jcoFunction = jcoFunction;
	}

	/**
	 * @return the billingDocumentFieldName
	 */
	public String getBillingDocumentFieldName()
	{
		return billingDocumentFieldName;
	}

	/**
	 * @param billingDocumentFieldName
	 *           the billingDocumentFieldName to set
	 */
	public void setBillingDocumentFieldName(final String billingDocumentFieldName)
	{
		this.billingDocumentFieldName = billingDocumentFieldName;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters()
	{
		return parameters;
	}

	/**
	 * @param parameters
	 *           the parameters to set
	 */
	public void setParameters(final Map<String, String> parameters)
	{
		this.parameters = parameters;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}

	private String tableName = null;

	/**
	 * @param tableName
	 *           the tableName to set
	 */
	public void setTableName(final String tableName)
	{
		this.tableName = tableName;
	}

	private String billingDocumentFieldName = null;
	private Map<String, String> parameters;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.ysapordermgmtb2baddon.backend.SapInvoiceBackend#getInvoiceInByte(java.lang.String)
	 */
	@Override
	public byte[] getInvoiceInByte(final String billingDocNumber) throws BackendException
	{
		JCoConnection connection = null;
		byte[] invoicePdfByteArray = null;
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try
		{



			connection = getDefaultJCoConnection();
			final JCoFunction function = connection.getFunction(getJcoFunction());
			final JCoParameterList importParameters = function.getImportParameterList();
			importParameters.setValue(getBillingDocumentFieldName(), billingDocNumber);


			for (final Map.Entry<String, String> entry : getParameters().entrySet())
			{
				importParameters.setValue(entry.getKey(), entry.getValue());
			}
			connection.execute(function);
			final JCoTable tableList = function.getTableParameterList().getTable(getTableName());
			do
			{
				final JCoRecordFieldIterator itr1 = tableList.getRecordFieldIterator();
				while (itr1.hasNextField() && tableList.getNumRows() > 0)
				{
					final JCoField tabField = itr1.nextField();
					if (("LINE").equals(tabField.getName()))
					{
						outputStream.write(tabField.getByteArray());
					}

				}
			}
			while (tableList.nextRow() == true);
			invoicePdfByteArray = outputStream.toByteArray();

		}
		catch (final IOException e)
		{

			logger.getLogger().error("Error in getPDF method::", e);
		}

		return invoicePdfByteArray;
	}

	public byte[] serialize(final Object obj) throws IOException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	protected void closeConnection(final JCoConnection connection)
	{
		try
		{

			if (connection != null)
			{
				((JCoStateful) connection).destroy();
			}
		}
		catch (final BackendException ex)
		{

			logger.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES, "Error during JCoStateful connection close! " + ex);

		}
	}
}
