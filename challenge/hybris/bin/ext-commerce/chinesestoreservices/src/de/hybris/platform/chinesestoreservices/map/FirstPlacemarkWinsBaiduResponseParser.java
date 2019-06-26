/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesestoreservices.map;

import de.hybris.platform.chinesestoreservices.map.data.GeocoderResponse;
import de.hybris.platform.chinesestoreservices.map.data.GeocoderResult;
import de.hybris.platform.storelocator.data.MapLocationData;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;


/**
 * Implementation of {@link ResponseExtractor} that parses baidu geo-location json response into a
 * {@link MapLocationData} object. It uses a first win strategy, so the first placemark in the response is parsed
 */

public class FirstPlacemarkWinsBaiduResponseParser implements ResponseExtractor<MapLocationData>
{

	@Override
	public MapLocationData extractData(final ClientHttpResponse response) throws IOException
	{
		final MapLocationData locationData = new MapLocationData();

		if (response.getStatusCode().equals(HttpStatus.OK))
		{
			final ObjectMapper mapper = new ObjectMapper();
			final GeocoderResponse responseObj = mapper.readValue(IOUtils.toString(response.getBody()), GeocoderResponse.class);

			locationData.setCode(responseObj.getStatus());

			final GeocoderResult result = responseObj.getResult();
			if (result != null && result.getLocation() != null)
			{
				locationData.setLatitude(result.getLocation().getLat());
				locationData.setLongitude(result.getLocation().getLng());
			}
		}
		else
		{
			locationData.setCode(response.getStatusCode().toString());
		}
		return locationData;

	}

}
