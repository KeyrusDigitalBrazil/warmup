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
 * This strategy manages the link between a given product (code) and the corresponding runtime configuration. In case a
 * runtime configuration was started, but not added to the cart, yet.
 */
public interface ConfigurationProductLinkStrategy
{
	/**
	 * Retrieves the associated configId for a given product code for the current user session
	 *
	 * @param productCode
	 *           product code
	 * @return configuration id
	 */
	String getConfigIdForProduct(final String productCode);

	/**
	 * Persists the link between a product and a configuration id for the current user session
	 *
	 * @param productCode
	 *           product code
	 * @param configId
	 *           configuration id
	 */
	void setConfigIdForProduct(final String productCode, String configId);

	/**
	 * Removes the link between product code and runtime configuration
	 *
	 * @param productCode
	 */
	void removeConfigIdForProduct(String productCode);

	/**
	 * Retrieves product code for given configuration id from persistence
	 *
	 * @param configId
	 *           configuration id
	 * @return product code
	 */
	String retrieveProductCode(final String configId);
}
