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
package de.hybris.platform.xyformsfacades.proxy.impl;

import com.sap.security.core.server.csi.XSSEncoder;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.xyformsfacades.proxy.ProxyFacade;
import de.hybris.platform.xyformsservices.enums.YFormDataActionEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;
import de.hybris.platform.xyformsservices.proxy.ProxyException;
import de.hybris.platform.xyformsservices.proxy.ProxyService;
import de.hybris.platform.xyformsservices.utils.YHttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



/**
 * Orchestrates calls to {@link ProxyService}
 */
public class DefaultProxyFacade implements ProxyFacade
{
	@Resource
	private ProxyService proxyService;

	@Override
	public String getInlineFormHtml(final String applicationId, final String formId, final YFormDataActionEnum action,
			final String formDataId) throws YFormServiceException
	{
		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final StringWriter out = new StringWriter();
		final OutputStream os = new WriterOutputStream(out, "UTF-8");

		try
		{
			final boolean editable = !YFormDataActionEnum.VIEW.equals(action);
			final String url = getProxyService().rewriteURL(applicationId, formId, formDataId, editable);
			final String namespace = getProxyService().getNextRandomNamespace();
			final Map<String, String> extraHeaders = getProxyService().getExtraHeaders();

			// The proxy needs a Response object... we provide a mocked one.
			final HttpServletResponse response = new YHttpServletResponse(os);

			getProxyService().proxy(request, response, namespace, url, true, extraHeaders);
			os.flush();
			return out.toString();
		}
		catch (final ProxyException | IOException e)
		{
			throw new YFormServiceException(e);
		}
		finally
		{
			IOUtils.closeQuietly(os);
		}
	}

	@Override
	public void proxy(final HttpServletRequest request, final HttpServletResponse response) throws ProxyException
	{
		try
		{
			final String url = request.getRequestURL().toString();

			final String namespace = urlEncode(getProxyService().extractNamespace(request));
			final String newURL = getProxyService().rewriteURL(url, false);
			final Map<String, String> extraHeaders = getProxyService().getExtraHeaders();

			getProxyService().proxy(request, response, namespace, newURL, false, extraHeaders);
		}
		catch (final MalformedURLException e)
		{
			throw new ProxyException(e);
		}
	}

	protected String urlEncode(final String url)
	{
		if (url == null)
		{
			return null;
		}
		try
		{
			return XSSEncoder.encodeURL(url);
		}
		catch (final UnsupportedEncodingException e)
		{
			return url;
		}
	}

	protected ProxyService getProxyService()
	{
		return this.proxyService;
	}

	public void setProxyService(final ProxyService proxyService)
	{
		this.proxyService = proxyService;
	}
}
