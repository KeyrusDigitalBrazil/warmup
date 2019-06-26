/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package com.hybris.cis.client.tax;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;

import com.hybris.cis.client.CisClient;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.models.CisTaxDoc;


/**
 * Charon Client to the CIS Tax API
 */
@Http("tax")
public interface TaxClient extends CisClient
{
	/**
	 * Returns a tax quote for the order.
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param order
	 *           The order details to get the quote for
	 * @return A tax document
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/quotes")
	@Control(retries = "3", retriesInterval = "500")
	CisTaxDoc quote(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
					@HeaderParam(value = "X-tenantId") final String tenantId, final CisOrder order);

	/**
	 * <p>
	 * Submits taxes for the given order.
	 * <p>
	 * <p>
	 * <p>
	 * Taxes can be posted when the order is placed, which will create a persisted tax document for later reference.
	 * </p>
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param order
	 *           The order details to post taxes for
	 * @return A tax document
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/posts")
	@Control(retries = "3", retriesInterval = "500")
	CisTaxDoc post(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final CisOrder order);

	/**
	 * <p>
	 * Creates an invoice based on the provided order details.
	 * <p>
	 * <p>
	 * <p>
	 * Invoiced orders can be adjusted or cancelled.
	 * </p>
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param order
	 *           The order details to invoice taxes for
	 * @return A tax document
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/invoices")
	@Control(retries = "3", retriesInterval = "500")
	CisTaxDoc invoice(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final CisOrder order);

	/**
	 * <p>
	 * Cancels a previously submitted or invoiced tax document.
	 * <p>
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param taxDocId
	 *           id of the tax document we want to cancel
	 */
	@DELETE
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/invoices/{taxDocId}")
	@Control(retries = "3", retriesInterval = "500")
	RawResponse<String> cancel(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, @PathParam("taxDocId") final String taxDocId);

	/**
	 * <p>
	 * Adjusts a previously submitted or invoiced tax document.
	 * <p>
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param taxDocId
	 *           id of the tax document we want to adjust
	 * @param order
	 *           CisOrder with lineitems to be cancelled
	 * @return A tax document
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/invoices/{taxDocId}/adjustments")
	@Control(retries = "3", retriesInterval = "500")
	CisTaxDoc adjust(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, @PathParam("taxDocId") final String taxDocId,
			final CisOrder order);

	/**
	 * <p>
	 * Checks if a tax document exists.
	 * <p>
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param taxDocId
	 *           The location URI of the document (can be relative or absolute)
	 */
	@HEAD
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/invoices/{taxDocId}")
	@Control(retries = "3", retriesInterval = "500")
	RawResponse<String> exists(final String xClientRef, @HeaderParam(value = "X-tenantId") final String tenantId,
			@PathParam("taxDocId") final String taxDocId);

}
