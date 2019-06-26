/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.merchandising.dao.MerchSynchronizationConfigDao;
import com.hybris.merchandising.model.MerchSynchronizationConfigModel;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.merchandising.yaas.client.CategoryHierarchyWrapper;
import com.hybris.merchandising.yaas.client.MerchCatalogServiceClient;
import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.impl.InMemoryChangesCollector;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test suite for {@link DefaultMerchCategoryExporter}.
 *
 */
public class DefaultMerchCategoryExporterTest {
	DefaultMerchCategoryExporter exporter;

	public static final String APPAREL_UK = "apparel-uk";
	public static final String ELECTRONICS = "electronics";
	public static final String CATALOG_ID = "123";
	public static final String CATALOG_VERSION_ID = "live";
	public static final String BASE_CAT_URL = "https://hybris.com";

	public static final Long ITEM_CHANGE_PK = Long.valueOf(1234);
	public static final String ITEM_CHANGE_INFO = "INFO";

	private MerchCatalogServiceClient client;
	private MerchCatalogService catService;
	private BaseSiteService baseSiteService;

	@Before
	public void setUp() {
		exporter = new DefaultMerchCategoryExporter();

		final MerchSynchronizationConfigDao configDao = Mockito.mock(MerchSynchronizationConfigDao.class);
		final Collection<MerchSynchronizationConfigModel> config = new ArrayList<>();
		config.add(getMockConfiguration(true));
		Mockito.when(configDao.findAllMerchSynchronizationConfig()).thenReturn(config);

		baseSiteService = Mockito.mock(BaseSiteService.class);
		exporter.setBaseSiteService(baseSiteService);

		catService = Mockito.mock(MerchCatalogService.class);
		exporter.setMerchCatalogService(catService);
		exporter.setMerchSynchronizationConfigDao(configDao);
		client = Mockito.mock(MerchCatalogServiceClient.class);
		exporter.setClient(client);
	}

	@Test
	public void testProcessEnabledConfig() {
		exporter.exportCategories();
		Mockito.verify(client, Mockito.times(1)).handleCategories(Mockito.any(CategoryHierarchyWrapper.class));
	}

	@Test
	public void testProcessDisabledConfig() {
		final MerchSynchronizationConfigDao configDao = Mockito.mock(MerchSynchronizationConfigDao.class);
		final Collection<MerchSynchronizationConfigModel> config = new ArrayList<>();
		config.add(getMockConfiguration(false));
		Mockito.when(configDao.findAllMerchSynchronizationConfig()).thenReturn(config);

		final List<CategoryHierarchy> categories = new ArrayList<>();
		final MerchCatalogService catService = Mockito.mock(MerchCatalogService.class);
		Mockito.when(catService.getCategories(APPAREL_UK, CATALOG_ID, CATALOG_VERSION_ID, BASE_CAT_URL)).thenReturn(categories);

		exporter.setMerchCatalogService(catService);
		exporter.setMerchSynchronizationConfigDao(configDao);

		final MerchCatalogServiceClient client = Mockito.mock(MerchCatalogServiceClient.class);
		exporter.setClient(client);

		exporter.exportCategories();
		Mockito.verify(client, Mockito.times(0)).handleCategories(Mockito.any(CategoryHierarchyWrapper.class));
	}

	@Test
	public void testExportCategoriesForCurrentBaseSite() {
		final MerchSynchronizationConfigDao configDao = Mockito.mock(MerchSynchronizationConfigDao.class);
		final Collection<MerchSynchronizationConfigModel> config = new ArrayList<>();
		final MerchSynchronizationConfigModel configModel = getMockConfiguration(true);
		config.add(configModel);
		Mockito.when(configDao.findAllMerchSynchronizationConfig()).thenReturn(config);
		final BaseSiteModel site = configModel.getBaseSite();
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		exporter.exportCategoriesForCurrentBaseSite();
		Mockito.verify(client).handleCategories(Mockito.any(CategoryHierarchyWrapper.class));
	}

	@Test
	public void testExportCategoriesForCurrentBaseSiteNonMatching() {
		final MerchSynchronizationConfigDao configDao = Mockito.mock(MerchSynchronizationConfigDao.class);
		final Collection<MerchSynchronizationConfigModel> config = new ArrayList<>();
		final MerchSynchronizationConfigModel configModel = getMockConfiguration(true);
		config.add(configModel);
		Mockito.when(configDao.findAllMerchSynchronizationConfig()).thenReturn(config);
		final BaseSiteModel site = Mockito.mock(BaseSiteModel.class);
	
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		exporter.exportCategoriesForCurrentBaseSite();
		Mockito.verify(client, Mockito.times(0)).handleCategories(Mockito.any(CategoryHierarchyWrapper.class));
	}

	@Test
	public void testGetMerchSynchronizationConfigDao() {
		final MerchSynchronizationConfigDao dao = Mockito.mock(MerchSynchronizationConfigDao.class);
		exporter.setMerchSynchronizationConfigDao(dao);
		final MerchSynchronizationConfigDao retrievedDao = exporter.getMerchSynchronizationConfigDao();
		Assert.assertEquals("Expected set DAO and retrieved DAO to be the same", dao, retrievedDao);
	}

	@Test
	public void testGetMerchCatalogService() {
		final MerchCatalogService catService = Mockito.mock(MerchCatalogService.class);
		exporter.setMerchCatalogService(catService);
		final MerchCatalogService retrieved = exporter.getMerchCatalogService();
		Assert.assertEquals("Expected catalog service to be the same", catService, retrieved);
	}

	@Test
	public void testGetBaseSiteService() {
		final BaseSiteService baseSiteServiceToSet = Mockito.mock(BaseSiteService.class);
		exporter.setBaseSiteService(baseSiteServiceToSet);
		final BaseSiteService retrieved = exporter.getBaseSiteService();
		Assert.assertEquals("Expected base site service to be the same", baseSiteServiceToSet, retrieved);
	}

	@Test
	public void testGetClient() {
		final MerchCatalogServiceClient client = Mockito.mock(MerchCatalogServiceClient.class);
		exporter.setClient(client);
		final MerchCatalogServiceClient retrieved = exporter.getClient();
		Assert.assertEquals("Expected client to be the same",  client, retrieved);
	}

	@Test
	public void testPerform() {
		final InMemoryChangesCollector collector = Mockito.mock(InMemoryChangesCollector.class);
		final ItemChangeDTO changedItem = Mockito.mock(ItemChangeDTO.class);
		Mockito.when(changedItem.getItemPK()).thenReturn(ITEM_CHANGE_PK);
		Mockito.when(changedItem.getInfo()).thenReturn(ITEM_CHANGE_INFO);
		final List<ItemChangeDTO> changes = new ArrayList<>();
		changes.add(changedItem);
		Mockito.when(collector.getChanges()).thenReturn(changes);

		final TypeService mockTypeService = Mockito.mock(TypeService.class);
		exporter.setTypeService(mockTypeService);

		final ChangeDetectionService mockChangeDetectionService = Mockito.mock(ChangeDetectionService.class);
		exporter.setChangeDetectionService(mockChangeDetectionService);

		exporter.perform(collector);
	}

	@Test
	public void testPerformNoChanges() {
		final InMemoryChangesCollector collector = Mockito.mock(InMemoryChangesCollector.class);
		final List<ItemChangeDTO> changes = new ArrayList<>();
	
		Mockito.when(collector.getChanges()).thenReturn(changes);

		final TypeService mockTypeService = Mockito.mock(TypeService.class);
		exporter.setTypeService(mockTypeService);

		final ChangeDetectionService mockChangeDetectionService = Mockito.mock(ChangeDetectionService.class);
		exporter.setChangeDetectionService(mockChangeDetectionService);
		exporter.perform(collector);
	}

	@Test
	public void testGetTypeService() {
		final TypeService mockTypeService = Mockito.mock(TypeService.class);
		exporter.setTypeService(mockTypeService);
		final TypeService retrievedTypeService = exporter.getTypeService();
		Assert.assertEquals("Expected type service to be the same", mockTypeService, retrievedTypeService);
	}

	@Test
	public void testGetChangeDetectionService() {
		final ChangeDetectionService cdService = Mockito.mock(ChangeDetectionService.class);
		exporter.setChangeDetectionService(cdService);
		final ChangeDetectionService retrievedCDService = exporter.getChangeDetectionService();
		Assert.assertEquals("Expected change deletion service to be the same", cdService, retrievedCDService);
	}

	private MerchSynchronizationConfigModel getMockConfiguration(final boolean enabled) {
		final MerchSynchronizationConfigModel config = Mockito.mock(MerchSynchronizationConfigModel.class);
		final BaseSiteModel baseSite = Mockito.mock(BaseSiteModel.class);
		Mockito.when(baseSite.getUid()).thenReturn(APPAREL_UK);
		final CatalogModel catalog = Mockito.mock(CatalogModel.class);
		Mockito.when(catalog.getId()).thenReturn(CATALOG_ID);
		final CatalogVersionModel catVersion = Mockito.mock(CatalogVersionModel.class);
		Mockito.when(catVersion.getVersion()).thenReturn(CATALOG_VERSION_ID);

		Mockito.when(config.getBaseSite()).thenReturn(baseSite);
		Mockito.when(config.getCatalog()).thenReturn(catalog);
		Mockito.when(config.getCatalogVersion()).thenReturn(catVersion);
		Mockito.when(config.isEnabled()).thenReturn(enabled);
		return config;
	}
}
