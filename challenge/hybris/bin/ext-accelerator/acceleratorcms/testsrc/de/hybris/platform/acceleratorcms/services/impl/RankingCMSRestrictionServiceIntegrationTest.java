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
package de.hybris.platform.acceleratorcms.services.impl;

import static org.hamcrest.Matchers.hasSize;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class RankingCMSRestrictionServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private RankingCMSRestrictionService cmsRestrictionService;

	private ContentPageModel homepageGlobal;
	private ContentPageModel homepageRegion;
	private ContentPageModel homepageLocal;

	@Before
	public void setUp() throws Exception
	{
		final ContentCatalogModel catalogLocal = modelService.create(ContentCatalogModel.class);
		catalogLocal.setId("CatalogLocal");

		final ContentCatalogModel catalogRegion = modelService.create(ContentCatalogModel.class);
		catalogRegion.setId("CatalogRegion");
		catalogRegion.setSubCatalogs(Collections.singleton(catalogLocal));

		final ContentCatalogModel catalogGlobal = modelService.create(ContentCatalogModel.class);
		catalogGlobal.setId("CatalogGlobal");
		catalogGlobal.setSubCatalogs(Collections.singleton(catalogRegion));

		final CatalogVersionModel catalogVersionLocal = modelService.create(CatalogVersionModel.class);
		catalogVersionLocal.setVersion("LocalTestVersion");
		catalogVersionLocal.setCatalog(catalogLocal);

		final CatalogVersionModel catalogVersionRegion = modelService.create(CatalogVersionModel.class);
		catalogVersionRegion.setVersion("RegionTestVersion");
		catalogVersionRegion.setCatalog(catalogRegion);

		final CatalogVersionModel catalogVersionGlobal = modelService.create(CatalogVersionModel.class);
		catalogVersionGlobal.setVersion("GlobalTestVersion");
		catalogVersionGlobal.setCatalog(catalogGlobal);

		final PageTemplateModel masterPageTemplateLocal = modelService.create(PageTemplateModel.class);
		masterPageTemplateLocal.setUid("MasterTemplateLocal");
		masterPageTemplateLocal.setCatalogVersion(catalogVersionLocal);

		final PageTemplateModel regionPageTemplateRegion = modelService.create(PageTemplateModel.class);
		regionPageTemplateRegion.setUid("MasterTemplateRegion");
		regionPageTemplateRegion.setCatalogVersion(catalogVersionRegion);

		final PageTemplateModel masterPageTemplateGlobal = modelService.create(PageTemplateModel.class);
		masterPageTemplateGlobal.setUid("MasterTemplateGlobal");
		masterPageTemplateGlobal.setCatalogVersion(catalogVersionGlobal);

		homepageLocal = modelService.create(ContentPageModel.class);
		homepageLocal.setUid("HomepageLocal");
		homepageLocal.setCatalogVersion(catalogVersionLocal);
		homepageLocal.setMasterTemplate(masterPageTemplateLocal);
		homepageLocal.setDefaultPage(Boolean.valueOf(true));

		homepageRegion = modelService.create(ContentPageModel.class);
		homepageRegion.setUid("HomepageRegion");
		homepageRegion.setCatalogVersion(catalogVersionRegion);
		homepageRegion.setMasterTemplate(regionPageTemplateRegion);
		homepageRegion.setDefaultPage(Boolean.valueOf(true));

		homepageGlobal = modelService.create(ContentPageModel.class);
		homepageGlobal.setUid("HomepageGlobal");
		homepageGlobal.setCatalogVersion(catalogVersionGlobal);
		homepageGlobal.setMasterTemplate(masterPageTemplateGlobal);
		homepageGlobal.setDefaultPage(Boolean.valueOf(true));

		modelService.saveAll(catalogLocal, catalogRegion, catalogGlobal, catalogVersionLocal, catalogVersionRegion,
				catalogVersionGlobal, masterPageTemplateLocal, regionPageTemplateRegion, masterPageTemplateGlobal, homepageLocal,
				homepageRegion, homepageGlobal);
	}

	@Test
	public void shouldFindGlobalPage() throws ImpExException
	{
		final AbstractPageModel[] data = new AbstractPageModel[]
		{ homepageGlobal };
		final Collection<AbstractPageModel> pages = Arrays.asList(data);

		final Collection<AbstractPageModel> evaluatePages = cmsRestrictionService.evaluatePages(pages, null);

		Assert.assertThat(evaluatePages, hasSize(1));
	}

	@Test
	public void shouldFindRegionPage() throws ImpExException
	{
		final AbstractPageModel[] data = new AbstractPageModel[]
		{ homepageRegion };
		final Collection<AbstractPageModel> pages = Arrays.asList(data);

		final Collection<AbstractPageModel> evaluatePages = cmsRestrictionService.evaluatePages(pages, null);

		Assert.assertThat(evaluatePages, hasSize(1));
	}

	@Test
	public void shouldFindLocalPage() throws ImpExException
	{
		final AbstractPageModel[] data = new AbstractPageModel[]
		{ homepageLocal };
		final Collection<AbstractPageModel> pages = Arrays.asList(data);

		final Collection<AbstractPageModel> evaluatePages = cmsRestrictionService.evaluatePages(pages, null);

		Assert.assertThat(evaluatePages, hasSize(1));
	}
}

