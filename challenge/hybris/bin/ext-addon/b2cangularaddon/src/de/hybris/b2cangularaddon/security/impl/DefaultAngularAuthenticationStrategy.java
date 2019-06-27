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
package de.hybris.b2cangularaddon.security.impl;

import static java.util.stream.Collectors.toSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.web.util.CookieGenerator;

import de.hybris.b2cangularaddon.security.AngularAuthenticationStrategy;
import de.hybris.platform.webservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.webservicescommons.oauth2.token.OAuthTokenService;


public class DefaultAngularAuthenticationStrategy implements AngularAuthenticationStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultAngularAuthenticationStrategy.class);
	protected static final String DEFAULT_ENCODING = "UTF-8";
	protected static final String CLIENT_ID = "client_id";
	protected static final String CLIENT_SECRET = "client_secret";
	protected static final String GRANT_TYPE = "grant_type";
	protected static final String USERNAME = "username";
	protected static final String PASSWORD = "password";
	protected static final String BASIC_SCOPE = "basic";

	private String clientId;
	private String clientSecret;
	private String usernameParameter;
	private String passwordParameter;
	private TokenGranter tokenGranter;
	private CookieGenerator tokenCookieGenerator;
	private CookieGenerator userIdCookieGenerator;
	private OAuthTokenService oauthTokenService;

	@Override
	public void login(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (!request.isSecure())
		{
			// We must not generate the cookie for insecure requests, otherwise there is not point doing this at all
			throw new IllegalStateException("Cannot set token/userId cookies on an insecure request!");
		}

		// Token cookie
		acquireOAuthToken(request).ifPresent(token -> getTokenCookieGenerator().addCookie(response, token));

		// User ID cookie
		final String userId = encodeUrlValue(getUsername(request));
		getUserIdCookieGenerator().addCookie(response, userId);
	}

	@Override
	public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
	{
		if (!request.isSecure())
		{
			LOG.error("Cannot remove token/userId cookies on an insecure request.");
		}
		else
		{
			// Remove the token from the DB
			Optional.ofNullable(authentication).map(Principal::getName).ifPresent(userId -> getOauthTokenService()
					.getAccessTokensForClientAndUser(clientId, userId).stream().forEach(this::removeToken));

			// Remove the token from the cookies
			getTokenCookieGenerator().removeCookie(response);

			// Remove userId from the cookies
			getUserIdCookieGenerator().removeCookie(response);
		}
	}

	protected Optional<String> acquireOAuthToken(final HttpServletRequest request)
	{
		try
		{
			final TokenRequest tokenRequest = new TokenRequest(getTokenRequestParams(request), getClientId(),
					Stream.of(BASIC_SCOPE).collect(toSet()), PASSWORD);
			final OAuth2AccessToken oAuth2AccessToken = getTokenGranter().grant(PASSWORD, tokenRequest);
			return Optional.of(oAuth2AccessToken.getValue());
		}
		catch (final RuntimeException exception)
		{
			LOG.error(exception);
			return Optional.empty();
		}
	}

	protected Map<String, String> getTokenRequestParams(final HttpServletRequest request)
	{
		final Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put(CLIENT_ID, getClientId());
		requestParameters.put(CLIENT_SECRET, getClientSecret());
		requestParameters.put(GRANT_TYPE, PASSWORD);
		requestParameters.put(USERNAME, getUsername(request));
		requestParameters.put(PASSWORD, getPassword(request));
		return requestParameters;
	}

	protected String encodeUrlValue(final String userId)
	{
		String encodedUserId = userId;
		try
		{
			encodedUserId = URLEncoder.encode(userId, DEFAULT_ENCODING);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error(e);
		}
		return encodedUserId;
	}

	protected String getUsername(final HttpServletRequest request)
	{
		return getParameterValue(request, getUsernameParameter());
	}

	protected String getPassword(final HttpServletRequest request)
	{
		return getParameterValue(request, getPasswordParameter());
	}

	protected String getParameterValue(final HttpServletRequest request, final String paramName)
	{
		final String parameterValue = Optional.ofNullable(request.getParameterMap().get(paramName)).map(values -> values[0])
				.orElse(StringUtils.EMPTY);
		if (parameterValue.isEmpty())
		{
			LOG.error(String.format("%s parameter should not be empty", paramName));
		}
		return parameterValue;
	}

	protected void removeToken(final OAuthAccessTokenModel token)
	{
		try
		{
			getOauthTokenService().removeAccessToken(token.getTokenId());
		}
		catch (final RuntimeException exception)
		{
			LOG.error(exception);
		}
	}

	protected String getClientId()
	{
		return clientId;
	}

	@Required
	public void setClientId(final String clientId)
	{
		this.clientId = clientId;
	}

	protected String getClientSecret()
	{
		return clientSecret;
	}

	@Required
	public void setClientSecret(final String clientSecret)
	{
		this.clientSecret = clientSecret;
	}

	protected String getPasswordParameter()
	{
		return passwordParameter;
	}

	@Required
	public void setPasswordParameter(final String passwordParameter)
	{
		this.passwordParameter = passwordParameter;
	}

	protected String getUsernameParameter()
	{
		return usernameParameter;
	}

	@Required
	public void setUsernameParameter(final String usernameParameter)
	{
		this.usernameParameter = usernameParameter;
	}

	protected TokenGranter getTokenGranter()
	{
		return tokenGranter;
	}

	@Required
	public void setTokenGranter(TokenGranter tokenGranter)
	{
		this.tokenGranter = tokenGranter;
	}

	protected CookieGenerator getTokenCookieGenerator()
	{
		return tokenCookieGenerator;
	}

	@Required
	public void setTokenCookieGenerator(final CookieGenerator tokenCookieGenerator)
	{
		this.tokenCookieGenerator = tokenCookieGenerator;
	}

	protected CookieGenerator getUserIdCookieGenerator()
	{
		return userIdCookieGenerator;
	}

	@Required
	public void setUserIdCookieGenerator(final CookieGenerator userIdCookieGenerator)
	{
		this.userIdCookieGenerator = userIdCookieGenerator;
	}

	protected OAuthTokenService getOauthTokenService()
	{
		return oauthTokenService;
	}

	@Required
	public void setOauthTokenService(final OAuthTokenService oauthTokenService)
	{
		this.oauthTokenService = oauthTokenService;
	}
}
