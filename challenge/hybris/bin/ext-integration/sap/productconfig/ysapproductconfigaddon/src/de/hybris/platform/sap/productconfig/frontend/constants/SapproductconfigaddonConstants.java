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
package de.hybris.platform.sap.productconfig.frontend.constants;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;


/**
 * Global class for all ysapproductconfigaddon constants.
 */
public class SapproductconfigaddonConstants extends GeneratedSapproductconfigaddonConstants
{
	/**
	 * name of the ysapproductconfigaddon extension
	 */
	public static final String EXTENSIONNAME = "ysapproductconfigaddon";
	/**
	 * If this method is available at the OrderEntryDTO, we assume that the UI is prepared to render the configuration
	 * link
	 *
	 * @see OrderEntryData
	 */
	public static final String CONFIGURABLE_SOM_DTO_METHOD = "isConfigurable";

	/**
	 * view attribute name for the {@link ConfigurationData} DTO
	 */
	public static final String CONFIG_ATTRIBUTE = "config";

	private SapproductconfigaddonConstants()
	{
		//empty
	}
}
