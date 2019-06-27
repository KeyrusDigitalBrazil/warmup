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
package de.hybris.platform.sap.sapcpiadapter.service.impl;

import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOAuthResult;
import de.hybris.platform.sap.sapcpiadapter.clients.SapCpiOAuthClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import rx.Observable;
import rx.observables.BlockingObservable;
import rx.observers.TestSubscriber;

import com.hybris.charon.conf.PropertiesResolver;


public class SapCpiOAuthServiceTest
{
	private PropertiesResolver propertiesResolver;
	private SapCpiOAuthClient authClient;
	private SapCpiOAuthServiceImpl sapOAuthService;

	private String basicAuth;
	private final String oauthToken = "some oauth token";

	@Before
	public void setup()
	{
		// Service to test
		sapOAuthService = new SapCpiOAuthServiceImpl();

		// PropertiesResolver mock
		propertiesResolver = Mockito.mock(PropertiesResolver.class);
		Mockito.when(propertiesResolver.lookup("oauth.client_id")).thenReturn("test");
		Mockito.when(propertiesResolver.lookup("oauth.client_secret")).thenReturn("test");

		// Basic auth
		final String encodedCredentials = sapOAuthService.encodeCredentials("test", "test");
		basicAuth = "Basic " + encodedCredentials;

		final SapCpiOAuthResult authResult = new SapCpiOAuthResult();
		authResult.setAccess_token(oauthToken);
		authResult.setExpires_in("3600");

		authClient = Mockito.mock(SapCpiOAuthClient.class);
		Mockito.when(authClient.getToken(basicAuth)).thenReturn(Observable.just(authResult));

		sapOAuthService.setAuthClient(authClient);
		sapOAuthService.setPropertiesResolver(propertiesResolver);
	}

	@Test
	public void encodeCredentials()
	{
		final String encodedCredentials = sapOAuthService.encodeCredentials("test", "test");
		Assert.assertEquals(encodedCredentials, "dGVzdDp0ZXN0");
	}

	@Test
	public void getToken()
	{
		// First token call
		final BlockingObservable<String> obs = sapOAuthService.getToken().toBlocking();
		final TestSubscriber<String> tester = new TestSubscriber<>();
		obs.subscribe(tester);

		tester.assertCompleted();
		tester.assertValueCount(1);
		tester.assertValue(oauthToken);

		// Cached token call
		final BlockingObservable<String> obs2 = sapOAuthService.getToken().toBlocking();
		final TestSubscriber<String> tester2 = new TestSubscriber<>();
		obs2.subscribe(tester2);

		tester.assertCompleted();
		tester.assertValueCount(1);
		tester.assertValue(oauthToken);

		Mockito.verify(authClient, Mockito.times(1)).getToken(basicAuth);
	}
}
