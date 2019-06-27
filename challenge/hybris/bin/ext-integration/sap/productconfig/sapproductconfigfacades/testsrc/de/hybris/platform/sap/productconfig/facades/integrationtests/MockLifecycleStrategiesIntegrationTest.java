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
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import javax.annotation.Resource;


@IntegrationTest
public class MockLifecycleStrategiesIntegrationTest extends LifecycleStrategiesIntegrationTestBase
{
	@Resource(name = "sapProductConfigSessionAccessService")
	private SessionAccessService sessionAccess;

	@Override
	protected void makeNewSessionByLoggingOutAndIn(final String userName) throws InvalidCredentialsException
	{
		// "rescue" configuration provider into new session, so we can test with persistent strategies
		final ConfigurationProvider configProvider = providerFactory.getConfigurationProvider();
		logout();
		login(userName, PASSWORD);
		sessionAccess.setConfigurationProvider(configProvider);
	}
}


