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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSComponentDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collections;


@IntegrationTest
public class DefaultCMSComponentDaoSharedComponentsIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CATALOG_VERSION = "CatalogVersion1";
	private static final String CONTENT_CATALOG = "cms_Catalog";
	private static final String FAQ_PAGE_UID = "faq";
	private static final String HELP_PAGE_UID = "help";
	private static final String SHARED_CONTENT_SLOT_ID = "TopContentSlot";
	private static final String FAQ_PAGE_SLOT_ID = "PunchedOutContentSlot";
	private static final String HELP_PAGE_SLOT_ID = "PunchedOutContentSlot_aboutUs";

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ModelService modelService;

	@Resource
	private CMSComponentDao cmsComponentDao;

	private CatalogVersionModel catalogVersion;
	private AbstractPageModel faqPage;

	private ContentSlotModel sharedSlot;
	private ContentSlotModel faqPageSlot;
	private ContentSlotModel helpPageSlot;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsCatalogVersionTestData.csv", "utf-8");
		catalogVersionService.setSessionCatalogVersion(CONTENT_CATALOG, CATALOG_VERSION);
		catalogVersion = catalogVersionService.getCatalogVersion(CONTENT_CATALOG, CATALOG_VERSION);

		final PageTemplateModel pageTemplate = modelService.create(PageTemplateModel.class);
		pageTemplate.setUid("pageTemplate");
		pageTemplate.setCatalogVersion(catalogVersion);

		sharedSlot = createSlotForTemplate(pageTemplate, SHARED_CONTENT_SLOT_ID);

		faqPage = createPage(pageTemplate, FAQ_PAGE_UID);
		faqPageSlot = createSlotForPage(faqPage, FAQ_PAGE_SLOT_ID);

		final AbstractPageModel helpPage = createPage(pageTemplate, HELP_PAGE_UID);
		helpPageSlot = createSlotForPage(helpPage, HELP_PAGE_SLOT_ID);

		modelService.saveAll();
	}

	@Test
	public void givenComponentIsUsedInSharedSlot_WhenGetComponentReferenceCountOutsidePageIsCalled_ThenItReturnsOne()
	{
		// GIVEN
		AbstractCMSComponentModel component = createNewComponentInSlot(sharedSlot);

		// WHEN
		long result = cmsComponentDao.getComponentReferenceCountOutsidePage(component, faqPage);

		// THEN
		assertThat(result, is(1L));
	}

	@Test
	public void givenComponentIsUsedInAnotherPage_WhenGetComponentReferenceCountOutsidePageIsCalled_ThenItReturnsOne()
	{
		// GIVEN
		AbstractCMSComponentModel component = createNewComponentInSlot(helpPageSlot);

		// WHEN
		long result = cmsComponentDao.getComponentReferenceCountOutsidePage(component, faqPage);

		// THEN
		assertThat(result, is(1L));
	}

	@Test
	public void givenComponentIsOnlyUsedWithinPage_WhenGetComponentReferenceCountOutsidePageIsCalled_ThenItReturnsZero()
	{
		// GIVEN
		AbstractCMSComponentModel component = createNewComponentInSlot(faqPageSlot);

		// WHEN
		long result = cmsComponentDao.getComponentReferenceCountOutsidePage(component, faqPage);

		// THEN
		assertThat(result, is(0L));
	}

	// -----------------------------------------------------------------------------------------------
	// Helper Methods
	// -----------------------------------------------------------------------------------------------
	protected AbstractCMSComponentModel createNewComponentInSlot(final ContentSlotModel contentSlot)
	{
		ABTestCMSComponentContainerModel component = new ABTestCMSComponentContainerModel();

		component.setCatalogVersion(catalogVersion);
		component.setUid("testComponent1");
		component.setName("test component");
		component.setSlots(Collections.singletonList(contentSlot));
		modelService.save(component);

		return component;
	}

	protected ContentSlotModel createSlotForPage(final AbstractPageModel page, final String slotId)
	{
		ContentSlotModel contentSlotModel = createSlot(slotId);
		ContentSlotForPageModel slotForPage = modelService.create(ContentSlotForPageModel.class);

		slotForPage.setContentSlot(contentSlotModel);
		slotForPage.setPage(page);
		slotForPage.setCatalogVersion(catalogVersion);
		slotForPage.setPosition("some position");

		return contentSlotModel;
	}

	protected ContentSlotModel createSlotForTemplate(final PageTemplateModel template, final String slotId)
	{
		ContentSlotModel contentSlotModel = createSlot(slotId);
		ContentSlotForTemplateModel slotForTemplate = modelService.create(ContentSlotForTemplateModel.class);

		slotForTemplate.setContentSlot(contentSlotModel);
		slotForTemplate.setPageTemplate(template);
		slotForTemplate.setCatalogVersion(catalogVersion);
		slotForTemplate.setPosition("some position");

		return contentSlotModel;
	}

	protected ContentSlotModel createSlot(final String slotId)
	{
		ContentSlotModel contentSlotModel = modelService.create(ContentSlotModel.class);
		contentSlotModel.setUid(slotId);
		contentSlotModel.setCatalogVersion(catalogVersion);

		return contentSlotModel;
	}

	protected AbstractPageModel createPage(final PageTemplateModel pageTemplate, final String pageUid)
	{
		final AbstractPageModel page = modelService.create(ContentPageModel.class);
		page.setUid(pageUid);
		page.setCatalogVersion(catalogVersion);
		page.setMasterTemplate(pageTemplate);
		page.setDefaultPage(Boolean.FALSE);

		return page;
	}
}
