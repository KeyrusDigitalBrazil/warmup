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

import de.hybris.platform.sap.sapcpiadapter.clients.SapCpiOAuthClient;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOAuthService;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Required;

import reactor.tuple.Tuple;
import reactor.tuple.Tuple2;
import rx.Observable;

import com.hybris.charon.conf.PropertiesResolver;


public class SapCpiOAuthServiceImpl implements SapCpiOAuthService
{
	final private static ConcurrentHashMap<String, Tuple2<Long, String>> tokens = new ConcurrentHashMap<>();

	private SapCpiOAuthClient authClient;
	private PropertiesResolver propertiesResolver;

	@Override
	public Observable<String> getToken()
	{
		final String clientId = propertiesResolver.lookup("oauth.client_id");
		final String clientSecret = propertiesResolver.lookup("oauth.client_secret");
		final String basicAuth = "Basic " + encodeCredentials(clientId, clientSecret);

		if (!tokens.containsKey(clientId) || tokens.get(clientId).getT1() < System.currentTimeMillis())
		{
			tokens.remove(clientId);

			return authClient.getToken(basicAuth).map(result -> {
				tokens.put(clientId, Tuple.of(System.currentTimeMillis() + Integer.valueOf(result.getExpires_in()) * 1000, result.getAccess_token()));
				return result.getAccess_token();
			});
		}
		else
		{
			return Observable.just(tokens.get(clientId).getT2());
		}
	}

	protected String encodeCredentials(final String clientId, final String clientSecret)
	{
		final String toEncode = clientId + ":" + clientSecret;
		return Base64.getEncoder().encodeToString(toEncode.getBytes());
	}



	/**
	 * @return the authClient
	 */
	public SapCpiOAuthClient getAuthClient()
	{
		return authClient;
	}

	/**
	 * @param authClient
	 *           the authClient to set
	 */
	@Required
	public void setAuthClient(final SapCpiOAuthClient authClient)
	{
		this.authClient = authClient;
	}

	public PropertiesResolver getPropertiesResolver()
	{
		return propertiesResolver;
	}

	@Required
	public void setPropertiesResolver(final PropertiesResolver propertiesResolver)
	{
		this.propertiesResolver = propertiesResolver;
	}
}
