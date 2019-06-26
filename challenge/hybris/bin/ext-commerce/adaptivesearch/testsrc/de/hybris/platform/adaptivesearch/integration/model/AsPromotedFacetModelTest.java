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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.data.AsRankChange;
import de.hybris.platform.adaptivesearch.data.AsRankChangeType;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetValueModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetValueModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsPromotedFacetModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";
	private static final String UID3 = "9e02bd4f-2785-45f9-884f-571871a492c0";

	private static final String FACET_VALUE1 = "FacetValue1";
	private static final String FACET_VALUE2 = "FacetValue2";

	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY3 = "property3";
	private static final String INDEX_PROPERTY4 = "property4";
	private static final String WRONG_INDEX_PROPERTY = "testPropertyError";

	private static final Integer PRIORITY1 = Integer.valueOf(1);
	private static final Integer PRIORITY2 = Integer.valueOf(2);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

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
		importCsv("/adaptivesearch/test/integration/model/asFacetConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void getNonExistingPromotedFacet() throws Exception
	{
		// when
		final Optional<AsPromotedFacetModel> promotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(promotedFacetOptional.isPresent());
	}

	@Test
	public void createPromotedFacetWithoutUid() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		// then
		assertNotNull(promotedFacet.getUid());
		assertFalse(promotedFacet.getUid().isEmpty());
	}

	@Test
	public void createPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdPromotedFacetOptional.isPresent());

		final AsPromotedFacetModel createdPromotedFacet = createdPromotedFacetOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedFacet.getCatalogVersion());
		assertEquals(UID1, createdPromotedFacet.getUid());
		assertEquals(searchConfiguration, createdPromotedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdPromotedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, createdPromotedFacet.getFacetType());
	}

	@Test
	public void failToCreatePromotedFacetWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithoutIndexProperty() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithWrongIndexProperty() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(WRONG_INDEX_PROPERTY);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithWrongIndexPropertyType() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY4);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void createMultiplePromotedFacets() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet1 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet1.setCatalogVersion(onlineCatalogVersion);
		promotedFacet1.setUid(UID1);
		promotedFacet1.setSearchConfiguration(searchConfiguration);
		promotedFacet1.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsPromotedFacetModel promotedFacet2 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet2.setCatalogVersion(onlineCatalogVersion);
		promotedFacet2.setUid(UID2);
		promotedFacet2.setSearchConfiguration(searchConfiguration);
		promotedFacet2.setIndexProperty(INDEX_PROPERTY2);
		promotedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(promotedFacet1);
		asConfigurationService.saveConfiguration(promotedFacet2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getPromotedFacets()).containsExactly(promotedFacet1, promotedFacet2);
	}

	@Test
	public void updatePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);
		promotedFacet.setPriority(PRIORITY1);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetModel createdPromotedFacet = createdPromotedFacetOptional.get();
		createdPromotedFacet.setFacetType(AsFacetType.MULTISELECT_AND);
		createdPromotedFacet.setPriority(PRIORITY2);
		asConfigurationService.saveConfiguration(createdPromotedFacet);

		final Optional<AsPromotedFacetModel> updatedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedPromotedFacetOptional.isPresent());

		final AsPromotedFacetModel updatedPromotedFacet = updatedPromotedFacetOptional.get();
		assertEquals(onlineCatalogVersion, updatedPromotedFacet.getCatalogVersion());
		assertEquals(UID1, updatedPromotedFacet.getUid());
		assertEquals(searchConfiguration, updatedPromotedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedPromotedFacet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, updatedPromotedFacet.getFacetType());
		assertEquals(PRIORITY2, updatedPromotedFacet.getPriority());
	}

	@Test
	public void failToUpdatePromotedFacetIndexProperty() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void clonePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID2);
		promotedValue.setFacetConfiguration(promotedFacet);
		promotedValue.setValue(FACET_VALUE2);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID3);
		excludedValue.setFacetConfiguration(promotedFacet);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);

		asConfigurationService.refreshConfiguration(promotedFacet);

		final AsPromotedFacetModel clonedPromotedFacet = asConfigurationService.cloneConfiguration(promotedFacet);
		clonedPromotedFacet.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(clonedPromotedFacet);

		// then
		assertEquals(promotedFacet.getCatalogVersion(), clonedPromotedFacet.getCatalogVersion());
		assertNotEquals(promotedFacet.getUid(), clonedPromotedFacet.getUid());
		assertEquals(promotedFacet.getSearchConfiguration(), clonedPromotedFacet.getSearchConfiguration());
		assertEquals(promotedFacet.getFacetType(), clonedPromotedFacet.getFacetType());

		assertThat(clonedPromotedFacet.getPromotedValues()).isNotNull().hasSize(1);
		assertThat(clonedPromotedFacet.getExcludedValues()).isNotNull().hasSize(1);

		final AsPromotedFacetValueModel clonedPromotedValue = clonedPromotedFacet.getPromotedValues().get(0);
		assertEquals(promotedValue.getCatalogVersion(), clonedPromotedValue.getCatalogVersion());
		assertNotEquals(promotedValue.getUid(), clonedPromotedValue.getUid());
		assertNotEquals(promotedValue.getFacetConfiguration(), clonedPromotedValue.getFacetConfiguration());
		assertEquals(promotedValue.getValue(), clonedPromotedValue.getValue());

		final AsExcludedFacetValueModel clonedExcludedValue = clonedPromotedFacet.getExcludedValues().get(0);
		assertEquals(excludedValue.getCatalogVersion(), clonedExcludedValue.getCatalogVersion());
		assertNotEquals(excludedValue.getUid(), clonedExcludedValue.getUid());
		assertNotEquals(excludedValue.getFacetConfiguration(), clonedExcludedValue.getFacetConfiguration());
		assertEquals(excludedValue.getValue(), clonedExcludedValue.getValue());
	}

	@Test
	public void removePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetModel createdPromotedFacet = createdPromotedFacetOptional.get();
		asConfigurationService.removeConfiguration(createdPromotedFacet);

		final Optional<AsPromotedFacetModel> removedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedPromotedFacetOptional.isPresent());
	}

	@Test
	public void promotedFacetIsNotCorrupted() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		final boolean corrupted = promotedFacet.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet1 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet1.setCatalogVersion(onlineCatalogVersion);
		promotedFacet1.setUid(UID1);
		promotedFacet1.setSearchConfiguration(searchConfiguration);
		promotedFacet1.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsPromotedFacetModel promotedFacet2 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet2.setCatalogVersion(onlineCatalogVersion);
		promotedFacet2.setUid(UID2);
		promotedFacet2.setSearchConfiguration(searchConfiguration);
		promotedFacet2.setIndexProperty(INDEX_PROPERTY2);
		promotedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(promotedFacet1);
		asConfigurationService.saveConfiguration(promotedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedFacets()).containsExactly(promotedFacet2, promotedFacet1);
	}

	@Test
	public void rankBeforePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet1 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet1.setCatalogVersion(onlineCatalogVersion);
		promotedFacet1.setUid(UID1);
		promotedFacet1.setSearchConfiguration(searchConfiguration);
		promotedFacet1.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsPromotedFacetModel promotedFacet2 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet2.setCatalogVersion(onlineCatalogVersion);
		promotedFacet2.setUid(UID2);
		promotedFacet2.setSearchConfiguration(searchConfiguration);
		promotedFacet2.setIndexProperty(INDEX_PROPERTY2);
		promotedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(promotedFacet1);
		asConfigurationService.saveConfiguration(promotedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedFacets()).containsExactly(promotedFacet2, promotedFacet1);
	}
}
