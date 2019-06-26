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
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CPSResponseAttributeStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;


/**
 * Not needed when skywalker service runs on yaas
 */
public interface CharonFacade
{

	/**
	 * Create default configuration and handle cookies
	 *
	 * @param kbKey
	 *           key of the knowledgebase
	 * @return runtime configuration
	 */
	CPSConfiguration createDefaultConfiguration(KBKey kbKey);

	/**
	 * Retrieves the external representation for a given runtime configuration
	 *
	 * @param configId
	 *           id of the runtime configuration
	 *
	 * @return external representation of the runtime configuration
	 * @throws ConfigurationEngineException
	 *            Service has failed, e.g. because session timed out
	 */
	String getExternalConfiguration(String configId) throws ConfigurationEngineException;

	/**
	 * Creates a new runtime configuration from the external representation of the configuration
	 *
	 * @param externalConfiguration
	 *           external representation of the configuration
	 * @return runtime configuration based on the external representation
	 * @deprecated since 18.11.0 - use {@link #createConfigurationFromExternal(String, String)}
	 */
	@Deprecated
	CPSConfiguration createConfigurationFromExternal(String externalConfiguration);



	/**
	 * Deletes the session for the specified runtime configuration on the client
	 *
	 * @param configId
	 *           id of the runtime configuration to be deleted
	 * @param version
	 *           version of the runtime configuration to be deleted
	 */
	void releaseSession(String configId, String version);



	/**
	 * Creates a new runtime configuration from the external typed representation of the configuration
	 *
	 * @param externalConfigStructured
	 *           The structured external configuration
	 * @return Runtime representation
	 * @deprecated since 18.11.0 - use {@link #createConfigurationFromExternal(CPSExternalConfiguration, String)}
	 */
	@Deprecated
	CPSConfiguration createConfigurationFromExternal(CPSExternalConfiguration externalConfigStructured);

	/**
	 * Updates configuration, sends cookies along with request. The cookies are handled internally and received from the
	 * {@link CPSResponseAttributeStrategy}
	 *
	 * @param configuration
	 *           runtime configuration that includes only the updates
	 * @return version of the updated runtime configuration
	 * @throws ConfigurationEngineException
	 *            when service call fails
	 */
	String updateConfiguration(CPSConfiguration configuration) throws ConfigurationEngineException;

	/**
	 * Gets configuration, sends cookies along with request. The cookies are handled internally and received from the
	 * {@link CPSResponseAttributeStrategy}
	 *
	 * @param configId
	 *           configuration id
	 * @return current state of the runtime configuration
	 * @throws ConfigurationEngineException
	 *            when service call fails
	 */
	CPSConfiguration getConfiguration(String configId) throws ConfigurationEngineException;


	/**
	 * Creates a new runtime configuration from the external representation of the configuration
	 *
	 * @param externalConfiguration
	 *           external representation of the configuration
	 * @param contextProduct
	 *           context product
	 * @return runtime configuration based on the external representation
	 */
	CPSConfiguration createConfigurationFromExternal(String externalConfiguration, String contextProduct);


	/**
	 * Creates a new runtime configuration from the external typed representation of the configuration
	 *
	 * @param externalConfigStructured
	 *           structured external configuration
	 * @param contextProduct
	 *           context product
	 * @return Runtime representation
	 */
	CPSConfiguration createConfigurationFromExternal(CPSExternalConfiguration externalConfigStructured, String contextProduct);

}
