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
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetValueModel;
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
public class AsExcludedFacetModelTest extends ServicelayerTransactionalTest
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
	public void getNonExistingExcludedFacet() throws Exception
	{
		// when
		final Optional<AsExcludedFacetModel> excludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(excludedFacetOptional.isPresent());
	}

	@Test
	public void createExcludedFacetWithoutUid() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		// then
		assertNotNull(excludedFacet.getUid());
		assertFalse(excludedFacet.getUid().isEmpty());
	}

	@Test
	public void createExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdExcludedFacetOptional.isPresent());

		final AsExcludedFacetModel createdExcludedFacet = createdExcludedFacetOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedFacet.getCatalogVersion());
		assertEquals(UID1, createdExcludedFacet.getUid());
		assertEquals(searchConfiguration, createdExcludedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdExcludedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, createdExcludedFacet.getFacetType());
	}

	@Test
	public void failToCreateExcludedFacetWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(stagedCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithoutIndexProperty() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithWrongIndexProperty() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(WRONG_INDEX_PROPERTY);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithWrongIndexPropertyType() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY4);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void createMultipleExcludedFacets() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet1 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet1.setCatalogVersion(onlineCatalogVersion);
		excludedFacet1.setUid(UID1);
		excludedFacet1.setSearchConfiguration(searchConfiguration);
		excludedFacet1.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsExcludedFacetModel excludedFacet2 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet2.setCatalogVersion(onlineCatalogVersion);
		excludedFacet2.setUid(UID2);
		excludedFacet2.setSearchConfiguration(searchConfiguration);
		excludedFacet2.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(excludedFacet1);
		asConfigurationService.saveConfiguration(excludedFacet2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getExcludedFacets()).containsExactly(excludedFacet1, excludedFacet2);
	}

	@Test
	public void updateExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);
		excludedFacet.setPriority(PRIORITY1);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetModel createdExcludedFacet = createdExcludedFacetOptional.get();
		createdExcludedFacet.setFacetType(AsFacetType.MULTISELECT_AND);
		createdExcludedFacet.setPriority(PRIORITY2);
		asConfigurationService.saveConfiguration(createdExcludedFacet);

		final Optional<AsExcludedFacetModel> updatedExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedExcludedFacetOptional.isPresent());

		final AsExcludedFacetModel updatedExcludedFacet = updatedExcludedFacetOptional.get();
		assertEquals(onlineCatalogVersion, updatedExcludedFacet.getCatalogVersion());
		assertEquals(UID1, updatedExcludedFacet.getUid());
		assertEquals(searchConfiguration, updatedExcludedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedExcludedFacet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, updatedExcludedFacet.getFacetType());
		assertEquals(PRIORITY2, updatedExcludedFacet.getPriority());
	}

	@Test
	public void failToUpdateExcludedFacetIndexProperty() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void cloneExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID2);
		promotedValue.setFacetConfiguration(excludedFacet);
		promotedValue.setValue(FACET_VALUE2);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID3);
		excludedValue.setFacetConfiguration(excludedFacet);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);

		asConfigurationService.refreshConfiguration(excludedFacet);

		final AsExcludedFacetModel clonedExcludedFacet = asConfigurationService.cloneConfiguration(excludedFacet);
		clonedExcludedFacet.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(clonedExcludedFacet);

		// then
		assertEquals(excludedFacet.getCatalogVersion(), clonedExcludedFacet.getCatalogVersion());
		assertNotEquals(excludedFacet.getUid(), clonedExcludedFacet.getUid());
		assertEquals(excludedFacet.getSearchConfiguration(), clonedExcludedFacet.getSearchConfiguration());
		assertEquals(excludedFacet.getFacetType(), clonedExcludedFacet.getFacetType());

		assertThat(clonedExcludedFacet.getPromotedValues()).isNotNull().hasSize(1);
		assertThat(clonedExcludedFacet.getExcludedValues()).isNotNull().hasSize(1);

		final AsPromotedFacetValueModel clonedPromotedValue = clonedExcludedFacet.getPromotedValues().get(0);
		assertEquals(promotedValue.getCatalogVersion(), clonedPromotedValue.getCatalogVersion());
		assertNotEquals(promotedValue.getUid(), clonedPromotedValue.getUid());
		assertNotEquals(promotedValue.getFacetConfiguration(), clonedPromotedValue.getFacetConfiguration());
		assertEquals(promotedValue.getValue(), clonedPromotedValue.getValue());

		final AsExcludedFacetValueModel clonedExcludedValue = clonedExcludedFacet.getExcludedValues().get(0);
		assertEquals(excludedValue.getCatalogVersion(), clonedExcludedValue.getCatalogVersion());
		assertNotEquals(excludedValue.getUid(), clonedExcludedValue.getUid());
		assertNotEquals(excludedValue.getFacetConfiguration(), clonedExcludedValue.getFacetConfiguration());
		assertEquals(excludedValue.getValue(), clonedExcludedValue.getValue());
	}

	@Test
	public void removeExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetModel createdExcludedFacet = createdExcludedFacetOptional.get();
		asConfigurationService.removeConfiguration(createdExcludedFacet);

		final Optional<AsExcludedFacetModel> removedExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedExcludedFacetOptional.isPresent());
	}

	@Test
	public void excludedFacetIsNotCorrupted() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		final boolean corrupted = excludedFacet.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet1 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet1.setCatalogVersion(onlineCatalogVersion);
		excludedFacet1.setUid(UID1);
		excludedFacet1.setSearchConfiguration(searchConfiguration);
		excludedFacet1.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsExcludedFacetModel excludedFacet2 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet2.setCatalogVersion(onlineCatalogVersion);
		excludedFacet2.setUid(UID2);
		excludedFacet2.setSearchConfiguration(searchConfiguration);
		excludedFacet2.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(excludedFacet1);
		asConfigurationService.saveConfiguration(excludedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedFacets()).containsExactly(excludedFacet2, excludedFacet1);
	}

	@Test
	public void rankBeforeExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet1 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet1.setCatalogVersion(onlineCatalogVersion);
		excludedFacet1.setUid(UID1);
		excludedFacet1.setSearchConfiguration(searchConfiguration);
		excludedFacet1.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsExcludedFacetModel excludedFacet2 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet2.setCatalogVersion(onlineCatalogVersion);
		excludedFacet2.setUid(UID2);
		excludedFacet2.setSearchConfiguration(searchConfiguration);
		excludedFacet2.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(excludedFacet1);
		asConfigurationService.saveConfiguration(excludedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedFacets()).containsExactly(excludedFacet2, excludedFacet1);
	}
}
