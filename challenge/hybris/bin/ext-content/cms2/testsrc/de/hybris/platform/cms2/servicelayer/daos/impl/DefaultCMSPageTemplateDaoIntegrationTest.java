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
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageTemplateDao;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSPageTemplateDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CATALOG_NAME = "MultiCountryTestContentCatalog";
	private static final String CATALOG_VERSION = "StagedVersion";
	private static final String CATALOG_VERSION_EMPTY = "Empty";
	private static final String CONTENT_PAGE_TYPE = "ContentPage";

	@Resource
	private CMSPageTemplateDao cmsPageTemplateDao;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CMSPageTypeModel pageType;
	private CatalogVersionModel catalogVersion;
	private CatalogVersionModel catalogVersionEmpty;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/cmsMultiCountryTestData.csv", "UTF-8");

		final CMSPageTypeModel typeModel = new CMSPageTypeModel();
		typeModel.setCode(CONTENT_PAGE_TYPE);
		pageType = flexibleSearchService.getModelByExample(typeModel);

		catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_NAME, CATALOG_VERSION);
		catalogVersionEmpty = catalogVersionService.getCatalogVersion(CATALOG_NAME, CATALOG_VERSION_EMPTY);
	}

	@Test
	public void shouldFindAllTemplatesForContentPages()
	{
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<>();
		catalogVersions.add(catalogVersion);

		final Collection<PageTemplateModel> templates = cmsPageTemplateDao
				.findAllRestrictedPageTemplatesByCatalogVersion(catalogVersions, true, pageType);

		assertThat(templates, hasSize(4));
	}

	@Test
	public void shouldFindZeroTemplatesForContentPages()
	{
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<>();
		catalogVersions.add(catalogVersionEmpty);

		final Collection<PageTemplateModel> templates = cmsPageTemplateDao
				.findAllRestrictedPageTemplatesByCatalogVersion(catalogVersions, true, pageType);

		assertThat(templates, empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFindZeroTemplatesWithException()
	{
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<>();

		cmsPageTemplateDao.findAllRestrictedPageTemplatesByCatalogVersion(catalogVersions, true, pageType);
	}
}
