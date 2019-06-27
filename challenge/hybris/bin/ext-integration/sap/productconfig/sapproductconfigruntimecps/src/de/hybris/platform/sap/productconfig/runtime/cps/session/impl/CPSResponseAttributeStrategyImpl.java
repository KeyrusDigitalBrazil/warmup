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
package de.hybris.platform.sap.productconfig.runtime.cps.session.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CPSResponseAttributeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.NewCookie;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.RawResponse;


/**
 * Default implementation of {@link CPSResponseAttributeStrategy}
 */
public class CPSResponseAttributeStrategyImpl implements CPSResponseAttributeStrategy
{
	private static final Logger LOG = Logger.getLogger(CPSResponseAttributeStrategyImpl.class);
	private CPSCache cpsCache;

	@Override
	public void setCookies(final String configId, final List<NewCookie> cookies)
	{
		if (cookies == null)
		{
			throw new IllegalArgumentException("We expect cookies at this point");
		}
		getCpsCache().setCookies(configId, convertToStringArray(cookies));
	}


	@Override
	public List<String> getCookiesAsString(final String configId)
	{
		final List<String> cookies = getCpsCache().getCookies(configId);
		if (cookies == null)
		{
			LOG.info("No cookies available for configuration: " + configId);
		}
		//We expect 2 cookies thus we need to check the size of the list that we got
		//Cookies can be null in case we didn't find cookies for an existing config in the cache
		if (cookies != null && cookies.size() != 2)
		{
			throw new IllegalStateException("Exactly 2 cookies are expected");
		}
		return cookies;
	}

	/**
	 * @param cookies
	 * @return List of cookies as string, name and value separated by '='
	 */
	public List<String> convertToStringArray(final List<NewCookie> cookies)
	{
		final List<String> cookiesAsString = new ArrayList<>();
		cookies.stream().forEach(cookie -> cookiesAsString.add(cookie2String(cookie)));
		LOG.debug("Cookies to be cached: " + cookiesAsString);
		return cookiesAsString;
	}


	protected String cookie2String(final NewCookie cookie)
	{
		final StringBuilder cookieAsString = new StringBuilder(cookie.getName());
		return cookieAsString.append("=").append(cookie.getValue()).toString();
	}


	@Override
	public void removeCookies(final String configId)
	{
		getCpsCache().removeCookies(configId);

	}


	@Override
	public void setCookiesAsString(final String newConfigId, final List<String> cookieList)
	{
		getCpsCache().setCookies(newConfigId, cookieList);
	}

	protected void extractAndSaveCookies(final RawResponse response, final String configId)
	{
		final List<NewCookie> cookies = response.getSetCookies();
		if (CollectionUtils.isNotEmpty(cookies))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("extractAndSaveCookies, we will store new cookies for configuration: " + configId);
				LOG.debug("Cookies from response: " + cookies);
			}
			setCookies(configId, cookies);
		}
	}

	protected String retrieveETag(final RawResponse response, final String configId)
	{
		final Optional<String> eTag = response.eTag();
		if (eTag.isPresent())
		{
			final String eTagValue = eTag.get();
			LOG.debug("ETag: " + eTagValue);
			return eTagValue;
		}
		throw new IllegalStateException("No eTag returned after update/get for configuration: " + configId);
	}

	@Override
	public String retrieveETagAndSaveResponseAttributes(final RawResponse rawResponse, final String cfgId)
	{
		extractAndSaveCookies(rawResponse, cfgId);
		return retrieveETag(rawResponse, cfgId);
	}


	protected CPSCache getCpsCache()
	{
		return cpsCache;
	}

	@Required
	public void setCpsCache(final CPSCache cpsCache)
	{
		this.cpsCache = cpsCache;
	}
}
