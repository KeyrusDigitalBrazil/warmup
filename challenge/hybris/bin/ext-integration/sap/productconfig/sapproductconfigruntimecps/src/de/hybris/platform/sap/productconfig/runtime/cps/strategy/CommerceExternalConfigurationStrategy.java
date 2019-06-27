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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;


/**
 * Defines the transition from {@link CPSExternalConfiguration} and {@link CPSCommerceExternalConfiguration} and vice
 * versa. We need to enrich the external format provided by the engine with additional commerce relevant information
 * like the SAP unit codes
 */
public interface CommerceExternalConfigurationStrategy
{

	/**
	 * Extracts the engine format from the commerce representation of the external configuration
	 *
	 * @param commerceExternalConfiguration
	 * @return External configuration in CPS engine format
	 */
	CPSExternalConfiguration extractCPSFormatFromCommerceRepresentation(
			CPSCommerceExternalConfiguration commerceExternalConfiguration);

	/**
	 * Creates a commerce format of the external configuration from the CPS engine format
	 * 
	 * @param externalConfiguration
	 * @return External configuration in commerce format
	 */
	CPSCommerceExternalConfiguration createCommerceFormatFromCPSRepresentation(CPSExternalConfiguration externalConfiguration);

}
