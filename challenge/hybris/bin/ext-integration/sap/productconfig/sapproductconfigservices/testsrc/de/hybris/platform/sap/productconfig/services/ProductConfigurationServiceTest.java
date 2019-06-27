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
package de.hybris.platform.sap.productconfig.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.Date;

import org.junit.Test;


@UnitTest
public class ProductConfigurationServiceTest
{

	@Test
	public void testReleaseSessionDefault()
	{
		final ProductConfigurationServiceStable defaultService = new ProductConfigurationServiceStable();
		defaultService.releaseSession("123", true);
		assertEquals("123", defaultService.releasedId);
	}

	@Test
	public void testReleaseSessionNotDefault()
	{
		final ProductConfigurationServiceStable defaultService = new ProductConfigurationServiceStable();
		defaultService.releaseSession("123", false);
		assertEquals("123", defaultService.releasedId);
	}

	@Test
	public void testCreateConfigurationFromExternalDefault()
	{
		final ProductConfigurationServiceStable defaultService = new ProductConfigurationServiceStable();
		final KBKeyImpl kbKey = new KBKeyImpl("p123");
		defaultService.createConfigurationFromExternal(kbKey, "extConfig", "123");
		// check that defualt implementation redirects the call
		assertEquals("extConfig", defaultService.externalConfiguration);
		assertSame(kbKey, defaultService.kbKey);

	}

	private static class ProductConfigurationServiceStable implements ProductConfigurationService
	{
		private String externalConfiguration;
		private KBKey kbKey;
		private String releasedId;

		@Override
		public ConfigModel createDefaultConfiguration(final KBKey kbKey)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
		{
			return null;
		}

		@Override
		public void updateConfiguration(final ConfigModel model)
		{
			// empty
		}

		@Override
		public ConfigModel retrieveConfigurationModel(final String configId)
		{
			return null;
		}

		@Override
		public String retrieveExternalConfiguration(final String configId)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
		{
			this.kbKey = kbKey;
			this.externalConfiguration = externalConfiguration;
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
		{
			return null;
		}

		@Override
		public void releaseSession(final String configId)
		{
			this.releasedId = configId;
		}

		@Override
		public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
		{
			return 0;
		}

		@Override
		public boolean hasKbForDate(final String productCode, final Date kbDate)
		{
			return false;
		}

		@Override
		public boolean hasKbForVersion(final KBKey kbKey, final String externalConfig)
		{
			return false;
		}

		@Override
		public int getTotalNumberOfIssues(final ConfigModel configModel)
		{
			return 0;
		}

		@Override
		public boolean isKbVersionValid(final KBKey kbKey)
		{
			return false;
		}

		@Override
		public KBKey extractKbKey(final String productCode, final String externalConfig)
		{
			return null;
		}
	}

}
