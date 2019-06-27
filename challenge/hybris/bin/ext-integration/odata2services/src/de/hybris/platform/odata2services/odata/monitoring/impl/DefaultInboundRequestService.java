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

import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus;
import de.hybris.platform.odata2services.odata.monitoring.InboundRequestService;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntity;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntity;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * The default implementation of the {@link InboundRequestService}
 */
public class DefaultInboundRequestService implements InboundRequestService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInboundRequestService.class);

	private ModelService modelService;

	@Override
	public void register(final List<RequestBatchEntity> requests, final List<ResponseChangeSetEntity> responses, final List<InboundRequestMediaModel> medias)
	{
		final Collection<InboundRequestModel> inboundRequests = new LinkedList<>();
		final InboundRequestPartsCoordinator iterator = new InboundRequestPartsCoordinator(requests, responses, medias);
		while (iterator.hasNext())
		{
			iterator.next();
			final InboundRequestModel inboundRequest = createInboundRequest(
					iterator.getBatch(), iterator.getChangeSet(), iterator.getMedia());
			inboundRequests.add(inboundRequest);
		}
		getModelService().saveAll(inboundRequests);
	}

	protected InboundRequestModel createInboundRequest(
			final RequestBatchEntity batch,
			final ResponseChangeSetEntity changeSet,
			final InboundRequestMediaModel payload)
	{
		final InboundRequestModel inboundRequestModel = new InboundRequestModel();
		inboundRequestModel.setMessageId(batch.getMessageId());
		inboundRequestModel.setType(batch.getIntegrationObjectType());
		if (changeSet != null)
		{
			inboundRequestModel.setIntegrationKey(changeSet.getIntegrationKey());
			inboundRequestModel.setStatus(changeSet.isSuccessful() ? IntegrationRequestStatus.SUCCESS : IntegrationRequestStatus.ERROR);
			changeSet.getRequestError().ifPresent(err -> inboundRequestModel.setErrors(Collections.singleton(err)));
		}
		inboundRequestModel.setPayload(payload);
		return inboundRequestModel;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Receives collections of the parts, from which an {@code InboundRequest} is created, and coordinates access to the collections
	 * in a way that after each iteration the state contains parts (batch entry, change set entry and media) related to exactly
	 * same {@code InboundRequest}.
	 */
	protected static class InboundRequestPartsCoordinator
	{
		private final Iterator<RequestBatchEntity> batches;
		private final Iterator<ResponseChangeSetEntity> changeSets;
		private final Iterator<InboundRequestMediaModel> payloads;
		private RequestBatchEntity batch;
		private ResponseChangeSetEntity changeSet;
		private InboundRequestMediaModel media;

		/**
		 * Instantiates this coordinator.
		 * @param requests all {@code RequestBatchEntity}s, for which {@code InboundRequest}s need to be created. They represent
		 * batches received in the original request.
		 * @param responses all {@code ResponseChangeSetEntity}s related to the batch entities. There is no one-to-one relation
		 * between batch extracted from the request and change sets extracted from the response. In case of success there will be
		 * a change set for every change set received in the request, e.g. if the original request contain a single batch with two
		 * change sets, then {@code requests} will contain only one {@code RequestBatchEntity} for the batch but {@code responses}
		 * will contain two {@code ResponseChangeSetEntity} for the change sets. In case of an error, there will be only one
		 * {@code ResponseChangeSetEntity} for the corresponding batch regardless of how many changes were in the batch received
		 * in the request. And finally, for a global error, e.g. limit of batches per request is exceeded, there will be only one
		 * {@code ResponseChangeSetEntity} in the {@code responses} regardless of how many batches were present in the request.
		 * @param medias bodies of the batches present in the request persisted as medias. Normally it's expected to have number of
		 * {@code medias} equal to number of {@code requests}.
		 */
		protected InboundRequestPartsCoordinator(
				final Collection<RequestBatchEntity> requests,
				final Collection<ResponseChangeSetEntity> responses,
				final Collection<InboundRequestMediaModel> medias)
		{
			batches = requests.iterator();
			changeSets = responses.iterator();
			payloads = medias.iterator();
		}

		/**
		 * Determines whether there are more batches not yet iterated.
		 * @return {@code true}, if there is at least one more not iterated batch; {@code false}, otherwise.
		 * @see #next()
		 */
		protected boolean hasNext()
		{
			return batches.hasNext() && changeSets.hasNext();
		}

		/**
		 * Advances to the next record by iterating across all collections. Since {@code InboundRequest}s correspond to
		 * {@link RequestBatchEntity}, this method uses the collection of {@code RequestBatchEntity} as the leader. It iterates
		 * that collection and then advances to the {@code ResponseChangeSetEntity} and {@code InboundRequestMediaModel}
		 * corresponding to the batch entity in other collections.
		 */
		protected void next()
		{
			batch = batches.next();
			changeSet = rollToLastChangeSetInBatch();
			media = getNextOrNull(payloads);
		}

		/**
		 * The number of the change sets returned from the parser is not consistent. In case of success, there is a
		 * {@link ResponseChangeSetEntity} created for every change set received with the request. In case of the error, however,
		 * it's always only one {@code ResponseChangeSetEntity} regardless of how many change sets were submitted in the batch in
		 * the request body. That is because if one change set fails, the whole batch is rolled back and therefore a status for
		 * the whole batch is returned rather then for each of its change sets.
		 * @return last change set for the current {@link RequestBatchEntity}.
		 * <p/>Note: this method relies on the {@code RequestBatchEntity} iterated first before this method is called, so that
		 * the current batch record would be correctly set. Implementations ensure this order in the {@code next()} method or, if
		 * the order has changed, this method also needs to be overridden to account for it.
		 * @see #next()
		 */
		protected ResponseChangeSetEntity rollToLastChangeSetInBatch()
		{
			ResponseChangeSetEntity entity = getNextOrNull(changeSets);
			if (entity != null && entity.isSuccessful())
			{
				for (int i=1; i < batch.getNumberOfChangeSets(); i++)
				{
					entity = getNextOrNull(changeSets);
				}
			}
			return entity;
		}

		private static <T> T getNextOrNull(final Iterator<T> it)
		{
			try
			{
				return it.next();
			}
			catch (final NoSuchElementException e)
			{
				LOGGER.trace(e.getMessage(), e);
				return null;
			}
		}

		protected RequestBatchEntity getBatch()
		{
			return batch;
		}

		protected ResponseChangeSetEntity getChangeSet()
		{
			return changeSet;
		}

		protected InboundRequestMediaModel getMedia()
		{
			return media;
		}
	}
}
