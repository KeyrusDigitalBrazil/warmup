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
/**
 *
 */
package com.hybris.ymkt.common.filter;

import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.constants.SapymktcommonConstants;


/**
 * This class will inject a PiwikId in the session
 */
public class YmktPersonalizationPiwikStrategyFilter implements Filter
{
	protected SessionService sessionService;

	@Override
	public void destroy()
	{
		// no need to implement
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		Optional.ofNullable(request) //
				.map(HttpServletRequest.class::cast) //
				.map(HttpServletRequest::getCookies) //
				.map(Arrays::stream).orElse(Stream.empty()) //
				.filter(this::isPiwikCookie) //
				.map(Cookie::getValue) //
				.map(s -> s.substring(0, 16)) //
				.findAny() //
				.ifPresent(this::setSessionPiwikId);

		chain.doFilter(request, response);
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException
	{
		// no need to implement
	}

	protected boolean isPiwikCookie(final Cookie c)
	{
		return c.getName().startsWith("_pk_id");
	}

	protected void setSessionPiwikId(final String piwik)
	{
		this.sessionService.setAttribute(SapymktcommonConstants.PERSONALIZATION_PIWIK_ID_SESSION_KEY, piwik);
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
