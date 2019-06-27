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
package de.hybris.platform.chinesestoreservices.map.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.hybris.platform.chinesestoreservices.exception.BaiduMapServiceException;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.data.MapLocationData;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.location.Location;


/**
 * The Spring Bean that performs the geo-location HTTP requests towards Baidu Maps Service
 *
 */
public class BaiduMapTools
{

	private static final Logger LOG = Logger.getLogger(BaiduMapTools.class);
	private Map<String, ResponseExtractor> geoParsersMap;
	private String baseUrl;
	private String baiduKey;
	private String output;

	/**
	 * geo-codes the given address data
	 *
	 * @param addressData
	 *           the address to geocode
	 * @return the calculated GPS
	 * @throws GeoServiceWrapperException
	 */
	public GPS geocodeAddress(final AddressData addressData)
	{
		try
		{

			final String urlAddress = getUrlAddressFromAddress(addressData);

			final MapLocationData locationData = getLocationFromService(urlAddress);

			final String latitude = locationData.getLatitude();
			final String longitude = locationData.getLongitude();
			if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude))
			{
				return (new DefaultGPS()).create(NumberUtils.toDouble(latitude), NumberUtils.toDouble(longitude));
			}
			else
			{
				if (BaiduMapServiceException.getErrorMessage(locationData.getCode()) != null)
				{
					LOG.warn("Can't get map service:" + BaiduMapServiceException.getErrorMessage(locationData.getCode()));
					throw new GeoServiceWrapperException(BaiduMapServiceException.getErrorMessage(locationData.getCode()));
				}
				else
				{
					LOG.warn("Can't get map service:"
							+ BaiduMapServiceException.getErrorMessage(BaiduMapServiceException.GEO_SERVICE_ERROR));
					throw new GeoServiceWrapperException(
							BaiduMapServiceException.getErrorMessage(BaiduMapServiceException.GEO_SERVICE_ERROR));
				}

			}
		}
		catch (ResourceAccessException | GeoLocatorException e)
		{
			throw new GeoServiceWrapperException(e);
		}
	}

	protected String getUrlAddressFromAddress(final AddressData addressData)
	{
		return appendBusinessParams(getBaseUrl() + "?address=" + getAddressQuery(addressData));
	}

	protected MapLocationData getLocationFromService(final String urlAddress)
	{
		final RestTemplate restTemplate = new RestTemplate();

		return (MapLocationData) restTemplate.execute(encodeUrl(urlAddress), HttpMethod.GET, null,
				getResponseExtractor(getGeoParsersMap()));

	}

	protected URI encodeUrl(final String address)
	{
		try
		{
			final URL url = new URL(address);
			return new URI(
					url.getProtocol() + "://" + url.getHost() + url.getPath() + "?" + UriUtils.encodeQuery(url.getQuery(), "UTF-8"));

		}
		catch (final UnsupportedEncodingException|URISyntaxException|MalformedURLException e)
		{
			throw new GeoServiceWrapperException("Couldn\'t encode the request.", e);
		}
	}

	/**
	 * geo-codes the given location
	 *
	 * @param address
	 *           the address to geocode
	 * @return the GPS of the given address
	 * @throws GeoServiceWrapperException
	 */
	public GPS geocodeAddress(final Location address)
	{
		Preconditions.checkNotNull(address, "Geocoding failed! Address cannot be null.");
		Preconditions.checkNotNull(address.getAddressData(), "Geocoding failed! Address cannot be null.");

		return geocodeAddress(address.getAddressData());
	}


	/**
	 * parses the given address data
	 *
	 * @param addressData
	 *           the address to parse
	 * @return parsed address string
	 */
	public String getAddressQuery(final AddressData addressData)
	{
		Preconditions.checkNotNull(addressData, "Get address failed! Address cannot be null.");
		final ArrayList collection = Lists.newArrayList(addressData.getCity(),
				addressData.getStreet(), addressData.getBuilding(), addressData.getZip());
		return Joiner.on("+").join(Iterables.filter(collection, Predicates.notNull()));
	}

	protected String appendBusinessParams(final String urlAddress)
	{

		final StringBuilder sb = new StringBuilder(urlAddress);
		if (!StringUtils.isEmpty(getBaiduKey()))
		{
			sb.append("&");
			sb.append("ak");
			sb.append("=");
			sb.append(getBaiduKey());
		}
		if (!StringUtils.isEmpty(getOutput()))
		{
			sb.append("&");
			sb.append("output=");
			sb.append(getOutput());
		}

		return sb.toString();
	}

	/**
	 * @return the parsersMap
	 */
	protected Map getGeoParsersMap()
	{
		return geoParsersMap;
	}

	/**
	 * @param parsersMap
	 *           the parsersMap to set
	 */
	public void setGeoParsersMap(final Map parsersMap)
	{
		geoParsersMap = parsersMap;
	}

	protected ResponseExtractor getResponseExtractor(final Map<String, ResponseExtractor> parsersMap)
	{
		if (parsersMap.get(getOutput()) != null)
		{
			return parsersMap.get(getOutput());
		}
		else
		{
			throw new GeoServiceWrapperException("There's no response parser for data mode " + getOutput());
		}

	}

	public void setBaseUrl(final String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	protected String getBaseUrl()
	{
		return baseUrl;
	}

	public void setOutput(final String output)
	{
		this.output = output;
	}

	protected String getOutput()
	{
		return output;
	}

	public void setBaiduKey(final String key)
	{
		this.baiduKey = key;
	}

	protected String getBaiduKey()
	{
		return baiduKey;
	}

}
