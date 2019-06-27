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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hybris.merchandising.exporter.MerchCategoryExporter;
import com.hybris.merchandising.model.AbstractMerchPropertyModel;
import com.hybris.merchandising.model.MerchImagePropertyModel;
import com.hybris.merchandising.model.MerchIndexingConfigModel;
import com.hybris.merchandising.model.MerchPropertyModel;
import com.hybris.merchandising.service.MerchIndexingConfigService;
import com.hybris.merchandising.yaas.client.MerchCatalogServiceClient;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerListener;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.provider.FacetDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;


/**
 * MerchIndexingListener is a listener for carrying out actions following a Solr index.
 *
 */
public class MerchIndexingListener implements IndexerBatchListener, BeanFactoryAware, IndexerListener
{
	protected static final String DEFAULT_QUERY_TEMPLATE = "DEFAULT";
	protected static final String EXECUTE = "execute";

	protected static final String ID_KEY = "id";
	protected static final String NAME_KEY = "name";
	protected static final String DESCRIPTION_KEY = "description";
	protected static final String SUMMARY_KEY = "summary";
	protected static final String ACTION_KEY = "action";
	protected static final String IMAGES_KEY = "images";
	protected static final String FACET_KEY = "facets";
	protected static final String FACET_VALUES_KEY = "values";
	protected static final String URL_KEY="url";
	protected static final String CATALOG_VERSION = "catalogVersion";
	protected static final String CATALOG_ID = "catalogId";

	private BeanFactory beanFactory;
	private FacetSearchService facetSearchService;
	private FieldNameTranslator fieldNameTranslator;
	private MerchCatalogServiceClient merchCatalogServiceClient;
	private MerchIndexingConfigService merchIndexingConfigService;
	private SessionService sessionService;
	private CommonI18NService commonI18NService;
	private MerchCategoryExporter merchCategoryExporter;

	private static final Logger LOG = LoggerFactory.getLogger(MerchIndexingListener.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterBatch(final IndexerBatchContext batchContext) throws IndexerException
	{
		final Optional<MerchIndexingConfigModel> merchIdxConf = merchIndexingConfigService
				.getMerchIndexingConfigForIndexedType(batchContext.getIndexedType().getIdentifier());

		if(isMerchIndexingEnabled(merchIdxConf))
		{
			LOG.info("Running after batch for Solr indexing");
			
			final MerchIndexingConfigModel merchIndexingConfig = merchIdxConf.get();
			final SearchQuery searchQuery = createSearchQuery(batchContext, merchIndexingConfig);

			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping = createIndexedPropertiesMapping(batchContext, searchQuery);
			final Map<String, String> merchPropertiesMapping = createMerchPropertiesMapping(merchIndexingConfig.getMerchProperties(), indexedPropertiesMapping);
			final Map<String, String> merchImagePropertiesMapping = createMerchImagePropertiesMapping(merchIndexingConfig.getMerchImageProperties(), indexedPropertiesMapping);
			final Map<String, FacetField> merchFacetPropertiesMapping = createMerchFacetPropertiesMapping(batchContext, searchQuery, indexedPropertiesMapping);

			final List<InputDocument> documents = batchContext.getInputDocuments();
			final List<Map<String, Object>> products = Lists.newLinkedList();

			final List<CatalogVersionModel> catalogVersionsToExport = merchIdxConf.get().getMerchCatalogVersions();
			if(CollectionUtils.isEmpty(catalogVersionsToExport))
			{
				LOG.info("No configured catalog versions to export found");
			}

			for (final InputDocument document : documents)
			{
				if(isToSynchronize(catalogVersionsToExport, document))
				{
					final Map<String, Object> product = Maps.newLinkedHashMap();
					populateBasicProperties(product, batchContext);
					populateMerchProperties(product, document, merchPropertiesMapping, indexedPropertiesMapping, merchIndexingConfig);
					populateMerchImagesProperties(product, document, merchImagePropertiesMapping, indexedPropertiesMapping, merchIndexingConfig);
					populateMerchFacetProperties(product, document, merchFacetPropertiesMapping, indexedPropertiesMapping, searchQuery);
	
					sanitiseFields(product);
					products.add(product);
				}
			}

			if(!products.isEmpty())
			{
				LOG.info("Products found to export to Merchandising: {}", products.size());
				if (batchContext.getIndexOperation().equals(IndexOperation.FULL))
				{
					merchCatalogServiceClient.handleProductsBatch(batchContext.getIndexOperationId(), products);
				} else {
					merchCatalogServiceClient.handleProductsBatch(products);
				}
			}
		}
	}

	/**
	 * isToSynchonize is a method for determining whether a given document is for the Online or Staging
	 * catalog.
	 * @param catalogVersionsToExport a list of configured catalog versions for synchronizing with Merchandising.
	 * @param document the {@link InputDocument} which has been indexed.
	 * @return true if the catalog / catalog version are to be synchronized, false otherwise.
	 */
	protected boolean isToSynchronize(final List<CatalogVersionModel> catalogVersionsToExport, final InputDocument document)
	{
		for(final CatalogVersionModel catalogVersionModel: catalogVersionsToExport)
		{
			final String documentCatalogId = (String) document.getFieldValue(CATALOG_ID);
			final String documentCatalogVersion = (String) document.getFieldValue(CATALOG_VERSION);
			if((documentCatalogId != null && documentCatalogVersion != null) && 
					(catalogVersionModel.getVersion().equals(documentCatalogVersion) 
							&& catalogVersionModel.getCatalog().getId().equals(documentCatalogId)))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Sanitise fields is a method for URL encoding specific fields of a product.
	 * @param product the product to sanitise (as a Map).
	 */
	protected void sanitiseFields(final Map<String, Object> product)
	{
		if(product.containsKey(NAME_KEY)) {
			product.put(NAME_KEY, sanitiseField((String)product.get(NAME_KEY)));
		}
		if(product.containsKey(DESCRIPTION_KEY)) {
			product.put(DESCRIPTION_KEY, sanitiseField((String)product.get(DESCRIPTION_KEY)));
		}
		if(product.containsKey(SUMMARY_KEY)) {
			product.put(SUMMARY_KEY, sanitiseField((String)product.get(SUMMARY_KEY)));
		}
	}

	/**
	 * Sanitise field is a method for URL encoding a field which may contain HTML characters.
	 * @param field the field to encode.
	 * @return the sanitised version.
	 */
	private String sanitiseField(final String field)
	{
		if(StringUtils.isEmpty(field))
		{
			return field;
		}
		try 
		{
			return URLEncoder.encode(field, StandardCharsets.UTF_8.name());
		}
		catch(final UnsupportedEncodingException e)
		{
			LOG.error("Unable to URL encode field", e);
			return field;
		}
	}

	/**
	 * createSearchQuery is used to generate an instance of {@link SearchQuery} to query Solr to retrieve updated product information.
	 * @param batchContext the {@link IndexerBatchContext} representing the indexer batch.
	 * @param merchIndexingConfig the {@link MerchIndexingConfigModel} representing the config we are using for the indexing.
	 * @return an {@link SearchQuery} to use with the search.
	 * @throws IndexerException in case of error.
	 */
	protected SearchQuery createSearchQuery(final IndexerBatchContext batchContext, final MerchIndexingConfigModel merchIndexingConfig) throws IndexerException
	{
		final FacetSearchConfig facetSearchConfig = batchContext.getFacetSearchConfig();
		final IndexedType indexedType = batchContext.getIndexedType();
		final SearchQuery searchQuery = facetSearchService
				.createSearchQueryFromTemplate(facetSearchConfig, indexedType, DEFAULT_QUERY_TEMPLATE);
		final String currIsoCode = merchIndexingConfig.getCurrency().getIsocode();
		final String langIsoCode = merchIndexingConfig.getLanguage().getIsocode();

		try
		{
			facetSearchService.search(searchQuery, Collections.singletonMap(EXECUTE, Boolean.FALSE.toString()));
		}
		catch (final FacetSearchException e)
		{
			throw new IndexerException(e);
		}

		searchQuery.setCurrency(currIsoCode);
		searchQuery.setLanguage(langIsoCode);

		return searchQuery;
	}

	/**
	 * createIndexedPropertiesMapping is a method for retrieving a map of configured key value pairs for handling mapping between Solr internal data model
	 * and what to export.
	 * @param batchContext the {@link IndexerBatchContext} to retrieve from.
	 * @param searchQuery the {@link SearchQuery} used to create the indexed property with.
	 * @return a Map containing key value pairs for the mapping.
	 */
	protected Map<String, IndexedPropertyInfo> createIndexedPropertiesMapping(final IndexerBatchContext batchContext,
			final SearchQuery searchQuery)
	{
		final Collection<IndexedProperty> indexedProperties = batchContext.getIndexedProperties();
		return indexedProperties.stream()
				.collect(Collectors
						.toMap(IndexedProperty::getName, i-> createIndexedPropertyInfo(searchQuery, i)));
	}

	/**
	 * createIndexedPropertyInfo is a method for returning an {@link IndexedPropertyInfo} representing a mapping from
	 * the Solr output to the configured Merchandising specific output.
	 * @param searchQuery the {@code SearchQuery} being used to retrieve the value from.
	 * @param indexedProperty the {@code IndexedProperty} being used for the query.
	 * @return an {@code IndexedPropertyInfo} representing the mapping.
	 */
	protected IndexedPropertyInfo createIndexedPropertyInfo(final SearchQuery searchQuery, final IndexedProperty indexedProperty)
	{
		final String translatedFieldName = translateField(searchQuery, indexedProperty.getName());

		return new IndexedPropertyInfo(indexedProperty, translatedFieldName);
	}

	/**
	 * createMerchPropertiesMapping is a method for mapping from the configured list of {@link MerchPropertyModel} to the indexed properties
	 * within Solr.
	 * @param merchProperties the mapping we wish to use for the output.
	 * @param indexedPropertiesMapping the Solr mapping.
	 * @return a Map containing the mapped output.
	 */
	protected Map<String, String> createMerchPropertiesMapping(final List<MerchPropertyModel> merchProperties,
			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping)
	{
		return merchProperties.stream()
				.filter( merchProp -> indexedPropertiesMapping.containsKey(merchProp.getIndexedProperty().getName()))
				.collect(Collectors.toMap(this::extractPropName, this::extractPropTranslatedName));
	}

	/**
	 * createMerchImagePropertiesMapping is an image specific method from mapping from a configured list of {@link MerchImagePropertyModel} to the indexed properties
	 * within Solr.
	 * @param merchImageProperties the mapping we wish to use for the image output.
	 * @param indexedPropertiesMapping the Solr mapping.
	 * @return a Map containing the mapped output.
	 */
	protected Map<String, String> createMerchImagePropertiesMapping(final List<MerchImagePropertyModel> merchImageProperties,
			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping)
	{
		return merchImageProperties.stream()
				.filter( merchProp -> indexedPropertiesMapping.containsKey(merchProp.getIndexedProperty().getName()))
				.collect(Collectors.toMap(this::extractPropName, this::extractPropTranslatedName));
	}

	/**
	 * extractPropName is a method for retrieving the name of an indexed property.
	 * @param merchProp an {@link AbstractMerchPropertyModel} we wish to retrieve the name of.
	 * @return a String containing the name.
	 */
	protected String extractPropName(final AbstractMerchPropertyModel merchProp)
	{
		return merchProp.getIndexedProperty().getName();
	}

	/**
	 * extractPropTranslatedName is a method for retrieving the mapped name of an indexed property. If not present, we default
	 * to the name of the indexed property.
	 * @param merchProp the {@link AbstractMerchPropertyModel} we wish to retrieve the name of.
	 * @return a String containing the name.
	 */
	protected String extractPropTranslatedName(final AbstractMerchPropertyModel merchProp)
	{
		final SolrIndexedPropertyModel indexedProperty = merchProp.getIndexedProperty();
		return StringUtils.isNotBlank(merchProp.getMerchMappedName()) ? merchProp.getMerchMappedName() : indexedProperty.getName();
	}

	/**
	 * createMerchFacetPropertiesMapping is a method for mapping facets to output for consumption by Merchandising.
	 * @param batchContext the {@link IndexerBatchContext} representing the indexer batch.
	 * @param searchQuery the {@link SearchQuery} used for the request.
	 * @param indexedPropertiesMapping the mapping used for the request.
	 * @return a Map containing key -> facet pairs.
	 */
	protected Map<String, FacetField> createMerchFacetPropertiesMapping(final IndexerBatchContext batchContext,
			final SearchQuery searchQuery,
			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping)
	{
		return searchQuery.getFacets().stream()
				.filter(facet -> indexedPropertiesMapping.containsKey(facet.getField()))
				.collect(Collectors.toMap(FacetField::getField, Function.identity()));
	}

	/**
	 * populateBasicProperties is used to retrieve a value from the {@link IndexerBatchContext} and update it in the
	 * provided output map.
	 * @param product a {@link Map} representing the product output.
	 * @param batchContext an {@link IndexerBatchContext} to retrieve product details from.
	 */
	protected void populateBasicProperties(final Map<String, Object> product, final IndexerBatchContext batchContext)
	{
		product.put(ACTION_KEY, createActionForIndexOperation(batchContext.getIndexOperation()));
	}

	/**
	 * populateMerchProperties is a method for retrieving values from the provided {@link InputDocument}.
	 * @param product a {@link Map} representing the product output.
	 * @param document the {@link InputDocument} representing the search result.
	 * @param merchPropertiesMapping a key -> value pair mapping for mapping to Merchandising output.
	 * @param indexedPropertiesMapping a map containing indexed properties.
	 * @param indexConfig an instance of {@link MerchIndexingConfigModel} containing the configuration being used for this indexing.
	 */
	protected void populateMerchProperties(final Map<String, Object> product, final InputDocument document,
			final Map<String, String> merchPropertiesMapping,
			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping,
			final MerchIndexingConfigModel indexConfig){

		for (final Map.Entry<String, String> merchPropEntry : merchPropertiesMapping.entrySet())
		{
			final IndexedPropertyInfo indexedPropertyInfo = indexedPropertiesMapping.get(merchPropEntry.getKey());
			final String translatedFieldName = indexedPropertyInfo.getTranslatedFieldName();
			final String merchKey = merchPropEntry.getValue();
			if(merchPropEntry.getKey().equals(URL_KEY))
			{
				final String basePageUrl = StringUtils.isNotEmpty(indexConfig.getBaseProductPageUrl()) ? indexConfig.getBaseProductPageUrl() : "";
				product.put(merchKey, basePageUrl + document.getFieldValue(translatedFieldName));
			} else
			{
				product.put(merchKey, document.getFieldValue(translatedFieldName));
			}
		}
	}

	/**
	 * populateMerchImagesProperties is a method for retrieving values from the provided {@link InputDocument}.
	 * @param product a {@link Map} representing the product output.
	 * @param document the {@link InputDocument} representing the search result.
	 * @param merchImagesPropertiesMapping a key -> value pair mapping for mapping to Merchandising output.
	 * @param indexedPropertiesMapping a map containing indexed properties.
	 * @param merchIndexingConfig an instance of {@link MerchIndexingConfigModel} containing the configuration being used for this indexing.
	 */
	protected void populateMerchImagesProperties(final Map<String, Object> product, final InputDocument document,
			final Map<String, String> merchImagesPropertiesMapping,
			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping,
			final MerchIndexingConfigModel merchIndexingConfig){

		final Map<String, Object> images = Maps.newHashMap();
		final String baseImageUrl = StringUtils.isNotEmpty(merchIndexingConfig.getBaseImageUrl()) ? merchIndexingConfig.getBaseImageUrl() : "";
		populateMerchProperties(images, document, merchImagesPropertiesMapping, indexedPropertiesMapping, merchIndexingConfig);
		images.forEach((key, value) -> {
			if(value instanceof String) {
				value = baseImageUrl + value;
				images.replace(key, value);
			}
		});
		product.put(IMAGES_KEY, images);
	}

	/**
	 * populateMerchFacetProperties is a method for populating facet information in the mapping to Merchandising.
	 * @param product a {@link Map} representing the product output.
	 * @param document the {@link InputDocument} representing the search result.
	 * @param merchFacetPropertiesMapping  a key -> value pair mapping for mapping to Merchandising output.
	 * @param indexedPropertiesMapping a map containing indexed properties.
	 * @param searchQuery the {@link SearchQuery} being used to retrieve facets from.
	 */
	protected void populateMerchFacetProperties(final Map<String, Object> product, final InputDocument document,
			final Map<String, FacetField> merchFacetPropertiesMapping,
			final Map<String, IndexedPropertyInfo> indexedPropertiesMapping,
			final SearchQuery searchQuery)
	{
		final List<Map<String, Object>> facetFields = Lists.newArrayList();

		for(final FacetField facetField : merchFacetPropertiesMapping.values())
		{
			final IndexedPropertyInfo indexedPropertyInfo = indexedPropertiesMapping.get(facetField.getField());
			final IndexedProperty indexedProperty = indexedPropertyInfo.getIndexedProperty();

			final HashMap<String, Object> facetFieldMap = Maps.newHashMap();

			facetFieldMap.put(ID_KEY, facetField.getField());
			facetFieldMap.put(NAME_KEY, indexedProperty.getDisplayName());
			facetFieldMap.put(FACET_VALUES_KEY, createMerchFacetValues(facetField, searchQuery, indexedProperty, document, indexedPropertyInfo));

			facetFields.add(facetFieldMap);
		}
		product.put(FACET_KEY, facetFields);
	}

	/**
	 * createMerchFacetValues is a method for retrieving the values for a provided facet.
	 * @param facet the {@link FacetField} we are retrieving values for.
	 * @param query the {@link SearchQuery} to retrieve values using.
	 * @param indexedProperty the {@link IndexedProperty} representing the value we wish to retrieve.
	 * @param indexedPropertyInfo the {@link IndexedPropertyInfo} representing the field to retrieve.
	 * @return a Map containing the facet mapping.
	 */
	protected List<Map<Object, Object>> createMerchFacetValues(final FacetField facet, final SearchQuery query,
			final IndexedProperty indexedProperty, final InputDocument document, final IndexedPropertyInfo indexedPropertyInfo)
	{
		final List<Map<Object, Object>> facetValues = Lists.newArrayList();

		final String displayNameProviderName = facet.getDisplayNameProvider();
		final Object displayNameProvider = resolveFacetValuesDisplayNameProvider(displayNameProviderName);

		final Object fieldValue = document.getFieldValue(indexedPropertyInfo.getTranslatedFieldName());

		if(fieldValue instanceof Collection)
		{
			final Collection<Object> fieldValues = (Collection<Object>) fieldValue;

			fieldValues.stream().map(fieldVal -> createFacetValueMapping(fieldVal, query, indexedProperty, displayNameProvider))
					.forEach(facetValues::add);
		}
		else if(fieldValue != null)
		{
			facetValues.add(createFacetValueMapping(fieldValue, query, indexedProperty, displayNameProvider));
		}

		return facetValues;
	}

	/**
	 * resolveFacetValuesDisplayNameProvider is a utility method for retrieving a bean from the configured bean factory.
	 * @param beanName the bean to retrieve.
	 * @return an {@code Object} representing the bean if found, null otherwise.
	 */
	protected Object resolveFacetValuesDisplayNameProvider(final String beanName)
	{
		return beanName != null ? beanFactory.getBean(beanName) : null;
	}

	/**
	 * resolveFacetValueDisplayName is a method for retrieving the display name for a given facet.
	 * @param searchQuery the {@code SearchQuery} we are using for the retrieval.
	 * @param indexedProperty the {@code IndexedProperty} we are retrieving.
	 * @param facetDisplayNameProvider the value provider for the display name.
	 * @param facetValue the value we wish to retrieve.
	 * @return the display name for the facet.
	 */
	protected String resolveFacetValueDisplayName(final SearchQuery searchQuery, final IndexedProperty indexedProperty,
			final Object facetDisplayNameProvider, final String facetValue)
	{
		if (facetDisplayNameProvider != null)
		{
			if (facetDisplayNameProvider instanceof FacetValueDisplayNameProvider)
			{
				return ((FacetValueDisplayNameProvider) facetDisplayNameProvider).getDisplayName(searchQuery,
						indexedProperty, facetValue);
			}
			else if (facetDisplayNameProvider instanceof FacetDisplayNameProvider)
			{
				//Whilst the line below is deprecated, this is intentional to ensure we support FacetDisplayNameProvider
				//as well as FacetValueDisplayNameProvider.
				return ((FacetDisplayNameProvider) facetDisplayNameProvider).getDisplayName(searchQuery, facetValue);
			}
		}

		return facetValue;
	}

	/**
	 * createFacetValueMapping creates a map of key -> value pairs for a given facet.
	 * @param fieldValue the value of the facet to use.
	 * @param query the {@code SearchQuery} being used to resolve the display name from.
	 * @param indexedProperty the {@code IndexedProperty} being used to resolve the display name from.
	 * @param displayNameProvider the configured display name provider.
	 * @return a Map containing facet value mapping.
	 */
	protected Map<Object, Object> createFacetValueMapping(final Object fieldValue, final SearchQuery query,
			final IndexedProperty indexedProperty, final Object displayNameProvider)
	{
		final HashMap<Object, Object> facetValueMapping = Maps.newHashMap();

		final String fieldDisplayValue = resolveFacetValueDisplayName(query, indexedProperty, displayNameProvider,
				String.valueOf(fieldValue));

		facetValueMapping.put(ID_KEY, fieldValue);
		facetValueMapping.put(NAME_KEY, fieldDisplayValue);

		return facetValueMapping;
	}

	/**
	 * translateField returns the translated value for a given query and field.
	 * @param query the {@code SearchQuery} we are using to retrieve the translated value from.
	 * @param field the query's field to retrieve.
	 * @return a String containing the translated field.
	 */
	protected String translateField(final SearchQuery query, final String field)
	{
		return fieldNameTranslator.translate(query, field, FieldNameProvider.FieldType.INDEX);
	}

	/**
	 * createActionForIndexOperation is a method for retrieving the action to use when sending to Merchandising
	 * for a given {@code IndexOperation}.
	 * @param indexOperation the operation to retrieve the value for.
	 * @return the action to use.
	 */
	protected String createActionForIndexOperation(final IndexOperation indexOperation)
	{
		switch(indexOperation)
		{
			case FULL:
				return "CREATE";
			case UPDATE:
			case PARTIAL_UPDATE:
				return "UPDATE";
			case DELETE:
				return "DELETE";
		}
		return null;
	}

	protected boolean isMerchIndexingEnabled(final Optional<MerchIndexingConfigModel> merchIdxConf)
	{
		return merchIdxConf.isPresent() && merchIdxConf.get().isEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterIndex(final IndexerContext context) throws IndexerException
	{
		final Optional<MerchIndexingConfigModel> merchIdxConf = merchIndexingConfigService
				.getMerchIndexingConfigForIndexedType(context.getIndexedType().getIdentifier());

		if(isMerchIndexingEnabled(merchIdxConf) && context.getIndexOperation().equals(IndexOperation.FULL))
		{
			LOG.info("Running full index - marking index as published");
			merchCatalogServiceClient.publishProducts(context.getIndexOperationId());
			merchCategoryExporter.exportCategoriesForCurrentBaseSite();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeBatch(final IndexerBatchContext batchContext) throws IndexerException
	{
		// NOOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterBatchError(final IndexerBatchContext batchContext) throws IndexerException
	{
		// NOOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeIndex(final IndexerContext context) throws IndexerException
	{
		// NOOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterIndexError(final IndexerContext context) throws IndexerException
	{
		// NOOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;
	}


	public FacetSearchService getFacetSearchService()
	{
		return facetSearchService;
	}

	
	@Required
	public void setFacetSearchService(final FacetSearchService facetSearchService)
	{
		this.facetSearchService = facetSearchService;
	}


	public FieldNameTranslator getFieldNameTranslator()
	{
		return fieldNameTranslator;
	}

	@Required
	public void setFieldNameTranslator(final FieldNameTranslator fieldNameTranslator)
	{
		this.fieldNameTranslator = fieldNameTranslator;
	}


	public MerchCatalogServiceClient getMerchCatalogServiceClient()
	{
		return merchCatalogServiceClient;
	}

	@Required
	public void setMerchCatalogServiceClient(final MerchCatalogServiceClient merchCatalogServiceClient)
	{
		this.merchCatalogServiceClient = merchCatalogServiceClient;
	}

	protected MerchIndexingConfigService getMerchIndexingConfigService()
	{
		return merchIndexingConfigService;
	}

	@Required
	public void setMerchIndexingConfigService(final MerchIndexingConfigService merchIndexingConfigService)
	{
		this.merchIndexingConfigService = merchIndexingConfigService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected MerchCategoryExporter getMerchCategoryExporter() {
		return merchCategoryExporter;
	}

	@Required
	public void setMerchCategoryExporter(final MerchCategoryExporter merchCategoryExporter)
	{
		this.merchCategoryExporter = merchCategoryExporter;
	}

	/**
	 * IndexedPropertyInfo represents an {@code IndexedProperty} with its translated field name.
	 *
	 */
	private static class IndexedPropertyInfo
	{
		private IndexedProperty indexedProperty;
		private String translatedFieldName;

		/**
		 * Generates an {@code IndexedPropertyInfo}.
		 * @param indexedProperty the property we wish to store.
		 * @param translatedFieldName the properties' translated field name.
		 */
		private IndexedPropertyInfo(final IndexedProperty indexedProperty, final String translatedFieldName)
		{
			this.indexedProperty = indexedProperty;
			this.translatedFieldName = translatedFieldName;
		}

		public IndexedProperty getIndexedProperty()
		{
			return indexedProperty;
		}

		public String getTranslatedFieldName()
		{
			return translatedFieldName;
		}
	}
}
