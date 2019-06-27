/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.kymaintegrationservices.services.impl;

import static de.hybris.platform.apiregistryservices.enums.DestinationChannel.KYMA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistrationService;
import de.hybris.platform.apiregistryservices.strategies.ApiRegistrationStrategy;
import de.hybris.platform.kymaintegrationservices.strategies.impl.KymaApiRegistrationStrategy;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;


@IntegrationTest
public class DefaultApiRegistrationServiceIT extends ServicelayerTransactionalTest
{
	private static final String URL = "https://localhost:8081/v1/metadata/services";
	private static final String FAKE_SYSTEM_ID = "fake-system-id";
	@Resource(name = "destinationService")
	private DestinationService<AbstractDestinationModel> destinationService;

	@Resource(name = "kymaDestinationRestTemplateWrapper")
	private RestTemplateWrapper restTemplate;

	private RestTemplateWrapper restTemplateSpy;

	@Resource
	private EventConfigurationDao eventConfigurationDao;
	@Resource(name = "kymaApiRegistrationStrategy")
	private KymaApiRegistrationStrategy apiRegistrationStrategy;

	private Map<DestinationChannel, ApiRegistrationStrategy> apiRegistrationStrategyMap;
	private DefaultApiRegistrationService apiRegistrationService;

	@Before
	public void setUp() throws Exception
	{
		restTemplateSpy = spy(restTemplate);
		doNothing().when(restTemplateSpy).updateCredentials(any());

		importCsv("/test/apiConfigurations.impex", "UTF-8");

		apiRegistrationStrategyMap = new HashMap();
		apiRegistrationStrategyMap.put(KYMA, apiRegistrationStrategy);

		apiRegistrationService = new DefaultApiRegistrationService();
		apiRegistrationService.setDestinationService(destinationService);

		apiRegistrationService.setApiRegistrationStrategyMap(apiRegistrationStrategyMap);
		apiRegistrationStrategy.setRestTemplate(restTemplateSpy);
	}

	@Test
	public void testRegisterWebservices() throws ApiRegistrationException
	{
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplateSpy.getUpdatedRestTemplate()).build();

		final List<ExposedDestinationModel> models = destinationService.getDestinationsByChannel(KYMA).stream()
			.filter(ExposedDestinationModel.class::isInstance).map(ExposedDestinationModel.class::cast)
			.collect(Collectors.toList());
		assertFalse(CollectionUtils.isEmpty(models));

		final ExposedDestinationModel destinationModel = models.get(0);
		destinationModel.setTargetId(null);
		destinationModel.getEndpoint().setDescription(null);

		mockServer.expect(times(1), requestTo(URL)).andExpect(method(HttpMethod.POST))
			.andExpect(clientHttpRequest -> {
				MockClientHttpRequest mockRequest = (MockClientHttpRequest) clientHttpRequest;
				assertFalse("Should be no null description in result ", mockRequest.getBodyAsString().contains("description"));})
			.andRespond(withSuccess());

		assertEquals(KYMA, destinationModel.getDestinationTarget().getDestinationChannel());

		apiRegistrationService.registerExposedDestination(destinationModel);
	}

	@Test
	public void testUpdateRegisteredWebservices() throws ApiRegistrationException
	{
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplateSpy.getUpdatedRestTemplate()).build();

		final List<ExposedDestinationModel> models = destinationService.getDestinationsByChannel(KYMA).stream()
				.filter(ExposedDestinationModel.class::isInstance).map(ExposedDestinationModel.class::cast)
				.collect(Collectors.toList());
		assertFalse(CollectionUtils.isEmpty(models));

		final ExposedDestinationModel destinationModel = models.get(0);
		destinationModel.setTargetId(FAKE_SYSTEM_ID);

		mockServer.expect(times(1), requestTo(URL + "/" + FAKE_SYSTEM_ID)).andExpect(method(HttpMethod.PUT))
				.andRespond(withNoContent());

		assertEquals(KYMA, destinationModel.getDestinationTarget().getDestinationChannel());

		apiRegistrationService.registerExposedDestination(destinationModel);
	}

	@Test
	public void testUnregisterWebservices() throws ApiRegistrationException
	{
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplateSpy.getUpdatedRestTemplate()).build();

		final List<ExposedDestinationModel> models = destinationService.getDestinationsByChannel(KYMA).stream()
				.filter(ExposedDestinationModel.class::isInstance).map(ExposedDestinationModel.class::cast)
				.collect(Collectors.toList());
		assertFalse(CollectionUtils.isEmpty(models));

		final ExposedDestinationModel destinationModel = models.get(0);
		destinationModel.setTargetId(FAKE_SYSTEM_ID);

		mockServer.expect(times(1), requestTo(URL + "/" + FAKE_SYSTEM_ID)).andExpect(method(HttpMethod.DELETE))
				.andRespond(withNoContent());

		assertEquals(KYMA, destinationModel.getDestinationTarget().getDestinationChannel());
		apiRegistrationService.unregisterExposedDestination(destinationModel);

		assertTrue(StringUtils.isEmpty(destinationModel.getTargetId()));
	}


}
