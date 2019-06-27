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

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * This class handles the transfer of configuration models to the data hub via property map.
 * 
 */
public class DataHubTransferHandler
{
	private static final Logger LOGGER = LogManager.getLogger(DataHubTransferHandler.class);
	private DataHubOutboundService outboundService;

	/**
	 * Injection setter for {@link DataHubOutboundService}
	 * 
	 * @param outboundService
	 *           {@link DataHubOutboundService}
	 */
	public void setDataHubOutboundService(final DataHubOutboundService outboundService)
	{
		this.outboundService = outboundService;
	}

	/**
	 * Invokes the data transfer of the given configuration models to data hub.
	 * 
	 * @param code
	 *           item code as string
	 * @param models
	 *           list of configuration models
	 * @param dataHubTransferConfigurations
	 *           list of data hub transfer configurations related to this model
	 * @param dataHubTransferLog
	 *           collect the log information for the data hub
	 * @return {@link de.hybris.platform.sap.core.configuration.datahub.DataHubTransferLog}
	 * @throws DataHubCommunicationException
	 *            if the Data Hub server was unable to successfully complete the export operation
	 */
	@SuppressWarnings("unchecked")
	public DataHubTransferLog invokeTransfer(final String code, final List<ItemModel> models,
			final List<DataHubTransferConfiguration> dataHubTransferConfigurations, DataHubTransferLog dataHubTransferLog)
			throws DataHubCommunicationException
	{
		if (dataHubTransferLog == null)
		{
			dataHubTransferLog = new DataHubTransferLog();
		}

		dataHubTransferLog.setItemCode(code);
		if (!models.isEmpty())
		{
			for (final DataHubTransferConfiguration dataHubTransferConfiguration : dataHubTransferConfigurations)
			{
				final List<Map<String, Object>> propertyMapList = new ArrayList<>();
				for (final ItemModel model : models)
				{
					final AbstractPopulatingConverter<ItemModel, Map<String, Object>> converter = dataHubTransferConfiguration
							.getConverter();
					final Map<String, Object> propertyMap = converter.convert(model);
					propertyMapList.add(propertyMap);
				}

				try
				{
					invokeDataHubTransfer(code, propertyMapList, dataHubTransferConfiguration.getRawType(), dataHubTransferLog);
				}
				catch (final DataHubOutboundException ex)
				{
					final String logMessage = ex.getLocalizedMessage();
					dataHubTransferLog.addLog(logMessage);
					dataHubTransferLog.setTransferException(new DataHubTransferException(logMessage, ex));
					LOGGER.warn("{}: {}", code, logMessage);
				}
			}
		}
		else
		{
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.noConfigurationItem");
			dataHubTransferLog.addLog(logMessage);
			LOGGER.info("{}: {}", code, logMessage);
		}
		return dataHubTransferLog;
	}

	/**
	 * Delete configuration items in the Data Hub.
	 * 
	 * @param code
	 *           item code as string
	 * @param models
	 *           list of configuration models
	 * @param dataHubTransferConfigurations
	 *           list of data hub transfer configurations related to this model
	 * @param dataHubTransferLog
	 *           collect the log information for the data hub
	 * @return {@link de.hybris.platform.sap.core.configuration.datahub.DataHubTransferLog}
	 * @throws DataHubCommunicationException
	 *            if the Data Hub server was unable to successfully complete the export operation
	 */
	@SuppressWarnings("unchecked")
	public DataHubTransferLog invokeDeleteItem(final String code, final List<ItemModel> models,
			final List<DataHubTransferConfiguration> dataHubTransferConfigurations, DataHubTransferLog dataHubTransferLog)
			throws DataHubCommunicationException
	{
		if (dataHubTransferLog == null)
		{
			dataHubTransferLog = new DataHubTransferLog();
		}
		dataHubTransferLog.setItemCode(code);

		if (!models.isEmpty())
		{
			for (final DataHubTransferConfiguration dataHubTransferConfiguration : dataHubTransferConfigurations)
			{
				prepareForDataHubDeleteItem(code, models, dataHubTransferLog, dataHubTransferConfiguration);
			}
		}

		return dataHubTransferLog;
	}

	private void prepareForDataHubDeleteItem(String code, List<ItemModel> models, DataHubTransferLog dataHubTransferLog, DataHubTransferConfiguration dataHubTransferConfiguration) {
		for (final ItemModel model : models)
        {

            final AbstractPopulatingConverter<ItemModel, Map<String, Object>> converter = dataHubTransferConfiguration
                    .getConverter();
            final Map<String, Object> propertyMap = converter.convert(model);
            try
            {
                invokeDataHubDeleteItem(code, propertyMap, dataHubTransferConfiguration.getRawType(), dataHubTransferLog);
            }
            catch (final DataHubOutboundException ex)
            {
                final String logMessage = ex.getLocalizedMessage();
                dataHubTransferLog.addLog(logMessage);
                dataHubTransferLog.setTransferException(new DataHubTransferException(logMessage, ex));
                LOGGER.warn("{}: {}", code, logMessage);
            }
        }
	}

	/**
	 * Delete configuration content in the Data Hub. #
	 * 
	 * @param code
	 *           item code as string
	 * @param dataHubTransferConfigurations
	 *           list of data hub transfer configurations related to this model
	 * @param dataHubTransferLog
	 *           collect the log information for the data hub
	 * @return {@link de.hybris.platform.sap.core.configuration.datahub.DataHubTransferLog}
	 * @throws DataHubCommunicationException
	 *            if the Data Hub server was unable to successfully complete the export operation
	 */
	public DataHubTransferLog invokeDeleteContent(final String code,
			final List<DataHubTransferConfiguration> dataHubTransferConfigurations, DataHubTransferLog dataHubTransferLog)
			throws DataHubCommunicationException
	{
		if (dataHubTransferLog == null)
		{
			dataHubTransferLog = new DataHubTransferLog();
		}
		dataHubTransferLog.setItemCode(code);

		for (final DataHubTransferConfiguration dataHubTransferConfiguration : dataHubTransferConfigurations)
		{
			try
			{
				invokeDataHubDeleteContent(code, dataHubTransferConfiguration.getRawType(), dataHubTransferLog);
			}
			catch (final DataHubOutboundException ex)
			{
				final String logMessage = ex.getLocalizedMessage();
				dataHubTransferLog.addLog(logMessage);
				dataHubTransferLog.setTransferException(new DataHubTransferException(logMessage, ex));
				LOGGER.warn("{}: {}", code, logMessage);
			}
		}

		return dataHubTransferLog;
	}


	/**
	 * Creates the property map for the given model.
	 * 
	 * @param model
	 *           the given model
	 * @param converter
	 *           converter for populating the property map
	 * @return the related property map of the event
	 */
	protected Map<String, Object> createConfigurationMap(final ItemModel model,
			final AbstractPopulatingConverter<ItemModel, Map<String, Object>> converter)
	{
		return converter.convert(model);
	}

	/**
	 * Creates the DTO for the item type of the save event.
	 * 
	 * @param model
	 *           source item model
	 * @param converter
	 *           converter for populating the dto
	 * @return the related DTO of the event
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	protected Object createConfigurationDTO(final ItemModel model, final AbstractPopulatingConverter converter)
	{
		return converter.convert(model);
	}

	/**
	 * Transfers a list of property maps to Data Hub.
	 * 
	 * @param itemCode
	 *           item code as string
	 * @param propertyMapList
	 *           the list of property maps
	 * @param rawType
	 *           the raw type
	 * @param transferLog
	 *           transfer log container
	 * @throws DataHubOutboundException
	 *            if communication with the Data Hub server failed
	 */
	private void invokeDataHubTransfer(final String itemCode, final List<Map<String, Object>> propertyMapList,
			final String rawType, final DataHubTransferLog transferLog) throws DataHubOutboundException
	{
		try
		{
			outboundService.sendToDataHub(getOutboundFeedName(), rawType, propertyMapList);
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.sendToRawItem.success", new Object[]
			{ propertyMapList.size(), rawType });
			transferLog.addLog(logMessage);
			LOGGER.info("{}: {}", itemCode, logMessage);
		}
		catch (final DataHubOutboundException ex) // NOPMD
		{
			throw ex;
		}
		catch (final Exception ex)
		{
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.sendToRawItem.exception", new Object[]
			{ rawType }) + ex.getLocalizedMessage();
			transferLog.addLog(logMessage);
			transferLog.setTransferException(new DataHubTransferException(logMessage, ex));
			LOGGER.warn("{}: {}", itemCode, logMessage);
		}
	}

	/**
	 * Delete configuration items in the Data Hub.
	 * 
	 * @param itemCode
	 *           item code as string
	 * @param propertyMap
	 *           the map of properties to be deleted
	 * @param rawType
	 *           the raw type
	 * @param transferLog
	 *           transfer log container
	 * @throws DataHubOutboundException
	 *            if communication with the Data Hub server failed
	 */
	private void invokeDataHubDeleteItem(final String itemCode, final Map<String, Object> propertyMap, final String rawType,
			final DataHubTransferLog transferLog) throws DataHubOutboundException
	{
		try
		{
			outboundService.deleteByFeed(getOutboundFeedName(), rawType, propertyMap);
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.deleteItem.success", new Object[]
			{ propertyMap.size(), rawType });
			transferLog.addLog(logMessage);
			LOGGER.info("{}: {}", itemCode, logMessage);
		}
		catch (final DataHubOutboundException ex) // NOPMD
		{
			throw ex;
		}
		catch (final Exception ex)
		{
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.deleteItem.exception", new Object[]
			{ rawType }) + ex.getLocalizedMessage();
			transferLog.addLog(logMessage);
			transferLog.setTransferException(new DataHubTransferException(logMessage, ex));
			LOGGER.warn("{}: {}", itemCode, logMessage);
		}

	}

	/**
	 * Delete configuration content in the Data Hub.
	 * 
	 * @param itemCode
	 *           item code as string
	 * @param rawType
	 *           the raw type
	 * @param transferLog
	 *           transfer log container
	 * @throws DataHubOutboundException
	 *            if communication with the Data Hub server failed
	 */
	private void invokeDataHubDeleteContent(final String itemCode, final String rawType, final DataHubTransferLog transferLog)
			throws DataHubOutboundException
	{
		try
		{
			outboundService.deleteByFeed(getOutboundFeedName(), rawType);
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.deleteContent.success", new Object[]
			{ rawType });
			transferLog.addLog(logMessage);
			LOGGER.info("{}: {}", itemCode, logMessage);

		}
		catch (final DataHubOutboundException ex) // NOPMD
		{
			throw ex;
		}
		catch (final Exception ex)
		{
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.deleteContent.exception", new Object[]
			{ rawType }) + ex.getLocalizedMessage();
			transferLog.addLog(logMessage);
			transferLog.setTransferException(new DataHubTransferException(logMessage, ex));
			LOGGER.warn("{}: {}", itemCode, logMessage);
		}

	}

	/**
	 * Returns the data hub outbound feed name to be used to transfer configurations.
	 * 
	 * @return outbound feed name
	 */
	private String getOutboundFeedName()
	{
		return Config.getString(SapcoreconfigurationConstants.CONFIGURATION_DATAHUB_OUTBOUND_FEED_PROPERTY,
				SapcoreconfigurationConstants.CONFIGURATION_DATAHUB_OUTBOUND_FEED);
	}

}
