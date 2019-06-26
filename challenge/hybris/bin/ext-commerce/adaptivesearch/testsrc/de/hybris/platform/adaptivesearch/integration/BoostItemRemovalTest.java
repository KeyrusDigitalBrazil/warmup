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
import de.hybris.platform.adaptivesearch.data.AsExcludedItem;
import de.hybris.platform.adaptivesearch.data.AsPromotedItem;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileCalculationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.adaptivesearch.util.MergeMap;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class BoostItemRemovalTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";

	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "index1";

	private static final String SIMPLE_SEARCH_PROFILE_CODE = "simpleProfile";

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";
	private static final String PRODUCT3_CODE = "product3";
	private static final String PRODUCT4_CODE = "product4";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchProfileCalculationService asSearchProfileCalculationService;

	@Resource
	private AsSearchProfileContextFactory asSearchProfileContextFactory;

	private CatalogVersionModel catalogVersion;

	private ProductModel product1;
	private ProductModel product2;
	private ProductModel product3;
	private ProductModel product4;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/boostItemRemovalTest.impex", "utf-8");

		catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		product1 = productService.getProductForCode(catalogVersion, PRODUCT1_CODE);
		product2 = productService.getProductForCode(catalogVersion, PRODUCT2_CODE);
		product3 = productService.getProductForCode(catalogVersion, PRODUCT3_CODE);
		product4 = productService.getProductForCode(catalogVersion, PRODUCT4_CODE);
	}

	@Test
	public void calculateSearchProfile() throws Exception
	{
		// given
		final AbstractAsSearchProfileModel searchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, SIMPLE_SEARCH_PROFILE_CODE).get();
		final AsSearchProfileContext searchProfileContext = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION,
				INDEX_TYPE, Collections.singletonList(catalogVersion), Collections.emptyList());

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(searchProfileContext,
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

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(2, excludedItems.size());

		final AsExcludedItem excludedItem1 = excludedItems.get(0).getConfiguration();
		assertEquals(product3.getPk(), excludedItem1.getItemPk());

		final AsExcludedItem excludedItem2 = excludedItems.get(1).getConfiguration();
		assertEquals(product4.getPk(), excludedItem2.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingPromotedItem() throws Exception
	{
		// given
		removeItem(product1);

		final AbstractAsSearchProfileModel searchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, SIMPLE_SEARCH_PROFILE_CODE).get();
		final AsSearchProfileContext searchProfileContext = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION,
				INDEX_TYPE, Collections.singletonList(catalogVersion), Collections.emptyList());

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(searchProfileContext,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), promotedItem1.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(2, excludedItems.size());

		final AsExcludedItem excludedItem1 = excludedItems.get(0).getConfiguration();
		assertEquals(product3.getPk(), excludedItem1.getItemPk());

		final AsExcludedItem excludedItem2 = excludedItems.get(1).getConfiguration();
		assertEquals(product4.getPk(), excludedItem2.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingExcludedItem() throws Exception
	{
		// given
		removeItem(product3);

		final AbstractAsSearchProfileModel searchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, SIMPLE_SEARCH_PROFILE_CODE).get();
		final AsSearchProfileContext searchProfileContext = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION,
				INDEX_TYPE, Collections.singletonList(catalogVersion), Collections.emptyList());

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(searchProfileContext,
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

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(1, excludedItems.size());

		final AsExcludedItem excludedItem1 = excludedItems.get(0).getConfiguration();
		assertEquals(product4.getPk(), excludedItem1.getItemPk());
	}

	@Test
	public void calculateSearchProfileAfterRemovingPromotedAndExcludedItems() throws Exception
	{
		// given
		removeItem(product2);
		removeItem(product4);

		final AbstractAsSearchProfileModel searchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, SIMPLE_SEARCH_PROFILE_CODE).get();
		final AsSearchProfileContext searchProfileContext = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION,
				INDEX_TYPE, Collections.singletonList(catalogVersion), Collections.emptyList());

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(searchProfileContext,
				Collections.singletonList(searchProfile));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem1 = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem1.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(1, result.getExcludedItems().size());

		final AsExcludedItem excludedItem1 = excludedItems.get(0).getConfiguration();
		assertEquals(product3.getPk(), excludedItem1.getItemPk());
	}

	protected void removeItem(final ItemModel item)
	{
		modelService.remove(item);
	}
}
