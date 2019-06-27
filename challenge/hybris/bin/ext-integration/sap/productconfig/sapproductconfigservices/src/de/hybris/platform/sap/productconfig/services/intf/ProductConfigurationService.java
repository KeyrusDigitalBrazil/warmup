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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * ProductConfigurationService provides access to the configuration engine implementation.
 *
 */
public interface ProductConfigurationService
{

	/**
	 * Based on the hybris product code, provided via the <code>KBKey.productCode</code>, the configuration engine will
	 * provide a default configuration for the requested product.
	 *
	 * @param kbKey
	 *           The product code for the configurable product
	 * @return The configurable product with default configuration
	 */
	ConfigModel createDefaultConfiguration(final KBKey kbKey);

	/**
	 * Based on the hybris product code, the configuration engine will provide a configuration for the requested product
	 * variant.
	 *
	 * @param baseProductCode
	 *           The product code for the configurable base product
	 * @param variantProductCode
	 *           The product code for the specific product variant
	 * @return The configurable product with default configuration
	 */
	ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode);

	/**
	 * Update the configuration model within the configuration engine.
	 *
	 * @param model
	 *           Updated model
	 */
	void updateConfiguration(final ConfigModel model);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in the <code>ConfigModel</code>
	 * format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	ConfigModel retrieveConfigurationModel(String configId);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in a <i>XML</i> format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 */
	String retrieveExternalConfiguration(final String configId);

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param kbKey
	 *           Key attributes needed to create a model
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternal(final KBKey kbKey, String externalConfiguration);

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 * and links it immediately with the given cart entry key
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 *
	 * @param kbKey
	 *           Key attributes needed to create a model
	 *
	 * @param cartEntryKey
	 *           cartEntryKey this config belongs to
	 *
	 * @return Configuration model
	 */
	default ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration);
	}

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 * and links it immediately with the given cart entry key
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 *
	 * @param kbKey
	 *           Key attributes needed to create a model
	 *
	 * @param cartEntryKey
	 *           cartEntryKey this config belongs to
	 * @param retrievalOptions
	 *           options to modify behavior
	 *
	 * @return Configuration model
	 */
	default ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey, final ConfigurationRetrievalOptions retrievalOptions)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration);
	}


	/**
	 * /** Create a <code>ConfigModel</code> based on a <code>Configuration</code> for the provided product code.
	 *
	 * @param extConfig
	 *           Configuration in a data structure
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(final Configuration extConfig);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 *
	 * @param configId
	 *           session id
	 */
	void releaseSession(String configId);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 *
	 * @param configId
	 *           session id
	 * @param keepModel
	 *           signifies whether config model should be kept despite releasing session
	 */
	default void releaseSession(final String configId, final boolean keepModel)
	{
		if (keepModel)
		{
			Logger.getLogger(ProductConfigurationService.class)
					.warn("Default implementation of releaseSession always deletes config model");
		}
		releaseSession(configId);
	}

	/**
	 * Get the number of errors (conflict, not filled mandatory fields), as it is set at the cart item
	 *
	 * @param configId
	 *           id of the configuration
	 * @return Total number of errors
	 */
	int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId);

	/**
	 * Checks whether a kb version exists for a given product and date
	 *
	 * @param productCode
	 *           product code
	 * @param kbDate
	 *           date of the knowledgebase
	 * @return true if KB version for the date exists
	 */
	boolean hasKbForDate(final String productCode, final Date kbDate);

	/**
	 * Checks whether kb version specified in the external config still exists
	 *
	 * @param kbKey
	 *           knowledgebase key which is used to extract product code
	 * @param externalConfig
	 *           external configuration
	 * @return true if KB version specified in the external config exists
	 * @deprecated since 18.08 use {@link ProductConfigurationService#isKbVersionValid)} instead
	 */
	@Deprecated
	boolean hasKbForVersion(KBKey kbKey, String externalConfig);

	/**
	 * Checks whether kb version exists and is valid for specified date
	 *
	 * @param kbKey
	 *           knowledgebase key
	 * @return true if KB version exists and is valid
	 */
	boolean isKbVersionValid(KBKey kbKey);

	/**
	 * Returns the total number of issues (number of solvable conflicts + number of incomplete cstics)
	 *
	 * @param configModel
	 *           configuration model
	 * @return total number of issues
	 */
	int getTotalNumberOfIssues(final ConfigModel configModel);

	/**
	 * Extracts the KBKey from the external configuration
	 *
	 * @param externalConfig
	 *           external config
	 * @param productCode
	 *           product code
	 * @return returns the kBKey of the given external config
	 */
	KBKey extractKbKey(final String productCode, final String externalConfig);

}
