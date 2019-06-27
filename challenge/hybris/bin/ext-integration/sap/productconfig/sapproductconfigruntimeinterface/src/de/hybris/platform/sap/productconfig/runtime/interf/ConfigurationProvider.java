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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.Date;

import org.apache.commons.lang.NotImplementedException;


/**
 * ConfigurationProvider provides access to all required interactions with SSC configuration an pricing engine.
 */
public interface ConfigurationProvider
{

	/**
	 * Creates a default configuration for the required knowledge base. The knowledge base (KB) can be identified e.g.
	 * via the product code or via the KB name, version and logical system.
	 *
	 * @param kbKey
	 *           Information needed to identify a knowledge base
	 * @return The configurable product with default configuration
	 */
	ConfigModel createDefaultConfiguration(KBKey kbKey);

	/**
	 * Checks the configuration model for changes since the last update and will send only the changes to the
	 * configuration engine, if any.
	 *
	 * @param model
	 *           Updated model
	 * @return <code>true</code>, only if it was necessary to send an update to the configuration engine
	 * @throws ConfigurationEngineException
	 */
	boolean updateConfiguration(ConfigModel model) throws ConfigurationEngineException;

	/**
	 * Checks the configuration model for changes since the last update and will send only the changes to the
	 * configuration engine, if any. The version of the runtime configuration must be included in the configuration model
	 * and the method returns the new version after the update.
	 *
	 * @param model
	 *           Updated model
	 * @return new configuration runtime version if update successful, otherwise null
	 * @throws ConfigurationEngineException
	 */
	String changeConfiguration(ConfigModel model) throws ConfigurationEngineException;

	/**
	 * Retrieve the current state of the configuration model for the requested <code>configId</code>.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 * @throws ConfigurationEngineException
	 *            Service has failed, e.g. because session timed out
	 */
	ConfigModel retrieveConfigurationModel(String configId) throws ConfigurationEngineException;

	/**
	 * Retrieve the current state of the configuration model for the requested <code>configId</code>.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @param options
	 *           options to consider while reading the configuration, such as additional/updated variant conditions for
	 *           pricing
	 * @return The actual configuration
	 * @throws ConfigurationEngineException
	 *            Service has failed, e.g. because session timed out
	 */
	default ConfigModel retrieveConfigurationModel(final String configId, final ConfigurationRetrievalOptions options)
			throws ConfigurationEngineException
	{
		return retrieveConfigurationModel(configId);
	}

	/**
	 * Retrieve the current state of the configuration for the requested <code>configId</code> as an XML string
	 * containing the configuration in external format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 * @throws ConfigurationEngineException
	 *            Service has failed, e.g. because session timed out
	 */
	String retrieveExternalConfiguration(String configId) throws ConfigurationEngineException;

	/**
	 * Creates a configuration from the configuration in external format which can be provided from outside, e.g. from
	 * the configuration prepared in the back end. <br>
	 * <br>
	 * This API does _not_ force the KB related attributes to be existing. In case only the product ID is provided, a
	 * matching KB version is determined.
	 *
	 * @param extConfig
	 *           External configuration in external format
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(Configuration extConfig);

	/**
	 * Creates a configuration from an XML string containing the configuration in external format. <br>
	 * <br>
	 * This API requires the KB related attributes as part of the external configuration to be available, they are
	 * directly forwarded to the configuration engine.
	 *
	 * @param kbKey
	 *           Information needed to create a knowledge base
	 * @param extConfig
	 *           External configuration as XML string
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(KBKey kbKey, String extConfig);

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
	 *
	 * @param version
	 *           version of the runtime configuration
	 */
	void releaseSession(String configId, String version);

	/**
	 * Get configuration for the base product, initialized with given configuration of the variant
	 *
	 * @param baseProductCode
	 * @param variantProductCode
	 * @return The pre-filled configuration model
	 */
	default ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		throw new NotImplementedException("This function is not supported by this configuration provider");
	}

	/**
	 * @param productCode
	 * @param kbDate
	 * @return <code>true</code> only if a <b>valid</b> KBVersion exists for the given product and date.
	 */
	boolean isKbForDateExists(final String productCode, final Date kbDate);

	/**
	 * @param kbKey
	 * @param externalConfig
	 * @return true if KB exist in required version
	 * @deprecated since 18.08.0 - please call {@link ConfigurationProvider#extractKbKey(String, String)} and
	 *             {@link ConfigurationProvider#isKbVersionExists(KBKey)} instead
	 */
	@Deprecated
	default boolean isKbVersionExists(final KBKey kbKey, final String externalConfig)
	{
		final KBKey extractKbKey = extractKbKey(kbKey.getProductCode(), externalConfig);
		return isKbVersionExists(extractKbKey);
	}

	/**
	 * extracts the KBKey from the external configuration
	 *
	 * @param externalConfig
	 * @param productCode
	 * @return returns the kBKey of the given external config
	 */
	default KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		throw new NotImplementedException(this.getClass());
	}

	/**
	 * @param kbKey
	 * @return returns <code>true</code> only if the KB version identified by the the given KBKey is known by the
	 *         underlying configuration engine.
	 */
	default boolean isKbVersionExists(final KBKey kbKey)
	{
		return true;
	}

	/**
	 * @param kbKey
	 * @return returns <code>true</code> only if the KB version identified by the the given KBKey is known by the
	 *         underlying configuration engine and <b>if it is still valid on the given date</b>.
	 */
	default boolean isKbVersionValid(final KBKey kbKey)
	{
		return true;
	}

	/**
	 * @return Does this provide support the re-configuration of a variant?
	 */
	default boolean isConfigureVariantSupported()
	{
		return true;
	}
}
