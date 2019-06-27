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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileActivationSetDao;
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileDao;
import de.hybris.platform.adaptivesearch.model.AbstractAsBoostItemConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsBoostRuleConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsFacetConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsBoostRuleModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedItemModel;
import de.hybris.platform.adaptivesearch.model.AsFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedItemModel;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSortExpressionModel;
import de.hybris.platform.adaptivesearch.model.AsSortModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class CatalogVersionSynchronizationTest extends ServicelayerTest
{
	private final static String INDEX_TYPE = "index1";

	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_PROFILE_CODE = "simpleProfile";
	private static final String CAT_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private AsSearchProfileDao asSearchProfileDao;

	@Resource
	private AsSearchProfileActivationSetDao asSearchProfileActivationSetDao;

	private CatalogVersionModel stagedCatalogVersion;
	private CatalogVersionModel onlineCatalogVersion;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest.impex", "utf-8");

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	@Test
	public void simpleSearchProfileNotFoundBeforeSynchronization() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_simpleSearchProfile.impex",
				"utf-8");

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		// then
		assertFalse(searchProfileResult.isPresent());
	}

	@Test
	public void synchronizeSimpleSearchProfile() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_simpleSearchProfile.impex",
				"utf-8");
		final Optional<AbstractAsSearchProfileModel> stagedSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(stagedCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);
		final Optional<AsSearchProfileActivationSetModel> stagedActivationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(stagedCatalogVersion, INDEX_TYPE);

		// when
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AbstractAsSearchProfileModel> onlineSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);
		final Optional<AsSearchProfileActivationSetModel> onlineActivationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(onlineCatalogVersion, INDEX_TYPE);

		// then
		assertTrue(stagedSearchProfile.get() instanceof AsSimpleSearchProfileModel);
		assertTrue(onlineSearchProfile.get() instanceof AsSimpleSearchProfileModel);
		assertEqualsSimpleSearchProfile((AsSimpleSearchProfileModel) stagedSearchProfile.get(),
				(AsSimpleSearchProfileModel) onlineSearchProfile.get());
		assertEqualsSearchProfileActivationSet(stagedActivationSet, onlineActivationSet);
	}

	@Test
	public void synchronizeSimpleSearchProfileAfterUpdate1() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_simpleSearchProfile.impex",
				"utf-8");
		final Optional<AbstractAsSearchProfileModel> stagedSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(stagedCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		// when
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_update1_simpleSearchProfile.impex",
				"utf-8");
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AbstractAsSearchProfileModel> onlineSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(stagedSearchProfile.get() instanceof AsSimpleSearchProfileModel);
		assertTrue(onlineSearchProfile.get() instanceof AsSimpleSearchProfileModel);
		assertEqualsSimpleSearchProfile((AsSimpleSearchProfileModel) stagedSearchProfile.get(),
				(AsSimpleSearchProfileModel) onlineSearchProfile.get());
	}

	@Test
	public void synchronizeSimpleSearchProfileAfterUpdate2() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_simpleSearchProfile.impex",
				"utf-8");
		final Optional<AbstractAsSearchProfileModel> stagedSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(stagedCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		// when
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_update2_simpleSearchProfile.impex",
				"utf-8");
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AbstractAsSearchProfileModel> onlineSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(stagedSearchProfile.get() instanceof AsSimpleSearchProfileModel);
		assertTrue(onlineSearchProfile.get() instanceof AsSimpleSearchProfileModel);
		assertEqualsSimpleSearchProfile((AsSimpleSearchProfileModel) stagedSearchProfile.get(),
				(AsSimpleSearchProfileModel) onlineSearchProfile.get());
	}

	@Test
	public void catAwareSearchProfileNotFoundBeforeSynchronization() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_catAwareSearchProfile.impex",
				"utf-8");

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		// then
		assertFalse(searchProfileResult.isPresent());
	}

	@Test
	public void synchronizeCatAwareSearchProfile() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_catAwareSearchProfile.impex",
				"utf-8");
		final Optional<AbstractAsSearchProfileModel> stagedSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(stagedCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);
		final Optional<AsSearchProfileActivationSetModel> stagedActivationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(stagedCatalogVersion, INDEX_TYPE);

		// when
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AbstractAsSearchProfileModel> onlineSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);
		final Optional<AsSearchProfileActivationSetModel> onlineActivationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(onlineCatalogVersion, INDEX_TYPE);

		// then
		assertTrue(stagedSearchProfile.get() instanceof AsCategoryAwareSearchProfileModel);
		assertTrue(onlineSearchProfile.get() instanceof AsCategoryAwareSearchProfileModel);
		assertEqualsCatAwareSearchProfile((AsCategoryAwareSearchProfileModel) stagedSearchProfile.get(),
				(AsCategoryAwareSearchProfileModel) onlineSearchProfile.get());
		assertEqualsSearchProfileActivationSet(stagedActivationSet, onlineActivationSet);
	}

	@Test
	public void synchronizeCatAwareSearchProfileAfterUpdate1() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_catAwareSearchProfile.impex",
				"utf-8");
		final Optional<AbstractAsSearchProfileModel> stagedSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(stagedCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		// when
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_update1_catAwareSearchProfile.impex",
				"utf-8");
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AbstractAsSearchProfileModel> onlineSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(stagedSearchProfile.get() instanceof AsCategoryAwareSearchProfileModel);
		assertTrue(onlineSearchProfile.get() instanceof AsCategoryAwareSearchProfileModel);
		assertEqualsCatAwareSearchProfile((AsCategoryAwareSearchProfileModel) stagedSearchProfile.get(),
				(AsCategoryAwareSearchProfileModel) onlineSearchProfile.get());
	}

	@Test
	public void synchronizeCatAwareSearchProfileAfterUpdate2() throws Exception
	{
		// given
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_create_catAwareSearchProfile.impex",
				"utf-8");
		final Optional<AbstractAsSearchProfileModel> stagedSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(stagedCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		// when
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);
		importCsv(
				"/adaptivesearch/test/integration/synchronization/catalogVersionSynchronizationTest_update2_catAwareSearchProfile.impex",
				"utf-8");
		performSynchronization(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AbstractAsSearchProfileModel> onlineSearchProfile = asSearchProfileDao
				.findSearchProfileByCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(stagedSearchProfile.get() instanceof AsCategoryAwareSearchProfileModel);
		assertTrue(onlineSearchProfile.get() instanceof AsCategoryAwareSearchProfileModel);
		assertEqualsCatAwareSearchProfile((AsCategoryAwareSearchProfileModel) stagedSearchProfile.get(),
				(AsCategoryAwareSearchProfileModel) onlineSearchProfile.get());
	}

	protected void performSynchronization(final CatalogVersionModel source, final CatalogVersionModel target)
	{
		catalogSynchronizationService.synchronizeFully(source, target);
	}

	protected void assertEqualsSearchProfile(final AbstractAsSearchProfileModel searchProfile1,
			final AbstractAsSearchProfileModel searchProfile2)
	{
		assertEquals(stagedCatalogVersion, searchProfile1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, searchProfile2.getCatalogVersion());
		assertEquals(searchProfile1.getCode(), searchProfile2.getCode());
		assertEquals(searchProfile1.getName(), searchProfile2.getName());
		assertEquals(searchProfile1.getIndexType(), searchProfile2.getIndexType());
	}

	protected void assertEqualsSimpleSearchProfile(final AsSimpleSearchProfileModel searchProfile1,
			final AsSimpleSearchProfileModel searchProfile2)
	{
		assertEqualsSearchProfile(searchProfile1, searchProfile2);
		assertEqualsSimpleSearchConfigurations(searchProfile1.getSearchConfigurations(), searchProfile2.getSearchConfigurations());
	}

	protected void assertEqualsCatAwareSearchProfile(final AsCategoryAwareSearchProfileModel searchProfile1,
			final AsCategoryAwareSearchProfileModel searchProfile2)
	{
		assertEqualsSearchProfile(searchProfile1, searchProfile2);
		assertEqualsCatAwareSearchConfigurations(searchProfile1.getSearchConfigurations(),
				searchProfile2.getSearchConfigurations());
	}

	protected void assertEqualsSearchConfiguration(final AbstractAsConfigurableSearchConfigurationModel searchConfiguration1,
			final AbstractAsConfigurableSearchConfigurationModel searchConfiguration2)
	{
		assertEquals(stagedCatalogVersion, searchConfiguration1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, searchConfiguration2.getCatalogVersion());

		assertEquals(searchConfiguration1.getFacetsMergeMode(), searchConfiguration2.getFacetsMergeMode());
		assertEqualsPromotedFacets(searchConfiguration1.getPromotedFacets(), searchConfiguration2.getPromotedFacets());
		assertEqualsFacets(searchConfiguration1.getFacets(), searchConfiguration2.getFacets());
		assertEqualsExcludedFacets(searchConfiguration1.getExcludedFacets(), searchConfiguration2.getExcludedFacets());

		assertEquals(searchConfiguration1.getBoostItemsMergeMode(), searchConfiguration2.getBoostItemsMergeMode());
		assertEqualsPromotedItems(searchConfiguration1.getPromotedItems(), searchConfiguration2.getPromotedItems());
		assertEqualsExcludedItems(searchConfiguration1.getExcludedItems(), searchConfiguration2.getExcludedItems());

		assertEquals(searchConfiguration1.getBoostRulesMergeMode(), searchConfiguration2.getBoostRulesMergeMode());
		assertEqualsBoostRules(searchConfiguration1.getBoostRules(), searchConfiguration2.getBoostRules());

		assertEquals(searchConfiguration1.getSortsMergeMode(), searchConfiguration2.getSortsMergeMode());
		assertEqualsSorts(searchConfiguration1.getSorts(), searchConfiguration2.getSorts());
	}

	protected void assertEqualsSimpleSearchConfiguration(final AsSimpleSearchConfigurationModel searchConfiguration1,
			final AsSimpleSearchConfigurationModel searchConfiguration2)
	{
		assertEqualsSearchConfiguration(searchConfiguration1, searchConfiguration2);
	}

	protected void assertEqualsSimpleSearchConfigurations(final List<AsSimpleSearchConfigurationModel> searchConfigurations1,
			final List<AsSimpleSearchConfigurationModel> searchConfigurations2)
	{
		assertEquals(searchConfigurations1.size(), searchConfigurations2.size());

		for (int index = 0; index < searchConfigurations1.size(); index++)
		{
			assertEqualsSimpleSearchConfiguration(searchConfigurations1.get(index), searchConfigurations2.get(index));
		}
	}

	protected void assertEqualsCatAwareSearchConfiguration(final AsCategoryAwareSearchConfigurationModel searchConfiguration1,
			final AsCategoryAwareSearchConfigurationModel searchConfiguration2)
	{
		assertEqualsSearchConfiguration(searchConfiguration1, searchConfiguration2);
		assertEqualsCategory(searchConfiguration1.getCategory(), searchConfiguration2.getCategory());
	}

	protected void assertEqualsCatAwareSearchConfigurations(
			final List<AsCategoryAwareSearchConfigurationModel> searchConfigurations1,
			final List<AsCategoryAwareSearchConfigurationModel> searchConfigurations2)
	{
		assertEquals(searchConfigurations1.size(), searchConfigurations2.size());

		for (int index = 0; index < searchConfigurations1.size(); index++)
		{
			assertEqualsCatAwareSearchConfiguration(searchConfigurations1.get(index), searchConfigurations2.get(index));
		}
	}

	protected void assertEqualsFacetConfiguration(final AbstractAsFacetConfigurationModel facetConfiguration1,
			final AbstractAsFacetConfigurationModel facetConfiguration2)
	{
		assertEquals(stagedCatalogVersion, facetConfiguration1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, facetConfiguration2.getCatalogVersion());
		assertEquals(facetConfiguration1.getUid(), facetConfiguration2.getUid());
		assertEquals(facetConfiguration1.getIndexProperty(), facetConfiguration2.getIndexProperty());
		assertEquals(facetConfiguration1.getFacetType(), facetConfiguration2.getFacetType());
		assertEquals(facetConfiguration1.getPriority(), facetConfiguration2.getPriority());
		assertEquals(facetConfiguration1.getValuesSortProvider(), facetConfiguration2.getValuesSortProvider());
		assertEquals(facetConfiguration1.getValuesDisplayNameProvider(), facetConfiguration2.getValuesDisplayNameProvider());
		assertEquals(facetConfiguration1.getTopValuesProvider(), facetConfiguration2.getTopValuesProvider());

	}

	protected void assertEqualsPromotedFacet(final AsPromotedFacetModel promotedFacet1, final AsPromotedFacetModel promotedFacet2)
	{
		assertEqualsFacetConfiguration(promotedFacet1, promotedFacet2);
	}

	protected void assertEqualsPromotedFacets(final List<AsPromotedFacetModel> promotedFacets1,
			final List<AsPromotedFacetModel> promotedFacets2)
	{
		assertEquals(promotedFacets1.size(), promotedFacets2.size());

		for (int index = 0; index < promotedFacets1.size(); index++)
		{
			assertEqualsPromotedFacet(promotedFacets1.get(index), promotedFacets2.get(index));
		}
	}

	protected void assertEqualsFacet(final AsFacetModel facet1, final AsFacetModel facet2)
	{
		assertEqualsFacetConfiguration(facet1, facet2);
	}

	protected void assertEqualsFacets(final List<AsFacetModel> facets1, final List<AsFacetModel> facets2)
	{
		assertEquals(facets1.size(), facets2.size());

		for (int index = 0; index < facets1.size(); index++)
		{
			assertEqualsFacet(facets1.get(index), facets2.get(index));
		}
	}

	protected void assertEqualsExcludedFacet(final AsExcludedFacetModel excludedFacet1, final AsExcludedFacetModel excludedFacet2)
	{
		assertEqualsFacetConfiguration(excludedFacet1, excludedFacet2);
	}

	protected void assertEqualsExcludedFacets(final List<AsExcludedFacetModel> excludedFacets1,
			final List<AsExcludedFacetModel> excludedFacets2)
	{
		assertEquals(excludedFacets1.size(), excludedFacets2.size());

		for (int index = 0; index < excludedFacets1.size(); index++)
		{
			assertEqualsExcludedFacet(excludedFacets1.get(index), excludedFacets2.get(index));
		}
	}

	protected void assertEqualsBoostItemConfiguration(final AbstractAsBoostItemConfigurationModel boostItemConfiguration1,
			final AbstractAsBoostItemConfigurationModel boostItemConfiguration2)
	{
		assertEquals(stagedCatalogVersion, boostItemConfiguration1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, boostItemConfiguration2.getCatalogVersion());
		assertEquals(boostItemConfiguration1.getUid(), boostItemConfiguration2.getUid());
		assertTrue(boostItemConfiguration1.getItem() instanceof ProductModel);
		assertTrue(boostItemConfiguration1.getItem() instanceof ProductModel);
		assertEqualsProduct((ProductModel) boostItemConfiguration1.getItem(), (ProductModel) boostItemConfiguration2.getItem());
	}

	protected void assertEqualsPromotedItem(final AsPromotedItemModel promotedItem1, final AsPromotedItemModel promotedItem2)
	{
		assertEqualsBoostItemConfiguration(promotedItem1, promotedItem2);
	}

	protected void assertEqualsPromotedItems(final List<AsPromotedItemModel> promotedItems1,
			final List<AsPromotedItemModel> promotedItems2)
	{
		assertEquals(promotedItems1.size(), promotedItems2.size());

		for (int index = 0; index < promotedItems1.size(); index++)
		{
			assertEqualsPromotedItem(promotedItems1.get(index), promotedItems2.get(index));
		}
	}

	protected void assertEqualsExcludedItem(final AsExcludedItemModel excludedItem1, final AsExcludedItemModel excludedItem2)
	{
		assertEqualsBoostItemConfiguration(excludedItem1, excludedItem2);
	}

	protected void assertEqualsExcludedItems(final List<AsExcludedItemModel> excludedItems1,
			final List<AsExcludedItemModel> excludedItems2)
	{
		assertEquals(excludedItems1.size(), excludedItems2.size());

		for (int index = 0; index < excludedItems1.size(); index++)
		{
			assertEqualsExcludedItem(excludedItems1.get(index), excludedItems2.get(index));
		}
	}

	protected void assertEqualsBoostRuleConfiguration(final AbstractAsBoostRuleConfigurationModel boostRule1,
			final AbstractAsBoostRuleConfigurationModel boostRule2)
	{
		assertEquals(stagedCatalogVersion, boostRule1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, boostRule2.getCatalogVersion());
		assertEquals(boostRule1.getUid(), boostRule2.getUid());
	}

	protected void assertEqualsBoostRule(final AsBoostRuleModel boostRule1, final AsBoostRuleModel boostRule2)
	{
		assertEqualsBoostRuleConfiguration(boostRule1, boostRule2);

		assertEquals(boostRule1.getIndexProperty(), boostRule2.getIndexProperty());
		assertEquals(boostRule1.getOperator(), boostRule2.getOperator());
		assertEquals(boostRule1.getValue(), boostRule2.getValue());
		assertEquals(boostRule1.getBoostType(), boostRule2.getBoostType());
		assertEquals(boostRule1.getBoost(), boostRule2.getBoost());
	}

	protected void assertEqualsBoostRules(final List<AsBoostRuleModel> boostRules1, final List<AsBoostRuleModel> boostRules2)
	{
		assertEquals(boostRules1.size(), boostRules2.size());

		for (int index = 0; index < boostRules1.size(); index++)
		{
			assertEqualsBoostRule(boostRules1.get(index), boostRules2.get(index));
		}
	}


	protected void assertEqualsCategory(final CategoryModel category1, final CategoryModel category2)
	{
		if (category1 == null && category2 == null)
		{
			return;
		}
		else if (category1 != null && category2 != null)
		{
			assertEquals(stagedCatalogVersion, category1.getCatalogVersion());
			assertEquals(onlineCatalogVersion, category2.getCatalogVersion());
			assertEquals(category1.getCode(), category2.getCode());
		}
		else
		{
			fail("Different categories found: " + category1 + "," + category2);
		}
	}

	protected void assertEqualsProduct(final ProductModel product1, final ProductModel product2)
	{
		if (product1 == null && product2 == null)
		{
			return;
		}
		else if (product1 != null && product2 != null)
		{
			assertEquals(stagedCatalogVersion, product1.getCatalogVersion());
			assertEquals(onlineCatalogVersion, product2.getCatalogVersion());
			assertEquals(product1.getCode(), product2.getCode());
		}
		else
		{
			fail("Different products found: " + product1 + "," + product2);
		}
	}

	protected void assertEqualsSortConfiguration(final AbstractAsSortConfigurationModel sortConfiguration1,
			final AbstractAsSortConfigurationModel sortConfiguration2)
	{
		assertEquals(stagedCatalogVersion, sortConfiguration1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, sortConfiguration2.getCatalogVersion());
		assertEquals(sortConfiguration1.getUid(), sortConfiguration2.getUid());
		assertEquals(sortConfiguration1.getCode(), sortConfiguration2.getCode());
		assertEquals(sortConfiguration1.getName(), sortConfiguration2.getName());
		assertEquals(sortConfiguration1.getName(), sortConfiguration2.getName());

		assertEquals(sortConfiguration1.getExpressions().size(), sortConfiguration2.getExpressions().size());
		for (int index = 0; index < sortConfiguration1.getExpressions().size(); index++)
		{
			assertEqualsSortExpression(sortConfiguration1.getExpressions().get(index),
					sortConfiguration2.getExpressions().get(index));
		}
	}

	protected void assertEqualsSort(final AsSortModel sort1, final AsSortModel sort2)
	{
		assertEqualsSortConfiguration(sort1, sort2);
	}

	protected void assertEqualsSorts(final List<AsSortModel> sorts1, final List<AsSortModel> sorts2)
	{
		assertEquals(sorts1.size(), sorts2.size());

		for (int index = 0; index < sorts1.size(); index++)
		{
			assertEqualsSort(sorts1.get(index), sorts2.get(index));
		}
	}

	protected void assertEqualsSortExpression(final AsSortExpressionModel sortExpression1,
			final AsSortExpressionModel sortExpression2)
	{
		assertEquals(stagedCatalogVersion, sortExpression1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, sortExpression2.getCatalogVersion());
		assertEquals(sortExpression1.getUid(), sortExpression2.getUid());
		assertEquals(sortExpression1.getExpression(), sortExpression2.getExpression());
		assertEquals(sortExpression1.getOrder(), sortExpression2.getOrder());
	}

	protected void assertEqualsSearchProfileActivationSet(final Optional<AsSearchProfileActivationSetModel> activationSetResult1,
			final Optional<AsSearchProfileActivationSetModel> activationSetResult2)
	{
		if (!activationSetResult1.isPresent() && !activationSetResult2.isPresent())
		{
			return;
		}
		else if (activationSetResult1.isPresent() != activationSetResult2.isPresent())
		{
			fail("Different search profile activation sets found: " + activationSetResult1 + "," + activationSetResult2);
		}

		final AsSearchProfileActivationSetModel activationSet1 = activationSetResult1.get();
		final AsSearchProfileActivationSetModel activationSet2 = activationSetResult2.get();

		assertEquals(stagedCatalogVersion, activationSet1.getCatalogVersion());
		assertEquals(onlineCatalogVersion, activationSet2.getCatalogVersion());
		assertEquals(activationSet1.getIndexType(), activationSet2.getIndexType());
		assertEquals(activationSet1.getSearchProfiles().size(), activationSet2.getSearchProfiles().size());

		for (int index = 0; index < activationSet1.getSearchProfiles().size(); index++)
		{
			final AbstractAsSearchProfileModel searchProfile1 = activationSet1.getSearchProfiles().get(index);
			final AbstractAsSearchProfileModel searchProfile2 = activationSet2.getSearchProfiles().get(index);

			assertEquals(stagedCatalogVersion, searchProfile1.getCatalogVersion());
			assertEquals(onlineCatalogVersion, searchProfile2.getCatalogVersion());
			assertEquals(searchProfile1.getCode(), searchProfile2.getCode());
		}
	}
}
