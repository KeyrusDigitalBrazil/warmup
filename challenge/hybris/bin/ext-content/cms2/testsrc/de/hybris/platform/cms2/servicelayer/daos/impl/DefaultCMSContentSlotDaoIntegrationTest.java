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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSContentSlotDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CATALOG_VERSION = "Staged";
	private static final String CONTENT_CATALOG = "sampleContentCatalog";
	private static final String MIDDLE_CONTENT_SLOT = "MiddleContentSlot";
	private static final String BOTTOM_CONTENT_SLOT = "BottomContentSlot";
	private static final String TOP_CONTENT_SLOT = "TopContentSlot";
	private static final String PUNCHED_OUT_CONTENT_SLOT = "PunchedOutContentSlot";
	private static final String FAQ_PAGE_UID = "faq";

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private DefaultCMSContenSlotDao cmsContentSlotDao;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsRelationTestData.csv", "utf-8");
		catalogVersion = catalogVersionService.getCatalogVersion(CONTENT_CATALOG, CATALOG_VERSION);
	}

	@Test
	public void shouldFindPageRelationsForContentSlot()
	{
		final List<ContentSlotForPageModel> relations = cmsContentSlotDao
				.findAllContentSlotRelationsByContentSlot(getContentSlotById(PUNCHED_OUT_CONTENT_SLOT), catalogVersion);

		assertThat(relations.size(), is(3));
	}

	@Test
	public void shouldFindPageRelationForContentSlot_Empty()
	{
		final List<ContentSlotForPageModel> relations = cmsContentSlotDao
				.findAllContentSlotRelationsByContentSlot(getContentSlotById(TOP_CONTENT_SLOT), catalogVersion);

		assertThat(relations, empty());
	}

	@Test
	public void shouldFindAllContentSlotsForCatalogVersion()
	{
		final Collection<ContentSlotModel> contentSlots = cmsContentSlotDao.findContentSlotsForCatalogVersion(catalogVersion);
		assertThat(contentSlots.size(), is(4));
	}

	protected ContentSlotModel getContentSlotById(final String slotId)
	{
		ContentSlotModel contentSlot = new ContentSlotModel();
		contentSlot.setUid(slotId);
		contentSlot = flexibleSearchService.getModelByExample(contentSlot);
		return contentSlot;
	}

	@Test
	public void shouldFindContentSlotRelationsByPageUid()
	{
		// when
		List<ContentSlotForPageModel> relations = cmsContentSlotDao.findAllContentSlotRelationsByPageUid(FAQ_PAGE_UID, catalogVersion);

		// then
		assertThat(relations.size(), is(1));
	}
}
