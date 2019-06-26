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
package com.hybris.merchandising.yaas.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import rx.Observable;

/**
 * NoOpMerchCatalogServiceClientAdapter is a no-operation adapter which
 * simply logs the output sent to it.
 *
 */
public class NoOpMerchCatalogServiceClientAdapter implements MerchCatalogServiceClient {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger LOG = LoggerFactory.getLogger(NoOpMerchCatalogServiceClientAdapter.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handleProductsBatch(Long version, List<Map<String, Object>> products) {
		LOG.debug("handleProductsBatch invoked");
		return processBatch(version, products);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<String> handleProductsBatchAsynch(Long version,
			List<Map<String, Object>> products) {
		LOG.debug("handleProductsBatchAsynch invoked");
		return Observable.just(processBatch(version, products));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void publishProducts(Long version) {
		processPublish(version);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<Void> publishProductsAsync(Long version) {
		processPublish(version);
		return Observable.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<String> handleCategoriesAsync(final CategoryHierarchyWrapper categories) {
		LOG.debug("Handle categories async invoked");
		return Observable.just(processCategories(categories));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handleCategories(final CategoryHierarchyWrapper categories) {
		LOG.debug("Handle categories invoked");
		return processCategories(categories);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handleProductsBatch(final List<Map<String, Object>> products) {
		return processBatch(null, products);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<String> handleProductsBatchAsynch(List<Map<String, Object>> products) {
		return Observable.just(processBatch(null, products));
	}

	/**
	 * processCategories is a method for processing a collection of categories.
	 * @param categories a set of categories, wrapped in a {@link CatalogVersionData} entity.
	 * @return Category structure ID.
	 */
	private String processCategories(final CategoryHierarchyWrapper categories) {
		LOG.debug("Processing categories:");
		if(LOG.isDebugEnabled()) {
			try {
				LOG.debug(objectMapper.writeValueAsString(categories));
			} catch (final JsonProcessingException e) {
				LOG.error("An error occurred serializing data", e);
			}
		}
		return UUID.randomUUID().toString();
	}

	/**
	 * processPublish is a method for handling the publish operation.
	 * @param version the version to publish.
	 */
	private void processPublish(final Long version) {
		LOG.debug("Publishing version: {}", version);
	}

	/**
	 * processBatch is a method for handling the batch operation.
	 * @param version the version to send the batch as.
	 * @param products the products to send.
	 * @return a list of responses.
	 */
	private String processBatch(final Long version, final List<Map<String, Object>> products) {
		if(version != null) {
			LOG.debug("handleProductsBatch invoked. Version:" + version);
		}
		products.forEach(product -> { 
			LOG.debug("Product received:");
			product.keySet().forEach(key -> LOG.debug("****** Key:{} : {}", key, product.get(key)));
			try {
				LOG.debug(objectMapper.writeValueAsString(product));
			} catch (final JsonProcessingException e) {
				LOG.error("An error occurred serializing product to string", e);
			}
		});
		return UUID.randomUUID().toString();
	}
}
