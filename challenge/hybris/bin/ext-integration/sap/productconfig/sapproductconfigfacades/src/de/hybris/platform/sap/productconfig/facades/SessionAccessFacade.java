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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;


/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
public interface SessionAccessFacade
{
	/**
	 * Stores configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#setConfigIdForCartEntry(String, String)} instead
	 */
	@Deprecated
	void setConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Retrieves configuration identifier from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ID of a runtime configuration object
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getConfigIdForCartEntry(String)} instead
	 */
	@Deprecated
	String getConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return T which represents the UiStatus
	 */
	<T> T getUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Stores object for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param uiStatus
	 *           the status of the UI
	 */
	void setUiStatusForCartEntry(String cartEntryKey, Object uiStatus);

	/**
	 * Stores object for a product key into the session
	 *
	 * @param productKey
	 *           Product key
	 * @param uiStatus
	 *           the status of the UI
	 */
	void setUiStatusForProduct(String productKey, Object uiStatus);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param productKey
	 *           Product key
	 * @return T which represents the UiStatus
	 */
	<T> T getUiStatusForProduct(String productKey);

	/**
	 * Removes object for a cart entry
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 */
	void removeUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Removes object for a product
	 *
	 * @param productKey
	 *           Product key
	 */
	void removeUiStatusForProduct(String productKey);

	/**
	 * Retrieves cart entry key belonging to a specific config ID
	 *
	 * @param configId
	 *           ID of the configuration
	 * @return String representation of the cart entry primary key
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getCartEntryForConfigId(String)} instead
	 */
	@Deprecated
	String getCartEntryForConfigId(String configId);

	/**
	 * Stores cart entry in session per product key
	 *
	 * @param productKey
	 *           product key
	 * @param cartEntryId
	 *           String representation of the cart entry primary key
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#setConfigIdForCartEntry(String, String)} and
	 *             {@link ConfigurationProductLinkStrategy#setConfigIdForProduct(String, String)} instead
	 */
	@Deprecated
	void setCartEntryForProduct(String productKey, String cartEntryId);

	/**
	 * Retrieves cart entry key per product
	 *
	 * @param productKey
	 *           product key
	 * @return String representation of the cart entry primary key
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getConfigIdForCartEntry(String)} and
	 *             {@link ConfigurationProductLinkStrategy#getConfigIdForProduct(String)} instead
	 */
	@Deprecated
	String getCartEntryForProduct(String productKey);

	/**
	 * Removes cart entry key for product
	 *
	 * @param productKey
	 *           product key
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#removeConfigIdForCartEntry(String)} and
	 *             {@link ConfigurationProductLinkStrategy#removeConfigIdForProduct(String)} instead
	 */
	@Deprecated
	void removeCartEntryForProduct(String productKey);

	/**
	 * Removes configuration ID for cart entry
	 *
	 * @param cartEntryKey
	 *           key of the cart entry
	 * @deprecated since 18.08.0 - call
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#removeConfigIdForCartEntry(String)} instead
	 */
	@Deprecated
	void removeConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Retrieves current session id
	 *
	 * @return session id of the current session
	 *
	 * @deprecated since 18.11.0 - call {@link SessionService#getCurrentSession()#getSessionId()} instead
	 */
	@Deprecated
	String getSessionId();

	/**
	 * Retrieves configModel from the session cache for a given configId
	 *
	 * @param configId
	 *           ID of the configuration
	 * @return configuration model
	 * @deprecated since 18.08.0 - call {@link ConfigurationModelCacheStrategy#getConfigurationModelEngineState(String)}
	 *             instead
	 */
	@Deprecated
	ConfigModel getConfigurationModelEngineState(String configId);

}
