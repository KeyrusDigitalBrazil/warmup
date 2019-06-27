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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;

/**
 * MerchCatalogServiceClient is a JAX-RS interface for the Merchandising Catalog Service.
 */
@OAuth
@Http
public interface MerchCatalogServiceClient
{
	/**
	 * handleProductsBatch is a handler for accepting a batch of products and sending them to the
	 * Merchandising Catalog service.
	 * @param version the version to send the products as.
	 * @param products a list of products to send.
	 * @return String - ID of the operation.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/products")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:30000}")
	String handleProductsBatch(@QueryParam("version") final Long version, final List<Map<String, Object>> products);

	/**
	 * handleProductsBatch is a handler for accepting a batch of products and sending them to the
	 * Merchandising Catalog service.
	 * @param products a list of products to send.
	 * @return String - ID of the operation.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/products")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:30000}")
	String handleProductsBatch(final List<Map<String, Object>> products);

	/**
	 * handleProductsBatchAsynch is a handler for accepting a batch of products and sending them to the
	 * Merchandising Catalog service.
	 * @param products a list of products to send.
	 * @return String - ID of the operation.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/products")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:30000}")
	Observable<String> handleProductsBatchAsynch(final List<Map<String, Object>> products);

	/**
	 * handleProductsBatchAsync is a handler for accepting a batch of products and sending them to the
	 * Merchandising Catalog service.
	 * @param version the version to send the products as.
	 * @param products a list of products to send.
	 * @return String - ID of the operation.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/products")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:30000}")
	Observable<String> handleProductsBatchAsynch(@QueryParam("version") final Long version, final List<Map<String, Object>> products);

	/**
	 * publishProducts is an API for marking a set of products as being published to live in a
	 * synchronous manner.
	 * @param version the version to publish.
	 * @return Void.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/products/publish")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:15000}")
	Void publishProducts(@QueryParam("version") final Long version);

	/**
	 * publishProducts is an API for marking a set of products as being published to live in an
	 * asynchronous manner.
	 * @param version the version to publish.
	 * @return Void.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/products/publish")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:15000}")
	Observable<Void> publishProductsAsync(@QueryParam("version") final Long version);

	/**
	 * handleCategoriesAsync is an asynchronous method for sending categories to Merch v2.
	 * @param categories the {@link CategoryHierarchyWrapper} object to publish.
	 * @return an Observable containing the response.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/categories")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:30000}")
	Observable<String> handleCategoriesAsync(final CategoryHierarchyWrapper categories);

	/**
	 * handleCategories is a method for sending categories to Merch v2.
	 * @param categories the {@link CategoryHierarchyWrapper} object to publish.
	 * @return String - ID of the operation.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/${tenant}/categories")
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:30000}")
	String handleCategories(final CategoryHierarchyWrapper categories);
}
