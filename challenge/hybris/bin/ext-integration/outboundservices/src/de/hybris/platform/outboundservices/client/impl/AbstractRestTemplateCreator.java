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
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.platform.outboundservices.client.IntegrationRestTemplateCreator;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

/**
 * The abstract RestTemplate creator.
 */
public abstract class AbstractRestTemplateCreator implements IntegrationRestTemplateCreator
{
	private List<HttpMessageConverter<Object>> messageConverters = Lists.newArrayList();
	private List<ClientHttpRequestInterceptor> requestInterceptors = Lists.newArrayList();
	private ClientHttpRequestFactory clientHttpRequestFactory;

	protected void addInterceptors(final RestTemplate template, final ClientHttpRequestInterceptor... interceptors)
	{
		final List<ClientHttpRequestInterceptor> list = Lists.newArrayList(interceptors);
		if (getRequestInterceptors() != null)
		{
			list.addAll(getRequestInterceptors());
		}
		list.addAll(template.getInterceptors());
		template.setInterceptors(list);
	}

	protected void addMessageConverters(final RestTemplate template, final HttpMessageConverter<?>... converters)
	{
		final List<HttpMessageConverter<?>> list = Lists.newArrayList(converters);
		if (getMessageConverters() != null)
		{
			list.addAll(getMessageConverters());
		}
		list.addAll(template.getMessageConverters());
		template.setMessageConverters(list);
	}

	protected List<HttpMessageConverter<Object>> getMessageConverters()
	{
		return messageConverters;
	}

	@Required
	public void setMessageConverters(final List<HttpMessageConverter<Object>> messageConverters)
	{
		this.messageConverters = messageConverters;
	}

	protected List<ClientHttpRequestInterceptor> getRequestInterceptors()
	{
		return requestInterceptors;
	}

	@Required
	public void setRequestInterceptors(final List<ClientHttpRequestInterceptor> requestInterceptors)
	{
		this.requestInterceptors = requestInterceptors;
	}

	@Required
	public void setClientHttpRequestFactory(final ClientHttpRequestFactory clientHttpRequestFactory)
	{
		this.clientHttpRequestFactory = clientHttpRequestFactory;
	}

	protected ClientHttpRequestFactory getClientHttpRequestFactory()
	{
		return this.clientHttpRequestFactory;
	}
}
