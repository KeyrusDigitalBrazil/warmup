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
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import de.hybris.platform.catalog.daos.CatalogDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ContentSlotForPageInitDefaultsInterceptorTest extends ServicelayerTest // NOPMD: Junit4 allows any name for test method
{
	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CatalogDao catalogDao;
	private CatalogVersionModel secondCtgVer;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		secondCtgVer = createCatalogVersion();
	}

	@Test
	public void shouldCreateContentSlotForPageWithCatalogVersionTakenFromPageIfPageExists()
	{
		// given
		final ContentSlotForPageModel contentSlotForPage = modelService.create(ContentSlotForPageModel.class);
		contentSlotForPage.setPage(getPage());
		contentSlotForPage.setPosition("FooBar1");
		contentSlotForPage.setContentSlot(getContentSlot());
		modelService.save(contentSlotForPage);

		// when
		final CatalogVersionModel catalogVersion = contentSlotForPage.getCatalogVersion();

		// then
		assertThat(catalogVersion).isNotNull();
		assertThat(catalogVersion).isEqualTo(contentSlotForPage.getPage().getCatalogVersion());
	}

	@Test
	public void shouldCreateContentSlotForPageWithoutGetingCatalogVersionFromPageIfCatalogVersionIsGivenExplicite()
	{
		// given
		final ContentSlotForPageModel contentSlotForPage = modelService.create(ContentSlotForPageModel.class);
		contentSlotForPage.setPage(getPage());
		contentSlotForPage.setPosition("FooBar1");
		contentSlotForPage.setContentSlot(getContentSlot());
		contentSlotForPage.setCatalogVersion(secondCtgVer);
		modelService.save(contentSlotForPage);

		// when
		final CatalogVersionModel catalogVersion = contentSlotForPage.getCatalogVersion();

		// then
		assertThat(catalogVersion).isNotNull();
		assertThat(catalogVersion).isNotEqualTo(contentSlotForPage.getPage().getCatalogVersion());
		assertThat(catalogVersion).isEqualTo(secondCtgVer);
	}

	protected ProductPageModel getPage()
	{
		final ProductPageModel page = modelService.create(ProductPageModel.class);
		page.setCatalogVersion(getCatalogVersion());
		page.setUid("FooBar2");
		page.setMasterTemplate(getPageTemplate());
		return page;
	}

	protected ContentSlotModel getContentSlot()
	{
		final ContentSlotModel contentSlot = modelService.create(ContentSlotModel.class);
		contentSlot.setUid("FooBar3");
		return contentSlot;
	}

	protected CatalogVersionModel getCatalogVersion()
	{
		return getCatalog().getActiveCatalogVersion();
	}

	protected CatalogModel getCatalog()
	{
		final CatalogModel example = new CatalogModel();
		example.setId("testCatalog");
		return flexibleSearchService.getModelByExample(example);
	}

	protected PageTemplateModel getPageTemplate()
	{
		final PageTemplateModel pageTemplate = modelService.create(PageTemplateModel.class);
		pageTemplate.setUid("FooBar4");
		return pageTemplate;
	}

	protected CatalogVersionModel createCatalogVersion()
	{
		final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setActive(Boolean.TRUE);
		catalogVersion.setVersion("Staged");
		catalogVersion.setCatalog(getCatalog());
		return catalogVersion;
	}
}