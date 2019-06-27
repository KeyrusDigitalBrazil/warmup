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
package de.hybris.platform.webservicescommons.util;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.testframework.Assert;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

//import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class LocalViewExecutorIntegrationTest extends ServicelayerTest
{
	@Resource
	LocalViewExecutor localViewExecutor;

	@Resource
	CatalogVersionService catalogVersionService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		createHardwareCatalog();
	}

	@Test
	public void testExecuteInLocalView()
	{
		//given
		catalogVersionService.setSessionCatalogVersions(Collections.emptyList());
		Assert.assertCollection(Collections.emptyList(), catalogVersionService.getSessionCatalogVersions());

		//when
		localViewExecutor.executeInLocalView(() -> {
			final Collection<CatalogVersionModel> allCatalogVersions = catalogVersionService.getAllCatalogVersions();
			catalogVersionService.setSessionCatalogVersions(allCatalogVersions);

			Assert.assertCollection(allCatalogVersions, catalogVersionService.getSessionCatalogVersions());

			return null;
		});

		//then
		Assert.assertCollection(Collections.emptyList(), catalogVersionService.getSessionCatalogVersions());
	}

	@Test
	public void testExecuteWithCatalogs()
	{
		//given
		catalogVersionService.setSessionCatalogVersions(Collections.emptyList());
		Assert.assertCollection(Collections.emptyList(), catalogVersionService.getSessionCatalogVersions());
		//when
		localViewExecutor.executeWithAllCatalogs(() -> {

			final Collection<CatalogVersionModel> allCatalogVersions = catalogVersionService.getAllCatalogVersions();
			Assert.assertCollection(allCatalogVersions, catalogVersionService.getSessionCatalogVersions());

			return null;
		});

		//then
		Assert.assertCollection(Collections.emptyList(), catalogVersionService.getSessionCatalogVersions());
	}
}
