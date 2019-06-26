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
package de.hybris.platform.outboundservices.decorator.impl;

import static de.hybris.platform.integrationservices.enums.IntegrationRequestStatus.ERROR;
import static de.hybris.platform.outboundservices.decorator.DecoratorContext.decoratorContextBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.outboundservices.monitoring.OutboundMonitoringException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.Map;

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

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOutboundMonitoringRequestDecoratorUnitTest
{
	private static final String INTEGRATION_KEY = "integrationKey";
	private static final String MY_INTEGRATION_OBJECT = "MyIntegrationObject";
	private static final String SAP_PASSPORT_HEADER = "SAP-PASSPORT";
	private static final String MY_SAP_PASSPORT = "MY-SAP-PASSPORT";
	private static final String DESTINATION_URL = "THE-DESTINATION-URL";
	private static final String MY_INTEGRATION_KEY = "MY-INTEGRATION-KEY";
	private static final String ERROR_MESSAGE = "my error message";
	private static final String OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME = "X-OutboundMonitoring-MessageId";

	@Mock
	private ModelService modelService;

	@InjectMocks
	private DefaultOutboundMonitoringRequestDecorator monitoringDecorator;

	@Mock
	private IntegrationObjectItemModel integrationObjectItemModel;
	@Mock
	private ItemModel itemModel;
	@Mock
	private OutboundRequestModel outboundRequestModel;
	@Mock
	private ConsumedDestinationModel consumedDestinationModel;
	@Mock
	private DecoratorExecution execution;
	@Mock
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> exceptionErrorParser;
	@Mock
	private MonitoredRequestErrorModel errorModel;
	@Captor
	private ArgumentCaptor<String> messageIdCaptor;

	private Map<String, Object> payload = Maps.newHashMap();
	private DecoratorContext decoratorContext;

	@Before
	public void setUp()
	{
		when(integrationObjectItemModel.getCode()).thenReturn("MyIOItemCode");
		when(itemModel.getItemtype()).thenReturn("MyItemtype");
		when(modelService.create(OutboundRequestModel.class)).thenReturn(outboundRequestModel);
		when(consumedDestinationModel.getUrl()).thenReturn(DESTINATION_URL);

		decoratorContext = decoratorContextBuilder().withDestinationModel(consumedDestinationModel)
													.withItemModel(itemModel)
													.withIntegrationObjectCode(MY_INTEGRATION_OBJECT)
													.withIntegrationObjectItemCode("MyIOItemCode")
													.build();

		when(errorModel.getMessage()).thenReturn(ERROR_MESSAGE);
		when(exceptionErrorParser.parseErrorFrom(any(), anyInt(), any())).thenReturn(errorModel);

		when(execution.createHttpEntity(any(), any(), any())).thenAnswer(arg -> {
			final HttpHeaders httpHeaders = arg.getArgumentAt(0, HttpHeaders.class);
			final Map<String, Object> payload = arg.getArgumentAt(1, Map.class);
			return new HttpEntity<>(payload, httpHeaders);
		});
	}

	@Test
	public void testDecorator()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(SAP_PASSPORT_HEADER, MY_SAP_PASSPORT);

		payload.put(INTEGRATION_KEY, MY_INTEGRATION_KEY);

		monitoringDecorator.decorate(httpHeaders, payload, decoratorContext, execution);

		verify(modelService).create(OutboundRequestModel.class);
		verify(modelService).save(outboundRequestModel);
		verify(outboundRequestModel).setDestination(DESTINATION_URL);
		verify(outboundRequestModel).setMessageId(messageIdCaptor.capture());
		verify(outboundRequestModel).setSapPassport(MY_SAP_PASSPORT);
		verify(outboundRequestModel).setIntegrationKey(MY_INTEGRATION_KEY);
		verify(outboundRequestModel).setStatus(ERROR);
		verify(outboundRequestModel, never()).setError(any());
		verify(outboundRequestModel).setType(MY_INTEGRATION_OBJECT);
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);

		assertThat(httpHeaders).contains(entry(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, Collections.singletonList(messageIdCaptor.getValue())));
	}

	@Test
	public void testDecorator_whenNoSapPassport()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		payload.put("integrationKey", "the key");
		assertThatThrownBy(() -> monitoringDecorator.decorate(httpHeaders, payload, decoratorContext, execution))
				.isInstanceOf(OutboundMonitoringException.class)
				.hasMessage("No SAP-PASSPORT header present in request.");

		verify(modelService).create(OutboundRequestModel.class);
		verify(modelService, times(2)).save(outboundRequestModel);
		verify(outboundRequestModel).setDestination(DESTINATION_URL);
		verify(outboundRequestModel).setMessageId(messageIdCaptor.capture());
		verify(outboundRequestModel).setSapPassport(null);
		verify(outboundRequestModel).setIntegrationKey("the key");
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(outboundRequestModel, times(2)).setStatus(ERROR);
		verify(outboundRequestModel).setType(MY_INTEGRATION_OBJECT);
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);

		assertThat(httpHeaders).contains(entry(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, Collections.singletonList(messageIdCaptor.getValue())));
	}

	@Test
	public void testDecorator_whenNoSapPassport_nullHeader()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(SAP_PASSPORT_HEADER, null);

		assertThatThrownBy(() -> monitoringDecorator.decorate(httpHeaders, payload, decoratorContext, execution))
				.isInstanceOf(OutboundMonitoringException.class)
				.hasMessage("No SAP-PASSPORT header present in request.");

		verify(modelService).create(OutboundRequestModel.class);
		verify(modelService, times(2)).save(outboundRequestModel);
		verify(outboundRequestModel).setDestination(DESTINATION_URL);
		verify(outboundRequestModel).setMessageId(messageIdCaptor.capture());
		verify(outboundRequestModel).setSapPassport(null);
		verify(outboundRequestModel).setIntegrationKey(null);
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(outboundRequestModel, times(2)).setStatus(ERROR);
		verify(outboundRequestModel).setType(MY_INTEGRATION_OBJECT);
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);

		assertThat(httpHeaders).contains(entry(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, Collections.singletonList(messageIdCaptor.getValue())));
	}

	@Test
	public void testDecorator_whenNoSapPassport_emptyHeader()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(SAP_PASSPORT_HEADER, "");

		assertThatThrownBy(() -> monitoringDecorator.decorate(httpHeaders, payload, decoratorContext, execution))
				.isInstanceOf(OutboundMonitoringException.class)
				.hasMessage("No SAP-PASSPORT header present in request.");

		verify(modelService).create(OutboundRequestModel.class);
		verify(modelService, times(2)).save(outboundRequestModel);
		verify(outboundRequestModel).setDestination(DESTINATION_URL);
		verify(outboundRequestModel).setMessageId(messageIdCaptor.capture());
		verify(outboundRequestModel).setSapPassport(null);
		verify(outboundRequestModel).setIntegrationKey(null);
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(outboundRequestModel, times(2)).setStatus(ERROR);
		verify(outboundRequestModel).setType(MY_INTEGRATION_OBJECT);
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);

		assertThat(httpHeaders).contains(entry(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, Collections.singletonList(messageIdCaptor.getValue())));
	}

	@Test
	public void testDecorator_whenNoIntegrationKey()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(SAP_PASSPORT_HEADER, MY_SAP_PASSPORT);

		monitoringDecorator.decorate(httpHeaders, payload, decoratorContext, execution);

		verify(modelService).create(OutboundRequestModel.class);
		verify(modelService).save(outboundRequestModel);
		verify(outboundRequestModel).setDestination(DESTINATION_URL);
		verify(outboundRequestModel).setMessageId(messageIdCaptor.capture());
		verify(outboundRequestModel).setSapPassport(MY_SAP_PASSPORT);
		verify(outboundRequestModel).setIntegrationKey(null);
		verify(outboundRequestModel, never()).setError(any());
		verify(outboundRequestModel).setStatus(ERROR);
		verify(outboundRequestModel).setType(MY_INTEGRATION_OBJECT);
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);

		assertThat(httpHeaders).contains(entry(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, Collections.singletonList(messageIdCaptor.getValue())));
	}

	@Test
	public void testDecorator_whenExceptionFromDecorators()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(SAP_PASSPORT_HEADER, MY_SAP_PASSPORT);
		payload.put(INTEGRATION_KEY, MY_INTEGRATION_KEY);

		doThrow(new RuntimeException("my exception")).when(execution).createHttpEntity(any(), any(), any());
		assertThatThrownBy(() -> monitoringDecorator.decorate(httpHeaders, payload, decoratorContext, execution))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("my exception");

		verify(modelService).create(OutboundRequestModel.class);
		verify(modelService, times(2)).save(outboundRequestModel);
		verify(outboundRequestModel).setDestination(DESTINATION_URL);
		verify(outboundRequestModel).setMessageId(messageIdCaptor.capture());
		verify(outboundRequestModel).setSapPassport(MY_SAP_PASSPORT);
		verify(outboundRequestModel).setIntegrationKey(MY_INTEGRATION_KEY);
		verify(outboundRequestModel, times(2)).setStatus(ERROR);
		verify(outboundRequestModel).setType(MY_INTEGRATION_OBJECT);
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);
		verify(exceptionErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, -1, "my exception");

		assertThat(httpHeaders).contains(entry(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, Collections.singletonList(messageIdCaptor.getValue())));
	}
}
