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

import java.util.List;



/**
 * Facade for integrating Configuration Variants into CPQ
 */
public interface ConfigurationVariantFacade
{

	/**
	 * Searches variants that are similar to the currently configured product identified by the given config id, and
	 * decorates the result with some additional data, such as price and image data.
	 *
	 * @param configId
	 *           configuration id of current configuration session
	 * @param productCode
	 *           product code of the currently configured product
	 * @return A List of variants that are similar to current configured product, along with some additional data.
	 */
	List<ConfigurationVariantData> searchForSimilarVariants(String configId, String productCode);
}
