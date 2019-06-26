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
package de.hybris.platform.outboundservices.facade.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOutboundServiceFacadeUnitTest
{
	private static final String ENDPOINT_URL = "http://my.consumed.destination/some/path";

	@InjectMocks
	private DefaultOutboundServiceFacade outboundServiceFacade;

	@Mock
	private DestinationService destinationService;
	@Mock
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	@Mock
	private IntegrationObjectService integrationObjectService;
	@Mock
	private ClientHttpRequestInterceptor monitoringInterceptor;
	@Mock
	private OutboundRequestDecorator monitoringDecorator;
	@Mock
	private ConsumedDestinationModel consumedDestinationModel;
	@Mock
	private OutboundServicesConfiguration outboundServicesConfiguration;

	@Mock
	private OutboundRequestDecorator decorator1;
	@Mock
	private OutboundRequestDecorator decorator2;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private IntegrationObjectItemModel integrationObjectItemModel;
	@Mock
	private ItemModel itemModel;

	@Captor
	private ArgumentCaptor<DecoratorContext> contextCaptor;
	@Captor
	private ArgumentCaptor<Map<String, Object>> payloadCaptor;
	@Captor
	private ArgumentCaptor<HttpHeaders> httpHeadersCaptor;
	@Captor
	private ArgumentCaptor<List<ClientHttpRequestInterceptor>> interceptorsCaptor;

	@Before
	public void setUp()
	{
		this.outboundServiceFacade.setOutboundRequestDecorators(Arrays.asList(decorator1, decorator2));

		when(outboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);

		when(consumedDestinationModel.getUrl()).thenReturn(ENDPOINT_URL);
		when(destinationService.getDestinationById("destination")).thenReturn(consumedDestinationModel);
		when(integrationRestTemplateFactory.create(consumedDestinationModel)).thenReturn(restTemplate);

		when(integrationObjectItemModel.getCode()).thenReturn("TheItemCode");
		when(integrationObjectService.findIntegrationObjectItemByTypeCode("integrationObjectCode", "MyType")).thenReturn(integrationObjectItemModel);

		when(itemModel.getItemtype()).thenReturn("MyType");
	}

	@Test
	public void testSend()
	{
		final CountDownLatch cd = new CountDownLatch(1);

		outboundServiceFacade.send(itemModel,"integrationObjectCode", "destination").subscribe(onNext -> cd.countDown());

		assertThat(cd.getCount()).isZero();

		verify(restTemplate).postForEntity(eq(ENDPOINT_URL), any(HttpEntity.class), eq(Map.class));
		verify(restTemplate).setInterceptors(interceptorsCaptor.capture());
		verify(monitoringDecorator).decorate(httpHeadersCaptor.capture(), payloadCaptor.capture(),  // first decorator in list is the monitoring one
				contextCaptor.capture(), any(DecoratorExecution.class));

		assertThat(httpHeadersCaptor.getValue()).isEmpty();
		assertThat(payloadCaptor.getValue()).isEmpty();
		assertThat(contextCaptor.getValue()).hasFieldOrPropertyWithValue("integrationObjectCode", "integrationObjectCode")
											.hasFieldOrPropertyWithValue("integrationObjectItemCode", "TheItemCode")
											.hasFieldOrPropertyWithValue("itemModel", itemModel)
											.hasFieldOrPropertyWithValue("destinationModel", consumedDestinationModel);

		assertThat(interceptorsCaptor.getValue()).contains(monitoringInterceptor);
	}

	@Test
	public void testSendMonitoringDisabled()
	{
		when(outboundServicesConfiguration.isMonitoringEnabled()).thenReturn(false);

		outboundServiceFacade.send(itemModel,"integrationObjectCode", "destination");

		verify(restTemplate, never()).setInterceptors(any()); // meaning no monitoringInterceptor was added.
	}

	@Test
	public void testSendWithNoIntegrationObjectItemCode_modelNotFound()
	{
		doThrow(ModelNotFoundException.class).when(integrationObjectService).findIntegrationObjectItemByTypeCode("integrationObjectCode", "MyType");

		final CountDownLatch cd = new CountDownLatch(1);

		outboundServiceFacade.send(itemModel,"integrationObjectCode", "destination").subscribe(onNext -> cd.countDown());

		assertThat(cd.getCount()).isZero();
		verify(restTemplate).postForEntity(eq(ENDPOINT_URL), any(HttpEntity.class), eq(Map.class));
		verify(restTemplate).setInterceptors(interceptorsCaptor.capture());
		verify(monitoringDecorator).decorate(httpHeadersCaptor.capture(), payloadCaptor.capture(),  // first decorator in list is the monitoring one
				contextCaptor.capture(), any(DecoratorExecution.class));

		assertThat(httpHeadersCaptor.getValue()).isEmpty();
		assertThat(payloadCaptor.getValue()).isEmpty();
		assertThat(contextCaptor.getValue()).hasFieldOrPropertyWithValue("integrationObjectCode", "integrationObjectCode")
											.hasFieldOrPropertyWithValue("integrationObjectItemCode", null)
											.hasFieldOrPropertyWithValue("itemModel", itemModel)
											.hasFieldOrPropertyWithValue("destinationModel", consumedDestinationModel);

		verify(restTemplate).setInterceptors(interceptorsCaptor.capture());
	}

	@Test
	public void testSendWithNoIntegrationObjectItemCode_ambiguousIdentifier()
	{
		doThrow(AmbiguousIdentifierException.class).when(integrationObjectService).findIntegrationObjectItemByTypeCode("integrationObjectCode", "MyType");

		final CountDownLatch cd = new CountDownLatch(1);

		outboundServiceFacade.send(itemModel,"integrationObjectCode", "destination").subscribe(onNext -> cd.countDown());

		assertThat(cd.getCount()).isZero();
		verify(restTemplate).postForEntity(eq(ENDPOINT_URL), any(HttpEntity.class), eq(Map.class));
		verify(restTemplate).setInterceptors(interceptorsCaptor.capture());
		verify(monitoringDecorator).decorate(httpHeadersCaptor.capture(), payloadCaptor.capture(),  // first decorator in list is the monitoring one
				contextCaptor.capture(), any(DecoratorExecution.class));

		assertThat(httpHeadersCaptor.getValue()).isEmpty();
		assertThat(payloadCaptor.getValue()).isEmpty();
		assertThat(contextCaptor.getValue()).hasFieldOrPropertyWithValue("integrationObjectCode", "integrationObjectCode")
											.hasFieldOrPropertyWithValue("integrationObjectItemCode", null)
											.hasFieldOrPropertyWithValue("itemModel", itemModel)
											.hasFieldOrPropertyWithValue("destinationModel", consumedDestinationModel);

		verify(restTemplate).setInterceptors(interceptorsCaptor.capture());
	}

	@Test
	public void testSendInvalidItemModel()
	{
		assertThatThrownBy(() -> outboundServiceFacade.send(null, "integrationObjectCode", "destination"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("itemModel cannot be null");
	}

	@Test
	public void testSendInvalidIntegrationObjectCode()
	{
		assertThatThrownBy(() -> outboundServiceFacade.send(mock(ItemModel.class), "", "destination"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("integrationObjectCode cannot be null or empty");
	}

	@Test
	public void testSendInvalidDestination()
	{
		assertThatThrownBy(() -> outboundServiceFacade.send(mock(ItemModel.class), "integrationObjectCode", ""))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("destination cannot be null or empty");
	}

	@Test
	public void testSendNonExistingDestination()
	{
		when(destinationService.getDestinationById(any())).thenReturn(null);

		assertThatThrownBy(() -> outboundServiceFacade.send(mock(ItemModel.class), "integrationObjectCode", "someDestination"))
				.isInstanceOf(ModelNotFoundException.class)
				.hasMessage("Provided destination was not found.");
	}
}