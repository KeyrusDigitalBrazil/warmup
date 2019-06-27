/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingc4cintegration.facade.utils;

import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;


public class HttpHeaderUtil
{
	private final static Logger LOGGER = Logger.getLogger(HttpHeaderUtil.class);
	private final static String BASIC = "Basic ";
	private SitePropsHolder sitePropsHolder;
	private RestTemplate restTemplate;

	/**
	 * Adds basic batch headers like
	 *
	 * @param uri
	 * @return HttpHeaders
	 */
	public HttpHeaders addBatchHeaders(final String uri)
	{
		final HttpHeaders headers = new HttpHeaders();

		headers.set("", uri);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Customerticketingc4cintegrationConstants.CONTENT_ID,
				Customerticketingc4cintegrationConstants.CONTENT_ID_VALUE_PREFIX + RandomUtils.nextInt(1000));

		return headers;
	}

	/**
	 * Make a request to a c4c system to get a special token for the other requests. wrapper for enrichHeaders(final
	 * HttpHeaders headers, final String siteId)
	 *
	 * @return enriched HttpHeaders
	 */
	public HttpHeaders getEnrichedHeaders()
	{
		final String siteId = getSitePropsHolder().getSiteId();
		LOGGER.info("SiteId: " + siteId);
		final HttpHeaders headers = getDefaultHeaders(siteId);
		return enrichHeaders(headers, siteId);
	}

	/**
	 * Make a request to a c4c system to get a special token for the other requests.
	 *
	 * @param headers
	 * @param siteId
	 * @return headers with special token
	 */
	public HttpHeaders enrichHeaders(final HttpHeaders headers, final String siteId)
	{
		final String url = Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX
				+ Customerticketingc4cintegrationConstants.TOKEN_URL_SUFFIX;
		LOGGER.info(url);

		final HttpHeaders tempHeaders = getDefaultHeaders(siteId);
		final HttpEntity<String> entity = new HttpEntity<>(tempHeaders);

		final ResponseEntity<String> result = getRestTemplate().exchange(url, HttpMethod.GET, entity, String.class); // try - catch !

		LOGGER.info(result.getHeaders());

		if (null != result.getHeaders().get(Customerticketingc4cintegrationConstants.RESPONSE_COOKIE_NAME))
		{
			headers.put(HttpHeaders.COOKIE, result.getHeaders().get(Customerticketingc4cintegrationConstants.RESPONSE_COOKIE_NAME));
		}

		if (result.getHeaders().containsKey(Customerticketingc4cintegrationConstants.TOKEN_NAMING))
		{
			final List<String> l = result.getHeaders().get(Customerticketingc4cintegrationConstants.TOKEN_NAMING);
			headers.put(Customerticketingc4cintegrationConstants.TOKEN_NAMING, Arrays.asList(l.get(0)));
		}

		return headers;
	}

	/**
	 * Encoding for username/password headers. Makes BasicAuthHeader
	 *
	 * @param username
	 * @param password
	 * @return basic authentication string header
	 */
	public String createBasicAuthHeader(final String username, final String password)
	{
		final String auth = username + ":" + password;
		final String authHeader = BASIC + Base64Utils.encodeToString(auth.getBytes());
		return authHeader;
	}

	/**
	 * Makes some default http headers for communication with c4c
	 *
	 * @param siteId
	 * @return default headers
	 */
	public HttpHeaders getDefaultHeaders(final String siteId)
	{
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.put(HttpHeaders.ACCEPT, Arrays.asList(Customerticketingc4cintegrationConstants.ACCEPT));

		headers.set(HttpHeaders.AUTHORIZATION, createBasicAuthHeader(Customerticketingc4cintegrationConstants.USERNAME,
				Customerticketingc4cintegrationConstants.PASSWORD));

		headers.put(Customerticketingc4cintegrationConstants.TOKEN_NAMING,
				Arrays.asList(Customerticketingc4cintegrationConstants.TOKEN_EMPTY));
		headers.put(Customerticketingc4cintegrationConstants.SITE_HEADER, Arrays.asList(siteId));

		return headers;
	}

	protected SitePropsHolder getSitePropsHolder()
	{
		return sitePropsHolder;
	}

	@Required
	public void setSitePropsHolder(final SitePropsHolder sitePropsHolder)
	{
		this.sitePropsHolder = sitePropsHolder;
	}

	protected RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	@Required
	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}
}
