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
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostOperator;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsSortsMergeMode;
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsBoostRuleModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedItemModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedSortModel;
import de.hybris.platform.adaptivesearch.model.AsFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedItemModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedSortModel;
import de.hybris.platform.adaptivesearch.model.AsSortModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsCategoryAwareSearchConfigurationSynchronizationTest extends AbstractAsSynchronizationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SEARCH_PROFILE_CODE = "searchProfile";

	private static final String UID1 = "c5be51d4-5649-4a7f-b27d-c18758c5dfff";
	private static final String UID2 = "397bad42-150c-472b-be12-9bcb81ec029e";
	private static final String UID3 = "0a3103a2-5c6d-4617-8c72-73b1ffc0e7fd";
	private static final String UID4 = "59c2f70d-c74e-4d6d-ba84-5832dd123437";
	private static final String UID5 = "9a501888-1602-4e4e-8d29-088028bbad31";
	private static final String UID6 = "d5ecbe66-e96a-4e6e-af89-7a628e9ed66a";
	private static final String UID7 = "6029f3b1-1e29-4366-904b-750b3f4be49a";
	private static final String UID8 = "d6513009-a050-412f-870a-32f09a06bc52";
	private static final String UID9 = "437265fd-9779-4eb7-853f-ddfeae8d1184";
	private static final String UID10 = "8fa55024-42b6-4e50-9d37-da67526439dd";

	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY3 = "property3";

	private static final String VALUE1 = "value1";

	private static final Float BOOST1 = Float.valueOf(1.1f);

	private static final String SORT_CODE1 = "code1";
	private static final String SORT_CODE2 = "code2";
	private static final String SORT_CODE3 = "code3";

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
	private AsCategoryAwareSearchProfileModel searchProfile;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asCategoryAwareSearchProfile.impex", CharEncoding.UTF_8);

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AsCategoryAwareSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, SEARCH_PROFILE_CODE);
		searchProfile = searchProfileOptional.get();
	}

	@Test
	public void searchConfigurationNotFoundBeforeSynchronization() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(searchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);

		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertFalse(searchConfigurationOptional.isPresent());
	}

	@Test
	public void synchronizeNewSearchConfiguration() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(searchProfile);
		searchConfiguration.setUid(UID1);

		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setUid(UID2);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(stagedCatalogVersion);
		facet.setUid(UID3);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);
		facet.setFacetType(AsFacetType.REFINE);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(stagedCatalogVersion);
		excludedFacet.setUid(UID4);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY3);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(stagedCatalogVersion);
		promotedItem.setUid(UID5);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(stagedCatalogVersion);
		excludedItem.setUid(UID6);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(stagedCatalogVersion);
		boostRule.setUid(UID7);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(stagedCatalogVersion);
		promotedSort.setUid(UID8);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(SORT_CODE1);

		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setUid(UID9);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(SORT_CODE2);

		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(stagedCatalogVersion);
		excludedSort.setUid(UID10);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(SORT_CODE3);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);
		asConfigurationService.saveConfiguration(promotedItem);
		asConfigurationService.saveConfiguration(excludedItem);
		asConfigurationService.saveConfiguration(boostRule);
		asConfigurationService.saveConfiguration(promotedSort);
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(excludedSort);
		asConfigurationService.refreshConfiguration(searchConfiguration);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(searchConfigurationOptional.isPresent());

		final AsCategoryAwareSearchConfigurationModel synchronizedSearchConfiguration = searchConfigurationOptional.get();
		assertSynchronized(searchConfiguration, synchronizedSearchConfiguration, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeUpdatedSearchConfiguration() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(searchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		searchConfiguration.setFacetsMergeMode(AsFacetsMergeMode.REPLACE);
		searchConfiguration.setPromotedFacets(Collections.emptyList());
		searchConfiguration.setFacets(Collections.emptyList());
		searchConfiguration.setExcludedFacets(Collections.emptyList());
		searchConfiguration.setBoostItemsMergeMode(AsBoostItemsMergeMode.REPLACE);
		searchConfiguration.setPromotedItems(Collections.emptyList());
		searchConfiguration.setExcludedItems(Collections.emptyList());
		searchConfiguration.setBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		searchConfiguration.setBoostRules(Collections.emptyList());
		searchConfiguration.setSortsMergeMode(AsSortsMergeMode.REPLACE);
		searchConfiguration.setPromotedSorts(Collections.emptyList());
		searchConfiguration.setSorts(Collections.emptyList());
		searchConfiguration.setExcludedSorts(Collections.emptyList());

		asConfigurationService.saveConfiguration(searchConfiguration);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(searchConfigurationOptional.isPresent());

		final AsCategoryAwareSearchConfigurationModel synchronizedSearchConfiguration = searchConfigurationOptional.get();
		assertSynchronized(searchConfiguration, synchronizedSearchConfiguration, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeRemovedSearchConfiguration() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(searchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(searchConfiguration);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertFalse(searchConfigurationOptional.isPresent());
	}
}
