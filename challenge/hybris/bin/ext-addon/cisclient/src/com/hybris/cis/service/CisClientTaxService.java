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
package com.hybris.cis.service;

import com.hybris.charon.RawResponse;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.models.CisTaxDoc;

import java.net.URI;


/**
 * Interface providing Tax services
 */
public interface CisClientTaxService extends CisClientService
{
	/**
	 * Returns a tax quote for the order.
	 *
	 * @param xClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param order
	 * 		The order details to get the quote for
	 * @return A tax document
	 */
	CisTaxDoc quote(final String xClientRef, final String tenantId, final CisOrder order);

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
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param order
	 * 		The order details to post taxes for
	 * @return A tax document
	 */
	CisTaxDoc post(final String xClientRef, final String tenantId, final CisOrder order);

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
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param order
	 * 		The order details to invoice taxes for
	 * @return A tax document
	 */
	CisTaxDoc invoice(final String xClientRef, String tenantId, final CisOrder order);

	/**
	 * <p>
	 * Cancels a previously submitted or invoiced tax document.
	 * <p>
	 *
	 * @param xClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param documentLocation
	 * 		The location URI of the document (can be relative or absolute)
	 */
	RawResponse<String> cancel(final String xClientRef, String tenantId, final URI documentLocation);

	/**
	 * <p>
	 * Adjusts a previously submitted or invoiced tax document.
	 * <p>
	 *
	 * @param xClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param documentLocation
	 * 		The location URI of the document to adjust
	 * @param order
	 * 		CisOrder with lineitems to be cancelled
	 * @return A tax document
	 */
	CisTaxDoc adjust(final String xClientRef, String tenantId, final URI documentLocation, final CisOrder order);

	/**
	 * <p>
	 * Checks if a tax document exists.
	 * <p>
	 *  @param xClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param documentLocation
	 */
	RawResponse<String> exists(final String xClientRef, String tenantId, final URI documentLocation);
}
