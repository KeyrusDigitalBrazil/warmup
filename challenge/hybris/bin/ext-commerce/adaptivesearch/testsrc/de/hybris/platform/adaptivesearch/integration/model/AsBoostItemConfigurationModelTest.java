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
package de.hybris.platform.adaptivesearch.integration.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedItemModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedItemModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsBoostItemConfigurationModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	@Resource
	private ModelService modelService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AsSimpleSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asBoostItemConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void createMultipleBoostItemConfigurations() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID2);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
		asConfigurationService.saveConfiguration(excludedItem);

		final Optional<AsPromotedItemModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);

		final Optional<AsExcludedItemModel> createdExcludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID2);

		// then
		assertTrue(createdPromotedItemOptional.isPresent());
		assertTrue(createdExcludedItemOptional.isPresent());

		final AsPromotedItemModel createdPromotedItem = createdPromotedItemOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedItem.getCatalogVersion());
		assertEquals(UID1, createdPromotedItem.getUid());
		assertEquals(searchConfiguration, createdPromotedItem.getSearchConfiguration());
		assertEquals(stagedCatalogVersion, createdPromotedItem.getItem());

		final AsExcludedItemModel createdExcludedItem = createdExcludedItemOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedItem.getCatalogVersion());
		assertEquals(UID2, createdExcludedItem.getUid());
		assertEquals(searchConfiguration, createdExcludedItem.getSearchConfiguration());
		assertEquals(onlineCatalogVersion, createdExcludedItem.getItem());
	}

	@Test
	public void failToCreateMultipleBoostItemConfigurationsWithSameUid() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
		asConfigurationService.saveConfiguration(excludedItem);
	}

	@Test
	public void failToCreateMultipleBoostItemConfigurationsWithSameItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID2);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(stagedCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
		asConfigurationService.saveConfiguration(excludedItem);
	}

	@Test
	public void moveBoostItemConfiguration1() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, UID1);

		final Optional<AsPromotedItemModel> promotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedItemModel> excludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(promotedItemOptional.isPresent());
		assertTrue(excludedItemOptional.isPresent());

		final AsExcludedItemModel excludedItem = excludedItemOptional.get();
		assertEquals(onlineCatalogVersion, excludedItem.getCatalogVersion());
		assertEquals(UID1, excludedItem.getUid());
		assertEquals(searchConfiguration, excludedItem.getSearchConfiguration());
		assertEquals(onlineCatalogVersion, excludedItem.getItem());
	}

	@Test
	public void moveBoostItemConfiguration2() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID1);

		final Optional<AsPromotedItemModel> promotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedItemModel> excludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertTrue(promotedItemOptional.isPresent());
		assertFalse(excludedItemOptional.isPresent());

		final AsPromotedItemModel promotedItem = promotedItemOptional.get();
		assertEquals(onlineCatalogVersion, promotedItem.getCatalogVersion());
		assertEquals(UID1, promotedItem.getUid());
		assertEquals(searchConfiguration, promotedItem.getSearchConfiguration());
		assertEquals(onlineCatalogVersion, promotedItem.getItem());
	}
}
