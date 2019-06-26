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
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
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
public class AsSortExpressionModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";
	private static final String SORT_CONFIGURATION_UID = "sort1";

	private static final String UID1 = "1e69e3ed-f88f-4d99-880d-dfacb0a470c6";
	private static final String UID2 = "e260f749-b006-49ae-9c0d-e13c54658bba";

	private static final String SCORE_EXPRESSION = "score";
	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY4 = "property4";
	private static final String WRONG_INDEX_PROPERTY = "testPropertyError";

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
	private AbstractAsSortConfigurationModel sortConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSortExpressionModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AbstractAsSortConfigurationModel> sortConfigurationOptional = asConfigurationService
				.getConfigurationForUid(AbstractAsSortConfigurationModel.class, onlineCatalogVersion, SORT_CONFIGURATION_UID);
		sortConfiguration = sortConfigurationOptional.get();
	}

	@Test
	public void getNonExistingSortExpression() throws Exception
	{
		// when
		final Optional<AsSortExpressionModel> sortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(sortExpressionOptional.isPresent());
	}

	@Test
	public void createSortExpressionWithoutUid() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		// then
		assertNotNull(sortExpression.getUid());
		assertFalse(sortExpression.getUid().isEmpty());
	}

	@Test
	public void createSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		final Optional<AsSortExpressionModel> createdSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSortExpressionOptional.isPresent());

		final AsSortExpressionModel createdSortExpression = createdSortExpressionOptional.get();
		assertEquals(onlineCatalogVersion, createdSortExpression.getCatalogVersion());
		assertEquals(UID1, createdSortExpression.getUid());
		assertEquals(sortConfiguration, createdSortExpression.getSortConfiguration());
		assertEquals(INDEX_PROPERTY1, createdSortExpression.getExpression());
		assertEquals(AsSortOrder.ASCENDING, createdSortExpression.getOrder());
	}

	@Test
	public void createSortExpressionWithScore() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(SCORE_EXPRESSION);
		sortExpression.setOrder(AsSortOrder.DESCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		final Optional<AsSortExpressionModel> createdSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSortExpressionOptional.isPresent());

		final AsSortExpressionModel createdSortExpression = createdSortExpressionOptional.get();
		assertEquals(onlineCatalogVersion, createdSortExpression.getCatalogVersion());
		assertEquals(UID1, createdSortExpression.getUid());
		assertEquals(sortConfiguration, createdSortExpression.getSortConfiguration());
		assertEquals(SCORE_EXPRESSION, createdSortExpression.getExpression());
		assertEquals(AsSortOrder.DESCENDING, createdSortExpression.getOrder());
	}

	@Test
	public void failToCreateSortExpressionWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(stagedCatalogVersion);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression);
	}

	@Test
	public void failToCreateSortExpressionWithoutSortConfiguration() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression);
	}

	@Test
	public void failToCreateSortExpressionWithoutExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression);
	}

	@Test
	public void failToCreateSortExpressionWithWrongExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(WRONG_INDEX_PROPERTY);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression);
	}

	@Test
	public void failToCreateSortExpressionWithNotAllowedSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(stagedCatalogVersion);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY4);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression);
	}

	@Test
	public void createSortExpressionWithoutOrder() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		final Optional<AsSortExpressionModel> createdSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSortExpressionOptional.isPresent());

		final AsSortExpressionModel createdSortExpression = createdSortExpressionOptional.get();
		assertEquals(onlineCatalogVersion, createdSortExpression.getCatalogVersion());
		assertEquals(UID1, createdSortExpression.getUid());
		assertEquals(sortConfiguration, createdSortExpression.getSortConfiguration());
		assertEquals(INDEX_PROPERTY1, createdSortExpression.getExpression());
		assertEquals(AsSortOrder.ASCENDING, createdSortExpression.getOrder());
	}

	@Test
	public void createMultipleSortExpressions() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression1 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression1.setCatalogVersion(onlineCatalogVersion);
		sortExpression1.setUid(UID1);
		sortExpression1.setSortConfiguration(sortConfiguration);
		sortExpression1.setExpression(INDEX_PROPERTY1);
		sortExpression1.setOrder(AsSortOrder.ASCENDING);

		final AsSortExpressionModel sortExpression2 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression2.setCatalogVersion(onlineCatalogVersion);
		sortExpression2.setUid(UID2);
		sortExpression2.setSortConfiguration(sortConfiguration);
		sortExpression2.setExpression(INDEX_PROPERTY2);
		sortExpression2.setOrder(AsSortOrder.DESCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression1);
		asConfigurationService.saveConfiguration(sortExpression2);

		modelService.refresh(sortConfiguration);

		// then
		assertThat(sortConfiguration.getExpressions()).containsExactly(sortExpression1, sortExpression2);
	}

	@Test
	public void failToCreateMultipleSortExpressionsWithSameUid() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression1 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression1.setCatalogVersion(onlineCatalogVersion);
		sortExpression1.setUid(UID1);
		sortExpression1.setSortConfiguration(sortConfiguration);
		sortExpression1.setExpression(INDEX_PROPERTY1);
		sortExpression1.setOrder(AsSortOrder.ASCENDING);

		final AsSortExpressionModel sortExpression2 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression2.setCatalogVersion(onlineCatalogVersion);
		sortExpression2.setUid(UID1);
		sortExpression2.setSortConfiguration(sortConfiguration);
		sortExpression2.setExpression(INDEX_PROPERTY2);
		sortExpression2.setOrder(AsSortOrder.DESCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression1);
		asConfigurationService.saveConfiguration(sortExpression2);
	}

	@Test
	public void failToCreateMultipleSortExpressionsWithSameExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression1 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression1.setCatalogVersion(onlineCatalogVersion);
		sortExpression1.setUid(UID1);
		sortExpression1.setSortConfiguration(sortConfiguration);
		sortExpression1.setExpression(INDEX_PROPERTY1);
		sortExpression1.setOrder(AsSortOrder.ASCENDING);

		final AsSortExpressionModel sortExpression2 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression2.setCatalogVersion(onlineCatalogVersion);
		sortExpression2.setUid(UID2);
		sortExpression2.setSortConfiguration(sortConfiguration);
		sortExpression2.setExpression(INDEX_PROPERTY1);
		sortExpression2.setOrder(AsSortOrder.DESCENDING);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sortExpression1);
		asConfigurationService.saveConfiguration(sortExpression2);
	}

	@Test
	public void updateSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		final Optional<AsSortExpressionModel> createdSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		final AsSortExpressionModel createdSortExpression = createdSortExpressionOptional.get();
		createdSortExpression.setOrder(AsSortOrder.DESCENDING);
		asConfigurationService.saveConfiguration(createdSortExpression);

		final Optional<AsSortExpressionModel> updatedSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedSortExpressionOptional.isPresent());

		final AsSortExpressionModel updatedSortExpression = updatedSortExpressionOptional.get();
		assertEquals(onlineCatalogVersion, updatedSortExpression.getCatalogVersion());
		assertEquals(UID1, updatedSortExpression.getUid());
		assertEquals(sortConfiguration, createdSortExpression.getSortConfiguration());
		assertEquals(INDEX_PROPERTY1, createdSortExpression.getExpression());
		assertEquals(AsSortOrder.DESCENDING, createdSortExpression.getOrder());
	}

	@Test
	public void cloneSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		final AsSortExpressionModel clonedSortExpression = asConfigurationService.cloneConfiguration(sortExpression);
		clonedSortExpression.setExpression(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(clonedSortExpression);

		// then
		assertNotSame(sortExpression, clonedSortExpression);

		assertEquals(sortExpression.getCatalogVersion(), clonedSortExpression.getCatalogVersion());
		assertNotEquals(sortExpression.getUid(), clonedSortExpression.getUid());
		assertEquals(sortExpression.getSortConfiguration(), clonedSortExpression.getSortConfiguration());
		assertEquals(sortExpression.getOrder(), clonedSortExpression.getOrder());
	}

	@Test
	public void removeSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression);

		final Optional<AsSortExpressionModel> createdSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		final AsSortExpressionModel createdSortExpression = createdSortExpressionOptional.get();
		asConfigurationService.removeConfiguration(createdSortExpression);

		final Optional<AsSortExpressionModel> removedSortExpressionOptional = asConfigurationService
				.getConfigurationForUid(AsSortExpressionModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedSortExpressionOptional.isPresent());
	}

	@Test
	public void sortExpressionIsNotCorrupted() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(onlineCatalogVersion);
		sortExpression.setUid(UID1);
		sortExpression.setSortConfiguration(sortConfiguration);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		final boolean corrupted = sortExpression.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression1 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression1.setCatalogVersion(onlineCatalogVersion);
		sortExpression1.setUid(UID1);
		sortExpression1.setSortConfiguration(sortConfiguration);
		sortExpression1.setExpression(INDEX_PROPERTY1);
		sortExpression1.setOrder(AsSortOrder.ASCENDING);

		final AsSortExpressionModel sortExpression2 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression2.setCatalogVersion(onlineCatalogVersion);
		sortExpression2.setUid(UID2);
		sortExpression2.setSortConfiguration(sortConfiguration);
		sortExpression2.setExpression(INDEX_PROPERTY2);
		sortExpression2.setOrder(AsSortOrder.DESCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression1);
		asConfigurationService.saveConfiguration(sortExpression2);

		modelService.refresh(sortConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(sortConfiguration,
				AbstractAsSortConfigurationModel.EXPRESSIONS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(sortConfiguration.getExpressions()).containsExactly(sortExpression2, sortExpression1);
	}

	@Test
	public void rankBeforeSortExpression() throws Exception
	{
		// given
		final AsSortExpressionModel sortExpression1 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression1.setCatalogVersion(onlineCatalogVersion);
		sortExpression1.setUid(UID1);
		sortExpression1.setSortConfiguration(sortConfiguration);
		sortExpression1.setExpression(INDEX_PROPERTY1);
		sortExpression1.setOrder(AsSortOrder.ASCENDING);

		final AsSortExpressionModel sortExpression2 = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression2.setCatalogVersion(onlineCatalogVersion);
		sortExpression2.setUid(UID2);
		sortExpression2.setSortConfiguration(sortConfiguration);
		sortExpression2.setExpression(INDEX_PROPERTY2);
		sortExpression2.setOrder(AsSortOrder.DESCENDING);

		// when
		asConfigurationService.saveConfiguration(sortExpression1);
		asConfigurationService.saveConfiguration(sortExpression2);

		modelService.refresh(sortConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(sortConfiguration,
				AbstractAsSortConfigurationModel.EXPRESSIONS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(sortConfiguration.getExpressions()).containsExactly(sortExpression2, sortExpression1);
	}
}
