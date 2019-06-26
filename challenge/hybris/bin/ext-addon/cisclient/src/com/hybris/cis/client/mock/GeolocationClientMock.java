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
package com.hybris.cis.client.mock;

import com.hybris.cis.client.geolocation.GeolocationClient;
import com.hybris.cis.client.geolocation.models.CisLocationRequest;
import com.hybris.cis.client.geolocation.models.GeoLocationResult;
import com.hybris.cis.client.shared.models.CisAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HeaderParam;
import java.util.ArrayList;
import java.util.List;


/**
 * Mock implementation of {@link GeolocationClientMock}
 */
public class GeolocationClientMock extends SharedClientMock implements GeolocationClient
{
	private final static Logger LOGGER = LoggerFactory.getLogger(GeolocationClientMock.class);

	public GeolocationClientMock()
	{
		LOGGER.info("Using MOCK Client to simulate Geolocation.");
	}

	@Override
	public GeoLocationResult getGeolocation(@HeaderParam(value = "X-CIS-Client-ref") final String xCisClientRef,
											@HeaderParam(value = "X-tenantId") final String tenantId, final CisLocationRequest location)
	{
		LOGGER.info("Using MOCK Client - getGeolocation()");

		final GeoLocationResult result = new GeoLocationResult();
		final List<CisAddress> locations = new ArrayList<CisAddress>();
		final CisAddress cisAddress = new CisAddress();

		cisAddress.setZipCode(location.getAddresses().get(0).getZipCode());
		cisAddress.setLatitude("0.00000");
		cisAddress.setLongitude("0.00000");
		locations.add(cisAddress);
		result.setGeoLocations(locations);

		return result;
	}
}
