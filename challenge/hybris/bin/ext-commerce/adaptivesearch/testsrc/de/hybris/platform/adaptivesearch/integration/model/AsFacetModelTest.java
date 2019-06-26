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
import de.hybris.platform.adaptivesearch.model.AsFacetModel;
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
public class AsFacetModelTest extends ServicelayerTransactionalTest
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
	public void getNonExistingFacet() throws Exception
	{
		// when
		final Optional<AsFacetModel> facetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(facetOptional.isPresent());
	}

	@Test
	public void createFacetWithoutUid() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		// then
		assertNotNull(facet.getUid());
		assertFalse(facet.getUid().isEmpty());
	}

	@Test
	public void createFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(createdFacetOptional.isPresent());

		final AsFacetModel createdFacet = createdFacetOptional.get();
		assertEquals(onlineCatalogVersion, createdFacet.getCatalogVersion());
		assertEquals(UID1, createdFacet.getUid());
		assertEquals(searchConfiguration, createdFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, createdFacet.getFacetType());
	}

	@Test
	public void failToCreateFacetWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(stagedCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithoutIndexProperty() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithWrongIndexProperty() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(WRONG_INDEX_PROPERTY);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithWrongIndexPropertyType() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY4);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void createMultipleFacets() throws Exception
	{
		// given
		final AsFacetModel facet1 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet1.setCatalogVersion(onlineCatalogVersion);
		facet1.setUid(UID1);
		facet1.setSearchConfiguration(searchConfiguration);
		facet1.setIndexProperty(INDEX_PROPERTY1);
		facet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsFacetModel facet2 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet2.setCatalogVersion(onlineCatalogVersion);
		facet2.setUid(UID2);
		facet2.setSearchConfiguration(searchConfiguration);
		facet2.setIndexProperty(INDEX_PROPERTY2);
		facet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(facet1);
		asConfigurationService.saveConfiguration(facet2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getFacets()).containsExactly(facet1, facet2);
	}

	@Test
	public void updateFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);
		facet.setPriority(PRIORITY1);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		final AsFacetModel createdFacet = createdFacetOptional.get();
		createdFacet.setFacetType(AsFacetType.MULTISELECT_AND);
		createdFacet.setPriority(PRIORITY2);
		asConfigurationService.saveConfiguration(createdFacet);

		final Optional<AsFacetModel> updatedFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedFacetOptional.isPresent());

		final AsFacetModel updatedFacet = updatedFacetOptional.get();
		assertEquals(onlineCatalogVersion, updatedFacet.getCatalogVersion());
		assertEquals(UID1, updatedFacet.getUid());
		assertEquals(searchConfiguration, updatedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedFacet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, updatedFacet.getFacetType());
		assertEquals(PRIORITY2, updatedFacet.getPriority());
	}

	@Test
	public void failToUpdateFacetIndexProperty() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdPromotedItemOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		final AsFacetModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void cloneFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID2);
		promotedValue.setFacetConfiguration(facet);
		promotedValue.setValue(FACET_VALUE2);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID3);
		excludedValue.setFacetConfiguration(facet);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);

		asConfigurationService.refreshConfiguration(facet);

		final AsFacetModel clonedFacet = asConfigurationService.cloneConfiguration(facet);
		clonedFacet.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(clonedFacet);

		// then
		assertEquals(facet.getCatalogVersion(), clonedFacet.getCatalogVersion());
		assertNotEquals(facet.getUid(), clonedFacet.getUid());
		assertEquals(facet.getSearchConfiguration(), clonedFacet.getSearchConfiguration());
		assertEquals(facet.getFacetType(), clonedFacet.getFacetType());

		assertThat(clonedFacet.getPromotedValues()).isNotNull().hasSize(1);
		assertThat(clonedFacet.getExcludedValues()).isNotNull().hasSize(1);

		final AsPromotedFacetValueModel clonedPromotedValue = clonedFacet.getPromotedValues().get(0);
		assertEquals(promotedValue.getCatalogVersion(), clonedPromotedValue.getCatalogVersion());
		assertNotEquals(promotedValue.getUid(), clonedPromotedValue.getUid());
		assertNotEquals(promotedValue.getFacetConfiguration(), clonedPromotedValue.getFacetConfiguration());
		assertEquals(promotedValue.getValue(), clonedPromotedValue.getValue());

		final AsExcludedFacetValueModel clonedExcludedValue = clonedFacet.getExcludedValues().get(0);
		assertEquals(excludedValue.getCatalogVersion(), clonedExcludedValue.getCatalogVersion());
		assertNotEquals(excludedValue.getUid(), clonedExcludedValue.getUid());
		assertNotEquals(excludedValue.getFacetConfiguration(), clonedExcludedValue.getFacetConfiguration());
		assertEquals(excludedValue.getValue(), clonedExcludedValue.getValue());
	}

	@Test
	public void removeFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		final AsFacetModel createdFacet = createdFacetOptional.get();
		asConfigurationService.removeConfiguration(createdFacet);

		final Optional<AsFacetModel> removedFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(removedFacetOptional.isPresent());
	}

	@Test
	public void facetIsNotCorrupted() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		final boolean corrupted = facet.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterFacet() throws Exception
	{
		// given
		final AsFacetModel facet1 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet1.setCatalogVersion(onlineCatalogVersion);
		facet1.setUid(UID1);
		facet1.setSearchConfiguration(searchConfiguration);
		facet1.setIndexProperty(INDEX_PROPERTY1);
		facet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsFacetModel facet2 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet2.setCatalogVersion(onlineCatalogVersion);
		facet2.setUid(UID2);
		facet2.setSearchConfiguration(searchConfiguration);
		facet2.setIndexProperty(INDEX_PROPERTY2);
		facet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(facet1);
		asConfigurationService.saveConfiguration(facet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.FACETS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getFacets()).containsExactly(facet2, facet1);
	}

	@Test
	public void rankBeforeFacet() throws Exception
	{
		// given
		final AsFacetModel facet1 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet1.setCatalogVersion(onlineCatalogVersion);
		facet1.setUid(UID1);
		facet1.setSearchConfiguration(searchConfiguration);
		facet1.setIndexProperty(INDEX_PROPERTY1);
		facet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsFacetModel facet2 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet2.setCatalogVersion(onlineCatalogVersion);
		facet2.setUid(UID2);
		facet2.setSearchConfiguration(searchConfiguration);
		facet2.setIndexProperty(INDEX_PROPERTY2);
		facet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(facet1);
		asConfigurationService.saveConfiguration(facet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.FACETS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getFacets()).containsExactly(facet2, facet1);
	}
}
