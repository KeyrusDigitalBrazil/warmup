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

import com.hybris.cis.client.geolocation.models.CisLocationRequest;
import com.hybris.cis.client.geolocation.models.GeoLocationResult;
import com.hybris.cis.client.shared.exception.AbstractCisServiceException;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.client.geolocation.GeolocationClient;
import com.hybris.cis.service.CisClientGeolocationService;


/**
 * Default implementation for {@link CisClientGeolocationService}
 */
public class DefaultCisClientGeolocationService implements CisClientGeolocationService
{
	private GeolocationClient geolocationClient;

	@Override
	public GeoLocationResult getGeolocation(final String xCisClientRef, final String tenantId, final CisLocationRequest location)
			throws AbstractCisServiceException
	{
		return getGeolocationClient().getGeolocation(xCisClientRef, tenantId, location);
	}

	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return getGeolocationClient().doPing(xCisClientRef, tenantId).status().equals(Status.CREATED) ? true : false;
	}

	protected GeolocationClient getGeolocationClient()
	{
		return geolocationClient;
	}

	@Required
	public void setGeolocationClient(final GeolocationClient geolocationClient)
	{
		this.geolocationClient = geolocationClient;
	}
}
