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
package de.hybris.platform.adaptivesearch.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContextFactory;
import de.hybris.platform.adaptivesearch.data.AbstractAsBoostItemConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsPromotedItem;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
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
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class SearchConfigurationRemovalTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";

	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "index1";

	private static final String CATEGORY_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_CONF_GLOBAL_UID = "fd7d8009-df2f-4b78-9747-5b50efb244df";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT10_UID = "f5ca2ff6-c856-4608-bb69-b97d5de8ef42";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT20_UID = "3d97bdd0-d3a6-4280-a7c7-06f34b7a72ff";

	private static final String CAT10_CODE = "crCat10";
	private static final String CAT20_CODE = "crCat20";

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";
	private static final String PRODUCT3_CODE = "product3";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

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
	public void setUp() throws ImpExException
	{
		asCacheStrategy.clear();

		importCsv("/adaptivesearch/test/integration/searchConfigurationRemovalTest.impex", "utf-8");

		catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		category10 = categoryService.getCategoryForCode(catalogVersion, CAT10_CODE);
		category20 = categoryService.getCategoryForCode(catalogVersion, CAT20_CODE);

		product1 = productService.getProductForCode(catalogVersion, PRODUCT1_CODE);
		product2 = productService.getProductForCode(catalogVersion, PRODUCT2_CODE);
		product3 = productService.getProductForCode(catalogVersion, PRODUCT3_CODE);
	}

	@Test
	public void calculateSearchProfileAfterRemovingGlobalSearchConfiguration() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_GLOBAL_UID));

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(2, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), promotedItem1.getItemPk());

		final AsPromotedItem promotedItem2 = promotedItems.get(1).getConfiguration();
		assertEquals(product3.getPk(), promotedItem2.getItemPk());
	}


	@Test
	public void calculateSearchProfileAfterRemovingGlobalSearchConfigurationForCachedSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context1 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final AsSearchProfileContext context2 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		asSearchProfileCalculationService.calculate(context1, Collections.singletonList(searchProfile));

		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_GLOBAL_UID));

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context2,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(2, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), promotedItem1.getItemPk());

		final AsPromotedItem promotedItem2 = promotedItems.get(1).getConfiguration();
		assertEquals(product3.getPk(), promotedItem2.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingCategorySearchConfiguration() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT10_UID));

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(2, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());

		final AsPromotedItem promotedItem2 = promotedItems.get(1).getConfiguration();
		assertEquals(product3.getPk(), promotedItem2.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingCategorySearchConfigurationForCachedSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context1 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final AsSearchProfileContext context2 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		asSearchProfileCalculationService.calculate(context1, Collections.singletonList(searchProfile));

		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT10_UID));

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context2,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(2, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());

		final AsPromotedItem promotedItem2 = promotedItems.get(1).getConfiguration();
		assertEquals(product3.getPk(), promotedItem2.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingAllSearchConfigurations() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_GLOBAL_UID));
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT10_UID));
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT20_UID));

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(0, promotedItems.size());
	}

	@Test
	public void calculateSearchProfileAfterRemovingAllSearchConfigurationsForCachedSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context1 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final AsSearchProfileContext context2 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		asSearchProfileCalculationService.calculate(context1, Collections.singletonList(searchProfile));

		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_GLOBAL_UID));
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT10_UID));
		modelService.remove(loadSearchConfiguration(CATEGORY_AWARE_SEARCH_PROFILE_CONF_CAT20_UID));

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context2,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(0, promotedItems.size());
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
