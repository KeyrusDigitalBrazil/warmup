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
package de.hybris.platform.marketplaceservices.vendor.impl;

import static de.hybris.platform.catalog.enums.SyncItemStatus.NOT_SYNC;
import static de.hybris.platform.core.PK.fromLong;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ItemSyncTimestampModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.catalog.synchronization.SyncItemInfo;
import de.hybris.platform.catalog.synchronization.SynchronizationStatusService;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentSlotDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageTemplateDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSRestrictionDao;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.marketplaceservices.dao.MarketplaceCMSComponentDao;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.model.restrictions.CMSVendorRestrictionModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.MockSessionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 *
 */
@UnitTest
public class DefaultVendorCMSServiceTest
{
	private static final String UID = "default";
	private static final String POSITION = "section";
	private static final String CATALOG_VERSION = "staged";

	@Spy
	private final DefaultVendorCMSService vendorCmsService = new DefaultVendorCMSService();

	private CatalogVersionModel catalogVersion;

	@Mock
	private CMSPageTemplateDao cmsPageTemplateDao;

	@Mock
	private CMSPageDao cmsPageDao;

	@Mock
	private CMSRestrictionDao cmsRestrictionDao;

	@Mock
	private CMSContentSlotDao cmsContentSlotDao;

	@Mock
	private MarketplaceCMSComponentDao cmsComponentDao;

	@Spy
	private final MockSessionService mockSessionService = new MockSessionService();
	@Mock
	private SyncConfig syncConfig;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private SynchronizationStatusService platformSynchronizationStatusService;
	@Mock
	private CatalogSynchronizationService catalogSynchronizationService;
	@Mock
	private ModelService modelService;

	@Mock
	private ProductCarouselComponentModel carousel;
	private final PK carouselPK = fromLong(123);
	private ItemModel item;
	private List<ItemModel> carousels;

	@Mock
	private CatalogModel catalog;
	private final String catalogId = "theCatalogId";

	@Mock
	private CatalogVersionModel sourceVersion;
	private final String sourceVersionId = "sourceVersionId";

	@Mock
	private CatalogVersionModel targetVersion;
	private final String targetVersionId = "targetVersionId";

	private Set<CatalogVersionModel> catalogVersions;

	@Mock
	private SyncItemJobModel targetToSourceJob;
	@Mock
	private SyncItemJobModel sourceToTargetJob;
	@Mock
	private SyncItemJobModel wrongJob2;
	@Mock
	private SyncItemJobModel wrongJob3;

	@Mock
	private SyncItemInfo syncItemInfo;
	private final PK lastSyncTimePK = fromLong(123456);
	@Mock
	private ItemSyncTimestampModel timeStamp;
	@Mock
	private Date lastSyncTime;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		catalogVersion = new CatalogVersionModel();
		catalogVersion.setVersion(CATALOG_VERSION);

		
		vendorCmsService.setCmsPageTemplateDao(cmsPageTemplateDao);
		vendorCmsService.setCmsPageDao(cmsPageDao);
		vendorCmsService.setCmsRestrictionDao(cmsRestrictionDao);
		vendorCmsService.setCmsComponentDao(cmsComponentDao);
		vendorCmsService.setCmsContentSlotDao(cmsContentSlotDao);

		vendorCmsService.setSyncConfig(syncConfig);
		vendorCmsService.setSearchRestrictionService(searchRestrictionService);
		vendorCmsService.setSessionService(mockSessionService);
		vendorCmsService.setCatalogTypeService(catalogTypeService);
		vendorCmsService.setCatalogVersionService(catalogVersionService);
		vendorCmsService.setPlatformSynchronizationStatusService(platformSynchronizationStatusService);
		vendorCmsService.setCatalogSynchronizationService(catalogSynchronizationService);

		item = carousel;
		carousels = new ArrayList<>();
		carousels.add(carousel);
		Mockito.when(vendorCmsService.getItemList(item)).thenReturn(carousels);

		catalogVersions = new HashSet<>();
		catalogVersions.add(sourceVersion);
		catalogVersions.add(targetVersion);

		when(sourceVersion.getVersion()).thenReturn(sourceVersionId);
		when(targetVersion.getVersion()).thenReturn(targetVersionId);

		when(sourceVersion.getVersion()).thenReturn(sourceVersionId);
		when(targetVersion.getVersion()).thenReturn(targetVersionId);

		when(carousel.getCatalogVersion()).thenReturn(sourceVersion);
		when(sourceVersion.getCatalog()).thenReturn(catalog);
		when(catalog.getId()).thenReturn(catalogId);
		when(catalog.getActiveCatalogVersion()).thenReturn(targetVersion);
		when(catalog.getCatalogVersions()).thenReturn(catalogVersions);

		when(catalogVersionService.getCatalogVersion(catalogId, sourceVersionId)).thenReturn(sourceVersion);
		when(catalogVersionService.getCatalogVersion(catalogId, targetVersionId)).thenReturn(targetVersion);

		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(carousel)).thenReturn(sourceVersion);

		when(targetToSourceJob.getSourceVersion()).thenReturn(targetVersion);
		when(targetToSourceJob.getTargetVersion()).thenReturn(sourceVersion);

		when(sourceToTargetJob.getSourceVersion()).thenReturn(sourceVersion);
		when(sourceToTargetJob.getTargetVersion()).thenReturn(targetVersion);

		when(wrongJob2.getSourceVersion()).thenReturn(sourceVersion);
		when(wrongJob2.getTargetVersion()).thenReturn(sourceVersion);

		when(wrongJob3.getSourceVersion()).thenReturn(targetVersion);
		when(wrongJob3.getTargetVersion()).thenReturn(targetVersion);

		when(platformSynchronizationStatusService.getInboundSynchronizations(carousel)).thenReturn(
				asList(targetToSourceJob, sourceToTargetJob, wrongJob2, wrongJob3));
		when(platformSynchronizationStatusService.getOutboundSynchronizations(carousel)).thenReturn(
				asList(targetToSourceJob, sourceToTargetJob, wrongJob2, wrongJob3));

		when(carousel.getPk()).thenReturn(carouselPK);

		when(syncItemInfo.getSyncStatus()).thenReturn(NOT_SYNC);
		when(syncItemInfo.getSyncTimestampPk()).thenReturn(lastSyncTimePK);
		when(modelService.get(lastSyncTimePK)).thenReturn(timeStamp);
		when(timeStamp.getLastSyncTime()).thenReturn(lastSyncTime);

		when(platformSynchronizationStatusService.getSyncInfo(carousel, sourceToTargetJob)).thenReturn(syncItemInfo);
		when(platformSynchronizationStatusService.getSyncInfo(carousel, targetToSourceJob)).thenReturn(syncItemInfo);

		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);
	}

	@Test
	public void testGetPageTemplateByIdAndCatalogVersion()
	{
		final PageTemplateModel pageTemplate = new PageTemplateModel();
		pageTemplate.setUid(UID);
		pageTemplate.setActive(true);

		final List<PageTemplateModel> pageTemplates = new ArrayList<>();
		pageTemplates.add(pageTemplate);

		Mockito.doReturn(pageTemplates).when(cmsPageTemplateDao).findPageTemplatesByIdAndCatalogVersion(Mockito.anyString(),
				Mockito.any(CatalogVersionModel.class));
		assertEquals(UID, vendorCmsService.getPageTemplateByIdAndCatalogVersion(UID, catalogVersion).get().getUid());
	}

	@Test
	public void testGetPageByIdAndCatalogVersion()
	{
		final AbstractPageModel page = new VendorPageModel();
		page.setCatalogVersion(catalogVersion);

		final List<AbstractPageModel> pages = new ArrayList<>();
		pages.add(page);

		Mockito.doReturn(pages).when(cmsPageDao).findPagesByIdAndCatalogVersion(Mockito.anyString(),
				Mockito.any(CatalogVersionModel.class));
		assertTrue(vendorCmsService.getPageByIdAndCatalogVersion(UID, catalogVersion).isPresent());
	}

	@Test
	public void testGetRestrictionByIdAndCatalogVersion()
	{
		final AbstractRestrictionModel restriction = new CMSVendorRestrictionModel();
		restriction.setCatalogVersion(catalogVersion);

		final List<AbstractRestrictionModel> restrictions = new ArrayList<>();
		restrictions.add(restriction);

		Mockito.doReturn(restrictions).when(cmsRestrictionDao).findRestrictionsById(Mockito.anyString(),
				Mockito.any(CatalogVersionModel.class));
		assertTrue(vendorCmsService.getRestrictionByIdAndCatalogVersion(UID, catalogVersion).isPresent());
	}

	@Test
	public void testGetContentSlotRelationByIdAndPositionInPage()
	{
		final ContentSlotForPageModel contentSlotForPage = new ContentSlotForPageModel();
		contentSlotForPage.setCatalogVersion(catalogVersion);
		contentSlotForPage.setUid(UID);

		final List<ContentSlotForPageModel> contentSlotRelations = new ArrayList<>();
		contentSlotRelations.add(contentSlotForPage);

		Mockito.doReturn(contentSlotRelations).when(cmsContentSlotDao).findContentSlotRelationsByPageAndPosition(
				Mockito.any(AbstractPageModel.class), Mockito.anyString(), Mockito.any(CatalogVersionModel.class));
		assertTrue(vendorCmsService.getContentSlotRelationByIdAndPositionInPage(UID, new VendorPageModel(), POSITION).isPresent());
	}

	@Test
	public void testGetContentSlotByIdAndCatalogVersion()
	{
		final ContentSlotModel contentSlot = new ContentSlotModel();
		contentSlot.setCatalogVersion(catalogVersion);
		contentSlot.setActive(true);

		final List<ContentSlotModel> contentSlots = new ArrayList<>();
		contentSlots.add(contentSlot);

		Mockito.doReturn(contentSlots).when(cmsContentSlotDao).findContentSlotsByIdAndCatalogVersions(Mockito.anyString(),
				Mockito.any());
		assertTrue(vendorCmsService.getContentSlotByIdAndCatalogVersion(UID, catalogVersion).isPresent());
	}

	@Test
	public void testGetCMSComponentByIdAndCatalogVersion()
	{
		final AbstractCMSComponentModel component = new AbstractCMSComponentModel();
		component.setCatalogVersion(catalogVersion);

		final List<AbstractCMSComponentModel> components = new ArrayList<>();
		components.add(component);

		Mockito.doReturn(components).when(cmsComponentDao).findCMSComponentsByIdAndCatalogVersion(Mockito.anyString(),
				Mockito.any());
		assertTrue(vendorCmsService.getCMSComponentByIdAndCatalogVersion(UID, catalogVersion).isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProductCarouselSynchronizationStatus_With_NullCarousel()
	{
		vendorCmsService.getProductCarouselSynchronizationStatus(null);
	}

	@Test
	public void testGetProductCarouselSynchronizationStatus()
	{
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);

		final SyncItemStatus status = vendorCmsService.getProductCarouselSynchronizationStatus(carousel);

		assertThat(status, is(NOT_SYNC));

		verify(platformSynchronizationStatusService, times(1)).getOutboundSynchronizations(carousel);
		verify(platformSynchronizationStatusService, never()).getInboundSynchronizations(any(ProductCarouselComponentModel.class));
		verify(platformSynchronizationStatusService, times(1)).getSyncInfo(carousel, sourceToTargetJob);
		verify(searchRestrictionService, times(1)).disableSearchRestrictions();
		verify(searchRestrictionService, times(1)).enableSearchRestrictions();
	}

	@Test
	public void testPerformItemSynchronization()
	{
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);

		vendorCmsService.performProductCarouselSynchronization(carousel, false);

		verify(catalogSynchronizationService, times(1)).performSynchronization(carousels,
				sourceToTargetJob, syncConfig);
		verify(platformSynchronizationStatusService, times(1)).getOutboundSynchronizations(carousel);
		verify(platformSynchronizationStatusService, never()).getInboundSynchronizations(any(ItemModel.class));
		verify(searchRestrictionService, times(1)).disableSearchRestrictions();
		verify(searchRestrictionService, times(1)).enableSearchRestrictions();
	}

}
