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
import de.hybris.platform.adaptivesearch.enums.AsBoostOperator;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
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
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsCategoryAwareSearchConfigurationModelTest extends ServicelayerTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private final static String SIMPLE_SEARCH_PROFILE_CODE = "simpleProfile";
	private final static String CAT_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";

	private final static String UID1 = "b413b620-a4b8-4b3c-9234-7fc88c6d3eb1";
	private final static String UID2 = "016090d9-e5d7-4c1f-ad9e-6fb96e36d0c0";
	private final static String UID3 = "65a87b34-5e61-4920-9434-648a00f4fe51";

	private final static String CAT_10_CODE = "cat10";
	private final static String CAT_20_CODE = "cat20";

	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY3 = "property3";

	private static final String VALUE1 = "value1";
	private static final Float BOOST1 = Float.valueOf(1.1f);

	private static final String CODE1 = "code1";
	private static final String CODE2 = "code2";
	private static final String CODE3 = "code3";

	private static final String NAME1 = "name1";
	private static final String NAME2 = "name2";
	private static final String NAME3 = "name3";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AsCategoryAwareSearchProfileModel categoryAwareSearchProfile;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSearchConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AsCategoryAwareSearchProfileModel> categoryAwareSearchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);
		categoryAwareSearchProfile = categoryAwareSearchProfileOptional.get();
	}

	@Test
	public void createCategoryAwareSearchConfigurationWithoutUid() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);

		// then
		assertNotNull(searchConfiguration.getUid());
		assertFalse(searchConfiguration.getUid().isEmpty());
	}

	@Test
	public void createCategoryAwareSearchConfiguration() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		final Optional<AsCategoryAwareSearchConfigurationModel> createdSearchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSearchConfigurationOptional.isPresent());

		final AsCategoryAwareSearchConfigurationModel createdSearchConfiguration = createdSearchConfigurationOptional.get();
		assertEquals(onlineCatalogVersion, createdSearchConfiguration.getCatalogVersion());
		assertEquals(categoryAwareSearchProfile, createdSearchConfiguration.getSearchProfile());
		assertEquals(UID1, createdSearchConfiguration.getUid());
	}

	@Test
	public void failToCreateCategoryAwareSearchConfigurationWithoutSearchProfile() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
	}

	@Test
	public void failToCreateCategoryAwareSearchConfigurationWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
	}

	@Test
	public void createMultipleCategoryAwareSearchConfigurations() throws Exception
	{
		// given
		final CategoryModel category1 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_10_CODE);
		final CategoryModel category2 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_20_CODE);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration1 = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration1.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration1.setUid(UID1);
		searchConfiguration1.setCategory(category1);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration2.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration2.setUid(UID2);
		searchConfiguration2.setCategory(category2);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration1);
		asConfigurationService.saveConfiguration(searchConfiguration2);

		final Optional<AsCategoryAwareSearchConfigurationModel> createdSearchConfiguration1Optional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);
		final Optional<AsCategoryAwareSearchConfigurationModel> createdSearchConfiguration2Optional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID2);

		// then
		assertTrue(createdSearchConfiguration1Optional.isPresent());

		final AsCategoryAwareSearchConfigurationModel createdSearchConfiguration1 = createdSearchConfiguration1Optional.get();
		assertEquals(onlineCatalogVersion, createdSearchConfiguration1.getCatalogVersion());
		assertEquals(UID1, createdSearchConfiguration1.getUid());

		assertTrue(createdSearchConfiguration2Optional.isPresent());

		final AsCategoryAwareSearchConfigurationModel createdSearchConfiguration2 = createdSearchConfiguration2Optional.get();
		assertEquals(onlineCatalogVersion, createdSearchConfiguration2.getCatalogVersion());
		assertEquals(UID2, createdSearchConfiguration2.getUid());
	}

	@Test
	public void failToCreateMultipleCategoryAwareSearchConfigurationsWithSameCategory() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration1 = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration1.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration1.setUid(UID1);
		searchConfiguration1.setCategory(null);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration2.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration2.setUid(UID2);
		searchConfiguration2.setCategory(null);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration1);
		asConfigurationService.saveConfiguration(searchConfiguration2);
	}

	@Test
	public void cloneCategoryAwareSearchConfigurationWithFacets() throws Exception
	{
		// given
		final CategoryModel category1 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_10_CODE);
		final CategoryModel category2 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_20_CODE);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);
		searchConfiguration.setCategory(category1);

		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID2);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);
		facet.setFacetType(AsFacetType.REFINE);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID3);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY3);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);

		asConfigurationService.refreshConfiguration(searchConfiguration);

		final AsCategoryAwareSearchConfigurationModel clonedSearchConfiguration = asSearchConfigurationService
				.cloneSearchConfiguration(searchConfiguration);
		clonedSearchConfiguration.setCategory(category2);

		asConfigurationService.saveConfiguration(clonedSearchConfiguration);

		// then
		assertNotSame(searchConfiguration, clonedSearchConfiguration);
		assertEquals(searchConfiguration.getCatalogVersion(), clonedSearchConfiguration.getCatalogVersion());
		assertEquals(searchConfiguration.getSearchProfile(), clonedSearchConfiguration.getSearchProfile());
		assertNotEquals(searchConfiguration.getUid(), clonedSearchConfiguration.getUid());
		assertEquals(category2, clonedSearchConfiguration.getCategory());

		assertThat(clonedSearchConfiguration.getPromotedFacets()).isNotNull().hasSize(1);
		assertThat(clonedSearchConfiguration.getFacets()).isNotNull().hasSize(1);
		assertThat(clonedSearchConfiguration.getExcludedFacets()).isNotNull().hasSize(1);

		final AsPromotedFacetModel clonedPromotedFacet = clonedSearchConfiguration.getPromotedFacets().get(0);
		assertEquals(promotedFacet.getCatalogVersion(), clonedPromotedFacet.getCatalogVersion());
		assertNotEquals(promotedFacet.getUid(), clonedPromotedFacet.getUid());
		assertNotEquals(promotedFacet.getSearchConfiguration(), clonedPromotedFacet.getSearchConfiguration());
		assertEquals(promotedFacet.getFacetType(), clonedPromotedFacet.getFacetType());

		final AsFacetModel clonedFacet = clonedSearchConfiguration.getFacets().get(0);
		assertEquals(facet.getCatalogVersion(), clonedFacet.getCatalogVersion());
		assertNotEquals(facet.getUid(), clonedFacet.getUid());
		assertNotEquals(facet.getSearchConfiguration(), clonedFacet.getSearchConfiguration());
		assertEquals(facet.getFacetType(), clonedFacet.getFacetType());

		final AsExcludedFacetModel clonedExcludedFacet = clonedSearchConfiguration.getExcludedFacets().get(0);
		assertEquals(excludedFacet.getCatalogVersion(), clonedExcludedFacet.getCatalogVersion());
		assertNotEquals(excludedFacet.getUid(), clonedExcludedFacet.getUid());
		assertNotEquals(excludedFacet.getSearchConfiguration(), clonedExcludedFacet.getSearchConfiguration());
		assertEquals(excludedFacet.getFacetType(), clonedExcludedFacet.getFacetType());
	}

	@Test
	public void cloneCategoryAwareSearchConfigurationWithBoosts() throws Exception
	{
		// given
		final CategoryModel category1 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_10_CODE);
		final CategoryModel category2 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_20_CODE);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);
		searchConfiguration.setCategory(category1);

		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID2);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		final AsBoostRuleModel boostRule = asConfigurationService.createConfiguration(AsBoostRuleModel.class);
		boostRule.setCatalogVersion(onlineCatalogVersion);
		boostRule.setUid(UID1);
		boostRule.setSearchConfiguration(searchConfiguration);
		boostRule.setIndexProperty(INDEX_PROPERTY1);
		boostRule.setOperator(AsBoostOperator.EQUAL);
		boostRule.setValue(VALUE1);
		boostRule.setBoost(BOOST1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		asConfigurationService.saveConfiguration(promotedItem);
		asConfigurationService.saveConfiguration(excludedItem);
		asConfigurationService.saveConfiguration(boostRule);

		asConfigurationService.refreshConfiguration(searchConfiguration);

		final AsCategoryAwareSearchConfigurationModel clonedSearchConfiguration = asSearchConfigurationService
				.cloneSearchConfiguration(searchConfiguration);
		clonedSearchConfiguration.setCategory(category2);

		asConfigurationService.saveConfiguration(clonedSearchConfiguration);

		// then
		assertNotSame(searchConfiguration, clonedSearchConfiguration);
		assertEquals(searchConfiguration.getCatalogVersion(), clonedSearchConfiguration.getCatalogVersion());
		assertEquals(searchConfiguration.getSearchProfile(), clonedSearchConfiguration.getSearchProfile());
		assertNotEquals(searchConfiguration.getUid(), clonedSearchConfiguration.getUid());
		assertEquals(category2, clonedSearchConfiguration.getCategory());

		assertThat(clonedSearchConfiguration.getPromotedItems()).isNotNull().hasSize(1);
		assertThat(clonedSearchConfiguration.getExcludedItems()).isNotNull().hasSize(1);
		assertThat(clonedSearchConfiguration.getBoostRules()).isNotNull().hasSize(1);

		final AsPromotedItemModel clonedPromotedItem = clonedSearchConfiguration.getPromotedItems().get(0);
		assertEquals(promotedItem.getCatalogVersion(), clonedPromotedItem.getCatalogVersion());
		assertNotEquals(promotedItem.getUid(), clonedPromotedItem.getUid());
		assertNotEquals(promotedItem.getSearchConfiguration(), clonedPromotedItem.getSearchConfiguration());
		assertEquals(promotedItem.getItem(), clonedPromotedItem.getItem());

		final AsExcludedItemModel clonedExcludedItem = clonedSearchConfiguration.getExcludedItems().get(0);
		assertEquals(excludedItem.getCatalogVersion(), clonedExcludedItem.getCatalogVersion());
		assertNotEquals(excludedItem.getUid(), clonedExcludedItem.getUid());
		assertNotEquals(excludedItem.getSearchConfiguration(), clonedExcludedItem.getSearchConfiguration());
		assertEquals(excludedItem.getItem(), clonedExcludedItem.getItem());

		final AsBoostRuleModel clonedBoostRule = clonedSearchConfiguration.getBoostRules().get(0);
		assertEquals(boostRule.getCatalogVersion(), clonedBoostRule.getCatalogVersion());
		assertNotEquals(boostRule.getUid(), clonedBoostRule.getUid());
		assertNotEquals(boostRule.getSearchConfiguration(), clonedBoostRule.getSearchConfiguration());
		assertEquals(boostRule.getIndexProperty(), clonedBoostRule.getIndexProperty());
		assertEquals(boostRule.getOperator(), clonedBoostRule.getOperator());
		assertEquals(boostRule.getValue(), clonedBoostRule.getValue());
		assertEquals(boostRule.getBoost(), clonedBoostRule.getBoost());
	}

	@Test
	public void cloneCategoryAwareSearchConfigurationWithSorts() throws Exception
	{
		// given
		final CategoryModel category1 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_10_CODE);
		final CategoryModel category2 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_20_CODE);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);
		searchConfiguration.setCategory(category1);

		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);

		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID2);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE2);

		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID3);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE3);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		asConfigurationService.saveConfiguration(promotedSort);
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(excludedSort);

		asConfigurationService.refreshConfiguration(searchConfiguration);

		final AsCategoryAwareSearchConfigurationModel clonedSearchConfiguration = asSearchConfigurationService
				.cloneSearchConfiguration(searchConfiguration);
		clonedSearchConfiguration.setCategory(category2);

		asConfigurationService.saveConfiguration(clonedSearchConfiguration);

		// then
		assertNotSame(searchConfiguration, clonedSearchConfiguration);
		assertEquals(searchConfiguration.getCatalogVersion(), clonedSearchConfiguration.getCatalogVersion());
		assertEquals(searchConfiguration.getSearchProfile(), clonedSearchConfiguration.getSearchProfile());
		assertNotEquals(searchConfiguration.getUid(), clonedSearchConfiguration.getUid());
		assertEquals(category2, clonedSearchConfiguration.getCategory());

		assertThat(clonedSearchConfiguration.getPromotedSorts()).isNotNull().hasSize(1);
		assertThat(clonedSearchConfiguration.getSorts()).isNotNull().hasSize(1);
		assertThat(clonedSearchConfiguration.getExcludedSorts()).isNotNull().hasSize(1);

		final AsPromotedSortModel clonedPromotedSort = clonedSearchConfiguration.getPromotedSorts().get(0);
		assertEquals(promotedSort.getCatalogVersion(), clonedPromotedSort.getCatalogVersion());
		assertNotEquals(promotedSort.getUid(), clonedPromotedSort.getUid());
		assertNotEquals(promotedSort.getSearchConfiguration(), clonedPromotedSort.getSearchConfiguration());
		assertEquals(promotedSort.getCode(), clonedPromotedSort.getCode());

		final AsSortModel clonedSort = clonedSearchConfiguration.getSorts().get(0);
		assertEquals(sort.getCatalogVersion(), clonedSort.getCatalogVersion());
		assertNotEquals(sort.getUid(), clonedSort.getUid());
		assertNotEquals(sort.getSearchConfiguration(), clonedSort.getSearchConfiguration());
		assertEquals(sort.getCode(), clonedSort.getCode());

		final AsExcludedSortModel clonedExcludedSort = clonedSearchConfiguration.getExcludedSorts().get(0);
		assertEquals(excludedSort.getCatalogVersion(), clonedExcludedSort.getCatalogVersion());
		assertNotEquals(excludedSort.getUid(), clonedExcludedSort.getUid());
		assertNotEquals(excludedSort.getSearchConfiguration(), clonedExcludedSort.getSearchConfiguration());
		assertEquals(excludedSort.getCode(), clonedExcludedSort.getCode());
	}


	@Test
	public void categoryAwareSearchConfigurationIsNotCorrupted() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);

		// when
		final boolean corrupted = searchConfiguration.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void categoryAwareSearchConfigurationIsCorrupted() throws Exception
	{
		// given
		final AsCategoryAwareSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		searchConfiguration.setUniqueIdx(null);

		final boolean corrupted = searchConfiguration.isCorrupted();

		// then
		assertTrue(corrupted);
	}
}
