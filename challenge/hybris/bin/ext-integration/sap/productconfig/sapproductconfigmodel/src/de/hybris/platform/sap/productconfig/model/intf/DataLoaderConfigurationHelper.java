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
package de.hybris.platform.sap.productconfig.model.intf;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.dataloader.configuration.DataloaderSourceParameters;

import java.util.Map;


/**
 * Handles configuration settings for Data Loader
 */
public interface DataLoaderConfigurationHelper
{

	/**
	 * Fetches source attributes for data load call which can be derived from the SAPConfiguration bean
	 * 
	 * @param configuration
	 * @return Source parameters
	 */
	DataloaderSourceParameters getDataloaderSourceParam(SAPConfigurationModel configuration);

	/**
	 * Creates configuration map for data load call
	 * 
	 * @param params
	 * @return Config map
	 */
	Map<String, String> createConfigMap(DataloaderSourceParameters params);

	/**
	 * Prepares filter files and stores them into the configuration map
	 * 
	 * @param dataloaderConfigMap
	 * @param sapConfiguration
	 */
	void prepareFilterFiles(Map<String, String> dataloaderConfigMap, SAPConfigurationModel sapConfiguration);

	/**
	 * Compiles path to the filter files
	 * 
	 * @param filterFile
	 * @return Path
	 */
	String getAbsolutFilePathForMedia(MediaModel filterFile);

}
