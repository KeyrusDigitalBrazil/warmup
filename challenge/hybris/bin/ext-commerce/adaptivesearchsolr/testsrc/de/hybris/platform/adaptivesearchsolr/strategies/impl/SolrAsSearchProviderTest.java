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
package de.hybris.platform.adaptivesearchsolr.strategies.impl;

import static de.hybris.platform.adaptivesearchsolr.constants.AdaptivesearchsolrConstants.SCORE_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.impl.DefaultAsSearchProfileContext;
import de.hybris.platform.adaptivesearch.data.AsExpressionData;
import de.hybris.platform.adaptivesearch.data.AsFacetData;
import de.hybris.platform.adaptivesearch.data.AsIndexConfigurationData;
import de.hybris.platform.adaptivesearch.data.AsIndexPropertyData;
import de.hybris.platform.adaptivesearch.data.AsIndexTypeData;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.data.AsSearchQueryData;
import de.hybris.platform.adaptivesearch.data.AsSearchResultData;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.daos.SolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.daos.SolrIndexedPropertyDao;
import de.hybris.platform.solrfacetsearch.daos.SolrIndexedTypeDao;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultDocument;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;
import de.hybris.platform.solrfacetsearch.solr.IndexedPropertyTypeInfo;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexedPropertyTypeRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class SolrAsSearchProviderTest
{
	private static final String ITEM_TYPE_CODE = "itemTypeCode";

	private static final String CATALOG_VERSION = "catalogVersion";

	private static final String CATALOG_ID = "catalogID";
	private static final String CATALOG_NAME = "catalogName";

	private static final String LANGUAGE_CODE = "EN";
	private static final String CURRENCY_CODE = "USD";

	private static final String FACET_SEARCH_CONFIG_NAME = "facetSearchConfig";
	private static final String FACET_SEARCH_CONFIG_DESCRIPTION = "description";

	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "indexType";
	private static final String INDEX_PROPERTY = "indexedProperty";

	private static final String INVALID_INDEX_PROPERTY = "123, invalid index property __";

	private static final String FACET_NAME = "facet";
	private static final String FACET_VALUE_NAME = "facetValue";
	private static final String ADAPTIVE_SEARCH_RESULT = "adaptiveSearchResult";
	private static final String DEFAULT_QUERY_TEMPLATE = "DEFAULT";
	private static final String SEARCH_TEXT = "searchText";
	private static final int ACTIVE_PAGE = 1;
	private static final int PAGE_SIZE = 20;
	private static final int OFFSET = 1;

	private SolrAsSearchProvider solrAsSearchProvider;

	@Mock
	private SolrFacetSearchConfigDao solrFacetSearchConfigDao;

	@Mock
	private SolrIndexedTypeDao solrIndexedTypeDao;

	@Mock
	private SolrIndexedPropertyDao solrIndexedPropertyDao;

	@Mock
	private SolrIndexedPropertyTypeRegistry solrIndexedPropertyTypeRegistry;

	@Mock
	private FacetSearchConfigService facetSearchConfigService;

	@Mock
	private FacetSearchService facetSearchService;

	@Mock
	private SolrIndexedTypeModel indexedType;

	@Mock
	private ComposedTypeModel itemType;

	@Mock
	private SolrFacetSearchConfigModel solrIndexConfiguration;

	@Mock
	private SolrIndexedPropertyModel solrIndexedProperty;

	@Mock
	private SolrIndexedPropertyModel solrIndexedPropertyWithInvalidName;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CatalogModel catalog;

	@Mock
	private LanguageModel language;

	@Mock
	private CurrencyModel currency;

	@Mock
	private SessionService sessionService;

	@Mock
	private I18NService i18nService;

	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		solrAsSearchProvider = new SolrAsSearchProvider();
		solrAsSearchProvider.setSolrFacetSearchConfigDao(solrFacetSearchConfigDao);
		solrAsSearchProvider.setSolrIndexedTypeDao(solrIndexedTypeDao);
		solrAsSearchProvider.setSolrIndexedPropertyDao(solrIndexedPropertyDao);
		solrAsSearchProvider.setSolrIndexedPropertyTypeRegistry(solrIndexedPropertyTypeRegistry);
		solrAsSearchProvider.setFacetSearchConfigService(facetSearchConfigService);
		solrAsSearchProvider.setFacetSearchService(facetSearchService);
		solrAsSearchProvider.setSessionService(sessionService);
		solrAsSearchProvider.setI18nService(i18nService);
		solrAsSearchProvider.setCommonI18NService(commonI18NService);

		when(indexedType.getIdentifier()).thenReturn(INDEX_PROPERTY);
		when(indexedType.getType()).thenReturn(itemType);

		when(itemType.getCode()).thenReturn(ITEM_TYPE_CODE);
		when(itemType.getCatalogItemType()).thenReturn(Boolean.TRUE);

		when(solrIndexConfiguration.getSolrIndexedTypes()).thenReturn(Collections.singletonList(indexedType));
		when(solrIndexConfiguration.getCatalogVersions()).thenReturn(Collections.singletonList(catalogVersion));

		when(solrIndexedProperty.getName()).thenReturn(INDEX_PROPERTY);
		when(solrIndexedProperty.getType()).thenReturn(SolrPropertiesTypes.STRING);

		when(solrIndexedPropertyWithInvalidName.getName()).thenReturn(INVALID_INDEX_PROPERTY);
		when(solrIndexedPropertyWithInvalidName.getType()).thenReturn(SolrPropertiesTypes.STRING);

		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		when(catalogVersion.getActive()).thenReturn(Boolean.TRUE);

		when(catalog.getId()).thenReturn(CATALOG_ID);
		when(catalog.getName()).thenReturn(CATALOG_NAME);

		when(solrIndexConfiguration.getLanguages()).thenReturn(Collections.singletonList(language));
		when(solrIndexConfiguration.getCurrencies()).thenReturn(Collections.singletonList(currency));

		when(this.language.getIsocode()).thenReturn(LANGUAGE_CODE);
		when(this.currency.getIsocode()).thenReturn(CURRENCY_CODE);

		when(sessionService.executeInLocalView(Mockito.any(SessionExecutionBody.class))).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final SessionExecutionBody args = (SessionExecutionBody) invocation.getArguments()[0];
				return args.execute();
			}
		});
	}

	@Test
	public void testGetIndexConfigurations() throws Exception
	{
		// given
		final SolrFacetSearchConfigModel solrFacetSearchConfig = new SolrFacetSearchConfigModel();
		solrFacetSearchConfig.setName(FACET_SEARCH_CONFIG_NAME);
		solrFacetSearchConfig.setDescription(FACET_SEARCH_CONFIG_DESCRIPTION);

		when(solrFacetSearchConfigDao.findAllFacetSearchConfigs()).thenReturn(Collections.singletonList(solrFacetSearchConfig));

		// when
		final List<AsIndexConfigurationData> indexConfigurations = solrAsSearchProvider.getIndexConfigurations();

		// then
		assertTrue(CollectionUtils.isNotEmpty(indexConfigurations));
		assertEquals(1, indexConfigurations.size());

		final AsIndexConfigurationData indexConfiguration = indexConfigurations.get(0);
		assertEquals(solrFacetSearchConfig.getDescription(), indexConfiguration.getName());
	}

	@Test
	public void testGetIndexTypes() throws Exception
	{
		// given
		when(solrIndexedTypeDao.findAllIndexedTypes()).thenReturn(Collections.singletonList(indexedType));

		// when
		final List<AsIndexTypeData> indexTypes = solrAsSearchProvider.getIndexTypes();

		// then
		assertTrue(CollectionUtils.isNotEmpty(indexTypes));
		assertEquals(1, indexTypes.size());

		final AsIndexTypeData indexType = indexTypes.get(0);
		assertEquals(indexedType.getIdentifier(), indexType.getName());
		assertEquals(indexedType.getIdentifier(), indexType.getCode());
		assertEquals(itemType.getCode(), indexType.getItemType());
	}

	@Test
	public void testGetIndexTypes1() throws Exception
	{
		// given
		when(solrFacetSearchConfigDao.findFacetSearchConfigByName(INDEX_CONFIGURATION)).thenReturn(solrIndexConfiguration);

		// when
		final List<AsIndexTypeData> indexTypes = solrAsSearchProvider.getIndexTypes(INDEX_CONFIGURATION);

		// then
		assertTrue(CollectionUtils.isNotEmpty(indexTypes));
		assertEquals(1, indexTypes.size());

		final AsIndexTypeData indexType = indexTypes.get(0);
		assertEquals(indexedType.getIdentifier(), indexType.getName());
		assertEquals(indexedType.getIdentifier(), indexType.getCode());
		assertEquals(itemType.getCode(), indexType.getItemType());
	}

	@Test
	public void testGetIndexTypeForCode() throws Exception
	{
		// given
		when(solrIndexedTypeDao.findIndexedTypeByIdentifier(INDEX_TYPE)).thenReturn(indexedType);

		// when
		final Optional<AsIndexTypeData> indexTypeData = solrAsSearchProvider.getIndexTypeForCode(INDEX_TYPE);

		// then
		assertTrue(indexTypeData.isPresent());
		assertEquals(indexedType.getIdentifier(), indexTypeData.get().getName());
		assertEquals(indexedType.getIdentifier(), indexTypeData.get().getCode());
		assertEquals(itemType.getCode(), indexTypeData.get().getItemType());
	}

	@Test
	public void testGetIndexProperties() throws Exception
	{
		// given
		when(solrIndexedTypeDao.findIndexedTypeByIdentifier(INDEX_TYPE)).thenReturn(indexedType);
		when(solrIndexedPropertyDao.findIndexedPropertiesByIndexedType(indexedType))
				.thenReturn(Collections.singletonList(solrIndexedProperty));
		when(solrIndexedPropertyTypeRegistry.getIndexPropertyTypeInfo(solrIndexedProperty.getType().getCode()))
				.thenReturn(mock(IndexedPropertyTypeInfo.class));

		// when
		final List<AsIndexPropertyData> indexProperties = solrAsSearchProvider.getIndexProperties(INDEX_TYPE);

		// then
		assertTrue(CollectionUtils.isNotEmpty(indexProperties));
		assertEquals(1, indexProperties.size());

		final AsIndexPropertyData indexPropertyData = indexProperties.get(0);
		assertEquals(INDEX_PROPERTY, indexPropertyData.getCode());
	}

	@Test
	public void testGetIndexPropertyForCode() throws Exception
	{
		// given
		when(solrIndexedTypeDao.findIndexedTypeByIdentifier(INDEX_TYPE)).thenReturn(indexedType);
		when(solrIndexedPropertyDao.findIndexedPropertyByName(indexedType, INDEX_PROPERTY)).thenReturn(solrIndexedProperty);
		when(solrIndexedPropertyTypeRegistry.getIndexPropertyTypeInfo(solrIndexedProperty.getType().getCode()))
				.thenReturn(mock(IndexedPropertyTypeInfo.class));
		// when
		final Optional<AsIndexPropertyData> indexPropertyData = solrAsSearchProvider.getIndexPropertyForCode(INDEX_TYPE,
				INDEX_PROPERTY);

		// then
		assertTrue(indexPropertyData.isPresent());
		assertEquals(INDEX_PROPERTY, indexPropertyData.get().getCode());
	}

	@Test
	public void testGetSupportedCatalogVersions() throws Exception
	{
		// given
		when(solrFacetSearchConfigDao.findFacetSearchConfigByName(INDEX_CONFIGURATION)).thenReturn(solrIndexConfiguration);

		// when
		final List<CatalogVersionModel> catalogVersions = solrAsSearchProvider.getSupportedCatalogVersions(INDEX_CONFIGURATION,
				INDEX_TYPE);

		// then
		assertThat(catalogVersions).hasSize(1);
		assertThat(catalogVersions).contains(catalogVersion);
	}

	@Test
	public void testGetSupportedLanguages() throws Exception
	{
		// given
		when(solrFacetSearchConfigDao.findFacetSearchConfigByName(INDEX_CONFIGURATION)).thenReturn(solrIndexConfiguration);

		// when
		final List<LanguageModel> languages = solrAsSearchProvider.getSupportedLanguages(INDEX_CONFIGURATION, INDEX_TYPE);

		// then
		assertThat(languages).hasSize(1);
		assertThat(languages).contains(language);
	}

	@Test
	public void testGetSupportedCurrencies() throws Exception
	{
		// given
		when(solrFacetSearchConfigDao.findFacetSearchConfigByName(INDEX_CONFIGURATION)).thenReturn(solrIndexConfiguration);

		// when
		final List<CurrencyModel> currencies = solrAsSearchProvider.getSupportedCurrencies(INDEX_CONFIGURATION, INDEX_TYPE);

		// then
		assertThat(currencies).hasSize(1);
		assertThat(currencies).contains(currency);
	}

	@Test
	public void testGetSupportedFacetIndexProperties() throws Exception
	{
		// given
		final IndexedPropertyTypeInfo indexedPropertyTypeInfo = new IndexedPropertyTypeInfo();
		indexedPropertyTypeInfo.setAllowFacet(true);
		indexedPropertyTypeInfo.setSupportedQueryOperators(Collections.emptySet());

		when(solrIndexedTypeDao.findIndexedTypeByIdentifier(INDEX_TYPE)).thenReturn(indexedType);
		when(solrIndexedPropertyDao.findIndexedPropertiesByIndexedType(indexedType))
				.thenReturn(Collections.singletonList(solrIndexedProperty));
		when(solrIndexedPropertyTypeRegistry.getIndexPropertyTypeInfo(solrIndexedProperty.getType().getCode()))
				.thenReturn(indexedPropertyTypeInfo);

		// when
		final List<AsIndexPropertyData> indexProperties = solrAsSearchProvider.getSupportedFacetIndexProperties(INDEX_TYPE);

		// then
		assertTrue(CollectionUtils.isNotEmpty(indexProperties));
		assertEquals(1, indexProperties.size());

		final AsIndexPropertyData indexProperty = indexProperties.get(0);
		assertEquals(INDEX_PROPERTY, indexProperty.getCode());
	}

	@Test
	public void testGetSupportedSortExpressions() throws Exception
	{
		// given
		when(solrIndexedTypeDao.findIndexedTypeByIdentifier(INDEX_TYPE)).thenReturn(indexedType);
		when(solrIndexedPropertyDao.findIndexedPropertiesByIndexedType(indexedType))
				.thenReturn(Arrays.asList(solrIndexedProperty, solrIndexedPropertyWithInvalidName));
		when(solrIndexedPropertyTypeRegistry.getIndexPropertyTypeInfo(solrIndexedProperty.getType().getCode()))
				.thenReturn(mock(IndexedPropertyTypeInfo.class));
		when(solrIndexedPropertyTypeRegistry.getIndexPropertyTypeInfo(solrIndexedPropertyWithInvalidName.getType().getCode()))
				.thenReturn(mock(IndexedPropertyTypeInfo.class));

		// when
		final List<AsExpressionData> expressions = solrAsSearchProvider.getSupportedSortExpressions(INDEX_TYPE);

		// then
		assertTrue(CollectionUtils.isNotEmpty(expressions));
		assertEquals(2, expressions.size());

		final AsExpressionData expression1 = expressions.get(0);
		assertEquals(SCORE_FIELD, expression1.getExpression());

		final AsExpressionData expression2 = expressions.get(1);
		assertEquals(INDEX_PROPERTY, expression2.getExpression());
	}

	@Test
	public void testSearch() throws Exception
	{
		// given
		final AsSearchProfileContext searchProfileContext = buildSearchProfileContext();

		final IndexedType indexedType = new IndexedType();
		indexedType.setIdentifier(INDEX_TYPE);

		final FacetSearchConfig facetSearchConfig = buildFacetSearchConfig(indexedType);

		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);
		searchQuery.setOffset(OFFSET);
		searchQuery.setPageSize(PAGE_SIZE);

		when(facetSearchConfigService.getConfiguration(any(String.class))).thenReturn(facetSearchConfig);

		when(facetSearchService.createFreeTextSearchQueryFromTemplate(facetSearchConfig, indexedType, DEFAULT_QUERY_TEMPLATE,
				SEARCH_TEXT)).thenReturn(searchQuery);

		when(facetSearchService.search(Matchers.eq(searchQuery))).thenReturn(buildSearchResult(searchQuery));

		final AsSearchQueryData asSearchQuery = buildAsSearchQueryData(SEARCH_TEXT, ACTIVE_PAGE, PAGE_SIZE);

		// when
		final AsSearchResultData asSearchResult = solrAsSearchProvider.search(searchProfileContext, asSearchQuery);

		// then
		assertNotNull(asSearchResult);

		final List<AsFacetData> facets = asSearchResult.getFacets();
		assertTrue(CollectionUtils.isNotEmpty(facets));
		assertEquals(1, facets.size());

		final AsFacetData facetData = facets.get(0);
		assertEquals(FACET_NAME, facetData.getName());
		assertEquals(FACET_VALUE_NAME, facetData.getValues().get(0).getName());

		assertTrue(CollectionUtils.isNotEmpty(asSearchResult.getResults()));

		assertEquals(PAGE_SIZE, asSearchResult.getPageSize());
		assertEquals(1, asSearchResult.getPageCount());
		assertEquals(1, asSearchResult.getActivePage());
		assertEquals(20, asSearchResult.getResultCount());

		assertNotNull(asSearchResult.getSearchProfileResult());
	}

	protected AsSearchProfileContext buildSearchProfileContext()
	{
		final DefaultAsSearchProfileContext searchProfileContext = new DefaultAsSearchProfileContext();
		searchProfileContext.setIndexType(INDEX_TYPE);
		searchProfileContext.setIndexConfiguration("indexConfiguration");
		searchProfileContext.setCatalogVersions(Collections.singletonList(new CatalogVersionModel()));
		searchProfileContext.setCategoryPath(Collections.emptyList());
		searchProfileContext.setLanguage(language);
		searchProfileContext.setCurrency(currency);

		return searchProfileContext;
	}

	protected FacetSearchConfig buildFacetSearchConfig(final IndexedType indexedType)
	{
		final IndexConfig indexConfig = new IndexConfig();
		indexConfig.setIndexedTypes(Collections.singletonMap("", indexedType));

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setIndexConfig(indexConfig);

		return facetSearchConfig;
	}

	protected SearchResult buildSearchResult(final SearchQuery searchQuery)
	{
		final SolrSearchResult searchResult = new SolrSearchResult();
		final FacetValue facetValue = new FacetValue(FACET_VALUE_NAME, 10l, true);
		final Facet facet = new Facet(FACET_NAME, Collections.singletonList(facetValue));

		searchResult.setFacetsMap(Collections.singletonMap(any(String.class), facet));

		searchResult.setDocuments(Collections.singletonList(new DefaultDocument()));
		searchResult.setSearchQuery(searchQuery);
		searchResult.setNumberOfResults(PAGE_SIZE);

		final Map<String, Object> attributes = searchResult.getAttributes();
		attributes.put(ADAPTIVE_SEARCH_RESULT, new AsSearchProfileResult());

		return searchResult;
	}

	protected AsSearchQueryData buildAsSearchQueryData(final String searchText, final int activePage, final int pageSize)
	{
		final AsSearchQueryData asSearchQueryData = new AsSearchQueryData();
		asSearchQueryData.setQuery(searchText);
		asSearchQueryData.setActivePage(activePage);
		asSearchQueryData.setPageSize(pageSize);

		return asSearchQueryData;
	}
}
