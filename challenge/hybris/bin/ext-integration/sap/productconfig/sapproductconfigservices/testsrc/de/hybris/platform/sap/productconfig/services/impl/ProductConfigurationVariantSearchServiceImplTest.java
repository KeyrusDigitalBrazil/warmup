/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.ProductSearchService;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.impl.DefaultSolrProductSearchService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.commerceservices.threadcontext.impl.DefaultThreadContextService;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.intf.SearchAttributeSelectionStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationVariantSearchServiceImplTest
{
	private static final String CONFIG_ID = "A";
	private static final String PRODUCT_CODE = "productCode";
	private static final String CSTIC_VALUE_NAME = "value";
	private static final String CSTIC_NAME = "cstic";
	private static final String CSTIC_NAME_UNKNOWN = "csticUn";
	private static final String CLASSIFICATION_CSTIC_VALUE_NAME = "classificationValueName";

	@Mock
	private SearchAttributeSelectionStrategy searchAttributeSelectionStrategy;

	@Mock
	private ProductConfigurationService productConfigurationService;

	@Mock
	private ThreadContextService threadContextService;

	@Mock
	private ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy;

	@InjectMocks
	private final ProductConfigurationVariantSearchServiceImpl classUnderTest = new ProductConfigurationVariantSearchServiceImpl();


	private final ConfigModel configurationModel = new ConfigModelImpl();
	private final InstanceModel rootInstance = new InstanceModelImpl();
	private final List<CsticModel> csticList = new ArrayList<>();
	private final CsticModel cstic = new CsticModelImpl();
	private final List<CsticValueModel> assignedValues = new ArrayList<>();
	private final CsticValueModel csticValue = new CsticValueModelImpl();

	private ProductSearchPageData solrSearchResult;
	private List results;
	private SearchResultValueData searchResultValueData;
	private final Set<String> solrIndexedProperties = new HashSet<>();

	@Before
	public void setUp() throws NoValidSolrConfigException
	{
		MockitoAnnotations.initMocks(this);
		when(productConfigurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(configurationModel);
		configurationModel.setRootInstance(rootInstance);
		csticList.add(cstic);
		cstic.setName(CSTIC_NAME);
		assignedValues.add(csticValue);
		csticValue.setName(CSTIC_VALUE_NAME);
		csticValue.setLanguageDependentName(CSTIC_VALUE_NAME);
		cstic.setAssignedValues(assignedValues);
		rootInstance.setCstics(csticList);
		when(Boolean.valueOf(searchAttributeSelectionStrategy.isAttributeAvailableOnSearchIndex(CSTIC_NAME, solrIndexedProperties)))
				.thenReturn(Boolean.valueOf(true));
		when(Boolean.valueOf(
				searchAttributeSelectionStrategy.isAttributeAvailableOnSearchIndex(CSTIC_NAME_UNKNOWN, solrIndexedProperties)))
						.thenReturn(Boolean.valueOf(false));

		solrSearchResult = new ProductSearchPageData<>();
		results = new ArrayList<SearchResultValueData>();
		searchResultValueData = new SearchResultValueData();
		final Map<String, Object> values = new HashMap<>();
		values.put("code", PRODUCT_CODE);
		searchResultValueData.setValues(values);
		results.add(searchResultValueData);
		solrSearchResult.setResults(results);
	}

	@Test
	public void testConfigurationservice()
	{
		final ProductConfigurationService configService = new ProductConfigurationServiceImpl();
		classUnderTest.setProductConfigurationService(configService);
		assertEquals(configService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testSearchService()
	{
		final ProductSearchService searchService = new DefaultSolrProductSearchService<ProductData>();
		classUnderTest.setProductSearchService(searchService);
		assertEquals(searchService, classUnderTest.getProductSearchService());
	}

	@Test(expected = NullPointerException.class)
	public void testGetRootCharacteristicsNoRootInstance()
	{
		configurationModel.setRootInstance(null);
		classUnderTest.getRootCharacteristics(CONFIG_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRootCharacteristicsNoConfigId()
	{
		classUnderTest.getRootCharacteristics(null);
	}

	@Test
	public void testGetRootCharacteristics()
	{
		final List<CsticModel> characteristics = classUnderTest.getRootCharacteristics(CONFIG_ID);
		assertEquals(csticList, characteristics);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateQueryDataTermNull()
	{
		classUnderTest.createQueryDataTerm(null, null, null);
	}

	@Test
	public void testCreateQueryDataTerm()
	{
		final SolrSearchQueryTermData queryTermData = classUnderTest.createQueryDataTerm(cstic, csticValue, null);
		assertNotNull(queryTermData);
		assertEquals(CSTIC_NAME, queryTermData.getKey());
		assertEquals(CSTIC_VALUE_NAME, queryTermData.getValue());
	}

	@Test
	public void testCreateQueryDataTermOverwriteValueNameViaClassification()
	{
		final Map<String, String> valueNames = new HashMap<>();
		valueNames.put(CSTIC_NAME + "_" + CSTIC_VALUE_NAME, CLASSIFICATION_CSTIC_VALUE_NAME);
		final ClassificationSystemCPQAttributesContainer cpqAttribute = new ClassificationSystemCPQAttributesContainer(CSTIC_NAME,
				CSTIC_NAME, null, valueNames, Collections.emptyMap(), new ArrayList(), new HashMap());

		final SolrSearchQueryTermData queryTermData = classUnderTest.createQueryDataTerm(cstic, csticValue, cpqAttribute);
		assertNotNull(queryTermData);
		assertEquals(CSTIC_NAME, queryTermData.getKey());
		assertEquals(CLASSIFICATION_CSTIC_VALUE_NAME, queryTermData.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertToQueryDataListNull()
	{
		classUnderTest.convertToQueryDataList(null, null, null);
	}

	@Test
	public void testConvertToQueryDataList()
	{
		final List<SolrSearchQueryTermData> queryDataList = classUnderTest.convertToQueryDataList(cstic, new HashMap(),
				solrIndexedProperties);
		assertNotNull(queryDataList);
		assertEquals(1, queryDataList.size());
		assertEquals(CSTIC_NAME, queryDataList.get(0).getKey());
		assertEquals(CSTIC_VALUE_NAME, queryDataList.get(0).getValue());
	}

	@Test
	public void testConvertToQueryDataListOverwriteValueNameViaClassification()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNamesMap = new HashMap<>();
		final Map<String, String> valueNames = new HashMap<>();
		valueNames.put(CSTIC_NAME + "_" + CSTIC_VALUE_NAME, CLASSIFICATION_CSTIC_VALUE_NAME);
		final ClassificationSystemCPQAttributesContainer cpqAttribute = new ClassificationSystemCPQAttributesContainer(CSTIC_NAME,
				CSTIC_NAME, null, valueNames, Collections.emptyMap(), new ArrayList(), new HashMap());
		hybrisNamesMap.put(CSTIC_NAME, cpqAttribute);

		final List<SolrSearchQueryTermData> queryDataList = classUnderTest.convertToQueryDataList(cstic, hybrisNamesMap,
				solrIndexedProperties);
		assertNotNull(queryDataList);
		assertEquals(1, queryDataList.size());
		assertEquals(CSTIC_NAME, queryDataList.get(0).getKey());
		assertEquals(CLASSIFICATION_CSTIC_VALUE_NAME, queryDataList.get(0).getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFilterTermsNull()
	{
		classUnderTest.getFilterTerms(null, PRODUCT_CODE);
	}

	@Test
	public void testGetFilterTerms()
	{
		final List<SolrSearchQueryTermData> filterTerms = classUnderTest.getFilterTerms(CONFIG_ID, PRODUCT_CODE);
		assertNotNull(filterTerms);
		assertEquals(1, filterTerms.size());
		assertEquals(CSTIC_NAME, filterTerms.get(0).getKey());
		assertEquals(CSTIC_VALUE_NAME, filterTerms.get(0).getValue());
	}

	@Test(expected = NullPointerException.class)
	public void testCompileSearchResultNull()
	{
		solrSearchResult = null;
		classUnderTest.compileSearchResult(solrSearchResult);
	}

	@Test
	public void testCompileSearchResult()
	{
		final List<VariantSearchResult> result = classUnderTest.compileSearchResult(solrSearchResult);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotNull(result.get(0));
		assertEquals(PRODUCT_CODE, result.get(0).getProductCode());
	}

	@Test
	public void testSearchAttributeSelectionStrategy()
	{
		assertEquals(searchAttributeSelectionStrategy, classUnderTest.getSearchAttributeSelectionStrategy());
	}

	@Test
	public void testIsUsedForSearch()
	{
		assertTrue(classUnderTest.isUsedForSearch(cstic, solrIndexedProperties));
	}


	@Test
	public void testIsUsedForSearchUnknown()
	{
		cstic.setName(CSTIC_NAME_UNKNOWN);
		assertFalse(classUnderTest.isUsedForSearch(cstic, solrIndexedProperties));
	}

	@Test
	public void testIsUsedForSearchKnownButNumeric()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		assertFalse(classUnderTest.isUsedForSearch(cstic, solrIndexedProperties));
	}

	@Test
	public void testIsUsedForSearchUnknownAndNumeric()
	{
		cstic.setName(CSTIC_NAME_UNKNOWN);
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		assertFalse(classUnderTest.isUsedForSearch(cstic, solrIndexedProperties));
	}


	@Test(expected = NullPointerException.class)
	public void testConvertToVariantSearchResultNull()
	{
		classUnderTest.convertToVariantSearchResult(null);
	}

	@Test(expected = NullPointerException.class)
	public void testConvertToVariantSearchResultAttributeNotAvailable()
	{
		searchResultValueData.getValues().remove("code");
		classUnderTest.convertToVariantSearchResult(searchResultValueData);
	}

	@Test
	public void testThreadContextService()
	{
		final ThreadContextService threadContextService = new DefaultThreadContextService();
		classUnderTest.setThreadContextService(threadContextService);
		assertEquals(threadContextService, classUnderTest.getThreadContextService());
	}

	@Test
	public void testAddBaseProductToQuery()
	{
		final SolrSearchQueryData searchQuery = new SolrSearchQueryData();
		searchQuery.setFilterTerms(new ArrayList<>());
		classUnderTest.addBaseProductToQuery(PRODUCT_CODE, searchQuery);
		final List<SolrSearchQueryTermData> filterTerms = searchQuery.getFilterTerms();
		assertEquals(1, filterTerms.size());
		final SolrSearchQueryTermData solrSearchQueryTermData = filterTerms.get(0);
		assertEquals(PRODUCT_CODE, solrSearchQueryTermData.getValue());
		assertEquals(ProductConfigurationVariantSearchServiceImpl.BASE_PRODUCT_ON_SOLR, solrSearchQueryTermData.getKey());
	}

	@Test
	public void testGetVariantsForConfiguration() throws Throwable
	{
		when(threadContextService.executeInContext(any())).thenReturn(solrSearchResult);
		final List<VariantSearchResult> searchResult = classUnderTest.getVariantsForConfiguration(CONFIG_ID, PRODUCT_CODE);

		assertNotNull(searchResult);
		assertEquals(1, searchResult.size());
		assertNotNull(searchResult.get(0));
		assertEquals(PRODUCT_CODE, searchResult.get(0).getProductCode());
	}

}
