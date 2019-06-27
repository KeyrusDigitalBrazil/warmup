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
package de.hybris.platform.adaptivesearch.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContextFactory;
import de.hybris.platform.adaptivesearch.data.AbstractAsBoostItemConfiguration;
import de.hybris.platform.adaptivesearch.data.AbstractAsFacetConfiguration;
import de.hybris.platform.adaptivesearch.data.AsBoostRule;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedFacet;
import de.hybris.platform.adaptivesearch.data.AsExcludedItem;
import de.hybris.platform.adaptivesearch.data.AsFacet;
import de.hybris.platform.adaptivesearch.data.AsMergeConfiguration;
import de.hybris.platform.adaptivesearch.data.AsPromotedFacet;
import de.hybris.platform.adaptivesearch.data.AsPromotedItem;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostOperator;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostType;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileCalculationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.adaptivesearch.strategies.AsCacheStrategy;
import de.hybris.platform.adaptivesearch.util.MergeMap;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAsSearchProfileCalculationServiceTest extends ServicelayerTransactionalTest
{
	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "index1";

	private static final String SIMPLE_SEARCH_PROFILE_1_CODE = "simpleProfile1";
	private static final String SIMPLE_SEARCH_PROFILE_1_CONF_UID = "searchConfiguration1";

	private static final String SIMPLE_SEARCH_PROFILE_2_CODE = "simpleProfile2";
	private static final String SIMPLE_SEARCH_PROFILE_2_CONF_UID = "searchConfiguration2";

	private static final String CATEGORY_AWARE_SEARCH_PROFILE_1_CODE = "categoryAwareProfile1";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_1_CONF_GLOBAL_UID = "globalSearchConfiguration1";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_1_CONF_CATEGORY_UID = "categorySearchConfiguration1";

	private static final String CATEGORY_AWARE_SEARCH_PROFILE_2_CODE = "categoryAwareProfile2";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_2_CONF_GLOBAL_UID = "globalSearchConfiguration2";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_3_CONF_CATEGORY_UID = "categorySearchConfiguration2";

	private static final String PROPERTY1 = "property1";
	private static final String PROPERTY2 = "property2";
	private static final String PROPERTY3 = "property3";

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";
	private static final String PRODUCT3_CODE = "product3";

	private static final String BOOST_VALUE = "value";

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private ProductService productService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsSearchProfileCalculationService asSearchProfileCalculationService;

	@Resource
	private AsSearchProfileContextFactory asSearchProfileContextFactory;

	@Resource
	private AsCacheStrategy asCacheStrategy;

	private CatalogVersionModel catalogVersion;
	private CategoryModel category10;
	private CategoryModel category20;
	private ProductModel product1;
	private ProductModel product2;
	private ProductModel product3;

	@Before
	public void setUp() throws Exception
	{
		asCacheStrategy.clear();

		importCsv("/adaptivesearch/test/services/defaultAsSearchProfileCalculationServiceTest.impex", "utf-8");

		catalogVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Staged");
		category10 = categoryService.getCategoryForCode(catalogVersion, "cat10");
		category20 = categoryService.getCategoryForCode(catalogVersion, "cat20");
		product1 = productService.getProductForCode(catalogVersion, PRODUCT1_CODE);
		product2 = productService.getProductForCode(catalogVersion, PRODUCT2_CODE);
		product3 = productService.getProductForCode(catalogVersion, PRODUCT3_CODE);
	}

	@Test
	public void calculateSimpleSearchProfile() throws Exception
	{
		// given
		final AbstractAsSearchProfileModel searchProfile = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);

		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) result
				.getPromotedFacets()).orderedValues();
		assertEquals(1, promotedFacets.size());

		final AsPromotedFacet promotedFacet = promotedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY1, promotedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) result
				.getFacets()).orderedValues();
		assertEquals(0, facets.size());

		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) result
				.getExcludedFacets()).orderedValues();
		assertEquals(1, excludedFacets.size());

		final AsExcludedFacet excludedFacet = excludedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY2, excludedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(1, excludedItems.size());

		final AsExcludedItem excludedItem = excludedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), excludedItem.getItemPk());

		assertEquals(1, result.getBoostRules().size());
		final AsBoostRule boostRule = result.getBoostRules().get(0).getConfiguration();
		assertEquals(PROPERTY1, boostRule.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule.getOperator());
		assertEquals(BOOST_VALUE, boostRule.getValue());
		assertEquals(Float.valueOf(1.1f), boostRule.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule.getBoostType());
	}

	@Test
	public void calculateCategoryAwareSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));

		final AbstractAsSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) result
				.getPromotedFacets()).orderedValues();
		assertEquals(1, promotedFacets.size());

		final AsPromotedFacet promotedFacet = promotedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY2, promotedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) result
				.getFacets()).orderedValues();
		assertEquals(1, facets.size());

		final AsFacet facet = facets.get(0).getConfiguration();
		assertEquals(PROPERTY1, facet.getIndexProperty());
		assertEquals(Integer.valueOf(12), facet.getPriority());
		assertEquals(AsFacetType.REFINE, facet.getFacetType());

		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) result
				.getExcludedFacets()).orderedValues();
		assertEquals(1, excludedFacets.size());

		final AsExcludedFacet excludedFacet = excludedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY3, excludedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(1, excludedItems.size());

		final AsExcludedItem excludedItem = excludedItems.get(0).getConfiguration();
		assertEquals(product3.getPk(), excludedItem.getItemPk());

		assertEquals(2, result.getBoostRules().size());

		final AsBoostRule boostRule1 = result.getBoostRules().get(0).getConfiguration();
		assertEquals(PROPERTY1, boostRule1.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule1.getOperator());
		assertEquals(BOOST_VALUE, boostRule1.getValue());
		assertEquals(Float.valueOf(1.3f), boostRule1.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule1.getBoostType());

		final AsBoostRule boostRule2 = result.getBoostRules().get(1).getConfiguration();
		assertEquals(PROPERTY2, boostRule2.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule2.getOperator());
		assertEquals(BOOST_VALUE, boostRule2.getValue());
		assertEquals(Float.valueOf(1.2f), boostRule2.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule2.getBoostType());
	}

	@Test
	public void calculateMultipleSearchProfiles() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));

		final AbstractAsSearchProfileModel searchProfile = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AbstractAsSearchProfileModel categoryAwareSearchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Arrays.asList(searchProfile, categoryAwareSearchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) result
				.getPromotedFacets()).orderedValues();
		assertEquals(1, promotedFacets.size());

		final AsPromotedFacet promotedFacet = promotedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY2, promotedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) result
				.getFacets()).orderedValues();
		assertEquals(1, facets.size());

		final AsFacet facet = facets.get(0).getConfiguration();
		assertEquals(PROPERTY1, facet.getIndexProperty());
		assertEquals(Integer.valueOf(12), facet.getPriority());
		assertEquals(AsFacetType.REFINE, facet.getFacetType());

		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) result
				.getExcludedFacets()).orderedValues();
		assertEquals(1, excludedFacets.size());

		final AsExcludedFacet excludedFacet = excludedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY3, excludedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(2, excludedItems.size());

		final AsExcludedItem excludedItem1 = excludedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), excludedItem1.getItemPk());

		final AsExcludedItem excludedItem2 = excludedItems.get(1).getConfiguration();
		assertEquals(product3.getPk(), excludedItem2.getItemPk());

		assertEquals(3, result.getBoostRules().size());

		final AsBoostRule boostRule1 = result.getBoostRules().get(0).getConfiguration();
		assertEquals(PROPERTY1, boostRule1.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule1.getOperator());
		assertEquals(BOOST_VALUE, boostRule1.getValue());
		assertEquals(Float.valueOf(1.1f), boostRule1.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule1.getBoostType());

		final AsBoostRule boostRule2 = result.getBoostRules().get(1).getConfiguration();
		assertEquals(PROPERTY1, boostRule2.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule2.getOperator());
		assertEquals(BOOST_VALUE, boostRule2.getValue());
		assertEquals(Float.valueOf(1.3f), boostRule2.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule2.getBoostType());

		final AsBoostRule boostRule3 = result.getBoostRules().get(2).getConfiguration();
		assertEquals(PROPERTY2, boostRule3.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule3.getOperator());
		assertEquals(BOOST_VALUE, boostRule3.getValue());
		assertEquals(Float.valueOf(1.2f), boostRule3.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule3.getBoostType());
	}

	@Test
	public void cacheSearchProfileCalculation() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile));
		}

		// then
		// 2L + 1C
		assertEquals(3, asCacheStrategy.getSize());
		assertEquals(3, asCacheStrategy.getMisses());
		// (10 * (2L + 1C)) - (2L + 1C)
		assertEquals(27, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfileCalculationWithUpdate1() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile));
		}

		searchProfile.setName("updated name");
		modelService.save(searchProfile);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile));
		}

		// then
		// (2L + 1C) + (1L + 1C)
		assertEquals(5, asCacheStrategy.getSize());
		assertEquals(5, asCacheStrategy.getMisses());
		// (10 * (2L + 1C)) - ((2L + 1C) + (1L + 1C))
		assertEquals(25, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfileCalculationWithUpdate2() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile));
		}

		final AsSimpleSearchConfigurationModel searchConfiguration = loadSearchConfiguration(SIMPLE_SEARCH_PROFILE_1_CONF_UID);
		searchConfiguration.setFacetsMergeMode(AsFacetsMergeMode.REPLACE);
		modelService.save(searchConfiguration);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile));
		}

		// then
		// (2L + 1C) + (2L + 1C)
		assertEquals(6, asCacheStrategy.getSize());
		assertEquals(6, asCacheStrategy.getMisses());
		// (10 * (2L + 1C)) - ((2L + 1C) + (2L + 1C))
		assertEquals(24, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfilesCalculation() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (2L + 1C) + (2L + 1C)
		assertEquals(6, asCacheStrategy.getSize());
		assertEquals(6, asCacheStrategy.getMisses());
		// (10 * (4L + 2C)) - (4L + 2C)
		assertEquals(54, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfilesCalculationWithUpdate1() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		searchProfile1.setName("updated name");
		modelService.save(searchProfile1);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (2L + 1C) + (2L + 1C) + (1L + 1C)
		assertEquals(8, asCacheStrategy.getSize());
		assertEquals(8, asCacheStrategy.getMisses());
		// (10 * (4L + 2C)) - ((4L + 2C) + (1L + 1C)))
		assertEquals(52, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfilesCalculationWithUpdate2() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		searchProfile2.setName("updated name");
		modelService.save(searchProfile2);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (2L + 1C) + (2L + 1C) + (1L + 1C)
		assertEquals(8, asCacheStrategy.getSize());
		assertEquals(8, asCacheStrategy.getMisses());
		// (10 * (4L + 2C)) - ((4L + 2C) + (1L + 1C)))
		assertEquals(52, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfilesCalculationWithUpdate3() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		final AsSimpleSearchConfigurationModel searchConfiguration1 = loadSearchConfiguration(SIMPLE_SEARCH_PROFILE_1_CONF_UID);
		searchConfiguration1.setFacetsMergeMode(AsFacetsMergeMode.REPLACE);
		modelService.save(searchConfiguration1);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (2L + 1C) + (2L + 1C) + (2L + 1C)
		assertEquals(9, asCacheStrategy.getSize());
		assertEquals(9, asCacheStrategy.getMisses());
		// (10 * (4L + 2C)) - ((4L + 2C) + (2L + 1C)))
		assertEquals(51, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfilesCalculationWithUpdate4() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = loadSearchConfiguration(
				CATEGORY_AWARE_SEARCH_PROFILE_1_CONF_GLOBAL_UID);
		searchConfiguration2.setBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		modelService.save(searchConfiguration2);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (2L + 1C) + (2L + 1C) + (2L + 1C)
		assertEquals(9, asCacheStrategy.getSize());
		assertEquals(9, asCacheStrategy.getMisses());
		// (10 * (4L + 2C)) - ((4L + 2C) + (2L + 1C)))
		assertEquals(51, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfilesCalculationWithUpdate5() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		final AsSimpleSearchConfigurationModel searchConfiguration1 = loadSearchConfiguration(SIMPLE_SEARCH_PROFILE_1_CONF_UID);
		searchConfiguration1.setFacetsMergeMode(AsFacetsMergeMode.REPLACE);
		modelService.save(searchConfiguration1);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = loadSearchConfiguration(
				CATEGORY_AWARE_SEARCH_PROFILE_1_CONF_GLOBAL_UID);
		searchConfiguration2.setBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		modelService.save(searchConfiguration2);

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (2L + 1C) + (2L + 1C) + (2L + 1C) + (2L + 1C)
		assertEquals(12, asCacheStrategy.getSize());
		assertEquals(12, asCacheStrategy.getMisses());
		// (10 * (4L + 2C)) - ((4L + 2C) + (4L + 2C))
		assertEquals(48, asCacheStrategy.getHits());
	}

	@Test
	public void cacheSearchProfileGroupsCalculation() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));

		final AsSimpleSearchProfileModel searchProfile1 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);
		final AsSimpleSearchProfileModel searchProfile3 = loadSearchProfile(SIMPLE_SEARCH_PROFILE_2_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile4 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_2_CODE);

		final AsMergeConfiguration mergeConfiguration1 = new AsMergeConfiguration();
		mergeConfiguration1.setFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		mergeConfiguration1.setBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		mergeConfiguration1.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_BEFORE);
		mergeConfiguration1.setResultFacetsMergeMode(AsFacetsMergeMode.ADD_BEFORE);
		mergeConfiguration1.setResultBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		mergeConfiguration1.setResultBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);

		final AsSearchProfileActivationGroup group1 = new AsSearchProfileActivationGroup();
		group1.setSearchProfiles(Arrays.asList(searchProfile1, searchProfile2));
		group1.setMergeConfiguration(mergeConfiguration1);

		final AsMergeConfiguration mergeConfiguration2 = new AsMergeConfiguration();
		mergeConfiguration2.setFacetsMergeMode(AsFacetsMergeMode.ADD_BEFORE);
		mergeConfiguration2.setBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		mergeConfiguration2.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);
		mergeConfiguration2.setResultFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		mergeConfiguration2.setResultBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		mergeConfiguration2.setResultBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_BEFORE);

		final AsSearchProfileActivationGroup group2 = new AsSearchProfileActivationGroup();
		group2.setSearchProfiles(Arrays.asList(searchProfile3, searchProfile4));
		group2.setMergeConfiguration(mergeConfiguration2);

		// when
		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculateGroups(context, Arrays.asList(group1, group2));
		}

		// then
		// (2L + 1C) + (3L + 1C) + (2L + 1C) + (3L + 1C)
		assertEquals(14, asCacheStrategy.getSize());
		assertEquals(14, asCacheStrategy.getMisses());
		// (10 * (10L + 4C)) - (10L + 4C)
		assertEquals(126, asCacheStrategy.getHits());
	}

	@Test
	public void calculateCategoryAwareSearchProfileCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context1 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));
		final AsSearchProfileContext context2 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context1, Collections.singletonList(searchProfile));
		}

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context2, Collections.singletonList(searchProfile));
		}

		// then
		// (3L + 1C)
		assertEquals(4, asCacheStrategy.getSize());
		assertEquals(4, asCacheStrategy.getMisses());
		// (10 * (3L + 1C)) - (3L + 1C)
		assertEquals(36, asCacheStrategy.getHits());
	}

	@Test
	public void calculateCategoryAwareSearchProfilesCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context1 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));
		final AsSearchProfileContext context2 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10));

		final AsCategoryAwareSearchProfileModel searchProfile1 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);
		final AsCategoryAwareSearchProfileModel searchProfile2 = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_2_CODE);

		// when
		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context1, Arrays.asList(searchProfile1, searchProfile2));
		}

		for (int i = 0; i < 5; i++)
		{
			asSearchProfileCalculationService.calculate(context2, Arrays.asList(searchProfile1, searchProfile2));
		}

		// then
		// (3L + 1C) + (3L + 1C)
		assertEquals(8, asCacheStrategy.getSize());
		assertEquals(8, asCacheStrategy.getMisses());
		// (10 * (6L + 2C)) - (6L + 2C)
		assertEquals(72, asCacheStrategy.getHits());
	}

	protected <T extends AbstractAsSearchProfileModel> T loadSearchProfile(final String searchProfileCode)
	{
		final Optional<T> searchProfileOptional = asSearchProfileService.getSearchProfileForCode(catalogVersion, searchProfileCode);
		return searchProfileOptional.get();
	}

	protected <T extends AbstractAsSearchConfigurationModel> T loadSearchConfiguration(final String searchConfigurationUid)
	{
		final Optional<T> searchConfigurationOptional = asSearchConfigurationService.getSearchConfigurationForUid(catalogVersion,
				searchConfigurationUid);
		return searchConfigurationOptional.get();
	}
}
