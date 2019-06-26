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
package com.hybris.cis.client.rest.geolocation.mock;

import com.hybris.cis.client.geolocation.models.CisLocationRequest;
import com.hybris.cis.client.geolocation.models.GeoLocationResult;
import com.hybris.cis.client.mock.AvsClientMock;
import com.hybris.cis.client.mock.GeolocationClientMock;
import com.hybris.cis.client.shared.models.CisAddress;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
public class GeolocationMockClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	private GeolocationClientMock geolocationClientMock;

	@Before
	public void setup()
	{
		geolocationClientMock = new GeolocationClientMock();
	}

	@Test
	public void shouldPingSuccess()
	{
		assertEquals(Response.Status.CREATED, geolocationClientMock.doPing(CLIENT_ID, TENANT_ID).status());
	}

	@Test
	public void shouldPingFail()
	{
		assertEquals(Response.Status.FORBIDDEN, geolocationClientMock.doPing(AvsClientMock.PING_FAIL, TENANT_ID).status());
	}

	@Test
	public void shouldGeolocate()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		final List<CisAddress> locations = new ArrayList<CisAddress>();
		locations.add(address);
		final CisLocationRequest cisLocationRequest = new CisLocationRequest();
		cisLocationRequest.setAddresses(locations);
		final GeoLocationResult geolocationResult = geolocationClientMock.getGeolocation(CLIENT_ID, TENANT_ID, cisLocationRequest);
		assertEquals("0.00000", geolocationResult.getGeoLocations().get(0).getLatitude());
		assertEquals("0.00000", geolocationResult.getGeoLocations().get(0).getLongitude());
	}

}
