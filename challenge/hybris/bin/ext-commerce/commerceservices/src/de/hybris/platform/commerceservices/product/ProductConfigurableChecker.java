/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.product;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * An interface for checking whether or not specific product is configurable.
 */
public interface ProductConfigurableChecker
{

	/**
	 * Checks if specified product is an instance of configurable product (i.e. has configuration settings assigned).
	 *
	 * @param product
	 *           product instance to check
	 * @return true if specified product is configurable, false otherwise
	 */
	boolean isProductConfigurable(final ProductModel product);

	/**
	 * Returns first configurator type attached to the product
	 * @param product Product model
	 * @return Configurator type. Null in case no configurator is assigned
	 */
	String getFirstConfiguratorType(final ProductModel product);
}
