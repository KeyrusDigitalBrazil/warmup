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

import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;


/**
 * Facade for integration of order objects with ProductConfiguration
 */
public interface ConfigurationOrderIntegrationFacade
{

	/**
	 * Retrieves ConfigurationOverviewData object for order entry identified by code and entry number.
	 *
	 * @param code
	 *           code of the order object
	 * @param entryNumber
	 *           entry number
	 * @return ConfigurationOverviewData object
	 */
	ConfigurationOverviewData getConfiguration(String code, int entryNumber);

	/**
	 * Checks for each item of the order, whether it can be re-ordered or not. Only if all items are re-orderable aso the
	 * whole order is considered re-orderable.<br>
	 * In case an item is configurable, it is checked whether the Knowledgebase (KB) version that was used to created the
	 * items current configuration state, as persistet in the external configuration, is still know to the configuration
	 * engine, so it can be guranteed that the exact same configuration can be recreated on re-order.
	 *
	 * @param orderCode
	 *           <code>code</code> of the source order
	 *
	 * @return <code>true</code>, only if the whole order can be re-ordered including all order items
	 */
	boolean isReorderable(final String orderCode);
}
