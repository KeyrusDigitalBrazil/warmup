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
package de.hybris.platform.marketplaceservices.vendor.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.marketplaceservices.strategies.impl.DefaultVendorOriginalEntryGroupDisplayStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultVendorOriginalEntryGroupDisplayStrategyTest
{
	private DefaultVendorOriginalEntryGroupDisplayStrategy vendorOriginalEntryGroupDisplayStrategy;
	private static final String TEST_DISPLAY_CONFIG = "test.should.display.original.entrygroup";
	private static final boolean TEST_DISPLAY_ORIGENTRYGROUP = false;
	@Mock
	private ConfigurationService configurationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		vendorOriginalEntryGroupDisplayStrategy = new DefaultVendorOriginalEntryGroupDisplayStrategy();
		vendorOriginalEntryGroupDisplayStrategy.setConfigurationService(configurationService);
	}

	@Test
	public void testGetMobilePhoneNumber()
	{
		final Configuration configuration = mock(Configuration.class);

		given(configuration.getBoolean(TEST_DISPLAY_CONFIG, false)).willReturn(TEST_DISPLAY_ORIGENTRYGROUP);
		given(configurationService.getConfiguration()).willReturn(configuration);

		assertEquals(TEST_DISPLAY_ORIGENTRYGROUP, vendorOriginalEntryGroupDisplayStrategy.shouldDisplayOriginalEntryGroup());
	}
}
