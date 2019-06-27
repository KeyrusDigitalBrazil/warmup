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
/**
 *
 */
package de.hybris.platform.personalizationservices.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.enums.CxCatalogLookupType;
import de.hybris.platform.personalizationservices.service.impl.DefaultCxCatalogService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxCatalogServiceIntegrationTest extends AbstractCxServiceTest
{
	private static final String CATALOG_NAME1 = "catalog1";
	private static final String CATALOG_NAME2 = "catalog2";

	@Resource(name = "cxCatalogService")
	DefaultCxCatalogService defaultCxCatalogService;

	@Resource
	ConfigurationService configurationService;

	@Resource
	CatalogVersionService catalogVersionService;

	@Resource
	ModelService modelService;


	@Before
	public void setup()
	{
		final CatalogVersionModel cv = createCatalogVersion(CATALOG_NAME1, "1");
		final CatalogVersionModel cv2 = createCatalogVersion(CATALOG_NAME2, "1");
		final Collection<CatalogVersionModel> sessionCatalogVersions = catalogVersionService.getSessionCatalogVersions();

		final List<CatalogVersionModel> newCatalogs = new ArrayList<>(sessionCatalogVersions);
		newCatalogs.add(cv);
		newCatalogs.add(cv2);
		catalogVersionService.setSessionCatalogVersions(newCatalogs);
	}

	private CatalogVersionModel createCatalogVersion(final String name, final String version)
	{
		final CatalogModel catalog = new CatalogModel();
		catalog.setId(name);

		final CatalogVersionModel cv = new CatalogVersionModel();
		cv.setVersion(version);
		cv.setCatalog(catalog);

		modelService.save(cv);

		return cv;
	}

	@Test
	public void testAllCatalogs()
	{
		//given
		configurationService.getConfiguration().setProperty(PersonalizationservicesConstants.CATALOG_LOOKUP_TYPE,
				CxCatalogLookupType.ALL_CATALOGS.getCode());

		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = defaultCxCatalogService.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		Assert.assertEquals(1, catalogVersionsForCalculation.size());
		Assert.assertEquals("testCatalog", catalogVersionsForCalculation.get(0).getCatalog().getId());
	}
}
