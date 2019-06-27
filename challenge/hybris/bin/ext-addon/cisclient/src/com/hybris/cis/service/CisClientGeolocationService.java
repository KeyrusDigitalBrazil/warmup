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

import com.hybris.cis.client.geolocation.models.CisLocationRequest;
import com.hybris.cis.client.geolocation.models.GeoLocationResult;
import com.hybris.cis.client.shared.exception.AbstractCisServiceException;

/**
 * Interface providing geolocation services
 */
public interface CisClientGeolocationService extends CisClientService
{
	/**
	 * Rest client to look up geolocations.
	 *
	 * @param xCisClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param location
	 * 		a location request containing at least zip code and country, optionally city
	 * @result a {@link GeoLocationResult} containing a long / lat for the given request
	 * @throws {@link AbstractCisServiceException}
	 */
	GeoLocationResult getGeolocation(final String xCisClientRef, final String tenantId, final CisLocationRequest location)
			throws AbstractCisServiceException;
}
