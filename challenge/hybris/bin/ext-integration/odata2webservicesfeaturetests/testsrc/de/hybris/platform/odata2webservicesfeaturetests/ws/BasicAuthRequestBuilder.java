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

package de.hybris.platform.odata2webservicesfeaturetests.ws;

import de.hybris.platform.webservicescommons.testsupport.client.WsAbstractRequestBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * A builder to be used for requests with basic authentication
 */
public class BasicAuthRequestBuilder extends WsAbstractRequestBuilder<BasicAuthRequestBuilder>
{
	private HttpAuthenticationFeature authenticator;
	private String accept;

	public BasicAuthRequestBuilder credentials(final String username, final String pwd)
	{
		authenticator = HttpAuthenticationFeature.basic(username, pwd);
		return this;
	}

	public BasicAuthRequestBuilder accept(final String accept)
	{
		this.accept = accept;
		return this;
	}

	@Override
	protected Client createClient()
	{
		final Client client = super.createClient();
		if (authenticator != null)
		{
			client.register(authenticator);
		}
		return client;
	}

	@Override
	public Invocation.Builder build()
	{
		return accept != null
				? super.build().accept(accept)
				: super.build();
	}

	@Override
	protected BasicAuthRequestBuilder getThis()
	{
		return this;
	}
}
