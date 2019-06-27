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
import de.hybris.e2e.hybrisrootcauseanalysis.changeanalysis.services.impl.DefaultSolrChangesService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class DefaultSolrChangesServiceTest extends HybrisJUnit4TransactionalTest
{

	private E2EChangesPropertiesService solrService;

	private static final String NAME = "solr.properties";

	@Before
	public void setUp()
	{
		solrService = Registry.getApplicationContext().getBean("defaultSolrChanges", DefaultSolrChangesService.class);
	}



	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}

	@Test
	public void testGetNameFile()
	{
		//test
		assertEquals("check the name file", solrService.getNameFile(), NAME);
		assertNotEquals("check name file is different from original", solrService.getNameFile(), "licensee.properties");
	}

}
