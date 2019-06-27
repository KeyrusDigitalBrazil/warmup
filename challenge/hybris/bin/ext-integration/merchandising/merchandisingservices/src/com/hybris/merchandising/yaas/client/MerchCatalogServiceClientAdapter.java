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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * MerchCatalogServiceClientAdapter is a client for the Merchandising Catalog Service.
 *
 */
public class MerchCatalogServiceClientAdapter implements MerchCatalogServiceClient
{
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private ApiRegistryClientService apiRegistryClientService;
	private final Scheduler scheduler = Schedulers.io();
	private static final Logger LOG = LoggerFactory.getLogger( MerchCatalogServiceClientAdapter.class);
	private static final String NO_OPERATION = "NO_OP";
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handleProductsBatch(final Long version, final List<Map<String, Object>> products)
	{
		if(LOG.isDebugEnabled()) {
			try {
				LOG.debug("Sending products to Catalog service:");
				LOG.debug(objectMapper.writeValueAsString(products));
			} catch (final JsonProcessingException e) {
				LOG.error("An error occurred serializing product to string", e);
			}
		}
		final MerchCatalogServiceClient adaptee = getAdaptee();
		if(adaptee != null) {
			return adaptee.handleProductsBatchAsynch(version, products)
					.subscribeOn(scheduler)
					.toBlocking().first();
		}
		return NO_OPERATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<String> handleProductsBatchAsynch(final Long version, final List<Map<String, Object>> products)
	{
		final MerchCatalogServiceClient adaptee = getAdaptee();
		if(adaptee != null) {
			return adaptee.handleProductsBatchAsynch(version, products);
		}
		return Observable.just(NO_OPERATION);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void publishProducts(final Long version)
	{
		final MerchCatalogServiceClient adaptee = getAdaptee();
		if(adaptee != null) {
			return adaptee.publishProductsAsync(version)//
				.subscribeOn(scheduler)//
				.toBlocking().firstOrDefault(null);
		}
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<Void> publishProductsAsync(final Long version)
	{
		final MerchCatalogServiceClient adaptee = getAdaptee();
		if(adaptee != null) {
			return adaptee.publishProductsAsync(version);
		}
		return Observable.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<String> handleCategoriesAsync(final CategoryHierarchyWrapper categories) {
		final MerchCatalogServiceClient adaptee = getAdaptee();
		if(adaptee != null) {
			return adaptee.handleCategoriesAsync(categories);
		}
		return Observable.just(NO_OPERATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handleCategories(final CategoryHierarchyWrapper categories) {
		if(LOG.isDebugEnabled()) {
			try {
				LOG.debug("Sending categories to Catalog service:");
				LOG.debug(objectMapper.writeValueAsString(categories));
			} catch (final JsonProcessingException e) {
				LOG.error("An error occurred serializing product to string", e);
			}
		}

		if(categories == null || getAdaptee() == null) {
			return null;
		}
		return getAdaptee().handleCategoriesAsync(categories)
				.subscribeOn(scheduler)
				.toBlocking().firstOrDefault(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handleProductsBatch(final List<Map<String, Object>> products) {
		return getAdaptee().handleProductsBatch(products);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<String> handleProductsBatchAsynch(final List<Map<String, Object>> products) {
		return getAdaptee().handleProductsBatchAsynch(products);
	}

	/**
	 * Gets the configured service.
	 * @return the configured {@link MerchCatalogServiceClient}.
	 */
	public MerchCatalogServiceClient getAdaptee()
	{
		try
		{
			return apiRegistryClientService.lookupClient(MerchCatalogServiceClient.class);
		}
		catch (final CredentialException | ModelNotFoundException e)
		{
			LOG.error("Unable to retrieve client for MerchCatalogService. Unable to synchronize products. Please create a client if you wish to use Merchandising functionality.", e);
		}
		return null;
	}

	/**
	 * Gets the configured client service.
	 * @return the configured {@link ApiRegistryClientService}.
	 */
	protected ApiRegistryClientService getYaasServiceFactory()
	{
		return apiRegistryClientService;
	}

	/**
	 * Sets the configured client service.
	 * @param apiRegistryClientService {@link ApiRegistryClientService}.
	 */
	@Required
	public void setApiRegistryClientService(final ApiRegistryClientService apiRegistryClientService)
	{
		this.apiRegistryClientService = apiRegistryClientService;
	}
}
