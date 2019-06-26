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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.DefaultUriTemplateHandler;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationOAuth2RestTemplateCreatorUnitTest
{
	private static final String DESTINATION_URL = "https://localhost:9002/odata2webservices/InboundProduct/Products";
	private static final String OAUTH_URL = "https://localhost:9002/authorizationserver/oauth/token";
	private static final String CLIENT_ID = "admin";
	private static final String CLIENT_SECRET = "nimda";

	@Mock
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@InjectMocks
	private DefaultIntegrationOAuth2RestTemplateCreator oAuth2RestTemplateCreator;

	@Mock
	private ConsumedDestinationModel consumedDestination;
	@Mock
	private ExposedOAuthCredentialModel oAuthCredential;
	@Mock
	private BasicCredentialModel basicCredential;
	@Mock
	private OAuthClientDetailsModel oAuthClientDetailsModel;
	@Mock
	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
	@Mock
	private ClientHttpRequestInterceptor interceptor;

	@Before
	public void setup()
	{
		when(consumedDestination.getUrl()).thenReturn(DESTINATION_URL);

		when(consumedDestination.getCredential()).thenReturn(oAuthCredential);
		when(oAuthCredential.getOAuthClientDetails()).thenReturn(oAuthClientDetailsModel);
		when(oAuthCredential.getPassword()).thenReturn(CLIENT_SECRET);

		when(oAuthClientDetailsModel.getOAuthUrl()).thenReturn(OAUTH_URL);
		when(oAuthClientDetailsModel.getClientId()).thenReturn(CLIENT_ID);

		oAuth2RestTemplateCreator.setMessageConverters(Collections.singletonList(mappingJackson2HttpMessageConverter));
		oAuth2RestTemplateCreator.setRequestInterceptors(Collections.singletonList(interceptor));
	}

	@Test
	public void shouldCreateRestTemplate()
	{
		verifyRestTemplateCreatedCorrectly(oAuth2RestTemplateCreator.create(consumedDestination));
	}

	@Test
	public void shouldThrowUnsupportedRestTemplateException()
	{
		when(consumedDestination.getCredential()).thenReturn(basicCredential);

		assertThatThrownBy(() -> oAuth2RestTemplateCreator.create(consumedDestination))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void shouldNoInterceptor()
	{
		oAuth2RestTemplateCreator.setRequestInterceptors(null);
		final OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) oAuth2RestTemplateCreator.create(consumedDestination);
		assertThat(restTemplate.getInterceptors()).isEmpty();
	}

	private void verifyRestTemplateCreatedCorrectly(final RestOperations restOperations)
	{
		assertThat(restOperations).isExactlyInstanceOf(OAuth2RestTemplate.class);

		final OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) restOperations;

		assertThat(restTemplate.getResource().getClientId()).isEqualTo(CLIENT_ID);
		assertThat(restTemplate.getResource().getClientSecret()).isEqualTo(CLIENT_SECRET);
		assertThat(restTemplate.getResource().getAccessTokenUri()).isEqualTo(OAUTH_URL);

		final DefaultUriTemplateHandler uriTemplateHandler = (DefaultUriTemplateHandler) restTemplate.getUriTemplateHandler();
		assertThat(uriTemplateHandler.getBaseUrl()).isEqualTo(null);
		assertThat(restTemplate.getMessageConverters()).contains(mappingJackson2HttpMessageConverter);
		assertThat(restTemplate.getInterceptors()).contains(interceptor);
		assertThat(restTemplate.getErrorHandler()).isInstanceOf(OAuth2ErrorHandler.class);
	}
}
