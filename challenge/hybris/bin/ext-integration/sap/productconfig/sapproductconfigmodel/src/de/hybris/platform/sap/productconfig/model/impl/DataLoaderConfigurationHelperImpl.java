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
package de.hybris.platform.sap.productconfig.model.impl;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.productconfig.model.dataloader.configuration.DataloaderSourceParameters;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderConfigurationHelper;
import de.hybris.platform.util.Config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.DataloaderConfiguration;


/**
 * Default implementation of {@link DataLoaderConfigurationHelper}
 */
public class DataLoaderConfigurationHelperImpl implements DataLoaderConfigurationHelper
{
	private static final String DB_JNDI_DATASOURCE = "DB_JNDI_DATASOURCE";
	private static final String DB_JNDI_USAGE = "DB_JNDI_USAGE";

	private static final Logger LOG = Logger.getLogger(DataLoaderConfigurationHelperImpl.class);

	private static final String SSC_DATABASE_TYPE = "crm.system_type";
	private static final String SSC_DATABASE_HOSTNAME = "crm.database_hostname";
	private static final String SSC_DATABASE_NAME = "crm.database";
	private static final String SSC_DATABASE_PORT = "crm.database_port";
	private static final String SSC_DATABASE_USER = "crm.database_user";
	private static final String SSC_DATABASE_PASSWORD = "crm.database_password";
	private static final String SSC_JNDI_USAGE = "crm.ssc_jndi_usage";
	private static final String SSC_JNDI_DATASOURCE = "crm.ssc_jndi_datasource";
	private static final String SSC_DATABASE_CLIENT = "crm.client";
	private Class<?> configClazz = DataloaderConfiguration.class;


	@Override
	public DataloaderSourceParameters getDataloaderSourceParam(final SAPConfigurationModel configuration)
	{

		final SAPRFCDestinationModel sapServer = configuration.getSapproductconfig_sapServer();
		final String rfcDestination = configuration.getSapproductconfig_sapRFCDestination();

		if (sapServer == null)
		{
			throw new IllegalArgumentException("An RFC destination is needed to connect to the backend system");
		}

		final DataloaderSourceParameters params = new DataloaderSourceParameters();
		params.setClient(sapServer.getClient());
		params.setClientRfcDestination(rfcDestination);
		params.setServerRfcDestination(sapServer.getRfcDestinationName());
		return params;
	}

	@Override
	public Map<String, String> createConfigMap(final DataloaderSourceParameters params)
	{
		final Map<String, String> dataloaderConfigMap = new HashMap<>();

		final String eccClient = params.getClient();
		final String outboundDestination = params.getServerRfcDestination();
		final String eccDestination = params.getClientRfcDestination();



		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("\n Dataloader configuration attributes:");
			debugOutput.append("\n ECC client               : ").append(eccClient);
			debugOutput.append("\n RFC outbound dest        : ").append(outboundDestination);
			debugOutput.append("\n RFC ECC dest             : ").append(eccDestination);
			LOG.debug(debugOutput.toString());
		}
		dataloaderConfigMap.put(DataloaderConfiguration.ECC_CLIENT, eccClient);
		dataloaderConfigMap.put(DataloaderConfiguration.OUTBOUND_DESTINATION_NAME, outboundDestination);
		dataloaderConfigMap.put(DataloaderConfiguration.ECC_RFC_DESTINATION, eccDestination);

		// SSC DB
		String targetFromProperties = Boolean.toString(true);
		if (fieldsAvailable(configClazz))
		{
			targetFromProperties = Boolean.toString(false);
			dataloaderConfigMap.put(DataloaderConfiguration.DB_TYPE, getHybrisConfigParam(SSC_DATABASE_TYPE));
			dataloaderConfigMap.put(DataloaderConfiguration.DB_HOST, getHybrisConfigParam(SSC_DATABASE_HOSTNAME));
			dataloaderConfigMap.put(DataloaderConfiguration.DB_NAME, getHybrisConfigParam(SSC_DATABASE_NAME));
			dataloaderConfigMap.put(DataloaderConfiguration.DB_PORT, getHybrisConfigParam(SSC_DATABASE_PORT));
			dataloaderConfigMap.put(DataloaderConfiguration.DB_USERNAME, getHybrisConfigParam(SSC_DATABASE_USER));
			dataloaderConfigMap.put(DataloaderConfiguration.DB_PASSWORD, getHybrisConfigParam(SSC_DATABASE_PASSWORD));
			dataloaderConfigMap.put(DB_JNDI_USAGE, getHybrisConfigParam(SSC_JNDI_USAGE));
			dataloaderConfigMap.put(DB_JNDI_DATASOURCE, getHybrisConfigParam(SSC_JNDI_DATASOURCE));
			dataloaderConfigMap.put(DataloaderConfiguration.DB_CLIENT, getHybrisConfigParam(SSC_DATABASE_CLIENT, "000"));
		}

		dataloaderConfigMap.put(DataloaderConfiguration.TARGET_FROM_PROPERTIES, targetFromProperties);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Target DB from properties: " + targetFromProperties);
		}

		return dataloaderConfigMap;
	}

	protected String getHybrisConfigParam(final String key)
	{
		return Config.getParameter(key);
	}

	protected String getHybrisConfigParam(final String key, final String defaultString)
	{
		return Config.getString(key, defaultString);
	}

	@Override
	public void prepareFilterFiles(final Map<String, String> dataloaderConfigMap, final SAPConfigurationModel sapConfiguration)
	{
		MediaModel filterFile = sapConfiguration.getSapproductconfig_filterKnowledgeBase();

		final String kbFilterFile = getAbsolutFilePathForMedia(filterFile);

		filterFile = sapConfiguration.getSapproductconfig_filterMaterial();

		final String materialsFilterFile = getAbsolutFilePathForMedia(filterFile);

		filterFile = sapConfiguration.getSapproductconfig_filterCondition();

		final String conditionsFilterFile = getAbsolutFilePathForMedia(filterFile);


		dataloaderConfigMap.put(DataloaderConfiguration.KB_FILTER_FILE_PATH, kbFilterFile);
		dataloaderConfigMap.put(DataloaderConfiguration.MATERIALS_FILTER_FILE_PATH, materialsFilterFile);
		dataloaderConfigMap.put(DataloaderConfiguration.CONDITIONS_FILTER_FILE_PATH, conditionsFilterFile);

		return;
	}

	@Override
	public String getAbsolutFilePathForMedia(final MediaModel filterFile)
	{

		String filterFileAbsolutPath = null;

		if (filterFile != null)
		{

			final boolean isAlive = !filterFile.getItemModelContext().isRemoved() && filterFile.getItemModelContext().isUpToDate();

			if (filterFile.getSize().longValue() != 0 && isAlive)
			{
				final File file = MediaManager.getInstance().getMediaAsFile(filterFile.getFolder().getQualifier(),
						filterFile.getLocation());

				filterFileAbsolutPath = file.getAbsolutePath();

			}
		}

		return filterFileAbsolutPath;
	}

	protected boolean fieldsAvailable(final Class<?> clazz)
	{
		boolean fieldJndiUsageExists = false;
		boolean fieldJndiDatasourceExists = false;
		for (final Field field : clazz.getFields())
		{
			if (DB_JNDI_USAGE.equals(field.getName()))
			{
				fieldJndiUsageExists = true;
			}
			if (DB_JNDI_DATASOURCE.equals(field.getName()))
			{
				fieldJndiDatasourceExists = true;
			}
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Additional fields are available in DataloaderConfiguration = "
					+ (fieldJndiUsageExists && fieldJndiDatasourceExists));
		}
		return fieldJndiUsageExists && fieldJndiDatasourceExists;
	}

	void setConfigClazz(final Class<?> configClazz)
	{
		this.configClazz = configClazz;
	}
}
