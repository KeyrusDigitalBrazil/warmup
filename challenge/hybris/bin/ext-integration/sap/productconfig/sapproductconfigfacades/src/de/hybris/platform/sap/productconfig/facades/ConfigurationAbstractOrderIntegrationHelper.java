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

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;


/**
 * Utility class for configuration integration with hybris entities based on the abstract order entry, such as
 * quotation, order or saved cart.
 */
public interface ConfigurationAbstractOrderIntegrationHelper
{

	/**
	 * Retrieves the configuration overview data for an AbstractOrderEntry identified by code and entry number.
	 *
	 * @param orderModel
	 *           AbstractOrderModel which contains the AbstractOrderEntry
	 * @param entryNumber
	 *           denotes the number of the entry contained in the AbstractOrder entity
	 *
	 * @return ConfigurationOverviewData object
	 */
	ConfigurationOverviewData retrieveConfigurationOverviewData(final AbstractOrderModel orderModel, final int entryNumber);

	/**
	 * For configurable products it is checked the the knowledge base version the attached runtime configuration is still
	 * known by the configuration engine.
	 *
	 * @param orderModel
	 * @return <code>true</code>, only if all order items can be diretly re-ordered.
	 */
	boolean isReorderable(final AbstractOrderModel orderModel);
}
