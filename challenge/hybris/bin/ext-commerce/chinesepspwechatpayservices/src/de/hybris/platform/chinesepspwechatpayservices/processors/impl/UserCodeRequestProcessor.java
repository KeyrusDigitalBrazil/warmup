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
package de.hybris.platform.chinesepspwechatpayservices.processors.impl;

import de.hybris.platform.chinesepspwechatpayservices.exception.WeChatPayException;
import de.hybris.platform.chinesepspwechatpayservices.processors.AbstractWeChatPayRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.CharEncoding;


/**
 * Processor for fetching usercode.
 */
public class UserCodeRequestProcessor extends AbstractWeChatPayRequestProcessor<Void>
{

	private static final String WECHAT_REDIRECT_PARAM = "#wechat_redirect";

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public UserCodeRequestProcessor(final WeChatPayConfiguration config, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		super(config, null);
		super.setUrl(config.getOauthURL());
		this.request = request;
		this.response = response;
		super.addParameter("redirect_uri", getRedirectUri());
		super.addParameter("response_type", "code");
		super.addParameter("scope", "snsapi_base");
	}

	private String getRedirectUri()
	{
		final String redirectUri = request.getRequestURL().toString() + "?showwxpaytitle=1";
		try
		{
			return URLEncoder.encode(redirectUri, CharEncoding.UTF_8);
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new WeChatPayException("Error encode redirect uri: " + redirectUri, e);
		}
	}

	@Override
	public Void process()
	{
		final String url = getParams().generateGetURL(this.getUrl()) + FilenameUtils.normalize(WECHAT_REDIRECT_PARAM);
		try
		{
			this.response.sendRedirect(url);//NOSONAR
		}
		catch (final IOException e)
		{
			throw new WeChatPayException("Error redirect to " + url, e);
		}
		return null;
	}
}
