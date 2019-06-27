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
package com.hybris.ymkt.common.filter;

import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import javax.annotation.Tainted;
import javax.annotation.Untainted;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Capture the URL parameter sap-outbound-id and transfer the value to the user session.
 */
public class SapOutboundIdFilter implements Filter
{
	protected static final IntPredicate IS_HEXADECIMAL = c -> Character.digit(c, 16) != -1;

	private static final Logger LOG = LoggerFactory.getLogger(SapOutboundIdFilter.class);

	protected static final String SAP_OUTBOUND_ID = "soid";

	protected SessionService sessionService;
	protected final ArrayList<String> urlKeys = new ArrayList(2);

	@Override
	public void destroy()
	{
		// no need to implement
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		final Optional<String> optId = this.urlKeys.stream() //
				.map(request::getParameter) //
				.map(this::processId) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.findAny();
		if (optId.isPresent())
		{
			final String id = optId.get();
			this.sessionService.setAttribute(SAP_OUTBOUND_ID, id);
			LOG.debug("Adding Tracking ID '{}' to session.", id);
		}

		chain.doFilter(request, response);
	}


	@Override
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		// no need to implement
	}

	@Untainted
	protected Optional<String> processId(@Tainted final String id)
	{
		if (id == null)
		{
			return Optional.empty();
		}
		if (id.length() != 40 || !id.chars().allMatch(IS_HEXADECIMAL))
		{
			LOG.warn("Invalid SAP Outbound ID '{}'", id);
			return Optional.empty();
		}
		return Optional.of(id);
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public void setTrackingURLKeys(final String trackingURLKeys)
	{
		LOG.debug("trackingURLKeys={}", trackingURLKeys);
		this.urlKeys.clear();
		Arrays.stream(trackingURLKeys.split(",")) //
				.map(String::trim) //
				.distinct() //
				.sorted() //
				.map(String::intern) //
				.collect(Collectors.toCollection(() -> this.urlKeys));
		this.urlKeys.trimToSize();
		LOG.debug("urlKeys={}", this.urlKeys);
	}
}
