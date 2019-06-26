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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

/**
 * This strategy manages the link between a given cart entry and the corresponding runtime configuration.
 */
public interface ConfigurationAbstractOrderEntryLinkStrategy
{
	/**
	 * Stores configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Stores the draft configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setDraftConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Retrieves config identifier from the session for a given cart entry key. In case a draft configuration exists,
	 * this is returned with priority.
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ID of a runtime configuration object
	 */
	String getConfigIdForCartEntry(String cartEntryKey);


	/**
	 * Retrieves the drafted config identifier from the session for a given cart entry key. In case a draft configuration
	 * exists, this is returned with priority.
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ID of a runtime configuration object
	 */
	String getDraftConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Retrieves cart entry key belonging to a specific config ID
	 *
	 * @param configId
	 *           id of the configuration
	 * @return String representation of the cart entry primary key
	 */
	String getCartEntryForConfigId(String configId);

	/**
	 * Retrieves cart entry key belonging to a specific darfted config ID
	 *
	 * @param configId
	 *           id of the configuration maintained as draft
	 * @return String representation of the cart entry primary key
	 */
	String getCartEntryForDraftConfigId(String configId);

	/**
	 * Removes config ID for cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 */
	void removeConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Removes the drafted config ID for a cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 */
	void removeDraftConfigIdForCartEntry(String cartEntryKey);


	/**
	 * Removes all session artifacts belonging to a cart entry
	 *
	 * @param cartEntryId
	 *           cart entry key
	 * @param productKey
	 *           product key
	 */
	void removeSessionArtifactsForCartEntry(String cartEntryId);

	/**
	 * Checks whether the given configuration is realted to any document
	 *
	 * @param configId
	 *           configId
	 * @return true if the config is related to any document
	 */
	boolean isDocumentRelated(final String configId);
}

