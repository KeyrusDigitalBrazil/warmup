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
package de.hybris.platform.xyformsservices.proxy.impl;

import de.hybris.platform.xyformsservices.proxy.ProxyEngine;
import de.hybris.platform.xyformsservices.proxy.ProxyException;
import de.hybris.platform.xyformsservices.proxy.ProxyService;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for proxy service.
 */
public class DefaultProxyService implements ProxyService
{
	private static final Logger LOG = Logger.getLogger(DefaultProxyService.class);

	protected static final String NEW_COMMAND = "/new";
	protected static final String EDIT_COMMAND = "/edit";
	protected static final String VIEW_COMMAND = "/view";
	protected static final String ORBEON_PREFIX = "/orbeon";
	protected static final String ORBEON_WEB_PREFIX = "/web-orbeon";

	protected String orbeonAddress;
	protected Map<String, String> extraHeaders;
	protected ProxyEngine proxyEngine;

	@Override
	public String rewriteURL(final String url, final boolean embeddable) throws MalformedURLException
	{
		LOG.debug("Got URL [" + url + "]");
		final URL u = new URL(url);

		final int index = u.getPath().indexOf(ORBEON_PREFIX);
		if (index < 0)
		{
			throw new MalformedURLException(ORBEON_PREFIX + " is not part of the URL");
		}

		// Take the URI part of the URL and remove the application context from it.
		final String path = u.getPath().substring(index).replaceFirst(ORBEON_PREFIX, ORBEON_WEB_PREFIX);
		LOG.debug("Call Proxy Service with path [" + path + "]");

		// We are assuming that orbeon is:
		//    - Deployed as /orbeon
		//    - /orbeon is part of the request uri.
		//    - We don't need to go through an Apache Server

		URIBuilder builder = null;
		try
		{
			builder = new URIBuilder(this.orbeonAddress + path);
		}
		catch (final URISyntaxException e)
		{
			LOG.debug("Got malformed URI", e);
			throw new MalformedURLException(e.getMessage());
		}

		// if the original URL had a QueryString...
		if (!StringUtils.isEmpty(u.getQuery()))
		{
			builder.setParameters(URLEncodedUtils.parse(u.getQuery(), Consts.UTF_8));
		}

		// If the form should be embeddable
		if (embeddable)
		{
			builder.addParameter("orbeon-embeddable", "true");
		}

		final String newURL = builder.toString();

		LOG.debug("Rewritten URL [" + newURL + "]");
		return newURL;
	}

	@Override
	public String rewriteURL(final String applicationId, final String formId, final String formDataId, final boolean editable)
			throws MalformedURLException
	{
		String url = this.orbeonAddress + ORBEON_PREFIX + "/fr/" + applicationId + "/" + formId;

		if (formDataId == null || formDataId.isEmpty())
		{
			url = url + NEW_COMMAND;
		}
		else
		{
			if (editable)
			{
				url = url + EDIT_COMMAND + "/" + formDataId;
			}
			else
			{
				url = url + VIEW_COMMAND + "/" + formDataId;
			}
		}

		return rewriteURL(url, true);
	}

	@Override
	public String rewriteURL(final String applicationId, final String formId, final String formDataId) throws MalformedURLException
	{
		return rewriteURL(applicationId, formId, formDataId, false);
	}

	@Override
	public String getNextRandomNamespace()
	{
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.substring(uuid.lastIndexOf('-') + 1);
		return "uuid_" + uuid;
	}

	@Override
	public void proxy(final HttpServletRequest request, final HttpServletResponse response, final String namespace,
			final String url, final boolean forceGetMethod, final Map<String, String> headers) throws ProxyException
	{
		this.proxyEngine.proxy(request, response, namespace, url, forceGetMethod, headers);
	}

	@Override
	public String extractNamespace(final HttpServletRequest request)
	{
		return this.proxyEngine.extractNamespace(request);
	}

	@Required
	public void setOrbeonAddress(final String orbeonAddress) throws MalformedURLException
	{
		String orbeonPrefixAddress = orbeonAddress;
		if (orbeonPrefixAddress.charAt(orbeonPrefixAddress.length() - 1) == '/')
		{
			// we remove the trailing slash
			orbeonPrefixAddress = orbeonPrefixAddress.substring(0, orbeonPrefixAddress.length() - 1);
		}
		if (!orbeonPrefixAddress.contains(ORBEON_WEB_PREFIX))
		{
			throw new MalformedURLException(ORBEON_WEB_PREFIX + " is not part of the given URL");
		}
		orbeonPrefixAddress = orbeonPrefixAddress.substring(0, orbeonPrefixAddress.indexOf(ORBEON_WEB_PREFIX));
		final URL orbeonPrefixURL = new URL(orbeonPrefixAddress);
		this.orbeonAddress = orbeonPrefixURL.toString();
	}

	@Override
	public Map<String, String> getExtraHeaders()
	{
		return extraHeaders;
	}

	@Required
	public void setExtraHeaders(final Map<String, String> extraHeaders)
	{
		this.extraHeaders = extraHeaders;
	}

	@Required
	public void setProxyEngine(final ProxyEngine proxyEngine)
	{
		this.proxyEngine = proxyEngine;
	}
}