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
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Retrieves providers and allows for switching between different provider factories
 */
public interface SwitchableProviderFactory extends ProviderFactory
{
	/**
	 * Activates the specified provider factory. After calling this method all provider requests will be processed by the
	 * specified factory.
	 *
	 * @param providerFactoryBeanName
	 *           provider factory to activate
	 */
	void switchProviderFactory(final String providerFactoryBeanName);

	/**
	 * @param providerFactoryBeanName
	 *           bean name to check
	 * @return <code>true</code>, only if the given provider factory is available
	 */
	boolean isProviderFactoryAvailable(String providerFactoryBeanName);
}
