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
package de.hybris.platform.cmsfacades.synchronization.service.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.*;
import de.hybris.platform.core.model.ItemModel;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_TARGET_CATALOG_VERSION;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;


@IntegrationTest
public class DefaultItemSynchronizationServiceIntegrationTest extends BaseIntegrationTest
{
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentCatalogModelMother contentCatalogModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private DefaultItemSynchronizationService defaultItemSynchronizationService;

	private CatalogVersionModel stagedCatalogVersionModel;
	private CatalogVersionModel onlineCatalogVersionModel;
	private ContentSlotForPageModel stagedContentSlotForPageModel;
	private ContentSlotForPageModel onlineContentSlotForPageModel;
	private ContentPageModel stagedContentPageModel;
	private ContentPageModel onlineContentPageModel;
	private Map<String, Object> ctx = new HashMap<>();



	@Before
	public void setup()
	{
		stagedCatalogVersionModel = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		onlineCatalogVersionModel = catalogVersionModelMother.createAppleOnlineCatalogVersionModel();

		final ContentCatalogModel appleContentCatalogModel = contentCatalogModelMother.createAppleContentCatalogModel(
				stagedCatalogVersionModel, onlineCatalogVersionModel);
		siteModelMother.createElectronics(appleContentCatalogModel);
	}

	@Test
	public void shouldCollectPageRelationsFromOnlineCatalogVersion()
	{
		// given
		stagedContentPageModel = contentPageModelMother.homePage(stagedCatalogVersionModel);
		stagedContentSlotForPageModel = contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(stagedCatalogVersionModel);
		onlineContentPageModel = contentPageModelMother.homePage(onlineCatalogVersionModel);
		onlineContentSlotForPageModel = contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(onlineCatalogVersionModel);
		ctx.put(VISITORS_CTX_TARGET_CATALOG_VERSION, onlineCatalogVersionModel);

		// when
		List<ItemModel> collItemModels = defaultItemSynchronizationService.collectRelatedItems(stagedContentPageModel, ctx);

		// then
		List<String> contentSlotForPageModelKeys = extractPageRelations(collItemModels).stream()
				.map(this::convertToUniqueKeyForContentSlotForPage).collect(Collectors.toList());


		assertThat("Collector should collect all relations for the page in target catalog version", contentSlotForPageModelKeys,
				hasItem(convertToUniqueKeyForContentSlotForPage(onlineContentSlotForPageModel)));
	}

	@Test
	public void shouldNotCollectPageRelationsFromPageThatDoesNotExistsInOnlineCatalogVersion()
	{
		// given
		stagedContentPageModel = contentPageModelMother.homePage(stagedCatalogVersionModel);
		stagedContentSlotForPageModel = contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(stagedCatalogVersionModel);
		ctx.put(VISITORS_CTX_TARGET_CATALOG_VERSION, onlineCatalogVersionModel);

		// when
		List<ItemModel> collItemModels = defaultItemSynchronizationService.collectRelatedItems(stagedContentPageModel, ctx);

		// then
		List<String> contentSlotForPageModelKeys = extractPageRelations(collItemModels).stream()
				.map(this::convertToCatalogVersion).collect(Collectors.toList());

		assertThat(
				"Collector should not contain relations from target catalog version if the page does not exist in target catalog",
				contentSlotForPageModelKeys, not(hasItem(CatalogVersionModelMother.CatalogVersion.ONLINE.getVersion())));
	}

	@Test
	public void shouldCollectPageRelationsFromOnlineCatalogVersionEvenIfTheRelationDoesNotExistInSourceCatalogVersion()
	{
		// given
		stagedContentPageModel = contentPageModelMother.homePage(stagedCatalogVersionModel);
		onlineContentPageModel = contentPageModelMother.homePage(onlineCatalogVersionModel);
		onlineContentSlotForPageModel = contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(onlineCatalogVersionModel);
		ctx.put(VISITORS_CTX_TARGET_CATALOG_VERSION, onlineCatalogVersionModel);

		// when
		List<ItemModel> collItemModels = defaultItemSynchronizationService.collectRelatedItems(stagedContentPageModel, ctx);

		// then
		List<String> contentSlotForPageModelKeys = extractPageRelations(collItemModels).stream()
				.map(this::convertToUniqueKeyForContentSlotForPage).collect(Collectors.toList());

		assertThat("Collector should collect all relations for the page in target catalog version even "
						+ "if the relation does not exist in source catalog version", contentSlotForPageModelKeys,
				hasItem(convertToUniqueKeyForContentSlotForPage(onlineContentSlotForPageModel)));
	}

	@Test
	public void shouldNotCollectTargetCatalogVersionRelationsIfTargetCatalogVersionIsNotProvided()
	{
		// given
		stagedContentPageModel = contentPageModelMother.homePage(stagedCatalogVersionModel);
		stagedContentSlotForPageModel = contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(stagedCatalogVersionModel);
		onlineContentPageModel = contentPageModelMother.homePage(onlineCatalogVersionModel);
		onlineContentSlotForPageModel = contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(onlineCatalogVersionModel);

		// when
		List<ItemModel> collItemModels = defaultItemSynchronizationService.collectRelatedItems(stagedContentPageModel, ctx);

		// then
		List<String> contentSlotForPageModelKeys = extractPageRelations(collItemModels).stream()
				.map(this::convertToUniqueKeyForContentSlotForPage).collect(Collectors.toList());
		assertThat("Collector should not collect target page relations if the target catalog version is not provided",
				contentSlotForPageModelKeys, not(hasItem(convertToUniqueKeyForContentSlotForPage(onlineContentSlotForPageModel))));
	}

	protected List<ContentSlotForPageModel> extractPageRelations(final List<ItemModel> items)
	{
		return items.stream().filter(item -> item instanceof ContentSlotForPageModel).map(item -> (ContentSlotForPageModel) item)
				.collect(Collectors.toList());
	}

	protected String convertToUniqueKeyForContentSlotForPage(final ContentSlotForPageModel contentSlotForPageModel)
	{
		return contentSlotForPageModel.getUid() + ":" + contentSlotForPageModel.getCatalogVersion().getVersion() + ":"
				+ contentSlotForPageModel.getCatalogVersion().getCatalog().getId();
	}

	protected String convertToCatalogVersion(final ContentSlotForPageModel contentSlotForPageModel)
	{
		return contentSlotForPageModel.getCatalogVersion().getVersion();
	}

}
