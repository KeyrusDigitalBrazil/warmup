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

import com.hybris.cis.client.avs.models.AvsResult;
import com.hybris.cis.client.shared.exception.AbstractCisServiceException;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.avs.AvsClient;
import com.hybris.cis.service.CisClientAvsService;


/**
 * Default implementation for {@link CisClientAvsService}
 */
public class DefaultCisClientAvsService implements CisClientAvsService
{
	private AvsClient avsClient;

	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return getAvsClient().doPing(xCisClientRef, tenantId).status().equals(Status.CREATED) ? true : false;
	}

	@Override
	public AvsResult verifyAddress(final String xCisClientRef, final String tenantId, final CisAddress address)
			throws AbstractCisServiceException
	{
		final AvsResult res = getAvsClient().verifyAddress(xCisClientRef, tenantId, address);
		return res;
	}

	protected AvsClient getAvsClient()
	{
		return avsClient;
	}

	@Required
	public void setAvsClient(final AvsClient avsClient)
	{
		this.avsClient = avsClient;
	}
}
