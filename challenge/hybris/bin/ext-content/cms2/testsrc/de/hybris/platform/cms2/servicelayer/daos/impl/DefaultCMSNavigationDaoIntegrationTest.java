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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSNavigationDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class DefaultCMSNavigationDaoIntegrationTest extends ServicelayerTransactionalTest
{
	public static final String UID = "someId";
	public static final String CATALOG_ID = "testcCategory";
	public static final String EMPTY_CATALOG_ID = "empty_catalog";
	public static final String CATALOG_VERSION_ID = "testVersion";
	public static final String EMPTY_CATALOG_VERSION_ID = "emptyTestVersion";
	public static final String ROOT_NAVIGATION_NODE = "testRootNavigationModel";
	public static final String ROOT_NAVIGATION_ENTRY_0 = "testRootNavigationEntryModel_0";
	public static final String ROOT_NAVIGATION_ENTRY_1 = "testRootNavigationEntryModel_1";
	public static final String CMS_LINK_COMNPONENT_0 = "testCmsLinkComnponent_0";
	public static final String NAVIGATION_NODE_LEVEL_1_0 = "level_1_0";
	public static final String NAVIGATION_NODE_LEVEL_1_1 = "level_1_1";
	public static final String PAGE_TEMPLATE = "testTemplate";
	public static final String CONTENT_PAGE_0 = "content_page_1";
	@Resource
	private ModelService modelService;

	@Resource
	private CMSNavigationDao cmsNavigationDao;

	private CMSNavigationNodeModel rootNavigationModel;
	private CMSNavigationNodeModel firstNavigationModel;
	private CMSNavigationNodeModel secondNavigationModel;
	private ContentPageModel firstContentPage;
	private CMSLinkComponentModel cmsLinkComponent;

	private CMSNavigationEntryModel rootNavigationEntryModel_0;
	private CMSNavigationEntryModel rootNavigationEntryModel_1;

	private CatalogVersionModel catalogVersionModel;
	private CatalogModel catalog;

	private CatalogVersionModel emptyCatalogVersionModel;
	private CatalogModel emptyCatalog;


	@Before
	public void createSampleCatalogStructure()
	{
		catalog = modelService.create(CatalogModel.class);
		catalog.setId(CATALOG_ID);
		modelService.save(catalog);

		emptyCatalog = modelService.create(CatalogModel.class);
		emptyCatalog.setId(EMPTY_CATALOG_ID);
		modelService.save(emptyCatalog);

		catalogVersionModel = modelService.create(CatalogVersionModel.class);
		catalogVersionModel.setActive(Boolean.TRUE);
		catalogVersionModel.setVersion(CATALOG_VERSION_ID);
		catalogVersionModel.setCatalog(catalog);
		catalog.setCatalogVersions(Collections.singleton(catalogVersionModel));
		modelService.save(catalogVersionModel);

		emptyCatalogVersionModel = modelService.create(CatalogVersionModel.class);
		emptyCatalogVersionModel.setActive(Boolean.TRUE);
		emptyCatalogVersionModel.setVersion(EMPTY_CATALOG_VERSION_ID);
		emptyCatalogVersionModel.setCatalog(catalog);
		emptyCatalog.setCatalogVersions(Collections.singleton(emptyCatalogVersionModel));
		modelService.save(emptyCatalogVersionModel);

		rootNavigationModel = new CMSNavigationNodeModel();
		rootNavigationModel.setUid(ROOT_NAVIGATION_NODE);
		rootNavigationModel.setCatalogVersion(catalogVersionModel);
		rootNavigationModel.setChildren(createNavigationNodes(catalogVersionModel));

		rootNavigationEntryModel_0 = new CMSNavigationEntryModel();
		rootNavigationEntryModel_0.setUid(ROOT_NAVIGATION_ENTRY_0);
		rootNavigationEntryModel_0.setNavigationNode(rootNavigationModel);
		rootNavigationEntryModel_0.setItem(firstContentPage);
		rootNavigationEntryModel_0.setCatalogVersion(emptyCatalogVersionModel);
		rootNavigationModel.setEntries(Arrays.asList(rootNavigationEntryModel_0));

		cmsLinkComponent= new CMSLinkComponentModel();
		cmsLinkComponent.setUid(CMS_LINK_COMNPONENT_0);
		cmsLinkComponent.setContentPage(firstContentPage);
		cmsLinkComponent.setCatalogVersion(emptyCatalogVersionModel);

		rootNavigationEntryModel_1 = new CMSNavigationEntryModel();
		rootNavigationEntryModel_1.setUid(ROOT_NAVIGATION_ENTRY_1);
		rootNavigationEntryModel_1.setNavigationNode(rootNavigationModel);
		rootNavigationEntryModel_1.setItem(cmsLinkComponent);
		rootNavigationEntryModel_1.setCatalogVersion(emptyCatalogVersionModel);
		rootNavigationModel.setEntries(Arrays.asList(rootNavigationEntryModel_1));

		modelService.save(rootNavigationModel);
		modelService.save(rootNavigationEntryModel_0);
		modelService.refresh(catalog);
	}

	protected List<CMSNavigationNodeModel> createNavigationNodes(final CatalogVersionModel catalogVersionModel)
	{
		createContentPages();
		final List<CMSNavigationNodeModel> navigationNodesChildren = new ArrayList<CMSNavigationNodeModel>();

		firstNavigationModel = new CMSNavigationNodeModel();
		firstNavigationModel.setUid(NAVIGATION_NODE_LEVEL_1_0);
		firstNavigationModel.setCatalogVersion(catalogVersionModel);
		navigationNodesChildren.add(firstNavigationModel);
		firstNavigationModel.setPages(Arrays.asList(firstContentPage));
		modelService.save(firstNavigationModel);

		secondNavigationModel = new CMSNavigationNodeModel();
		secondNavigationModel.setUid(NAVIGATION_NODE_LEVEL_1_1);
		secondNavigationModel.setCatalogVersion(catalogVersionModel);
		navigationNodesChildren.add(secondNavigationModel);
		modelService.save(secondNavigationModel);

		return navigationNodesChildren;
	}

	protected void createContentPages()
	{
		final PageTemplateModel template = modelService.create(PageTemplateModel.class);
		template.setUid(PAGE_TEMPLATE);
		template.setCatalogVersion(catalogVersionModel);
		modelService.save(template);

		firstContentPage = new ContentPageModel();
		firstContentPage.setUid(CONTENT_PAGE_0);
		firstContentPage.setMasterTemplate(template);
		firstContentPage.setCatalogVersion(catalogVersionModel);

		modelService.save(firstContentPage);
	}

	@Test
	public void testFindCMSNavigationNodes() throws CMSItemNotFoundException
	{
		//Assert that firstNavigationModel exists when sending a page that exists in a catalogVersion
		List<CMSNavigationNodeModel> result1 = cmsNavigationDao.findNavigationNodesByContentPage(firstContentPage,
				Arrays.asList(catalogVersionModel));
		assertTrue(result1.contains(firstNavigationModel));

		//Assert that secondNavigationModel does not contain page "firstContentPage"
		result1 = cmsNavigationDao.findNavigationNodesByContentPage(firstContentPage, Arrays.asList(catalogVersionModel));
		assertFalse(result1.contains(secondNavigationModel));

		//Assert that no CMSNavigationNodeModels were found when sending a page that does not exist in certain catalogVersion
		result1 = cmsNavigationDao.findNavigationNodesByContentPage(firstContentPage, Arrays.asList(emptyCatalogVersionModel));
		assertTrue(result1.isEmpty());
	}

	@Test
	public void testFindPageNavigationEntriesByContentPage()
	{
		// WHEN
		final List<CMSNavigationEntryModel> result1 = cmsNavigationDao.findNavigationEntriesByPage(firstContentPage);

		// THEN
		assertTrue(result1.contains(rootNavigationEntryModel_0));
	}

	@Test
	public void testFindPageNavigationEntriesByUid()
	{
		// WHEN
		final CMSNavigationEntryModel result1 = cmsNavigationDao.findNavigationEntryByUid(rootNavigationEntryModel_1.getUid(), emptyCatalogVersionModel);

		// THEN
		assertThat(result1, Matchers.is(rootNavigationEntryModel_1));
	}
}
