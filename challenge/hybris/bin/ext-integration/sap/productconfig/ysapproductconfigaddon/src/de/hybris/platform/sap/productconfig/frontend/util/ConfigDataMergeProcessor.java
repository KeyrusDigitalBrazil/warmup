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
package de.hybris.platform.sap.productconfig.frontend.util;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;


/**
 * This class is able to merge two UI States for CPQ.<br>
 * This is required, because the CPQ UI usually renders only the part ({@link UiGroupData}) the user is currently
 * working on. hence when the controller receives the update request from the UI, the data is incomplete, There is the
 * need to fetch the the actual complete state and merge the updates from the UI into it, so that a complete and fully
 * updated configuration state is available for further processing.<br>
 * <b>Especially for big configurations, with a lot of cstics, this process is much faster than rendering the full state
 * and sending it back along with the updated values.</b>
 */
public interface ConfigDataMergeProcessor
{

	/**
	 * Fetches the actual configuration sate from the underlying layers and uses it to complete the given partial
	 * configuration state to build a complete and fully update state. So after the method call the given configuration
	 * will contain all values and each value will be updated to the most recent user input.
	 *
	 * @param targetConfigData
	 *           partial configuration to complete
	 */
	void completeInput(final ConfigurationData targetConfigData);

	/**
	 * Merges the source configuration into the target configuration. User input in the target configuration has the
	 * highest priority, so that it will be kept. The corresponding values of the source configuration are considered out
	 * dated and will be discarded.
	 *
	 * @param source
	 *           source configuration, missing data in the target configuration will be read from here
	 * @param target
	 *           target configuration, any values present will be kept, only missing data is read from source
	 *           configuration
	 */
	void mergeConfigurationData(final ConfigurationData source, final ConfigurationData target);

}
