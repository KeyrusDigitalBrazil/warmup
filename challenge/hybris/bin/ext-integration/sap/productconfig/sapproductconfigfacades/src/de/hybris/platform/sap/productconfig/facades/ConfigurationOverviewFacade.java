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
 * Facade for Configuration Overview.
 */
public interface ConfigurationOverviewFacade
{
	/**
	 * Determine DTO which represents the configuration overview for a configuration available in the session
	 *
	 * @param configId
	 *           Configuration ID
	 * @param overview
	 *           DTO representing overview in case it has been once determined previously. Holds applied filters. In case
	 *           not determined so far, called as null
	 * @return DTO representing configuration overview
	 */
	ConfigurationOverviewData getOverviewForConfiguration(String configId, ConfigurationOverviewData overview);

	/**
	 * Determine DTO which represents the variant overview for a product variant
	 *
	 * @param productCode
	 *           Product Code
	 * @param overview
	 *           DTO representing overview in case it has been once determined previously. Holds applied filters. In case
	 *           not determined so far, called as null
	 * @return DTO representing configuration overview
	 */
	ConfigurationOverviewData getOverviewForProductVariant(String productCode, ConfigurationOverviewData overview);

}
