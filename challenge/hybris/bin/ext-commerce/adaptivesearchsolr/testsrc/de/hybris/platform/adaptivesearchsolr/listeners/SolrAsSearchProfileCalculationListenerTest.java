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
package de.hybris.platform.adaptivesearchsolr.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContextFactory;
import de.hybris.platform.adaptivesearch.data.AsBoostRule;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedFacet;
import de.hybris.platform.adaptivesearch.data.AsExcludedItem;
import de.hybris.platform.adaptivesearch.data.AsFacet;
import de.hybris.platform.adaptivesearch.data.AsPromotedFacet;
import de.hybris.platform.adaptivesearch.data.AsPromotedItem;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.data.AsSort;
import de.hybris.platform.adaptivesearch.data.AsSortExpression;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostType;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsSortOrder;
import de.hybris.platform.adaptivesearch.enums.AsSortsMergeMode;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileActivationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileCalculationService;
import de.hybris.platform.adaptivesearch.strategies.impl.DefaultMergeMap;
import de.hybris.platform.adaptivesearchsolr.strategies.SolrAsCatalogVersionResolver;
import de.hybris.platform.adaptivesearchsolr.strategies.SolrAsCategoryPathResolver;
import de.hybris.platform.adaptivesearchsolr.strategies.SolrAsTypeMappingRegistry;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSortField;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.search.BoostField;
import de.hybris.platform.solrfacetsearch.search.BoostField.BoostType;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.QueryOperator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.context.impl.DefaultFacetSearchContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link SolrAsSearchProfileCalculationListener}
 */
@UnitTest
public class SolrAsSearchProfileCalculationListenerTest
{
	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "indexType";

	private static final String INDEX_PROPERTY_1 = "property1";
	private static final String INDEX_PROPERTY_2 = "property2";
	private static final String INDEX_PROPERTY_3 = "property3";

	private static final String SORT1_CODE = "sort1Code";
	private static final String SORT2_CODE = "sort2Code";
	private static final String SORT1_NAME = "sort1Name";
	private static final String SORT2_NAME = "sort2Name";

	private static final String FACET_VALUE_1 = "facetValue1";
	private static final String FACET_VALUE_2 = "facetValue2";
	private static final String FACET_VALUE_3 = "facetValue3";

	private static final PK PK_1 = PK.parse("1");
	private static final PK PK_2 = PK.parse("2");

	private static final String LANGUAGE_CODE = "en";
	private static final String CURRENCY_CODE = "currencyCode";

	@Mock
	private SolrAsCatalogVersionResolver solrAsCatalogVersionResolver;

	@Mock
	private SolrAsCategoryPathResolver solrAsCategoryPathResolver;

	@Mock
	private SolrAsTypeMappingRegistry solrAsTypeMappingRegistry;

	@Mock
	private AsSearchProfileContextFactory asSearchProfileContextFactory;

	@Mock
	private AsSearchProfileActivationService asSearchProfileActivationService;

	@Mock
	private AsSearchProfileCalculationService asSearchProfileCalculationService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private I18NService i18NService;

	@Mock
	private AbstractAsSearchProfileModel searchProfile;

	@Mock
	private AsSearchProfileContext context;

	@Mock
	private CatalogVersionModel catalogVersion1;

	@Mock
	private CatalogVersionModel catalogVersion2;

	@Mock
	private LanguageModel language;

	@Mock
	private CurrencyModel currency;

	@Mock
	private SearchResult searchResult;

	private DefaultFacetSearchContext facetSearchContext;

	private SolrAsSearchProfileCalculationListener listener;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		listener = new SolrAsSearchProfileCalculationListener();
		listener.setSolrAsCatalogVersionResolver(solrAsCatalogVersionResolver);
		listener.setSolrAsCategoryPathResolver(solrAsCategoryPathResolver);
		listener.setSolrAsTypeMappingRegistry(solrAsTypeMappingRegistry);
		listener.setAsSearchProfileContextFactory(asSearchProfileContextFactory);
		listener.setAsSearchProfileActivationService(asSearchProfileActivationService);
		listener.setAsSearchProfileCalculationService(asSearchProfileCalculationService);
		listener.setCommonI18NService(commonI18NService);

		final IndexedType indexedType = new IndexedType();
		final Map<String, IndexedProperty> indexedProperties = new HashMap<>();

		indexedProperties.put(INDEX_PROPERTY_1, new IndexedProperty());
		indexedProperties.put(INDEX_PROPERTY_2, new IndexedProperty());
		indexedProperties.put(INDEX_PROPERTY_3, new IndexedProperty());
		indexedType.setIndexedProperties(indexedProperties);
		indexedType.setIdentifier(INDEX_TYPE);

		final SearchConfig searchConfig = new SearchConfig();

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setName(INDEX_CONFIGURATION);
		facetSearchConfig.setSearchConfig(searchConfig);

		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);
		searchQuery.setLanguage(LANGUAGE_CODE);
		searchQuery.setCurrency(CURRENCY_CODE);
		final Locale currentLocale = new Locale(LANGUAGE_CODE);
		when(commonI18NService.getLanguage(LANGUAGE_CODE)).thenReturn(language);
		when(commonI18NService.getCurrency(CURRENCY_CODE)).thenReturn(currency);
		when(i18NService.getCurrentLocale()).thenReturn(currentLocale);

		facetSearchContext = new DefaultFacetSearchContext();
		facetSearchContext.setFacetSearchConfig(facetSearchConfig);
		facetSearchContext.setIndexedType(indexedType);
		facetSearchContext.setSearchQuery(searchQuery);
	}

	protected AsSearchProfileResult createResult()
	{
		final AsSearchProfileResult result = new AsSearchProfileResult();
		result.setFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		result.setPromotedFacets(new DefaultMergeMap<>());
		result.setFacets(new DefaultMergeMap<>());
		result.setExcludedFacets(new DefaultMergeMap<>());
		result.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);
		result.setPromotedItems(new DefaultMergeMap<>());
		result.setExcludedItems(new DefaultMergeMap<>());
		result.setBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		result.setBoostRules(new ArrayList<>());
		result.setSortsMergeMode(AsSortsMergeMode.ADD_AFTER);
		result.setPromotedSorts(new DefaultMergeMap<>());
		result.setSorts(new DefaultMergeMap<>());
		result.setExcludedSorts(new DefaultMergeMap<>());
		return result;
	}

	protected <T, R> AsConfigurationHolder<T, R> createConfigurationHolder(final T configuration)
	{
		final AsConfigurationHolder<T, R> configurationHolder = new AsConfigurationHolder<>();
		configurationHolder.setConfiguration(configuration);

		return configurationHolder;
	}

	protected <T, R> AsConfigurationHolder<T, R> createConfigurationHolder(final T configuration, final Object data)
	{
		final AsConfigurationHolder<T, R> configurationHolder = new AsConfigurationHolder<>();
		configurationHolder.setConfiguration(configuration);
		configurationHolder.setData(data);

		return configurationHolder;
	}

	@Test
	public void calculateWithLegacyMode() throws Exception
	{
		// given
		facetSearchContext.getFacetSearchConfig().getSearchConfig().setLegacyMode(true);

		// when
		listener.beforeSearch(facetSearchContext);

		// then
		verify(asSearchProfileCalculationService, never()).createResult(context);
		verify(asSearchProfileCalculationService, never()).calculate(Mockito.any(AsSearchProfileContext.class),
				Mockito.any(AsSearchProfileResult.class), Mockito.any(List.class));
	}

	@Test
	public void calculate() throws Exception
	{
		// given
		final SearchQuery searchQuery = facetSearchContext.getSearchQuery();
		final AsSearchProfileResult searchQueryResult = createResult();
		final AsSearchProfileActivationGroup group = new AsSearchProfileActivationGroup();
		final List<AsSearchProfileActivationGroup> groups = Collections.singletonList(group);
		group.setSearchProfiles(Collections.singletonList(searchProfile));

		final AsSearchProfileResult result = createResult();

		final List<CatalogVersionModel> catalogVersions = Collections.singletonList(catalogVersion1);
		final List<CatalogVersionModel> sessionCatalogVersions = Arrays.asList(catalogVersion1, catalogVersion2);
		final List<CategoryModel> categoryPath = new ArrayList<>();

		facetSearchContext.setParentSessionCatalogVersions(sessionCatalogVersions);

		when(solrAsCatalogVersionResolver.resolveCatalogVersions(searchQuery)).thenReturn(catalogVersions);
		when(solrAsCategoryPathResolver.resolveCategoryPath(searchQuery, catalogVersions)).thenReturn(categoryPath);
		when(asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE, catalogVersions, sessionCatalogVersions,
				categoryPath, language, currency)).thenReturn(context);
		when(asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)).thenReturn(groups);
		when(asSearchProfileCalculationService.createResult(context)).thenReturn(searchQueryResult);
		when(asSearchProfileCalculationService.calculateGroups(context, searchQueryResult, groups)).thenReturn(result);

		// when
		listener.beforeSearch(facetSearchContext);

		// then
		verify(asSearchProfileCalculationService).createResult(context);
		verify(asSearchProfileCalculationService).calculateGroups(context, searchQueryResult, groups);
	}

	@Test
	public void createFromFacetSearchContext() throws Exception
	{
		// given
		final SearchQuery searchQuery = facetSearchContext.getSearchQuery();

		final FacetField facetField = new FacetField(INDEX_PROPERTY_1, FacetType.MULTISELECTAND);
		facetField.setPriority(Integer.valueOf(100));

		final BoostField boostField = new BoostField(INDEX_PROPERTY_2, QueryOperator.EQUAL_TO, "abc", Float.valueOf(1.2f),
				BoostType.MULTIPLICATIVE);

		searchQuery.addFacet(facetField);
		searchQuery.addBoost(boostField);
		searchQuery.addPromotedItem(PK_1);
		searchQuery.addExcludedItem(PK_2);

		final IndexedTypeSort indexedTypeSort1 = buildIndexedTypeSort(SORT1_CODE);
		indexedTypeSort1.setFields(
				Arrays.asList(buildIndexedTypeSortField(INDEX_PROPERTY_1, true), buildIndexedTypeSortField(INDEX_PROPERTY_2, false)));
		final IndexedTypeSort indexedTypeSort2 = buildIndexedTypeSort(SORT2_CODE);
		indexedTypeSort2.setFields(Arrays.asList(buildIndexedTypeSortField(INDEX_PROPERTY_3, true)));

		facetSearchContext.setAvailableNamedSorts(Arrays.asList(indexedTypeSort1, indexedTypeSort2));

		when(solrAsTypeMappingRegistry.toAsFacetType(FacetType.MULTISELECTAND)).thenReturn(AsFacetType.MULTISELECT_AND);
		when(solrAsTypeMappingRegistry.toAsBoostType(BoostType.MULTIPLICATIVE)).thenReturn(AsBoostType.MULTIPLICATIVE);

		final AsSearchProfileResult expectedSearchQueryResult = createResult();
		when(asSearchProfileCalculationService.createResult(context)).thenReturn(expectedSearchQueryResult);

		when(asSearchProfileCalculationService.createConfigurationHolder(any(), any()))
				.thenAnswer(invocation -> createConfigurationHolder(invocation.getArguments()[1]));

		when(asSearchProfileCalculationService.createConfigurationHolder(any(), any(), any()))
				.thenAnswer(invocation -> createConfigurationHolder(invocation.getArguments()[1], invocation.getArguments()[2]));

		// when
		final AsSearchProfileResult searchQueryResult = listener.createResultFromFacetSearchContext(context, facetSearchContext);

		// then
		assertEquals(0, searchQueryResult.getPromotedFacets().size());

		assertEquals(1, searchQueryResult.getFacets().size());
		final AsFacet facet = searchQueryResult.getFacets().get(INDEX_PROPERTY_1).getConfiguration();
		assertEquals(INDEX_PROPERTY_1, facet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, facet.getFacetType());
		assertEquals(Integer.valueOf(100), facet.getPriority());

		assertEquals(0, searchQueryResult.getExcludedFacets().size());

		assertEquals(1, searchQueryResult.getBoostRules().size());
		final AsBoostRule boostRule = searchQueryResult.getBoostRules().get(0).getConfiguration();
		assertEquals(INDEX_PROPERTY_2, boostRule.getIndexProperty());
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule.getBoostType());
		assertEquals(Float.valueOf(1.2f), boostRule.getBoost());

		assertEquals(1, searchQueryResult.getPromotedItems().size());
		final AsPromotedItem promotedItem = searchQueryResult.getPromotedItems().get(PK_1).getConfiguration();
		assertEquals(PK_1, promotedItem.getItemPk());

		assertEquals(1, searchQueryResult.getExcludedItems().size());
		final AsExcludedItem excludedItem = searchQueryResult.getExcludedItems().get(PK_2).getConfiguration();
		assertEquals(PK_2, excludedItem.getItemPk());

		assertEquals(2, searchQueryResult.getSorts().size());
		assertNotNull(searchQueryResult.getSorts().get(SORT1_CODE));
		final AsSort asSort1 = searchQueryResult.getSorts().get(SORT1_CODE).getConfiguration();
		assertEquals(2, asSort1.getExpressions().size());
		Assertions.assertThat(asSort1.getExpressions().stream().map(AsSortExpression::getExpression).collect(Collectors.toList()))
				.containsExactly(INDEX_PROPERTY_1, INDEX_PROPERTY_2);

		assertNotNull(searchQueryResult.getSorts().get(SORT2_CODE));
		final AsSort asSort2 = searchQueryResult.getSorts().get(SORT2_CODE).getConfiguration();
		assertEquals(1, asSort2.getExpressions().size());
		Assertions.assertThat(asSort2.getExpressions().stream().map(AsSortExpression::getExpression).collect(Collectors.toList()))
				.containsExactly(INDEX_PROPERTY_3);
	}

	@Test
	public void applyResult() throws Exception
	{
		// given
		final SearchQuery searchQuery = facetSearchContext.getSearchQuery();
		final AsSearchProfileResult result = prepareAsSearchResult();

		// when
		listener.applyResult(facetSearchContext, result);

		// then
		assertEquals(2, searchQuery.getFacets().size());

		final FacetField promotedFacetField = searchQuery.getFacets().get(0);
		assertEquals(INDEX_PROPERTY_1, promotedFacetField.getField());
		assertEquals(FacetType.MULTISELECTOR, promotedFacetField.getFacetType());
		assertEquals(Integer.valueOf(Integer.MAX_VALUE), promotedFacetField.getPriority());

		final FacetField facetField = searchQuery.getFacets().get(1);
		assertEquals(INDEX_PROPERTY_2, facetField.getField());
		assertEquals(FacetType.MULTISELECTAND, facetField.getFacetType());

		assertEquals(1, searchQuery.getBoosts().size());
		final BoostField boostField = searchQuery.getBoosts().get(0);
		assertEquals(INDEX_PROPERTY_2, boostField.getField());
		assertEquals(BoostType.MULTIPLICATIVE, boostField.getBoostType());
		assertEquals(Float.valueOf(1.4f), boostField.getBoostValue());

		assertEquals(1, searchQuery.getPromotedItems().size());
		assertEquals(PK_1, searchQuery.getPromotedItems().get(0));

		assertEquals(1, searchQuery.getExcludedItems().size());
		assertEquals(PK_2, searchQuery.getExcludedItems().get(0));

		assertEquals(2, facetSearchContext.getAvailableNamedSorts().size());

		Assertions
				.assertThat(
						facetSearchContext.getAvailableNamedSorts().stream().map(IndexedTypeSort::getCode).collect(Collectors.toList()))
				.containsExactly(SORT1_CODE, SORT2_CODE);
		final IndexedTypeSort indexedTypeSort1 = facetSearchContext.getAvailableNamedSorts().get(0);
		Assertions
				.assertThat(
						indexedTypeSort1.getFields().stream().map(IndexedTypeSortField::getFieldName).collect(Collectors.toList()))
				.containsExactly(INDEX_PROPERTY_1, INDEX_PROPERTY_2);
		final IndexedTypeSort indexedTypeSort2 = facetSearchContext.getAvailableNamedSorts().get(1);
		Assertions
				.assertThat(
						indexedTypeSort2.getFields().stream().map(IndexedTypeSortField::getFieldName).collect(Collectors.toList()))
				.containsExactly(INDEX_PROPERTY_3);

	}

	@Test
	public void applyResultWithMissingIndexedProperties() throws Exception
	{
		// given
		final SearchQuery searchQuery = facetSearchContext.getSearchQuery();
		facetSearchContext.getIndexedType().getIndexedProperties().remove(INDEX_PROPERTY_2);
		final AsSearchProfileResult result = prepareAsSearchResult();

		// when
		listener.applyResult(facetSearchContext, result);

		// then
		assertEquals(1, searchQuery.getFacets().size());

		final FacetField promotedFacetField = searchQuery.getFacets().get(0);
		assertEquals(INDEX_PROPERTY_1, promotedFacetField.getField());
		assertEquals(FacetType.MULTISELECTOR, promotedFacetField.getFacetType());
		assertEquals(Integer.valueOf(Integer.MAX_VALUE), promotedFacetField.getPriority());

		assertEquals(0, searchQuery.getBoosts().size());

		assertEquals(1, searchQuery.getPromotedItems().size());
		assertEquals(PK_1, searchQuery.getPromotedItems().get(0));

		assertEquals(1, searchQuery.getExcludedItems().size());
		assertEquals(PK_2, searchQuery.getExcludedItems().get(0));

		assertEquals(2, facetSearchContext.getAvailableNamedSorts().size());

		Assertions
				.assertThat(
						facetSearchContext.getAvailableNamedSorts().stream().map(IndexedTypeSort::getCode).collect(Collectors.toList()))
				.containsExactly(SORT1_CODE, SORT2_CODE);
		final IndexedTypeSort indexedTypeSort1 = facetSearchContext.getAvailableNamedSorts().get(0);
		Assertions
				.assertThat(
						indexedTypeSort1.getFields().stream().map(IndexedTypeSortField::getFieldName).collect(Collectors.toList()))
				.containsExactly(INDEX_PROPERTY_1);
		final IndexedTypeSort indexedTypeSort2 = facetSearchContext.getAvailableNamedSorts().get(1);
		Assertions
				.assertThat(
						indexedTypeSort2.getFields().stream().map(IndexedTypeSortField::getFieldName).collect(Collectors.toList()))
				.containsExactly(INDEX_PROPERTY_3);

	}

	protected AsSearchProfileResult prepareAsSearchResult()
	{
		final SearchQuery searchQuery = facetSearchContext.getSearchQuery();

		final AsPromotedFacet promotedFacet = new AsPromotedFacet();
		promotedFacet.setIndexProperty(INDEX_PROPERTY_1);
		promotedFacet.setFacetType(AsFacetType.MULTISELECT_OR);

		final AsFacet facet = new AsFacet();
		facet.setIndexProperty(INDEX_PROPERTY_2);
		facet.setFacetType(AsFacetType.MULTISELECT_AND);
		facet.setPriority(Integer.valueOf(230));

		final AsExcludedFacet excludedFacet = new AsExcludedFacet();
		excludedFacet.setIndexProperty(INDEX_PROPERTY_3);

		final AsBoostRule boostRule = new AsBoostRule();
		boostRule.setIndexProperty(INDEX_PROPERTY_2);
		boostRule.setBoostType(AsBoostType.MULTIPLICATIVE);
		boostRule.setBoost(Float.valueOf(1.4f));

		final AsPromotedItem promotedItem = new AsPromotedItem();
		promotedItem.setItemPk(PK_1);

		final AsExcludedItem excludedItem = new AsExcludedItem();
		excludedItem.setItemPk(PK_2);

		final AsSort sort1 = new AsSort();
		sort1.setCode(SORT1_CODE);
		sort1.setName(buildLocalizedNameMap(SORT1_NAME));
		sort1.setExpressions(Arrays.asList(buildAsSortExpression(INDEX_PROPERTY_1, AsSortOrder.ASCENDING),
				buildAsSortExpression(INDEX_PROPERTY_2, AsSortOrder.DESCENDING)));
		final AsSort sort2 = new AsSort();
		sort2.setCode(SORT2_CODE);
		sort2.setName(buildLocalizedNameMap(SORT2_NAME));
		sort2.setExpressions(Arrays.asList(buildAsSortExpression(INDEX_PROPERTY_3, AsSortOrder.DESCENDING)));

		final AsSearchProfileResult result = createResult();
		result.getPromotedFacets().put(promotedFacet.getIndexProperty(), createConfigurationHolder(promotedFacet));
		result.getFacets().put(facet.getIndexProperty(), createConfigurationHolder(facet));
		result.getExcludedFacets().put(excludedFacet.getIndexProperty(), createConfigurationHolder(excludedFacet));
		result.getBoostRules().add(createConfigurationHolder(boostRule));
		result.getPromotedItems().put(promotedItem.getItemPk(), createConfigurationHolder(promotedItem));
		result.getExcludedItems().put(excludedItem.getItemPk(), createConfigurationHolder(excludedItem));
		result.getSorts().put(SORT1_CODE, createConfigurationHolder(sort1));
		result.getSorts().put(SORT2_CODE, createConfigurationHolder(sort2));

		searchQuery.addFacet(INDEX_PROPERTY_3);

		when(solrAsTypeMappingRegistry.toFacetType(AsFacetType.MULTISELECT_AND)).thenReturn(FacetType.MULTISELECTAND);
		when(solrAsTypeMappingRegistry.toFacetType(AsFacetType.MULTISELECT_OR)).thenReturn(FacetType.MULTISELECTOR);
		when(solrAsTypeMappingRegistry.toBoostType(AsBoostType.MULTIPLICATIVE)).thenReturn(BoostType.MULTIPLICATIVE);

		return result;
	}

	private AsSortExpression buildAsSortExpression(final String indexProperty, final AsSortOrder asSortOrder)
	{
		final AsSortExpression asSortExpression = new AsSortExpression();
		asSortExpression.setOrder(asSortOrder);
		asSortExpression.setExpression(indexProperty);
		return asSortExpression;
	}

	private Map<String, String> buildLocalizedNameMap(final String name)
	{
		final LinkedHashMap<String, String> localizedNameMap = new LinkedHashMap<>();
		localizedNameMap.put(LANGUAGE_CODE, name);

		return localizedNameMap;
	}

	private IndexedTypeSort buildIndexedTypeSort(final String code)
	{
		final IndexedTypeSort indexedTypeSort = new IndexedTypeSort();
		indexedTypeSort.setCode(code);
		return indexedTypeSort;
	}

	private IndexedTypeSortField buildIndexedTypeSortField(final String fieldName, final boolean ascending)
	{
		final IndexedTypeSortField indexedTypeSortField = new IndexedTypeSortField();
		indexedTypeSortField.setFieldName(fieldName);
		indexedTypeSortField.setAscending(ascending);
		return indexedTypeSortField;
	}
}
