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

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestOperations;


/**
 * The default implementation for OAuth2RestTemplate creator.
 */
public class DefaultIntegrationOAuth2RestTemplateCreator extends AbstractRestTemplateCreator
{
	@Override
	public RestOperations create(final ConsumedDestinationModel destination)
	{
		if (isApplicable(destination))
		{
			final ExposedOAuthCredentialModel credential = (ExposedOAuthCredentialModel) destination.getCredential();
			final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(oAuth2Resource(credential));

			final ClientHttpRequestFactory clientFactory = getClientHttpRequestFactory();
			restTemplate.setAccessTokenProvider(accessTokenProvider(clientFactory));
			restTemplate.setRequestFactory(clientFactory);

			addInterceptors(restTemplate);
			addMessageConverters(restTemplate);

			return restTemplate;
		}

		throw new UnsupportedRestTemplateException();
	}

	@Override
	public boolean isApplicable(final ConsumedDestinationModel destination)
	{
		return destination.getCredential() instanceof ExposedOAuthCredentialModel;
	}

	protected ClientCredentialsAccessTokenProvider accessTokenProvider(final ClientHttpRequestFactory clientFactory)
	{
		final ClientCredentialsAccessTokenProvider provider = new ClientCredentialsAccessTokenProvider();
		provider.setRequestFactory(clientFactory);

		return provider;
	}

	protected OAuth2ProtectedResourceDetails oAuth2Resource(final ExposedOAuthCredentialModel credential)
	{
		final OAuthClientDetailsModel oAuthClientDetails = credential.getOAuthClientDetails();

		final ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri(oAuthClientDetails.getOAuthUrl());
		resource.setClientId(oAuthClientDetails.getClientId());
		resource.setClientSecret(credential.getPassword());
		return resource;
	}
}
