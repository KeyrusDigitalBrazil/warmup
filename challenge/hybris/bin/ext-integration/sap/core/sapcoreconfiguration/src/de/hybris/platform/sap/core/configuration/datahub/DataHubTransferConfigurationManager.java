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
package de.hybris.platform.sap.core.configuration.datahub;

import de.hybris.platform.core.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Central class that manages all the Data Hub Transfer configurations. Example to add a Data Hub Transfer configuration
 * via the corresponding spring.xml file:
 * 
 * <pre>
 * {@code 
 * 	<bean id="sapCoreSAPBaseStoreDataHubConfiguration" parent="sapCoreDataHubTransferConfiguration">
 *  		<property name="itemCode"  value="SAPConfiguration" />
 * 		<property name="rawType"   value="RawSAPBaseStoreConfiguration" />
 * 		<property name="converter" ref="sapCoreSAPBaseStoreConfigurationConverter" />
 * 	}
 * </pre>
 */
public class DataHubTransferConfigurationManager
{
	private static final Logger LOG = Logger.getLogger(DataHubTransferConfigurationManager.class.getName());
	private final Map<String, List<DataHubTransferConfiguration>> dataHubTransferConfigurationMap = new HashMap<String, List<DataHubTransferConfiguration>>();
	private Map<Integer, String> typeCodeMap = null;

	/**
	 * Returns the map of Data Hub Transfer configurations.
	 * 
	 * @return the Map of Data Hub Transfer configurations
	 */
	public Map<String, List<DataHubTransferConfiguration>> getAllDataHubConfigurations()
	{
		return dataHubTransferConfigurationMap;
	}

	/**
	 * Determines the Data Hub configuration for the given type code.
	 * 
	 * @param code
	 *           the given code
	 * @return the Data Hub configuration
	 */
	public List<DataHubTransferConfiguration> getDataHubTransferConfigurations(final String code)
	{
		return dataHubTransferConfigurationMap.get(code);
	}

	/**
	 * Adds the Data Hub Transfer configuration to list of existing configurations.
	 * 
	 * @param dataHubTransferConfiguration
	 *           the Data Hub Transfer configuration
	 */
	public void addToDataHubTransferConfigurations(final DataHubTransferConfiguration dataHubTransferConfiguration)
	{
		final String code = dataHubTransferConfiguration.getItemCode();
		if (code != null && !code.isEmpty())
		{
			if (!dataHubTransferConfigurationMap.containsKey(code))
			{
				final List<DataHubTransferConfiguration> dataHubTransferConfigurations = new ArrayList<DataHubTransferConfiguration>();
				dataHubTransferConfigurationMap.put(code, dataHubTransferConfigurations);
			}
			dataHubTransferConfigurationMap.get(code).add(dataHubTransferConfiguration);
			LOG.debug("Datahub transfer configuration added to manager: " + dataHubTransferConfiguration);
		}
	}

	/**
	 * Translates the integer type code to string code.
	 * 
	 * @param typeCodeID
	 *           integer type code
	 * @return string typeCodeName
	 */
	public String getItemCode(final int typeCodeID)
	{
		if (typeCodeMap == null)
		{
			typeCodeMap = new HashMap<Integer, String>();
			final Set<String> typeCodeKeySet = dataHubTransferConfigurationMap.keySet();

			for (final String typeCodeName : typeCodeKeySet)
			{
				final int localTypeCodeID = getItemTypeCode(typeCodeName);
				if (localTypeCodeID == -1)
				{
					LOG.warn("No item type code found for item code '" + typeCodeName
							+ "'! Data won't be replicated to data hub after save.");
				}
				else
				{
					typeCodeMap.put(localTypeCodeID, typeCodeName);
				}
			}
		}

		return typeCodeMap.get(typeCodeID);
	}

	/**
	 * Gets the integer type code for string code.
	 * 
	 * @param typeCodeName
	 *           item code as string
	 * @return item type code as integer
	 */
	public int getItemTypeCode(final String typeCodeName)
	{
		try
		{
			return Registry.getCurrentTenant().getPersistenceManager().getPersistenceInfo(typeCodeName).getItemTypeCode();
		}
		catch (final IllegalStateException ex)
		{
			LOG.error(ex);
			LOG.debug(
					"Error while retrieving item code '" + typeCodeName + "'! Possibly caused by persistence manager timing issue.");
			return -1;
		}
	}
}
