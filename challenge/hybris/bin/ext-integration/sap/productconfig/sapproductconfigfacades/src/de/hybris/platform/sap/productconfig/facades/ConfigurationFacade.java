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

import de.hybris.platform.commercefacades.product.data.ProductData;


/**
 * Facade for Product Configuration.
 */
public interface ConfigurationFacade
{

	/**
	 * Get the default configuration for the given Knowledge Base. In case the product identified by the productCode of
	 * the KBkey is a varaint, the runtime configuration of the corresponding base product is instantiated.
	 *
	 * @param kbKey
	 *           key of the Knowledge Base
	 * @return default configuration
	 */
	ConfigurationData getConfiguration(KBKeyData kbKey);

	/**
	 * Get the default configuration for the given Product.
	 *
	 * @param productData
	 *           product code to get the Knowledge Base
	 * @return default configuration
	 * @deprecated since 18.08.0 - call {@link ConfigurationFacade#getConfiguration(KBKeyData)} instead. Check for
	 *             variant is handled internally
	 */
	@Deprecated
	default ConfigurationData getConfiguration(final ProductData productData)
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productData.getCode());
		return getConfiguration(kbKey);
	}

	/**
	 * Update the configuration with the values provided
	 *
	 * @param configuration
	 *           actual configuration
	 */
	void updateConfiguration(ConfigurationData configuration);

	/**
	 * Read the actual configuration from the Backend. Current values in the model will be overwritten.
	 *
	 * @param configuration
	 *           configuration to be refreshed
	 * @return actual configuration
	 */
	ConfigurationData getConfiguration(ConfigurationData configuration);

	/**
	 * Get the number of errors (conflict, not filled mandatory fields), as it is set at the cart item
	 *
	 * @param configId
	 *           ID of the configuration
	 * @return Total number of errors
	 */
	int getNumberOfErrors(String configId);
}
