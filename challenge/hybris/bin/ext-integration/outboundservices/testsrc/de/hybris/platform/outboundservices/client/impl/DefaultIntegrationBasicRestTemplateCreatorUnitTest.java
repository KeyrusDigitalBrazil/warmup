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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationBasicRestTemplateCreatorUnitTest
{
	private static final String DESTINATION_URL = "https://localhost:9002/odata2webservices/InboundProduct/Products";
	private static final String USERNAME = "admin";
	private static final String PASS = "nimda";

	@Mock
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@InjectMocks
	private DefaultIntegrationBasicRestTemplateCreator basicRestTemplateCreator;

	@Mock
	private ConsumedDestinationModel consumedDestination;
	@Mock
	private ExposedOAuthCredentialModel oAuthCredential;
	@Mock
	private BasicCredentialModel basicCredential;
	@Mock
	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
	@Mock
	private ClientHttpRequestInterceptor interceptor;

	@Before
	public void setup()
	{
		when(consumedDestination.getUrl()).thenReturn(DESTINATION_URL);

		when(consumedDestination.getCredential()).thenReturn(basicCredential);
		when(basicCredential.getUsername()).thenReturn(USERNAME);
		when(basicCredential.getPassword()).thenReturn(PASS);

		basicRestTemplateCreator.setMessageConverters(Collections.singletonList(mappingJackson2HttpMessageConverter));
		basicRestTemplateCreator.setRequestInterceptors(Collections.singletonList(interceptor));
	}

	@Test
	public void shouldCreateRestTemplate()
	{
		verifyRestTemplateCreatedCorrectly(basicRestTemplateCreator.create(consumedDestination));
	}

	@Test
	public void shouldThrowUnsupportedRestTemplateException()
	{
		when(consumedDestination.getCredential()).thenReturn(oAuthCredential);

		assertThatThrownBy(() -> basicRestTemplateCreator.create(consumedDestination))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void shouldOnlyHaveBasicAuthorizationInterceptor()
	{
		basicRestTemplateCreator.setRequestInterceptors(null);
		final RestTemplate restTemplate = (RestTemplate) basicRestTemplateCreator.create(consumedDestination);
		assertThat(restTemplate.getInterceptors().size()).isGreaterThanOrEqualTo(1);
		assertThat(restTemplate.getInterceptors().get(0)).extracting("username", "password")
				.containsExactly(USERNAME, PASS);
	}

	private void verifyRestTemplateCreatedCorrectly(final RestOperations restOperations)
	{
		assertThat(restOperations).isExactlyInstanceOf(RestTemplate.class);

		final RestTemplate restTemplate = (RestTemplate) restOperations;

		assertThat(restTemplate.getInterceptors()).isNotEmpty();
		assertThat(restTemplate.getInterceptors()).hasAtLeastOneElementOfType(BasicAuthorizationInterceptor.class);

		assertThat(restTemplate.getInterceptors().stream()
				.filter(i -> i instanceof BasicAuthorizationInterceptor).findFirst().get()).extracting("username", "password")
				.containsExactly(USERNAME, PASS);

		final DefaultUriTemplateHandler uriTemplateHandler = (DefaultUriTemplateHandler) restTemplate.getUriTemplateHandler();

		assertThat(uriTemplateHandler.getBaseUrl()).isEqualTo(null);
		assertThat(restTemplate.getMessageConverters()).contains(mappingJackson2HttpMessageConverter);
		assertThat(restTemplate.getInterceptors()).contains(interceptor);
		assertThat(restTemplate.getErrorHandler()).isInstanceOf(DefaultResponseErrorHandler.class);
	}
}
