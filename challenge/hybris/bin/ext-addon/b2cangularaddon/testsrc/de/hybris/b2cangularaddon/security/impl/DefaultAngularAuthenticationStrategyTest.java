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

import static de.hybris.b2cangularaddon.security.impl.DefaultAngularAuthenticationStrategy.CLIENT_ID;
import static de.hybris.b2cangularaddon.security.impl.DefaultAngularAuthenticationStrategy.CLIENT_SECRET;
import static de.hybris.b2cangularaddon.security.impl.DefaultAngularAuthenticationStrategy.GRANT_TYPE;
import static de.hybris.b2cangularaddon.security.impl.DefaultAngularAuthenticationStrategy.PASSWORD;
import static de.hybris.b2cangularaddon.security.impl.DefaultAngularAuthenticationStrategy.USERNAME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.webservicescommons.oauth2.token.OAuthTokenService;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.web.util.CookieGenerator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAngularAuthenticationStrategyTest
{
	private static final String token = "token";
	private static final String username = "user@mail.com";
	private static final String encodedUsername = "user%40mail.com";
	private static final String password = "12341234";
	private static final String clientId = "clientId";
	private static final String clientSecret = "clientSecret";
	private static final String usernameParameter = "usernameParameter";
	private static final String passwordParameter = "passwordParameter";

	@Mock
	private Map<String, String[]> parameterMap;

	@Mock
	private TokenGranter tokenGranter;

	@Mock
	private CookieGenerator tokenCookieGenerator;

	@Mock
	private CookieGenerator userIdCookieGenerator;

	@Mock
	private OAuthTokenService oauthTokenService;

	@Mock
	private Authentication authentication;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private OAuth2AccessToken accessToken;

	@Mock
	private OAuthAccessTokenModel tokenModel1, tokenModel2;

	@Spy
	@InjectMocks
	private final DefaultAngularAuthenticationStrategy authenticationStrategy = new DefaultAngularAuthenticationStrategy();

	@Before()
	public void setup()
	{
		authenticationStrategy.setClientId(clientId);
		authenticationStrategy.setClientSecret(clientSecret);
		authenticationStrategy.setUsernameParameter(usernameParameter);
		authenticationStrategy.setPasswordParameter(passwordParameter);

		doReturn(token).when(accessToken).getValue();
		doReturn(token).when(tokenModel1).getTokenId();
		doReturn(username).when(authentication).getName();
		doReturn(parameterMap).when(request).getParameterMap();
		doReturn(new String[]
		{ username }).when(parameterMap).get(usernameParameter);
		doReturn(new String[]
		{ password }).when(parameterMap).get(passwordParameter);
	}

	@Test(expected = IllegalStateException.class)
	public void testLoginWhenUnsecureRequest()
	{
		doReturn(Boolean.FALSE).when(request).isSecure();
		authenticationStrategy.login(request, response);
	}

	@Test
	public void testLoginWhenSecureRequestButNoTokenAcquired()
	{
		doReturn(Boolean.TRUE).when(request).isSecure();
		doReturn(Optional.empty()).when(authenticationStrategy).acquireOAuthToken(request);

		authenticationStrategy.login(request, response);

		verify(tokenCookieGenerator, never()).addCookie(response, token);
		verify(userIdCookieGenerator).addCookie(response, encodedUsername);
	}

	@Test
	public void testLoginWhenSecureRequestAndTokenAcquired()
	{
		doReturn(Boolean.TRUE).when(request).isSecure();
		doReturn(Optional.of(token)).when(authenticationStrategy).acquireOAuthToken(request);

		authenticationStrategy.login(request, response);

		verify(tokenCookieGenerator).addCookie(response, token);
		verify(userIdCookieGenerator).addCookie(response, encodedUsername);
	}

	@Test
	public void testLogoutWhenUnsecureRequest()
	{
		doReturn(Boolean.FALSE).when(request).isSecure();

		authenticationStrategy.logout(request, response, authentication);

		verify(authenticationStrategy, never()).removeToken(any(OAuthAccessTokenModel.class));
		verify(tokenCookieGenerator, never()).removeCookie(response);
		verify(userIdCookieGenerator, never()).removeCookie(response);
	}

	@Test
	public void testLogoutWhenAuthenticationParmNotAvaialble()
	{
		doReturn(Boolean.TRUE).when(request).isSecure();

		authenticationStrategy.logout(request, response, null);

		verify(authenticationStrategy, never()).removeToken(any(OAuthAccessTokenModel.class));
		verify(tokenCookieGenerator).removeCookie(response);
		verify(userIdCookieGenerator).removeCookie(response);
	}

	@Test
	public void testLogoutWhenSecureRequestAndAuthenticationParamAvailable()
	{
		doReturn(Boolean.TRUE).when(request).isSecure();
		doReturn(asList(tokenModel1, tokenModel2)).when(oauthTokenService).getAccessTokensForClientAndUser(clientId, username);

		authenticationStrategy.logout(request, response, authentication);

		verify(authenticationStrategy).removeToken(tokenModel1);
		verify(authenticationStrategy).removeToken(tokenModel2);
		verify(tokenCookieGenerator).removeCookie(response);
		verify(userIdCookieGenerator).removeCookie(response);
	}

	@Test
	public void testAcquireOAuthTokenWhenTokenNotGranted()
	{
		doReturn(null).when(tokenGranter).grant(anyString(), any(TokenRequest.class));

		assertEquals(Optional.empty(), authenticationStrategy.acquireOAuthToken(request));
	}

	@Test
	public void testAcquireOAuthTokenWhenTokenGranted()
	{
		doReturn(accessToken).when(tokenGranter).grant(anyString(), any(TokenRequest.class));

		final Optional<String> optionalToken = authenticationStrategy.acquireOAuthToken(request);

		assertTrue(optionalToken.isPresent());
		assertEquals(token, optionalToken.get());
	}

	@Test
	public void testGetTokenRequestParams()
	{
		final Map<String, String> requestParameters = authenticationStrategy.getTokenRequestParams(request);

		assertEquals(requestParameters.get(CLIENT_ID), clientId);
		assertEquals(requestParameters.get(CLIENT_SECRET), clientSecret);
		assertEquals(requestParameters.get(GRANT_TYPE), PASSWORD);
		assertEquals(requestParameters.get(USERNAME), username);
		assertEquals(requestParameters.get(PASSWORD), password);
	}

	@Test
	public void testEncodeUrlValue()
	{
		assertEquals(encodedUsername, authenticationStrategy.encodeUrlValue(username));
	}

	@Test
	public void testGetUsername()
	{
		assertEquals(username, authenticationStrategy.getUsername(request));
	}

	@Test
	public void testGetPassword()
	{
		assertEquals(password, authenticationStrategy.getPassword(request));
	}

	@Test
	public void testRemoveToken()
	{
		authenticationStrategy.removeToken(tokenModel1);

		verify(oauthTokenService).removeAccessToken(token);
	}

	@Test
	public void testRemoveTokenWhenTokenNull()
	{
		authenticationStrategy.removeToken(null);

		verify(oauthTokenService, never()).removeAccessToken(token);
	}
}
