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

import javax.ws.rs.core.Response.Status;

import com.hybris.cis.client.fraud.models.CisFraudReportRequest;
import com.hybris.cis.client.fraud.models.CisFraudReportResult;
import com.hybris.cis.client.fraud.models.CisFraudTransaction;
import com.hybris.cis.client.fraud.models.CisFraudTransactionResult;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.client.fraud.FraudClient;
import com.hybris.cis.service.CisClientFraudService;



/**
 * Default implementation for {@link CisClientFraudService}
 */
public class DefaultCisClientFraudService implements CisClientFraudService
{
	private FraudClient fraudClient;

	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return getFraudClient().doPing(xCisClientRef, tenantId).status().equals(Status.CREATED) ? true : false;
	}

	@Override
	public CisFraudReportResult generateFraudReport(final String xCisClientRef, final String tenantId,
													final CisFraudReportRequest reportRequest)
	{
		return getFraudClient().generateFraudReport(xCisClientRef, tenantId, reportRequest);
	}

	@Override
	public CisFraudTransactionResult handleOrderStatusUpdate(final String xCisClientRef, final String tenantId, final String xml)
	{
		return getFraudClient().handleOrderStatusUpdate(xCisClientRef, tenantId, xml);
	}

	@Override
	public CisFraudTransactionResult calculateFraudScore(final String xCisClientRef, final String tenantId,
			final CisFraudTransaction cisTransaction)
	{
		return getFraudClient().calculateFraudScore(xCisClientRef, tenantId, cisTransaction);
	}

	protected FraudClient getFraudClient()
	{
		return fraudClient;
	}

	@Required
	public void setFraudClient(final FraudClient fraudClient)
	{
		this.fraudClient = fraudClient;
	}
}
