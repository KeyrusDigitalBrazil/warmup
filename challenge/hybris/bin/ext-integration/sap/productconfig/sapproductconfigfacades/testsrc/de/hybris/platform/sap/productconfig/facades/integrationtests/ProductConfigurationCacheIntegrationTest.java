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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.services.impl.PricingServiceImpl;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class ProductConfigurationCacheIntegrationTest extends CPQFacadeLayerTest
{

	private static final Logger LOG = Logger.getLogger(ProductConfigurationCacheIntegrationTest.class);

	@Resource(name = "sapProductConfigPricingService")
	protected PricingServiceImpl pricingService;

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
		ensureMockProvider();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}

	/**
	 * Tests running with Currency: isoCode=EUR; sapCode=EUR
	 */
	@Test
	public void testCacheBehaviourAfterLoginAndLogout() throws InvalidCredentialsException
	{
		ConfigurationData configAnonymus = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		final boolean isPricingActive = pricingService.isActive();
		if (isPricingActive)
		{
			pricingService.getPriceSummary(configAnonymus.getConfigId());
		}
		checkedCachedConfigData(configAnonymus, isPricingActive);

		login(USER_NAME, PASSWORD);
		checkCachedConfigDataEmpty(configAnonymus);

		configAnonymus = cpqFacade.getConfiguration(configAnonymus);
		if (isPricingActive)
		{
			pricingService.getPriceSummary(configAnonymus.getConfigId());
		}
		checkedCachedConfigData(configAnonymus, isPricingActive);

		logout();
		checkCachedConfigDataEmpty(configAnonymus);
	}

	protected void checkCachedConfigDataEmpty(final ConfigurationData configAnonymus)
	{
		final ConfigModel cashedConfiguration = productConfigurationCacheAccessService
				.getConfigurationModelEngineState(configAnonymus.getConfigId());
		assertNull(cashedConfiguration);
		final PriceSummaryModel cashedPriceSummary = productConfigurationCacheAccessService
				.getPriceSummaryState(configAnonymus.getConfigId());
		assertNull(cashedPriceSummary);
	}

	protected void checkedCachedConfigData(final ConfigurationData configAnonymus, final boolean isPricingActive)
	{
		final ConfigModel cashedConfiguration = productConfigurationCacheAccessService
				.getConfigurationModelEngineState(configAnonymus.getConfigId());
		assertNotNull(cashedConfiguration);

		if (isPricingActive)
		{
			final PriceSummaryModel cashedPriceSummary = productConfigurationCacheAccessService
					.getPriceSummaryState(configAnonymus.getConfigId());
			assertNotNull(cashedPriceSummary);
		}
		else
		{
			LOG.warn("Test runs with pricing deactivated, can't check cache behaviour for prricing data! pricingProvider="
					+ providerFactory.getPricingProvider().getClass());
		}
	}
}
