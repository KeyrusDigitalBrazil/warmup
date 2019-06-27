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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.constants.AdaptivesearchConstants;
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
public class CategoryRemovalTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";

	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "index1";

	private static final String CATEGORY_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";

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

		importCsv("/adaptivesearch/test/integration/categoryRemovalTest.impex", "utf-8");

		catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		category10 = categoryService.getCategoryForCode(catalogVersion, CAT10_CODE);
		category20 = categoryService.getCategoryForCode(catalogVersion, CAT20_CODE);

		product1 = productService.getProductForCode(catalogVersion, PRODUCT1_CODE);
		product2 = productService.getProductForCode(catalogVersion, PRODUCT2_CODE);
		product3 = productService.getProductForCode(catalogVersion, PRODUCT3_CODE);
	}

	@Test
	public void createSearchProfileContext() throws Exception
	{
		// when
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		// then
		assertNotNull(context);

		final List<String> categoryQualifiers = context.getQualifiers().get(AdaptivesearchConstants.CATEGORY_QUALIFIER_TYPE);
		assertThat(categoryQualifiers).containsExactly(CAT10_CODE, CAT20_CODE);
	}

	@Test
	public void createSearchProfileContextAfterRemovingCat10() throws Exception
	{
		// when
		modelService.remove(category10);

		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		// then
		assertNotNull(context);

		final List<String> categoryQualifiers = context.getQualifiers().get(AdaptivesearchConstants.CATEGORY_QUALIFIER_TYPE);
		assertThat(categoryQualifiers).containsExactly(CAT20_CODE);
	}

	@Test
	public void calculateSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(3, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());

		final AsPromotedItem promotedItem2 = promotedItems.get(1).getConfiguration();
		assertEquals(product2.getPk(), promotedItem2.getItemPk());

		final AsPromotedItem promotedItem3 = promotedItems.get(2).getConfiguration();
		assertEquals(product3.getPk(), promotedItem3.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingCat10() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		modelService.remove(category10);

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
	public void calculateSearchProfileAfterRemovingCat20() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		modelService.remove(category20);

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
		assertEquals(product2.getPk(), promotedItem2.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingCat10AndCat20() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		modelService.remove(category10);
		modelService.remove(category20);

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingCat10AndCat20ForCachedSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context1 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final AsSearchProfileContext context2 = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		asSearchProfileCalculationService.calculate(context1, Collections.singletonList(searchProfile));

		modelService.remove(category10);
		modelService.remove(category20);

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context2,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingCat10AndCat20ForCachedSearchProfileAndConfigurations() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10, category20));

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile));

		modelService.remove(category10);
		modelService.remove(category20);

		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(3, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());

		final AsPromotedItem promotedItem2 = promotedItems.get(1).getConfiguration();
		assertEquals(product2.getPk(), promotedItem2.getItemPk());

		final AsPromotedItem promotedItem3 = promotedItems.get(2).getConfiguration();
		assertEquals(product3.getPk(), promotedItem3.getItemPk());
	}

	@Test
	public void getSearchConfigurationForGlobalCat() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationService
				.getSearchConfigurationForContext(context, searchProfile);

		// then
		assertNotNull(searchConfigurationResult);
		assertTrue(searchConfigurationResult.isPresent());
	}

	@Test
	public void getSearchConfigurationForGlobalCatAfterRemovingCat10AndCat20() throws Exception
	{
		// given
		final AsCategoryAwareSearchProfileModel searchProfile = loadSearchProfile(CATEGORY_AWARE_SEARCH_PROFILE_CODE);

		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());

		// when
		modelService.remove(category10);
		modelService.remove(category20);

		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationService
				.getSearchConfigurationForContext(context, searchProfile);

		// then
		assertNotNull(searchConfigurationResult);
		assertTrue(searchConfigurationResult.isPresent());
	}

	@Test
	public void getSearchConfigurationForCat10() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10));

		final AbstractAsSearchProfileModel searchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, CATEGORY_AWARE_SEARCH_PROFILE_CODE).get();

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationService
				.getSearchConfigurationForContext(context, searchProfile);

		// then
		assertNotNull(searchConfigurationResult);
		assertTrue(searchConfigurationResult.isPresent());
	}

	@Test
	public void getSearchConfigurationForCat10AfterRemovingCat10() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category10));

		final AbstractAsSearchProfileModel searchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, CATEGORY_AWARE_SEARCH_PROFILE_CODE).get();

		// when
		modelService.remove(category10);

		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationService
				.getSearchConfigurationForContext(context, searchProfile);

		// then
		assertNotNull(searchConfigurationResult);
		assertFalse(searchConfigurationResult.isPresent());
	}

	protected <T extends AbstractAsSearchProfileModel> T loadSearchProfile(final String searchProfileCode)
	{
		final Optional<T> searchProfileOptional = asSearchProfileService.getSearchProfileForCode(catalogVersion, searchProfileCode);
		return searchProfileOptional.get();
	}
}
