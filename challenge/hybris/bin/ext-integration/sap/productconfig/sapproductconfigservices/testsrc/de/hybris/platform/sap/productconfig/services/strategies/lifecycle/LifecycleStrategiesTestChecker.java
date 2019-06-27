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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle;

/**
 *
 */
public interface LifecycleStrategiesTestChecker
{

	void checkLinkToProduct(final String productCode, final String configId);

	void checkBasicData(final String userName, final String configId);

	void checkLinkToCart(final String configId, final String cartItemHandle, final boolean isDraft);

	void checkConfigDeleted(final String configId, final String cartItemKey);

	void checkNumberOfConfigsPersisted(final int numExpected);

	default void checkNumberOfConfigsPersisted(final String message, final int numExpected)
	{
		checkNumberOfConfigsPersisted(numExpected);
	}

	default void checkProductConfiguration(final String configId, final String userName)
	{
		checkProductConfiguration(configId, userName, null, null, false);
	}

	default void checkProductConfiguration(final String configId, final String userName, final String productCode)
	{
		checkProductConfiguration(configId, userName, productCode, null, false);
	}

	default void checkProductConfiguration(final String configId, final String userName, final String productCode,
			final String cartItemHandle, final boolean isDraft)
	{
		checkBasicData(userName, configId);
		checkLinkToProduct(productCode, configId);
		checkLinkToCart(configId, cartItemHandle, isDraft);
	}

}
