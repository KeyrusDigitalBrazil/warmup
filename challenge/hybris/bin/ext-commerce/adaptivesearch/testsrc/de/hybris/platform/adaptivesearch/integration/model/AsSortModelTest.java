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
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSortExpressionModel;
import de.hybris.platform.adaptivesearch.model.AsSortModel;
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
public class AsSortModelTest extends ServicelayerTransactionalTest
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
	public void getNonExistingSort() throws Exception
	{
		// when
		final Optional<AsSortModel> sortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(sortOptional.isPresent());
	}

	@Test
	public void createSortWithoutUid() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);

		// then
		assertNotNull(sort.getUid());
		assertFalse(sort.getUid().isEmpty());
	}

	@Test
	public void createSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> createdSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSortOptional.isPresent());

		final AsSortModel createdSort = createdSortOptional.get();
		assertEquals(onlineCatalogVersion, createdSort.getCatalogVersion());
		assertEquals(UID1, createdSort.getUid());
		assertEquals(searchConfiguration, createdSort.getSearchConfiguration());
		assertEquals(CODE1, createdSort.getCode());
		assertEquals(NAME1, createdSort.getName());
	}

	@Test
	public void failToCreateSortWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void failToCreateSortWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void failToCreateSortWithoutCode() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setName(NAME1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void createMultipleSorts() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(CODE1);
		sort1.setName(NAME1);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(CODE2);
		sort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getSorts()).containsExactly(sort1, sort2);
	}

	@Test
	public void updateSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> createdSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		final AsSortModel createdSort = createdSortOptional.get();
		createdSort.setCode(CODE2);
		createdSort.setName(NAME2);
		asConfigurationService.saveConfiguration(createdSort);

		final Optional<AsSortModel> updatedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedSortOptional.isPresent());

		final AsSortModel updatedSort = updatedSortOptional.get();
		assertEquals(onlineCatalogVersion, updatedSort.getCatalogVersion());
		assertEquals(UID1, updatedSort.getUid());
		assertEquals(searchConfiguration, updatedSort.getSearchConfiguration());
		assertEquals(CODE2, updatedSort.getCode());
		assertEquals(NAME2, updatedSort.getName());
	}

	@Test
	public void cloneSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(sortExpression);

		asConfigurationService.refreshConfiguration(sort);

		final AsSortModel clonedSort = asConfigurationService.cloneConfiguration(sort);
		clonedSort.setCode(CODE2);
		clonedSort.setName(NAME2);
		asConfigurationService.saveConfiguration(clonedSort);

		// then
		assertNotSame(sort, clonedSort);

		assertEquals(sort.getCatalogVersion(), clonedSort.getCatalogVersion());
		assertNotEquals(sort.getUid(), clonedSort.getUid());
		assertEquals(sort.getSearchConfiguration(), clonedSort.getSearchConfiguration());

		assertThat(clonedSort.getExpressions()).isNotNull().hasSize(1);

		final AsSortExpressionModel clonedSortExpression = clonedSort.getExpressions().get(0);
		assertEquals(sortExpression.getCatalogVersion(), clonedSortExpression.getCatalogVersion());
		assertNotEquals(sortExpression.getUid(), clonedSortExpression.getUid());
		assertNotEquals(sortExpression.getSortConfiguration(), clonedSortExpression.getSortConfiguration());
		assertEquals(sortExpression.getExpression(), clonedSortExpression.getExpression());
		assertEquals(sortExpression.getOrder(), clonedSortExpression.getOrder());
	}

	@Test
	public void removeSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> createdSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		final AsSortModel createdSort = createdSortOptional.get();
		asConfigurationService.removeConfiguration(createdSort);

		final Optional<AsSortModel> removedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(removedSortOptional.isPresent());
	}

	@Test
	public void sortIsNotCorrupted() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		final boolean corrupted = sort.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterSort() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(CODE1);
		sort1.setName(NAME1);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(CODE2);
		sort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.SORTS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getSorts()).containsExactly(sort2, sort1);
	}

	@Test
	public void rankBeforeSort() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(CODE1);
		sort1.setName(NAME1);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(CODE2);
		sort2.setName(NAME2);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.SORTS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getSorts()).containsExactly(sort2, sort1);
	}
}
