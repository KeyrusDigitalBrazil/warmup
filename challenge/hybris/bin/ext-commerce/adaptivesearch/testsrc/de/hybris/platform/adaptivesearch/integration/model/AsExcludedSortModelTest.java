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
import de.hybris.platform.adaptivesearch.model.AsExcludedSortModel;
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
public class AsExcludedSortModelTest extends ServicelayerTransactionalTest
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
	public void getNonExistingExcludedSort() throws Exception
	{
		// when
		final Optional<AsExcludedSortModel> excludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(excludedSortOptional.isPresent());
	}

	@Test
	public void createExcludedSortWithoutUid() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);

		// then
		assertNotNull(excludedSort.getUid());
		assertFalse(excludedSort.getUid().isEmpty());
	}

	@Test
	public void createExcludedSort() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);

		final Optional<AsExcludedSortModel> createdExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdExcludedSortOptional.isPresent());

		final AsExcludedSortModel createdExcludedSort = createdExcludedSortOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedSort.getCatalogVersion());
		assertEquals(UID1, createdExcludedSort.getUid());
		assertEquals(searchConfiguration, createdExcludedSort.getSearchConfiguration());
		assertEquals(CODE1, createdExcludedSort.getCode());
		assertEquals(NAME1, createdExcludedSort.getName());
	}

	@Test
	public void failToCreateExcludedSortWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(stagedCatalogVersion);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
	}

	@Test
	public void failToCreateExcludedSortWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
	}

	@Test
	public void failToCreateExcludedSortWithoutCode() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
	}

	@Test
	public void createMultipleExcludedSorts() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort1 = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort1.setCatalogVersion(onlineCatalogVersion);
		excludedSort1.setUid(UID1);
		excludedSort1.setSearchConfiguration(searchConfiguration);
		excludedSort1.setCode(CODE1);
		excludedSort1.setName(NAME1);

		final AsExcludedSortModel excludedSort2 = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort2.setCatalogVersion(onlineCatalogVersion);
		excludedSort2.setUid(UID2);
		excludedSort2.setSearchConfiguration(searchConfiguration);
		excludedSort2.setCode(CODE2);
		excludedSort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(excludedSort1);
		asConfigurationService.saveConfiguration(excludedSort2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getExcludedSorts()).containsExactly(excludedSort1, excludedSort2);
	}

	@Test
	public void updateExcludedSort() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);

		final Optional<AsExcludedSortModel> createdExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		final AsExcludedSortModel createdExcludedSort = createdExcludedSortOptional.get();
		createdExcludedSort.setCode(CODE2);
		createdExcludedSort.setName(NAME2);
		asConfigurationService.saveConfiguration(createdExcludedSort);

		final Optional<AsExcludedSortModel> updatedExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedExcludedSortOptional.isPresent());

		final AsExcludedSortModel updatedExcludedSort = updatedExcludedSortOptional.get();
		assertEquals(onlineCatalogVersion, updatedExcludedSort.getCatalogVersion());
		assertEquals(UID1, updatedExcludedSort.getUid());
		assertEquals(searchConfiguration, updatedExcludedSort.getSearchConfiguration());
		assertEquals(CODE2, updatedExcludedSort.getCode());
		assertEquals(NAME2, updatedExcludedSort.getName());
	}

	@Test
	public void cloneExcludedSort() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(excludedSort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
		asConfigurationService.saveConfiguration(sortExpression);

		asConfigurationService.refreshConfiguration(excludedSort);

		final AsExcludedSortModel clonedExcludedSort = asConfigurationService.cloneConfiguration(excludedSort);
		clonedExcludedSort.setCode(CODE2);
		clonedExcludedSort.setName(NAME2);
		asConfigurationService.saveConfiguration(clonedExcludedSort);

		// then
		assertNotSame(excludedSort, clonedExcludedSort);

		assertEquals(excludedSort.getCatalogVersion(), clonedExcludedSort.getCatalogVersion());
		assertNotEquals(excludedSort.getUid(), clonedExcludedSort.getUid());
		assertEquals(excludedSort.getSearchConfiguration(), clonedExcludedSort.getSearchConfiguration());

		assertThat(clonedExcludedSort.getExpressions()).isNotNull().hasSize(1);

		final AsSortExpressionModel clonedSortExpression = clonedExcludedSort.getExpressions().get(0);
		assertEquals(sortExpression.getCatalogVersion(), clonedSortExpression.getCatalogVersion());
		assertNotEquals(sortExpression.getUid(), clonedSortExpression.getUid());
		assertNotEquals(sortExpression.getSortConfiguration(), clonedSortExpression.getSortConfiguration());
		assertEquals(sortExpression.getExpression(), clonedSortExpression.getExpression());
		assertEquals(sortExpression.getOrder(), clonedSortExpression.getOrder());
	}

	@Test
	public void removeExcludedSort() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);

		final Optional<AsExcludedSortModel> createdExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		final AsExcludedSortModel createdExcludedSort = createdExcludedSortOptional.get();
		asConfigurationService.removeConfiguration(createdExcludedSort);

		final Optional<AsExcludedSortModel> removedExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedExcludedSortOptional.isPresent());
	}

	@Test
	public void excludedSortIsNotCorrupted() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		final boolean corrupted = excludedSort.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterExcludedSort() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort1 = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort1.setCatalogVersion(onlineCatalogVersion);
		excludedSort1.setUid(UID1);
		excludedSort1.setSearchConfiguration(searchConfiguration);
		excludedSort1.setCode(CODE1);
		excludedSort1.setName(NAME1);

		final AsExcludedSortModel excludedSort2 = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort2.setCatalogVersion(onlineCatalogVersion);
		excludedSort2.setUid(UID2);
		excludedSort2.setSearchConfiguration(searchConfiguration);
		excludedSort2.setCode(CODE2);
		excludedSort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(excludedSort1);
		asConfigurationService.saveConfiguration(excludedSort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDSORTS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedSorts()).containsExactly(excludedSort2, excludedSort1);
	}

	@Test
	public void rankBeforeExcludedSort() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort1 = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort1.setCatalogVersion(onlineCatalogVersion);
		excludedSort1.setUid(UID1);
		excludedSort1.setSearchConfiguration(searchConfiguration);
		excludedSort1.setCode(CODE1);
		excludedSort1.setName(NAME1);

		final AsExcludedSortModel excludedSort2 = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort2.setCatalogVersion(onlineCatalogVersion);
		excludedSort2.setUid(UID2);
		excludedSort2.setSearchConfiguration(searchConfiguration);
		excludedSort2.setCode(CODE2);
		excludedSort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(excludedSort1);
		asConfigurationService.saveConfiguration(excludedSort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDSORTS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedSorts()).containsExactly(excludedSort2, excludedSort1);
	}
}
