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
package de.hybris.platform.adaptivesearch.integration.synchronization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.enums.AsBoostOperator;
import de.hybris.platform.adaptivesearch.enums.AsBoostType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsBoostRuleModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsBoostRuleSynchronizationTest extends AbstractAsSynchronizationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SEARCH_PROFILE_CODE = "searchProfile";
	private static final String SEARCH_CONFIGURATION_UID = "searchConfiguration";

	private static final String UID1 = "cde588ec-d453-48bd-a3b1-b9aa00402256";

	private static final String INDEX_PROPERTY1 = "property1";

	private static final String VALUE1 = "value1";

	private static final Float BOOST1 = Float.valueOf(1.1f);
	private static final Float BOOST2 = Float.valueOf(1.2f);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AbstractAsSearchProfileModel searchProfile;
	private AbstractAsConfigurableSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asSimpleSearchProfile.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asSimpleSearchConfiguration.impex", CharEncoding.UTF_8);

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AbstractAsSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, SEARCH_PROFILE_CODE);
		searchProfile = searchProfileOptional.get();

		final Optional<AbstractAsConfigurableSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(stagedCatalogVersion, SEARCH_CONFIGURATION_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void boostRuleNotFoundBeforeSynchronization() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(stagedCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);

		final Optional<AsBoostRuleModel> synchronizedBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedBoostRuleOptional.isPresent());
	}

	@Test
	public void synchronizeNewBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(stagedCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsBoostRuleModel> synchronizedBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedBoostRuleOptional.isPresent());

		final AsBoostRuleModel synchronizedBoostRule = synchronizedBoostRuleOptional.get();
		assertSynchronized(boostRule, synchronizedBoostRule);
	}

	@Test
	public void synchronizeUpdatedBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(stagedCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		boostRule.setBoostType(AsBoostType.ADDITIVE);
		boostRule.setBoost(BOOST2);

		asConfigurationService.saveConfiguration(boostRule);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsBoostRuleModel> synchronizedBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedBoostRuleOptional.isPresent());

		final AsBoostRuleModel synchronizedBoostRule = synchronizedBoostRuleOptional.get();
		assertSynchronized(boostRule, synchronizedBoostRule);
	}

	@Test
	public void synchronizeRemovedBoostRule() throws Exception
	{
		// given
		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(stagedCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(boostRule);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(boostRule);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsBoostRuleModel> synchronizedBoostRuleOptional = asConfigurationService
				.getConfigurationForUid(AsBoostRuleModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedBoostRuleOptional.isPresent());
	}
}
