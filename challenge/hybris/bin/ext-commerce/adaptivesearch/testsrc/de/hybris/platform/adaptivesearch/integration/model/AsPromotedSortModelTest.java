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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.data.AsRankChange;
import de.hybris.platform.adaptivesearch.data.AsRankChangeType;
import de.hybris.platform.adaptivesearch.enums.AsSortOrder;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedSortModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSortExpressionModel;
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
public class AsPromotedSortModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "d3299865-5a12-4985-bcde-0726f302b6f1";
	private static final String UID2 = "381c1991-65d5-4c60-bff5-c0761842d60d";

	private static final String INDEX_PROPERTY1 = "property1";

	private static final String CODE1 = "code1";
	private static final String CODE2 = "code2";

	private static final String NAME1 = "name1";
	private static final String NAME2 = "name2";

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
		importCsv("/adaptivesearch/test/integration/model/asSortConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void getNonExistingPromotedSort() throws Exception
	{
		// when
		final Optional<AsPromotedSortModel> promotedSortOptional = asConfigurationService.getConfigurationForUid(
				AsPromotedSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(promotedSortOptional.isPresent());
	}

	@Test
	public void createPromotedSortWithoutUid() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(promotedSort);

		// then
		assertNotNull(promotedSort.getUid());
		assertFalse(promotedSort.getUid().isEmpty());
	}

	@Test
	public void createPromotedSort() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(promotedSort);

		final Optional<AsPromotedSortModel> createdPromotedSortOptional = asConfigurationService.getConfigurationForUid(
				AsPromotedSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(createdPromotedSortOptional.isPresent());

		final AsPromotedSortModel createdPromotedSort = createdPromotedSortOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedSort.getCatalogVersion());
		assertEquals(UID1, createdPromotedSort.getUid());
		assertEquals(searchConfiguration, createdPromotedSort.getSearchConfiguration());
		assertEquals(CODE1, createdPromotedSort.getCode());
		assertEquals(NAME1, createdPromotedSort.getName());
	}

	@Test
	public void failToCreatePromotedSortWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(stagedCatalogVersion);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
	}

	@Test
	public void failToCreatePromotedSortWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
	}

	@Test
	public void failToCreatePromotedSortWithoutCode() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
	}

	@Test
	public void createMultiplePromotedSorts() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort1 = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort1.setCatalogVersion(onlineCatalogVersion);
		promotedSort1.setUid(UID1);
		promotedSort1.setSearchConfiguration(searchConfiguration);
		promotedSort1.setCode(CODE1);
		promotedSort1.setName(NAME1);

		final AsPromotedSortModel promotedSort2 = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort2.setCatalogVersion(onlineCatalogVersion);
		promotedSort2.setUid(UID2);
		promotedSort2.setSearchConfiguration(searchConfiguration);
		promotedSort2.setCode(CODE2);
		promotedSort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(promotedSort1);
		asConfigurationService.saveConfiguration(promotedSort2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getPromotedSorts()).containsExactly(promotedSort1, promotedSort2);
	}

	@Test
	public void updatePromotedSort() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(promotedSort);

		final Optional<AsPromotedSortModel> createdPromotedSortOptional = asConfigurationService.getConfigurationForUid(
				AsPromotedSortModel.class,
				onlineCatalogVersion, UID1);

		final AsPromotedSortModel createdPromotedSort = createdPromotedSortOptional.get();
		createdPromotedSort.setCode(CODE2);
		createdPromotedSort.setName(NAME2);
		asConfigurationService.saveConfiguration(createdPromotedSort);

		final Optional<AsPromotedSortModel> updatedPromotedSortOptional = asConfigurationService.getConfigurationForUid(
				AsPromotedSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedPromotedSortOptional.isPresent());

		final AsPromotedSortModel updatedPromotedSort = updatedPromotedSortOptional.get();
		assertEquals(onlineCatalogVersion, updatedPromotedSort.getCatalogVersion());
		assertEquals(UID1, updatedPromotedSort.getUid());
		assertEquals(searchConfiguration, updatedPromotedSort.getSearchConfiguration());
		assertEquals(CODE2, updatedPromotedSort.getCode());
		assertEquals(NAME2, updatedPromotedSort.getName());
	}

	@Test
	public void clonePromotedSort() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(promotedSort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
		asConfigurationService.saveConfiguration(sortExpression);

		asConfigurationService.refreshConfiguration(promotedSort);

		final AsPromotedSortModel clonedPromotedSort = asConfigurationService.cloneConfiguration(promotedSort);
		clonedPromotedSort.setCode(CODE2);
		clonedPromotedSort.setName(NAME2);
		asConfigurationService.saveConfiguration(clonedPromotedSort);

		// then
		assertNotSame(promotedSort, clonedPromotedSort);

		assertEquals(promotedSort.getCatalogVersion(), clonedPromotedSort.getCatalogVersion());
		assertNotEquals(promotedSort.getUid(), clonedPromotedSort.getUid());
		assertEquals(promotedSort.getSearchConfiguration(), clonedPromotedSort.getSearchConfiguration());

		assertThat(clonedPromotedSort.getExpressions()).isNotNull().hasSize(1);

		final AsSortExpressionModel clonedSortExpression = clonedPromotedSort.getExpressions().get(0);
		assertEquals(sortExpression.getCatalogVersion(), clonedSortExpression.getCatalogVersion());
		assertNotEquals(sortExpression.getUid(), clonedSortExpression.getUid());
		assertNotEquals(sortExpression.getSortConfiguration(), clonedSortExpression.getSortConfiguration());
		assertEquals(sortExpression.getExpression(), clonedSortExpression.getExpression());
		assertEquals(sortExpression.getOrder(), clonedSortExpression.getOrder());
	}

	@Test
	public void removePromotedSort() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(promotedSort);

		final Optional<AsPromotedSortModel> createdPromotedSortOptional = asConfigurationService.getConfigurationForUid(
				AsPromotedSortModel.class,
				onlineCatalogVersion, UID1);

		final AsPromotedSortModel createdPromotedSort = createdPromotedSortOptional.get();
		asConfigurationService.removeConfiguration(createdPromotedSort);

		final Optional<AsPromotedSortModel> removedPromotedSortOptional = asConfigurationService.getConfigurationForUid(
				AsPromotedSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(removedPromotedSortOptional.isPresent());
	}

	@Test
	public void promotedSortIsNotCorrupted() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// when
		final boolean corrupted = promotedSort.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterPromotedSort() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort1 = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort1.setCatalogVersion(onlineCatalogVersion);
		promotedSort1.setUid(UID1);
		promotedSort1.setSearchConfiguration(searchConfiguration);
		promotedSort1.setCode(CODE1);
		promotedSort1.setName(NAME1);

		final AsPromotedSortModel promotedSort2 = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort2.setCatalogVersion(onlineCatalogVersion);
		promotedSort2.setUid(UID2);
		promotedSort2.setSearchConfiguration(searchConfiguration);
		promotedSort2.setCode(CODE2);
		promotedSort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(promotedSort1);
		asConfigurationService.saveConfiguration(promotedSort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDSORTS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedSorts()).containsExactly(promotedSort2, promotedSort1);
	}

	@Test
	public void rankBeforePromotedSort() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort1 = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort1.setCatalogVersion(onlineCatalogVersion);
		promotedSort1.setUid(UID1);
		promotedSort1.setSearchConfiguration(searchConfiguration);
		promotedSort1.setCode(CODE1);
		promotedSort1.setName(NAME1);

		final AsPromotedSortModel promotedSort2 = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort2.setCatalogVersion(onlineCatalogVersion);
		promotedSort2.setUid(UID2);
		promotedSort2.setSearchConfiguration(searchConfiguration);
		promotedSort2.setCode(CODE2);
		promotedSort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(promotedSort1);
		asConfigurationService.saveConfiguration(promotedSort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDSORTS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedSorts()).containsExactly(promotedSort2, promotedSort1);
	}
}
