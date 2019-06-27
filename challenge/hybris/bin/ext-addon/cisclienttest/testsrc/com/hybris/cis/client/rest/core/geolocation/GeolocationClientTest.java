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
package com.hybris.cis.client.rest.core.geolocation;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.geolocation.GeolocationClient;
import com.hybris.cis.client.geolocation.models.CisLocationRequest;
import com.hybris.cis.client.geolocation.models.GeoLocationResult;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@ManualTest
public class GeolocationClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	@Resource
	private GeolocationClient geolocationClient;

	@Test
	public void shouldReturnEmptyResult()
	{
		final CisAddress address = new CisAddress();
		address.setCity("Lindenau");
		address.setZipCode("1945");
		address.setCountry("DE");

		final CisLocationRequest location = new CisLocationRequest();
		final List<CisAddress> locations = new ArrayList<CisAddress>();
		locations.add(address);
		location.setAddresses(locations);

		final GeoLocationResult result = this.geolocationClient.getGeolocation(CLIENT_ID, TENANT_ID, location);

		Assert.assertEquals(null, result.getGeoLocations());
	}

}
