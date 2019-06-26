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
package com.hybris.cis.service.impl;

import java.net.URI;

import javax.ws.rs.core.Response.Status;

import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.models.CisTaxDoc;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.RawResponse;
import com.hybris.cis.client.tax.TaxClient;
import com.hybris.cis.service.CisClientTaxService;


/**
 * Default implementation for {@link CisClientTaxService}
 */
public class DefaultCisClientTaxService implements CisClientTaxService
{
	private TaxClient taxClient;

	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return getTaxClient().doPing(xCisClientRef, tenantId).status().equals(Status.CREATED) ? true : false;
	}

	@Override
	public CisTaxDoc quote(final String xClientRef, final String tenantId, final CisOrder order)
	{
		final CisTaxDoc taxDoc = getTaxClient().quote(xClientRef, tenantId, order);
		return taxDoc;
	}

	@Override
	public CisTaxDoc post(final String xClientRef, final String tenantId, final CisOrder order)
	{
		final CisTaxDoc taxDoc = getTaxClient().post(xClientRef, tenantId, order);
		return taxDoc;
	}

	@Override
	public CisTaxDoc invoice(final String xClientRef, final String tenantId, final CisOrder order)
	{
		final CisTaxDoc taxDoc = getTaxClient().invoice(xClientRef, tenantId, order);
		return taxDoc;
	}

	@Override
	public RawResponse<String> cancel(final String xClientRef, final String tenantId, final URI documentLocation)
	{
		final String[] uriStrings = documentLocation.getPath().split("/invoices/");
		final String taxDocId = uriStrings[1].replace("/", "");
		return getTaxClient().cancel(xClientRef, tenantId, taxDocId);
	}

	@Override
	public CisTaxDoc adjust(final String xClientRef, final String tenantId, final URI documentLocation, final CisOrder order)
	{
		final String[] uriStrings = documentLocation.getPath().split("/invoices/");
		final String taxDocId = uriStrings[1].replace("/adjustments", "").replace("/", "");
		final CisTaxDoc taxDoc = getTaxClient().adjust(xClientRef, tenantId, taxDocId, order);
		return taxDoc;
	}

	@Override
	public RawResponse<String> exists(final String xClientRef, final String tenantId, final URI documentLocation)
	{
		final String[] uriStrings = documentLocation.getPath().split("/invoices/");
		final String taxDocId = uriStrings[1].replace("/", "");
		return getTaxClient().exists(xClientRef, tenantId, taxDocId);
	}

	protected TaxClient getTaxClient()
	{
		return taxClient;
	}

	@Required
	public void setTaxClient(final TaxClient taxClient)
	{
		this.taxClient = taxClient;
	}
}
