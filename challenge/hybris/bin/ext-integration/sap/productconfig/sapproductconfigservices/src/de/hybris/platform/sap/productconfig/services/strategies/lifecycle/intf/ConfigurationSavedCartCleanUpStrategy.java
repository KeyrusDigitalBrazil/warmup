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
 * Clean strategy for configurable product, to eliminate the product link to product configuration before saving cart.
 */
public interface ConfigurationSavedCartCleanUpStrategy
{
	/**
	 * Cleans up the cart with regards to its product configuration relevant aspects
	 */
	void cleanUpCart();


}
