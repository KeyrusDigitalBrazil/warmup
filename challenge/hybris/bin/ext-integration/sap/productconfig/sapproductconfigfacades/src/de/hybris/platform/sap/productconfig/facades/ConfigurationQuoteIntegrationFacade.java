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
 * Facade for integration of quote objects with ProductConfiguration
 */
@FunctionalInterface
public interface ConfigurationQuoteIntegrationFacade
{

	/**
	 * Retrieves ConfigurationOverviewData object for quote entry identified by code and entry number.
	 *
	 * @param code
	 *           code of the quote object
	 * @param entryNumber
	 *           entry number
	 * @return ConfigurationOverviewData object
	 */
	ConfigurationOverviewData getConfiguration(String code, int entryNumber);
}
