/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.indexer.listeners;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;

import com.hybris.merchandising.exporter.MerchCategoryExporter;
import com.hybris.merchandising.model.MerchImagePropertyModel;
import com.hybris.merchandising.model.MerchIndexingConfigModel;
import com.hybris.merchandising.model.MerchPropertyModel;
import com.hybris.merchandising.service.MerchIndexingConfigService;
import com.hybris.merchandising.yaas.client.MerchCatalogServiceClient;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;


@UnitTest
public class MerchIndexingListenerTest
{
	public static final long INDEXER_CONTEXT_OPERATION_ID = 1234;
	public static final long BATCH_INDEXER_CONTEXT_OPERATION_ID = 5678;
	public static final String BATCH_INDEXER_ID = "batch001";
	public static final String ENGLISH = "en";
	public static final String GBP = "gbp";
	public static final String ONLINE = "ONLINE";
	public static final String CATALOG = "CATALOG";

	private MerchIndexingListener merchIndexingListener;
	private IndexConfig indexConfig;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private FacetSearchService facetSearchService;

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	@Mock
	private MerchCatalogServiceClient merchCatalogServiceClient;

	@Mock
	private Index index;

	@Mock
	private IndexerContext indexerContext;

	@Mock
	private IndexerBatchContext indexerBatchContext;

	@Mock
	private InputDocument inputDocument1;

	@Mock
	private InputDocument inputDocument2;

	@Mock
	private MerchIndexingConfigService configService;

	@Mock
	private MerchCategoryExporter categoryExporter;

	@Before
	public void setUp() throws SolrServiceException
	{
		MockitoAnnotations.initMocks(this);

		final IndexOperation indexOperation = IndexOperation.FULL;
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();

		indexConfig = new IndexConfig();
		facetSearchConfig.setIndexConfig(indexConfig);

		final InputDocument mockDocument = Mockito.mock(InputDocument.class);
		final List<InputDocument> documents = new ArrayList<>();
		documents.add(mockDocument);

		when(mockDocument.getFieldValue(MerchIndexingListener.CATALOG_VERSION)).thenReturn(ONLINE);
		when(mockDocument.getFieldValue(MerchIndexingListener.CATALOG_ID)).thenReturn(CATALOG);
		when(indexerContext.getIndex()).thenReturn(index);
		when(indexerContext.getIndexOperation()).thenReturn(indexOperation);
		when(indexerContext.getIndexOperationId()).thenReturn(INDEXER_CONTEXT_OPERATION_ID);
		when(indexerContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);

		when(indexerBatchContext.getIndex()).thenReturn(index);
		when(indexerBatchContext.getIndexOperation()).thenReturn(indexOperation);
		when(indexerBatchContext.getIndexOperationId()).thenReturn(BATCH_INDEXER_CONTEXT_OPERATION_ID);
		when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
		when(indexerBatchContext.getInputDocuments()).thenReturn(documents);

		final IndexedType type = Mockito.mock(IndexedType.class);
		when(type.getIdentifier()).thenReturn(BATCH_INDEXER_ID);
		when(indexerBatchContext.getIndexedType()).thenReturn(type);
		when(indexerContext.getIndexedType()).thenReturn(type);

		final CatalogModel mockCatalogModel = Mockito.mock(CatalogModel.class);
		Mockito.when(mockCatalogModel.getId()).thenReturn(CATALOG);
		final CatalogVersionModel mockOnlineCatalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		Mockito.when(mockOnlineCatalogVersionModel.getVersion()).thenReturn(ONLINE);
		Mockito.when(mockOnlineCatalogVersionModel.getCatalog()).thenReturn(mockCatalogModel);
		final List<CatalogVersionModel> catalogVersionsToExport = new ArrayList<>();
		catalogVersionsToExport.add(mockOnlineCatalogVersionModel);

		final MerchIndexingConfigModel config = new MerchIndexingConfigModel();
		config.setEnabled(true);
		config.setMerchProperties(new ArrayList<MerchPropertyModel>());
		config.setMerchImageProperties(new ArrayList<MerchImagePropertyModel>());
		config.setMerchCatalogVersions(catalogVersionsToExport);

		final CurrencyModel mockCurrencyModel = Mockito.mock(CurrencyModel.class);
		when(mockCurrencyModel.getIsocode()).thenReturn(GBP);
		config.setCurrency(mockCurrencyModel);

		final LanguageModel mockLanguageModel = Mockito.mock(LanguageModel.class);
		when(mockLanguageModel.getIsocode()).thenReturn(ENGLISH);
		config.setLanguage(mockLanguageModel);

		final SearchQuery query = Mockito.mock(SearchQuery.class);
		when(facetSearchService.createSearchQueryFromTemplate(facetSearchConfig, type, "DEFAULT")).thenReturn(query);

		when(configService.getMerchIndexingConfigForIndexedType(BATCH_INDEXER_ID)).thenReturn(Optional.of(config));
		merchIndexingListener = new MerchIndexingListener();
		merchIndexingListener.setBeanFactory(beanFactory);
		merchIndexingListener.setFacetSearchService(facetSearchService);
		merchIndexingListener.setFieldNameTranslator(fieldNameTranslator);
		merchIndexingListener.setMerchCatalogServiceClient(merchCatalogServiceClient);
		merchIndexingListener.setMerchIndexingConfigService(configService);
		merchIndexingListener.setMerchCategoryExporter(categoryExporter);
	}

	@Test
	public void mappedInputDocumentsShouldBeSentToHandleProductsBatch() throws Exception
	{

		// given
		when(indexerContext.getIndexOperation()).thenReturn(IndexOperation.FULL);

		// when
		runListeners();

		// then
		verify(merchCatalogServiceClient, times(1)).handleProductsBatch(Mockito.eq(indexerBatchContext.getIndexOperationId()), Mockito.any());
		verify(merchCatalogServiceClient, times(1)).publishProducts(indexerContext.getIndexOperationId());
	}


	@Test
	public void testSanitisation()
	{
		final String nameField = "name";
		final String descriptionField = "description";
		final String summaryField = "summary";
		
		final String nonSanitisedName = "MyProductName<>";
		final String sanitisedName = "MyProductName%3C%3E";

		final String nonSanitisedDescription = "MyProductDescription<>";
		final String sanitisedDescription = "MyProductDescription%3C%3E";

		final String nonSanitisedSummary = "MyProductSummary<>";
		final String sanitisedSummary = "MyProductSummary%3C%3E";

		final Map<String, Object> product = new HashMap<>();
		product.put(nameField, nonSanitisedName);
		product.put(descriptionField, nonSanitisedDescription);
		product.put(summaryField, nonSanitisedSummary);

		merchIndexingListener.sanitiseFields(product);
		Assert.assertEquals("Expected summary to be sanitised",  sanitisedSummary, product.get(summaryField));
		Assert.assertEquals("Expected description to be sanitised", sanitisedDescription, product.get(descriptionField));
		Assert.assertEquals("Expected name to be sanitised", sanitisedName, product.get(nameField));

		final Map<String, Object> productNull = new HashMap<>();
		productNull.put(nameField, null);
		merchIndexingListener.sanitiseFields(productNull);
		Assert.assertNull("Expect name to be null", productNull.get(nameField));
	}

	@Test
	public void testIsToSynchronize()
	{
		final CatalogModel mockCatalogModel = Mockito.mock(CatalogModel.class);
		Mockito.when(mockCatalogModel.getId()).thenReturn(CATALOG);

		final CatalogVersionModel mockOnlineCatalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		Mockito.when(mockOnlineCatalogVersionModel.getVersion()).thenReturn(ONLINE);
		final List<CatalogVersionModel> catalogVersionsToExport = new ArrayList<>();
		catalogVersionsToExport.add(mockOnlineCatalogVersionModel);
		final InputDocument mockInputDocument = Mockito.mock(InputDocument.class);
		Mockito.when(mockInputDocument.getFieldValue(MerchIndexingListener.CATALOG_VERSION)).thenReturn(ONLINE);
		Mockito.when(mockInputDocument.getFieldValue(MerchIndexingListener.CATALOG_ID)).thenReturn(CATALOG);
		Mockito.when(mockOnlineCatalogVersionModel.getCatalog()).thenReturn(mockCatalogModel);
	
		final boolean isToSynchronize = merchIndexingListener.isToSynchronize(catalogVersionsToExport, mockInputDocument);
		Assert.assertTrue("Expected catalog to be marked for synchronizing", isToSynchronize);

		final InputDocument mockStagedInputDocument = Mockito.mock(InputDocument.class);
		Mockito.when(mockStagedInputDocument.getFieldValue(MerchIndexingListener.CATALOG_VERSION)).thenReturn("Staged");
		final boolean isStagedOnline = merchIndexingListener.isToSynchronize(catalogVersionsToExport, mockStagedInputDocument);
		Assert.assertFalse("Expected catalog to not be marked for synchronizing", isStagedOnline);
	}

	@Test
	public void createActionForFullIndexOpShouldReturnCreate() throws Exception
	{

		// given
		final IndexOperation indexOperation = IndexOperation.FULL;
		// when
		final String actionForIndexOperation = merchIndexingListener.createActionForIndexOperation(indexOperation);

		// then
		Assert.assertEquals("CREATE", actionForIndexOperation);
	}


	@Test
	public void createActionForUpdateIndexOpShouldReturnUpdate() throws Exception
	{

		// given
		final IndexOperation indexOperation = IndexOperation.UPDATE;
		// when
		final String actionForIndexOperation = merchIndexingListener.createActionForIndexOperation(indexOperation);

		// then
		Assert.assertEquals("UPDATE", actionForIndexOperation);
	}

	@Test
	public void createActionForPartialUpdateIndexOpShouldReturnUpdate() throws Exception
	{

		// given
		final IndexOperation indexOperation = IndexOperation.PARTIAL_UPDATE;
		// when
		final String actionForIndexOperation = merchIndexingListener.createActionForIndexOperation(indexOperation);

		// then
		Assert.assertEquals("UPDATE", actionForIndexOperation);
	}

	@Test
	public void createActionForDeleteIndexOpShouldReturnDelete() throws Exception
	{
		// given
		final IndexOperation indexOperation = IndexOperation.DELETE;

		// when
		final String actionForIndexOperation = merchIndexingListener.createActionForIndexOperation(indexOperation);

		// then
		Assert.assertEquals("DELETE", actionForIndexOperation);
	}

	@Test
	public void handleAndPublishShouldBeCalledAfterFullIndexOperation() throws Exception
	{
		// given
		final IndexOperation fullIndexOp = IndexOperation.FULL;

		// when
		when(indexerContext.getIndexOperation()).thenReturn(fullIndexOp);
		runListeners();

		// then
		verify(merchCatalogServiceClient, times(1)).handleProductsBatch(Mockito.eq(indexerBatchContext.getIndexOperationId()), Mockito.any());
		verify(merchCatalogServiceClient, times(1)).publishProducts(indexerContext.getIndexOperationId());
	}

	@Test
	public void handleShouldBeCalledAfterDeleteIndexOperation() throws Exception
	{
		// given
		final IndexOperation deleteIndexOp = IndexOperation.DELETE;

		// when
		when(indexerContext.getIndexOperation()).thenReturn(deleteIndexOp);
		runListeners();

		// then
		verify(merchCatalogServiceClient, times(1)).handleProductsBatch(Mockito.eq(indexerBatchContext.getIndexOperationId()), Mockito.any());
		verify(merchCatalogServiceClient, times(0)).publishProducts(any());
	}

	@Test
	public void handleShouldBeCalledAfterPartialUpdateIndexOperation() throws Exception
	{
		// given
		final IndexOperation partialUpdateIndexOp = IndexOperation.PARTIAL_UPDATE;

		// when
		when(indexerContext.getIndexOperation()).thenReturn(partialUpdateIndexOp);
		runListeners();

		// then
		verify(merchCatalogServiceClient, times(1)).handleProductsBatch(Mockito.eq(indexerBatchContext.getIndexOperationId()), Mockito.any());
		verify(merchCatalogServiceClient, times(0)).publishProducts(any());
	}

	private void runListeners() throws Exception
	{
		merchIndexingListener.afterBatch(indexerBatchContext);
		merchIndexingListener.afterIndex(indexerContext);
	}
}
