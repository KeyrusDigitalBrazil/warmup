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
package de.hybris.platform.personalizationpromotions.service;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxServicePromotionIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String USER_ID = "customer1@hybris.com";

	@Resource
	private CxService cxService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationpromotions/test/testdata_personalizationpromotions.impex", "UTF-8"));
	}


	@Test
	public void testActionProcessing()
	{
		final UserModel user = userService.getUserForUID(USER_ID);

		//when
		cxService.calculateAndStorePersonalization(user, catalogVersionService.getCatalogVersion("testCatalog", "Online"));

		//then
		// We expect no exception is thrown.
		// Actions will get recalculated, but there is nothing returned.
		// So we have no easy way to verify things other than no exception happened here.
		// Actual effect of recalculation can be verified in tests on higher level.
	}


}
