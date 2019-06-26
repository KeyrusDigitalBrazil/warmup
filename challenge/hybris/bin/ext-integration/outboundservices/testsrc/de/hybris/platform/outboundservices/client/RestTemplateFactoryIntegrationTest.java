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
package de.hybris.platform.outboundservices.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.outboundservices.client.impl.UnsupportedRestTemplateException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@IntegrationTest
public class RestTemplateFactoryIntegrationTest extends ServicelayerTest
{
	private static final String TEST_SECRET = UUID.randomUUID().toString();

	@Resource
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	@Resource
	private DestinationService<ConsumedDestinationModel> destinationService;
	@Resource
	private IntegrationRestTemplateCreator integrationOAuth2RestTemplateCreator;
	@Resource
	private IntegrationRestTemplateCreator integrationBasicRestTemplateCreator;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/test/outboundservices-resttemplatefactory.impex", "UTF-8");

		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE ExposedOAuthCredential; id[unique = true]; password",
				"; testOauthCredential ; " + TEST_SECRET,
				"INSERT_UPDATE BasicCredential; id[unique = true]; password",
				"; testBasicCredential ; " + TEST_SECRET
		);
	}

	@Test
	public void shouldCreateOAuth2RestTemplate()
	{
		final ConsumedDestinationModel scpi = destinationService.getDestinationById("scpi-oauth");

		final RestOperations restOperations = integrationRestTemplateFactory.create(scpi);

		assertThat(restOperations).isExactlyInstanceOf(OAuth2RestTemplate.class);

		final OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) restOperations;

		assertThat(restTemplate.getResource().getClientId()).isEqualTo("testOauthClient");
		assertThat(restTemplate.getResource().getClientSecret()).isEqualTo(TEST_SECRET);
		assertThat(restTemplate.getResource().getAccessTokenUri())
				.isEqualTo("https://some.url.com/oauth2/api/v1/token");

		final DefaultUriTemplateHandler uriTemplateHandler = (DefaultUriTemplateHandler) restTemplate.getUriTemplateHandler();
		assertThat(uriTemplateHandler.getBaseUrl()).isEqualTo(null);

		assertThat(restTemplate.getMessageConverters().get(0)).isExactlyInstanceOf(MappingJackson2HttpMessageConverter.class);
		assertThat(restTemplate.getInterceptors()).hasSize(0);
		assertThat(restTemplate.getErrorHandler()).isInstanceOf(OAuth2ErrorHandler.class);
	}

	@Test
	public void shouldCreateBasicRestTemplate()
	{
		final ConsumedDestinationModel platformBasic = destinationService.getDestinationById("platform-basic");

		final RestOperations restOperations = integrationRestTemplateFactory.create(platformBasic);

		assertThat(restOperations).isExactlyInstanceOf(RestTemplate.class);

		final RestTemplate restTemplate = (RestTemplate) restOperations;

		assertThat(restTemplate.getInterceptors()).hasSize(1);
		verifyBasicAuthorization(restTemplate);

		final DefaultUriTemplateHandler uriTemplateHandler = (DefaultUriTemplateHandler) restTemplate.getUriTemplateHandler();
		assertThat(uriTemplateHandler.getBaseUrl()).isEqualTo(null);

		assertThat(restTemplate.getMessageConverters().size()).isGreaterThan(1);
		assertThat(findHttpMessageConverter(restTemplate)).isPresent();
		assertThat(restTemplate.getErrorHandler()).isInstanceOf(DefaultResponseErrorHandler.class);
	}

	private Optional<HttpMessageConverter<?>> findHttpMessageConverter(final RestTemplate restTemplate)
	{
		return restTemplate.getMessageConverters().stream()
						   .filter(mc -> mc instanceof MappingJackson2HttpMessageConverter)
						   .findFirst();
	}

	@Test
	public void shouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> integrationRestTemplateFactory.create(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Consumed destination model cannot be null.");
	}

	@Test
	public void shouldThrowUnsupportedRestTemplateException_forOauth2RestTemplateCreator()
	{
		final ConsumedDestinationModel basic = destinationService.getDestinationById("platform-basic");
		assertThatThrownBy(() -> integrationOAuth2RestTemplateCreator.create(basic))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void shouldThrowUnsupportedRestTemplateException_forBasicRestTemplateCreator()
	{
		final ConsumedDestinationModel oauth = destinationService.getDestinationById("scpi-oauth");
		assertThatThrownBy(() -> integrationBasicRestTemplateCreator.create(oauth))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	private void verifyBasicAuthorization(final RestTemplate restTemplate)
	{
		final ClientHttpRequestInterceptor interceptor =
				restTemplate.getInterceptors().stream().filter(i -> i instanceof BasicAuthorizationInterceptor).findFirst().get();
		assertThat(interceptor).isExactlyInstanceOf(BasicAuthorizationInterceptor.class);
		assertThat((BasicAuthorizationInterceptor)interceptor)
				.extracting("username", "password").containsExactly("admin", TEST_SECRET);
	}
}
