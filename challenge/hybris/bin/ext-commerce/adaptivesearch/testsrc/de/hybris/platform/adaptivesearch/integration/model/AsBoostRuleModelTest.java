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
import de.hybris.platform.adaptivesearch.enums.AsBoostOperator;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsBoostRuleModel;
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
public class AsBoostRuleModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY3 = "property3";
	private static final String WRONG_INDEX_PROPERTY = "testPropertyError";

	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";
	private static final String NUMERIC_VALUE = "1";

	private static final Float BOOST1 = Float.valueOf(1.1f);
	private static final Float BOOST2 = Float.valueOf(1.2f);

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
		importCsv("/adaptivesearch/test/integration/model/asBoostRuleConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void getNonExistingBoostRule() throws Exception
	{
		// when
		final Optional<AsBoostRuleModel> boostRuleOptional = asConfigurationService.getConfigurationForUid(AsBoostRuleModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(boostRuleOptional.isPresent());
	}

	@Test
	public void createBoostRuleWithoutUid() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);

		// then
		assertNotNull(boostRule.getUid());
		assertFalse(boostRule.getUid().isEmpty());
	}

	@Test
	public void createBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);

		final Optional<AsBoostRuleModel> createdBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdBoostRuleOptional.isPresent());

		final AsBoostRuleModel createdBoostRule = createdBoostRuleOptional.get();
		assertEquals(onlineCatalogVersion, createdBoostRule.getCatalogVersion());
		assertEquals(UID1, createdBoostRule.getUid());
		assertEquals(searchConfiguration, createdBoostRule.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdBoostRule.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, createdBoostRule.getOperator());
		assertEquals(VALUE1, createdBoostRule.getValue());
		assertEquals(BOOST1, createdBoostRule.getBoost());
	}

	@Test
	public void failToCreateBoostRuleWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(stagedCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setValue(VALUE1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void failToCreateBoostRuleWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void failToCreateBoostRuleWithoutIndexProperty() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setValue(VALUE1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void failToCreateBoostRuleWithWrongIndexProperty() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(WRONG_INDEX_PROPERTY);
		boostRule.setValue(VALUE1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void failToCreateBoostRuleWithWrongBoostOperator() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.GREATER_THAN);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void failToCreateBoostRuleWithNumericValueForStringIndexProperty() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY2);
		boostRule.setOperator(AsBoostOperator.MATCH);
		boostRule.setValue(NUMERIC_VALUE);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void failToCreateBoostRuleWithStringValueForNumericIndexProperty() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY3);
		boostRule.setOperator(AsBoostOperator.LESS_THAN);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule);
	}

	@Test
	public void createMultipleBoostRules() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule1 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule1.setCatalogVersion(onlineCatalogVersion);
		boostRule1.setUid(UID1);
		boostRule1.setSearchConfiguration(searchConfiguration);
		boostRule1.setIndexProperty(INDEX_PROPERTY1);
		boostRule1.setOperator(AsBoostOperator.EQUAL);
		boostRule1.setValue(VALUE1);
		boostRule1.setBoost(BOOST1);

		final AsBoostRuleModel boostRule2 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule2.setCatalogVersion(onlineCatalogVersion);
		boostRule2.setUid(UID2);
		boostRule2.setSearchConfiguration(searchConfiguration);
		boostRule2.setIndexProperty(INDEX_PROPERTY2);
		boostRule2.setOperator(AsBoostOperator.EQUAL);
		boostRule2.setValue(VALUE2);
		boostRule2.setBoost(BOOST2);

		// when
		asConfigurationService.saveConfiguration(boostRule1);
		asConfigurationService.saveConfiguration(boostRule2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getBoostRules()).containsExactly(boostRule1, boostRule2);
	}

	@Test
	public void failToCreateMultipleBoostRulesWithSameUid() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule1 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule1.setCatalogVersion(onlineCatalogVersion);
		boostRule1.setUid(UID1);
		boostRule1.setSearchConfiguration(searchConfiguration);
		boostRule1.setIndexProperty(INDEX_PROPERTY1);
		boostRule1.setOperator(AsBoostOperator.EQUAL);
		boostRule1.setValue(VALUE1);
		boostRule1.setBoost(BOOST1);

		final AsBoostRuleModel boostRule2 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule2.setCatalogVersion(onlineCatalogVersion);
		boostRule2.setUid(UID1);
		boostRule2.setSearchConfiguration(searchConfiguration);
		boostRule2.setIndexProperty(INDEX_PROPERTY2);
		boostRule2.setOperator(AsBoostOperator.EQUAL);
		boostRule2.setValue(VALUE2);
		boostRule2.setBoost(BOOST2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(boostRule1);
		asConfigurationService.saveConfiguration(boostRule2);
	}

	@Test
	public void updateBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);

		final Optional<AsBoostRuleModel> createdBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		final AsBoostRuleModel createdBoostRule = createdBoostRuleOptional.get();
		createdBoostRule.setValue(VALUE2);
		createdBoostRule.setBoost(BOOST2);
		asConfigurationService.saveConfiguration(createdBoostRule);

		final Optional<AsBoostRuleModel> updatedBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedBoostRuleOptional.isPresent());

		final AsBoostRuleModel updatedBoostRule = updatedBoostRuleOptional.get();
		assertEquals(onlineCatalogVersion, updatedBoostRule.getCatalogVersion());
		assertEquals(UID1, updatedBoostRule.getUid());
		assertEquals(searchConfiguration, updatedBoostRule.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedBoostRule.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, updatedBoostRule.getOperator());
		assertEquals(VALUE2, updatedBoostRule.getValue());
		assertEquals(BOOST2, updatedBoostRule.getBoost());
	}

	@Test
	public void cloneBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);

		final AsBoostRuleModel clonedRuleModel = asConfigurationService.cloneConfiguration(boostRule);
		clonedRuleModel.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(clonedRuleModel);

		assertEquals(boostRule.getCatalogVersion(), clonedRuleModel.getCatalogVersion());
		assertNotEquals(boostRule.getUid(), clonedRuleModel.getUid());
		assertEquals(boostRule.getSearchConfiguration(), clonedRuleModel.getSearchConfiguration());
		assertEquals(boostRule.getOperator(), clonedRuleModel.getOperator());
		assertEquals(boostRule.getValue(), clonedRuleModel.getValue());
		assertEquals(boostRule.getBoost(), clonedRuleModel.getBoost());
	}

	@Test
	public void removeBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);

		final Optional<AsBoostRuleModel> createdBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		final AsBoostRuleModel createdBoostRule = createdBoostRuleOptional.get();
		asConfigurationService.removeConfiguration(createdBoostRule);

		final Optional<AsBoostRuleModel> removedBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedBoostRuleOptional.isPresent());
	}

	@Test
	public void boostRuleIsNotCorrupted() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		final boolean corrupted = boostRule.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule1 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule1.setCatalogVersion(onlineCatalogVersion);
		boostRule1.setUid(UID1);
		boostRule1.setSearchConfiguration(searchConfiguration);
		boostRule1.setIndexProperty(INDEX_PROPERTY1);
		boostRule1.setOperator(AsBoostOperator.EQUAL);
		boostRule1.setValue(VALUE1);
		boostRule1.setBoost(BOOST1);

		final AsBoostRuleModel boostRule2 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule2.setCatalogVersion(onlineCatalogVersion);
		boostRule2.setUid(UID2);
		boostRule2.setSearchConfiguration(searchConfiguration);
		boostRule2.setIndexProperty(INDEX_PROPERTY2);
		boostRule2.setOperator(AsBoostOperator.EQUAL);
		boostRule2.setValue(VALUE2);
		boostRule2.setBoost(BOOST2);

		// when
		asConfigurationService.saveConfiguration(boostRule1);
		asConfigurationService.saveConfiguration(boostRule2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.BOOSTRULES, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getBoostRules()).containsExactly(boostRule2, boostRule1);
	}

	@Test
	public void rankBeforeBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule1 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule1.setCatalogVersion(onlineCatalogVersion);
		boostRule1.setUid(UID1);
		boostRule1.setSearchConfiguration(searchConfiguration);
		boostRule1.setIndexProperty(INDEX_PROPERTY1);
		boostRule1.setOperator(AsBoostOperator.EQUAL);
		boostRule1.setValue(VALUE1);
		boostRule1.setBoost(BOOST1);

		final AsBoostRuleModel boostRule2 = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule2.setCatalogVersion(onlineCatalogVersion);
		boostRule2.setUid(UID2);
		boostRule2.setSearchConfiguration(searchConfiguration);
		boostRule2.setIndexProperty(INDEX_PROPERTY2);
		boostRule2.setOperator(AsBoostOperator.EQUAL);
		boostRule2.setValue(VALUE2);
		boostRule2.setBoost(BOOST2);

		// when
		asConfigurationService.saveConfiguration(boostRule1);
		asConfigurationService.saveConfiguration(boostRule2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.BOOSTRULES, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getBoostRules()).containsExactly(boostRule2, boostRule1);
	}
}
