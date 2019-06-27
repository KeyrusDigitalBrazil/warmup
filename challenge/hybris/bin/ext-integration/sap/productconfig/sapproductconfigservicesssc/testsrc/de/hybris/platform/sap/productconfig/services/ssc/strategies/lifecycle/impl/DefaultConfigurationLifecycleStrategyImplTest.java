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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultConfigurationLifecycleStrategyImplTest
{
	private static final String CONFIG_ID = "config id";
	private DefaultConfigurationLifecycleStrategyImpl classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultConfigurationLifecycleStrategyImpl();
	}

	@Test
	public void testIsConfigInSession()
	{
		assertTrue(classUnderTest.isConfigForCurrentUser(CONFIG_ID));
	}

	@Test
	public void testIsConfigKnown()
	{
		assertTrue(classUnderTest.isConfigKnown(CONFIG_ID));
	}
}
