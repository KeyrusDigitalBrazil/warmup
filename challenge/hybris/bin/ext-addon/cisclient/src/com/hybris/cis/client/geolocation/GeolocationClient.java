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
package com.hybris.cis.client.geolocation;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.cis.client.CisClient;
import com.hybris.cis.client.geolocation.models.CisLocationRequest;
import com.hybris.cis.client.geolocation.models.GeoLocationResult;


/**
 * Charon Client to the {@link GeolocationClient}.
 */
@Http("geolocation")
public interface GeolocationClient extends CisClient
{

	/**
	 * Get geolocations of the different addresses contained in request.
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param location
	 *           the addresses to verify
	 * @return see {@link GeoLocationResult}å
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/geolocation")
	@Control(retries = "3", retriesInterval = "500")
	GeoLocationResult getGeolocation(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
									 @HeaderParam(value = "X-tenantId") String tenantId, final CisLocationRequest location);

}
