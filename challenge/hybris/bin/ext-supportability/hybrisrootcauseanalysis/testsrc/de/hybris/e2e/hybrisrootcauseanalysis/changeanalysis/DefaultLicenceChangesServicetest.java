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
package de.hybris.e2e.hybrisrootcauseanalysis.changeanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.hybris.e2e.hybrisrootcauseanalysis.changeanalysis.services.E2EChangesPropertiesService;
import de.hybris.e2e.hybrisrootcauseanalysis.changeanalysis.services.impl.DefaultLicenceChangesService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class DefaultLicenceChangesServicetest extends HybrisJUnit4TransactionalTest
{

	private E2EChangesPropertiesService licenceService;

	private static final String NAME = "licence.properties";

	@Before
	public void setUp()
	{
		licenceService = Registry.getApplicationContext().getBean("defaultLicenceChanges", DefaultLicenceChangesService.class);
	}



	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testSaveLicenceInProperties()
	{
		//test
		final Properties info = licenceService.getInfo();
		assertEquals("check the name file", info.getProperty("licence.email"), "support@hybris.com");
		assertEquals("check the name file", info.getProperty("licence.purpose"), null);
	}
}
