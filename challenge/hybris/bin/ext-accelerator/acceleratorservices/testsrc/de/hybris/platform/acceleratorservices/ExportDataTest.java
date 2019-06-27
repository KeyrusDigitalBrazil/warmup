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
package de.hybris.platform.acceleratorservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ExportDataTest extends ServicelayerTest
{

	public static final String GOOGLE_BUSINESS_CODE = "googleBusinessTest";

	@Before
	public void setUp()
	{
		// implement here code executed before each test
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}


	@Test
	public void testExport()
	{
		//ExportDataEvent event = new ExportDataEvent(GOOGLE_BUSINESS_CODE);
		//eventService.publishEvent(event);
	}



}
