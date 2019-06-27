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
package de.hybris.platform.odata2services.odata.monitoring.impl;

import static de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntityBuilder.requestBatchEntity;
import static de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntityBuilder.responseChangeSetEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntity;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntity;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultInboundRequestServiceUnitTest
{
	private static final String INTEGRATION_MESSAGE_ID = "sillyRabbit";
	private static final String INTEGRATION_OBJECT_TYPE = "trixAreForKids";
	private static final InboundRequestMediaModel MEDIA_MODEL = media();

	private static InboundRequestMediaModel media()
	{
		return mock(InboundRequestMediaModel.class);
	}

	@Mock
	private ModelService modelService;
	@InjectMocks
	private DefaultInboundRequestService inboundRequestService;

	@Test
	public void testRegisterOneBatchWithOneSuccessfulChangeSet()
	{
		final RequestBatchEntity batch = requestBatchEntity()
				.withIntegrationObjectType(INTEGRATION_OBJECT_TYPE)
				.withMessageId(INTEGRATION_MESSAGE_ID)
				.build();
		final ResponseChangeSetEntity changeSet = responseChangeSetEntity()
				.withIntegrationKey("key")
				.withStatusCode(201)
				.build();

		inboundRequestService.register(asList(batch), asList(changeSet), asList(MEDIA_MODEL));

		final Collection<InboundRequestModel> requests = interceptPersistedInboundRequests();
		assertThat(requests).hasSize(1);
		assertThat(requests.iterator().next())
				.hasFieldOrPropertyWithValue("messageId", INTEGRATION_MESSAGE_ID)
				.hasFieldOrPropertyWithValue("type", INTEGRATION_OBJECT_TYPE)
				.hasFieldOrPropertyWithValue("integrationKey", "key")
				.hasFieldOrPropertyWithValue("status", IntegrationRequestStatus.SUCCESS)
				.hasFieldOrPropertyWithValue("payload", MEDIA_MODEL)
				.hasFieldOrPropertyWithValue("errors", null);
	}

	@Test
	public void testRegisterOneBatchWithOneErrorChangeSet()
	{
		final InboundRequestErrorModel error = mock(InboundRequestErrorModel.class);
		final RequestBatchEntity batch = requestBatchEntity().build();
		final ResponseChangeSetEntity changeSet = responseChangeSetEntity()
				.withStatusCode(400)
				.withRequestError(error)
				.build();

		inboundRequestService.register(asList(batch), asList(changeSet), asList(MEDIA_MODEL));

		final Collection<InboundRequestModel> requests = interceptPersistedInboundRequests();
		assertThat(requests).hasSize(1);
		final InboundRequestModel request = requests.iterator().next();
		assertThat(request)
				.hasFieldOrPropertyWithValue("status", IntegrationRequestStatus.ERROR);
		assertThat(request.getErrors()).containsExactly(error);
	}

	@Test
	public void testRegisterOneBatchWithMultipleChangeSets()
	{
		final RequestBatchEntity batch = requestBatchEntity().build();
		final ResponseChangeSetEntity[] changeSets = {
				responseChangeSetEntity().withStatusCode(200).build(),
				responseChangeSetEntity().withStatusCode(201).build(),
				responseChangeSetEntity().withStatusCode(202).build()};

		inboundRequestService.register(asList(batch), asList(changeSets), asList(MEDIA_MODEL));

		final Collection<InboundRequestModel> requests = interceptPersistedInboundRequests();
		assertThat(requests).hasSize(1);
	}

	@Test
	public void testRegisterWithoutPayloads()
	{
		final RequestBatchEntity batch = requestBatchEntity().build();
		final ResponseChangeSetEntity changeSets = responseChangeSetEntity().build();

		inboundRequestService.register(asList(batch), asList(changeSets), Collections.emptyList());

		final Collection<InboundRequestModel> requests = interceptPersistedInboundRequests();
		assertThat(requests).hasSize(1);
		assertThat(requests.iterator().next())
				.hasFieldOrPropertyWithValue("payload", null);
	}

	@Test
	public void testMultipleRequestsResultInOneErrorResponse()
	{
		final RequestBatchEntity[] batches = {
				requestBatchEntity().withMessageId("1").withNumberOfChangeSets(1).build(),
				requestBatchEntity().withMessageId("2").withNumberOfChangeSets(1).build(),
				requestBatchEntity().withMessageId("3").withNumberOfChangeSets(1).build()};
		final ResponseChangeSetEntity changeSet = responseChangeSetEntity().withStatusCode(400).build();
		final InboundRequestMediaModel[] payloads = {media(), media(), media()};

		inboundRequestService.register(asList(batches), asList(changeSet), asList(payloads));

		final Collection<InboundRequestModel> requests = interceptPersistedInboundRequests();
		assertThat(requests)
				.hasSize(1)
				.extracting("messageId", "status", "payload").containsExactly(
				tuple("1", IntegrationRequestStatus.ERROR, payloads[0]));
	}

	@Test
	public void testRegisterSeveralBatchesWithMultipleChangeSets()
	{
		final RequestBatchEntity[] batches = {
				requestBatchEntity().withMessageId("1").withNumberOfChangeSets(3).build(),
				requestBatchEntity().withMessageId("2").withNumberOfChangeSets(2).build(),
				requestBatchEntity().withMessageId("3").withNumberOfChangeSets(1).build()};
		final ResponseChangeSetEntity[] changeSets = {
				responseChangeSetEntity().withStatusCode(200).build(),
				responseChangeSetEntity().withStatusCode(200).build(),
				responseChangeSetEntity().withStatusCode(200).build(),
				responseChangeSetEntity().withStatusCode(500).build(),
				responseChangeSetEntity().withStatusCode(200).build(),
				responseChangeSetEntity().withStatusCode(200).build()};
		final InboundRequestMediaModel[] payloads = {media(), media(), media()};

		inboundRequestService.register(asList(batches), asList(changeSets), asList(payloads));

		final Collection<InboundRequestModel> requests = interceptPersistedInboundRequests();
		assertThat(requests)
				.hasSize(3)
				.extracting("messageId", "status", "payload").containsExactly(
						tuple("1", IntegrationRequestStatus.SUCCESS, payloads[0]),
						tuple("2", IntegrationRequestStatus.ERROR, payloads[1]),
						tuple("3", IntegrationRequestStatus.SUCCESS, payloads[2]));
	}

	@SuppressWarnings("unchecked")
	private Collection<InboundRequestModel> interceptPersistedInboundRequests()
	{
		final ArgumentCaptor<Collection> requestsCaptor = ArgumentCaptor.forClass(Collection.class);
		verify(modelService).saveAll(requestsCaptor.capture());
		return (Collection<InboundRequestModel>) requestsCaptor.getValue();
	}

	@SafeVarargs
	private static <T> List<T> asList(final T... elements)
	{
		return Arrays.asList(elements);
	}
}
