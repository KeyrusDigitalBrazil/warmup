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
package de.hybris.platform.odata2webservices.odata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.inboundservices.config.InboundServicesConfiguration;
import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;
import de.hybris.platform.integrationservices.service.MediaPersistenceService;
import de.hybris.platform.odata2services.odata.monitoring.InboundRequestService;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntity;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntityExtractor;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntity;
import de.hybris.platform.odata2services.odata.monitoring.ResponseEntityExtractor;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ODataFacadeMonitoringPersistenceProxyUnitTest
{
	private static final ODataContext ODATA_CONTEXT = oDataContext();
	private static final ODataResponse SCHEMA_RESPONSE = mock(ODataResponse.class);
	private static final ODataResponse GET_RESPONSE = mock(ODataResponse.class);
	private static final ODataResponse POST_RESPONSE = mock(ODataResponse.class);
	private static final String REQUEST_PARAM = "~odataRequest";
	private static final String MESSAGE_ID = "MSG-1";
	private static final String OBJECT_TYPE = "Integration";
	@Mock
	private MediaPersistenceService mediaPersistenceService;
	@Mock
	private InboundRequestService inboundRequestService;
	@Mock
	private ODataFacade realFacade;
	@Mock
	private RequestBatchEntityExtractor requestEntityExtractor;
	@Mock
	private ResponseEntityExtractor responseEntityExtractor;
	@Mock
	private InboundServicesConfiguration inboundServicesConfiguration;
	@InjectMocks
	private ODataFacadeMonitoringPersistenceProxy facadeProxy;

	@Captor
	private ArgumentCaptor<List<InputStream>> payloadsCaptor;

	private static ODataContext oDataContext()
	{
		final ODataContext context = mock(ODataContext.class);
		try
		{
			final ODataRequest request = mock(ODataRequest.class);
			final PathInfo pathInfo = mock(PathInfo.class);

			doReturn(request).when(context).getParameter(REQUEST_PARAM);
			doReturn(pathInfo).when(context).getPathInfo();
			doReturn(MESSAGE_ID).when(context).getRequestHeader("messageId");
			doReturn(OBJECT_TYPE).when(context).getParameter("service");
		}
		catch (final ODataException e)
		{
			e.printStackTrace();
		}
		return context;
	}

	@Before
	public void setUp()
	{
		doReturn(true).when(inboundServicesConfiguration).isPayloadRetentionForErrorEnabled();
		doReturn(true).when(inboundServicesConfiguration).isPayloadRetentionForSuccessEnabled();
		doReturn(true).when(inboundServicesConfiguration).isMonitoringEnabled();
		doReturn(SCHEMA_RESPONSE).when(realFacade).handleGetSchema(ODATA_CONTEXT);
		doReturn(GET_RESPONSE).when(realFacade).handleGetEntity(ODATA_CONTEXT);
		doReturn(POST_RESPONSE).when(realFacade).handlePost(ODATA_CONTEXT);
	}

	@Test
	public void testGetSchemaDelegatesToFacade()
	{
		final ODataResponse response = facadeProxy.handleGetSchema(ODATA_CONTEXT);

		assertThat(response).isSameAs(SCHEMA_RESPONSE);
	}

	@Test
	public void testGetEntityDelegatesToFacade()
	{
		final ODataResponse response = facadeProxy.handleGetEntity(ODATA_CONTEXT);

		assertThat(response).isSameAs(GET_RESPONSE);
	}

	@Test
	public void testPostDelegatesToFacade()
	{

		final ODataResponse response = facadeProxy.handlePost(ODATA_CONTEXT);

		assertThat(response).isSameAs(POST_RESPONSE);
	}

	@Test
	public void testMonitoringPersistenceTurnedOn()
	{
		final List<RequestBatchEntity> requestEntities = Collections.emptyList();
		final List<ResponseChangeSetEntity> responseEntities = Collections.emptyList();
		final List<InboundRequestMediaModel> medias = Collections.emptyList();
		when(requestEntityExtractor.extractFrom(ODATA_CONTEXT)).thenReturn(requestEntities);
		when(responseEntityExtractor.extractFrom(any(ODataResponse.class))).thenReturn(responseEntities);
		doReturn(medias).when(mediaPersistenceService).persistMedias(anyListOf(InputStream.class), any());

		when(inboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);

		facadeProxy.handlePost(ODATA_CONTEXT);

		verify(inboundRequestService).register(requestEntities, responseEntities, medias);
	}

	@Test
	public void testMonitoringPersistenceTurnedOnRetentionForSuccessOff()
	{
		final RequestBatchEntity requestBatchEntity = mock(RequestBatchEntity.class);
		when(requestBatchEntity.getContent()).thenReturn(mock(InputStream.class));
		final ResponseChangeSetEntity responseChangeSetEntity = mock(ResponseChangeSetEntity.class);
		when(responseChangeSetEntity.isSuccessful()).thenReturn(true);

		final List<RequestBatchEntity> requestEntities = Collections.singletonList(requestBatchEntity);
		final List<ResponseChangeSetEntity> responseEntities = Collections.singletonList(responseChangeSetEntity);

		when(requestEntityExtractor.extractFrom(ODATA_CONTEXT)).thenReturn(requestEntities);
		when(responseEntityExtractor.extractFrom(any(ODataResponse.class))).thenReturn(responseEntities);
		when(inboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);
		when(inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).thenReturn(false);

		facadeProxy.handlePost(ODATA_CONTEXT);
		verify(mediaPersistenceService).persistMedias(payloadsCaptor.capture(), any());

		assertThat(payloadsCaptor.getValue()).isNotEmpty().containsNull();
		verify(inboundRequestService).register(eq(requestEntities), eq(responseEntities), any());
	}

	@Test
	public void testMonitoringPersistenceTurnedOnRetentionForErrorOff()
	{
		final RequestBatchEntity requestBatchEntity = mock(RequestBatchEntity.class);
		when(requestBatchEntity.getContent()).thenReturn(mock(InputStream.class));
		final ResponseChangeSetEntity responseChangeSetEntity = mock(ResponseChangeSetEntity.class);
		when(responseChangeSetEntity.isSuccessful()).thenReturn(false);

		final List<RequestBatchEntity> requestEntities = Collections.singletonList(requestBatchEntity);
		final List<ResponseChangeSetEntity> responseEntities = Collections.singletonList(responseChangeSetEntity);

		when(requestEntityExtractor.extractFrom(ODATA_CONTEXT)).thenReturn(requestEntities);
		when(responseEntityExtractor.extractFrom(any(ODataResponse.class))).thenReturn(responseEntities);
		when(inboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);
		when(inboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).thenReturn(false);

		facadeProxy.handlePost(ODATA_CONTEXT);
		verify(mediaPersistenceService).persistMedias(payloadsCaptor.capture(), any());

		assertThat(payloadsCaptor.getValue()).isNotEmpty().containsNull();
		verify(inboundRequestService).register(eq(requestEntities), eq(responseEntities), any());
	}

	@Test
	public void testHasErrorsMonitoringPersistenceTurnedOnRetentionForSuccessOff_havingErrors()
	{
		final InputStream payload1 = mock(InputStream.class);
		final InputStream payload2 = mock(InputStream.class);
		final RequestBatchEntity requestBatchEntity1 = mock(RequestBatchEntity.class);
		when(requestBatchEntity1.getContent()).thenReturn(payload1);
		final ResponseChangeSetEntity responseChangeSetEntity1 = mock(ResponseChangeSetEntity.class);
		when(responseChangeSetEntity1.isSuccessful()).thenReturn(true);
		final RequestBatchEntity requestBatchEntity2 = mock(RequestBatchEntity.class);
		when(requestBatchEntity2.getContent()).thenReturn(payload2);
		final ResponseChangeSetEntity responseChangeSetEntity2 = mock(ResponseChangeSetEntity.class);
		when(responseChangeSetEntity2.isSuccessful()).thenReturn(false);

		final List<RequestBatchEntity> requestEntities = Arrays.asList(requestBatchEntity1, requestBatchEntity2);
		final List<ResponseChangeSetEntity> responseEntities = Arrays.asList(responseChangeSetEntity1, responseChangeSetEntity2);

		when(requestEntityExtractor.extractFrom(ODATA_CONTEXT)).thenReturn(requestEntities);
		when(responseEntityExtractor.extractFrom(any(ODataResponse.class))).thenReturn(responseEntities);
		when(inboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);
		when(inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).thenReturn(false);

		facadeProxy.handlePost(ODATA_CONTEXT);
		verify(mediaPersistenceService).persistMedias(payloadsCaptor.capture(), any());

		assertThat(payloadsCaptor.getValue()).isNotEmpty().containsNull().contains(payload2);

		verify(inboundRequestService).register(eq(requestEntities), eq(responseEntities), any());
	}

	@Test
	public void testMonitoringPersistenceTurnedOnRetentionForErrorOff_havingSuccess()
	{
		final InputStream payload1 = mock(InputStream.class);
		final InputStream payload2 = mock(InputStream.class);
		final RequestBatchEntity requestBatchEntity1 = mock(RequestBatchEntity.class);
		when(requestBatchEntity1.getContent()).thenReturn(payload1);
		final ResponseChangeSetEntity responseChangeSetEntity1 = mock(ResponseChangeSetEntity.class);
		when(responseChangeSetEntity1.isSuccessful()).thenReturn(true);
		final RequestBatchEntity requestBatchEntity2 = mock(RequestBatchEntity.class);
		when(requestBatchEntity2.getContent()).thenReturn(payload2);
		final ResponseChangeSetEntity responseChangeSetEntity2 = mock(ResponseChangeSetEntity.class);
		when(responseChangeSetEntity2.isSuccessful()).thenReturn(false);

		final List<RequestBatchEntity> requestEntities = Arrays.asList(requestBatchEntity1, requestBatchEntity2);
		final List<ResponseChangeSetEntity> responseEntities = Arrays.asList(responseChangeSetEntity1, responseChangeSetEntity2);

		when(requestEntityExtractor.extractFrom(ODATA_CONTEXT)).thenReturn(requestEntities);
		when(responseEntityExtractor.extractFrom(any(ODataResponse.class))).thenReturn(responseEntities);
		when(inboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);
		when(inboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).thenReturn(false);

		facadeProxy.handlePost(ODATA_CONTEXT);
		verify(mediaPersistenceService).persistMedias(payloadsCaptor.capture(), any());

		assertThat(payloadsCaptor.getValue()).isNotEmpty().contains(payload1).containsNull();

		verify(inboundRequestService).register(eq(requestEntities), eq(responseEntities), any());
	}

	@Test
	public void testMonitoringPersistenceIsTurnedOff()
	{
		when(inboundServicesConfiguration.isMonitoringEnabled()).thenReturn(false);

		facadeProxy.handlePost(ODATA_CONTEXT);

		verifyZeroInteractions(mediaPersistenceService, inboundRequestService, requestEntityExtractor, responseEntityExtractor);
	}
}
