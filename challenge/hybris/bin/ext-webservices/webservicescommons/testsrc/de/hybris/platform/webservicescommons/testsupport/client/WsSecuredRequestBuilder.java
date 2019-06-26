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
package de.hybris.platform.webservicescommons.testsupport.client;

import de.hybris.platform.core.Registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class WsSecuredRequestBuilder extends WsAbstractRequestBuilder<WsSecuredRequestBuilder>
{
	public static final String WS_TEST_OAUTH2_TOKEN_ENDPOINT_PATH_KEY = "webservices.test.oauth2.endpoint";
	protected static final String HEADER_AUTH_KEY = "Authorization";
	protected static final String HEADER_AUTH_VALUE_PREFIX = "Bearer";
	private static final Logger LOG = Logger.getLogger(WsSecuredRequestBuilder.class);
	private static final String GRANT_TYPE_CLIENT_CRIDENTIALS = "client_credentials";
	private static final String GRANT_TYPE_PASSWORD = "password";

	private final ObjectMapper jsonMapper = new ObjectMapper();

	private static final String OAUTH_EXTENSION_NAME = "oauth2";
	private final String oAuthEndpointPath = getDefaultOAuthEndpoint();
	private String oAuthClientId;
	private String oAuthClientSecret;
	private String oAuthResourceOwnerName;
	private String oAuthResourceOwnerPassword;
	private String oAuthScope;

	private OAuthGrantType oAuthGrantType;

	public enum OAuthGrantType
	{
		RESOURCE_OWNER_PASSWORD_CREDENTIALS, CLIENT_CREDENTIALS;
	}

	public WsSecuredRequestBuilder client(final String clientId, final String clientSecret)
	{
		this.oAuthClientId = clientId;
		this.oAuthClientSecret = clientSecret;
		return getThis();
	}

	public WsSecuredRequestBuilder scope(final String... scope)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("scope has to have not null value");
		}
		this.oAuthScope = String.join(",", scope);
		return getThis();
	}

	public WsSecuredRequestBuilder resourceOwner(final String oAuthResourceOwnerName, final String oAuthResourceOwnerPassword)
	{
		this.oAuthResourceOwnerName = oAuthResourceOwnerName;
		this.oAuthResourceOwnerPassword = oAuthResourceOwnerPassword;
		return getThis();
	}

	public WsSecuredRequestBuilder grantClientCredentials()
	{
		this.oAuthGrantType = OAuthGrantType.CLIENT_CREDENTIALS;
		return getThis();
	}

	public WsSecuredRequestBuilder grantResourceOwnerPasswordCredentials()
	{
		this.oAuthGrantType = OAuthGrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS;
		return getThis();
	}

	protected String getDefaultOAuthEndpoint()
	{
		return Registry.getCurrentTenant().getConfig().getString(WS_TEST_OAUTH2_TOKEN_ENDPOINT_PATH_KEY, "/oauth/token");
	}

	protected String getOAuth2TokenUsingClientCredentials()
	{
		return getOAuth2TokenUsingClientCredentials(buildOAuthWebTarget(), oAuthClientId, oAuthClientSecret, oAuthScope);
	}

	protected String getOAuth2TokenUsingClientCredentials(final WebTarget oAuthWebTarget, final String clientID,
			final String clientSecret, final String scope)
	{
		try
		{
			final Response result = oAuthWebTarget.queryParam("grant_type", GRANT_TYPE_CLIENT_CRIDENTIALS)
					.queryParam("client_id", clientID).queryParam("client_secret", clientSecret)
					.queryParam("scope", scope).request()
					.accept(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
			result.bufferEntity();

			if (result.hasEntity())
			{
				return getTokenFromJsonStr(result.readEntity(String.class));
			}
			else
			{
				LOG.error("Empty response body!!");
				return null;
			}
		}
		catch (final IOException ex)
		{
			LOG.error("Error during authorizing REST client client credentials!!", ex);
			return null;
		}
	}

	protected String getOAuth2Token()
	{
		if (oAuthGrantType == null)
		{
			throw new WsRequestBuilderException("OAuth grant type not set!");
		}
		switch (oAuthGrantType)
		{
			case CLIENT_CREDENTIALS:
				return getOAuth2TokenUsingClientCredentials();
			case RESOURCE_OWNER_PASSWORD_CREDENTIALS:
				return getOAuth2TokenUsingResourceOwnerPassword();
			default:
				return null;
		}
	}


	protected String getOAuth2TokenUsingResourceOwnerPassword()
	{
		return getOAuth2TokenUsingResourceOwnerPassword(buildOAuthWebTarget(), oAuthClientId, oAuthClientSecret,
				oAuthResourceOwnerName, oAuthResourceOwnerPassword, oAuthScope);
	}

	protected String getOAuth2TokenUsingResourceOwnerPassword(final WebTarget oAuthWebTarget, final String clientID,
			final String clientSecret, final String resourceOwnerName, final String resourceOwnerPassword, final String scope)
	{
		try
		{
			final Response result = oAuthWebTarget.queryParam("grant_type", GRANT_TYPE_PASSWORD)
					.queryParam("username", resourceOwnerName).queryParam("password", resourceOwnerPassword)
					.queryParam("client_id", clientID).queryParam("client_secret", clientSecret)
					.queryParam("scope", scope).request()
					.accept(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
			result.bufferEntity();

			if (result.hasEntity())
			{
				return getTokenFromJsonStr(result.readEntity(String.class));
			}
			else
			{
				LOG.error("Empty response body!!");
				return null;
			}
		}
		catch (final IOException ex)
		{
			LOG.error("Error during authorizing REST client using Resource owner password!!", ex);
			return null;
		}
	}

	protected WebTarget buildOAuthWebTarget()
	{
		return createWebTarget(getHost(), getPort(), isUseHttps(), OAUTH_EXTENSION_NAME, oAuthEndpointPath);
	}

	@Override
	public Invocation.Builder build()
	{
		final String token = getOAuth2Token();
		final String authHeaderValue = HEADER_AUTH_VALUE_PREFIX + " " + token;

		final Invocation.Builder builder = super.build();
		builder.header(HEADER_AUTH_KEY, authHeaderValue);
		return builder;
	}

	public String getTokenFromJsonStr(final String jsonStr) throws IOException
	{
		final Map<String, String> map = jsonMapper.readValue(jsonStr, new TypeReference<HashMap<String, String>>()
		{/* empty */});
		return map.get("access_token");
	}

	@Override
	protected WsSecuredRequestBuilder getThis()
	{
		return this;
	}
}
