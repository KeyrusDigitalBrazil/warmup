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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


@UnitTest
public class ConfigurationFacadeTest
{
	private static final String CONFIG_ID = "123";
	private final ConfigurationFacade classUnderTest = new ConfigurationFacadeStable();

	@Test
	public void testGetConfigurationDefault()
	{
		final ProductData product = new ProductData();
		final ConfigurationData config = classUnderTest.getConfiguration(product);

		assertNotNull(config);
		assertEquals(CONFIG_ID, config.getConfigId());
	}


	private static final class ConfigurationFacadeStable implements ConfigurationFacade
	{
		@Override
		public void updateConfiguration(final ConfigurationData configuration)
		{
			throw new NotImplementedException();
		}

		@Override
		public int getNumberOfErrors(final String configId)
		{
			throw new NotImplementedException();
		}

		@Override
		public ConfigurationData getConfiguration(final ConfigurationData configuration)
		{
			throw new NotImplementedException();
		}

		@Override
		public ConfigurationData getConfiguration(final KBKeyData kbKey)
		{
			final ConfigurationData testConfig = new ConfigurationData();
			testConfig.setConfigId(CONFIG_ID);

			return testConfig;
		}
	}
}
