/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.recommendation.services;

import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.dao.InteractionContext;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoClickthroughModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * This service is used for tracking, persisting and sending the successfully converted recommendation scenario.<br>
 * These interactions are clickthrough.
 */
public class InteractionService
{
	private static final Logger LOG = LoggerFactory.getLogger(InteractionService.class);

	protected static final int MAX_FAILURE = 3;

	protected String interactionType;
	protected ModelService modelService;
	protected ODataService oDataService;
	protected int readBatchSize;
	protected RecommendationBufferService recommendationBufferService;
	protected UserContextService userContextService;	

	/**
	 * Send a single clickthrough entry via OData service
	 *
	 * @param model
	 *           {@link SAPRecoClickthroughModel}
	 * @return true if posting was a success, false otherwise.
	 */
	public boolean postInteraction(final SAPRecoClickthroughModel model)
	{
		try
		{
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST",
					this.oDataService.createURL("Interactions"));
			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", MediaType.APPLICATION_JSON);

			final Map<String, Object> interaction = new HashMap<>();
			interaction.put("ScenarioId", model.getScenarioId());
			interaction.put("UserId", model.getUserId());
			interaction.put("UserType", model.getUserType());
			interaction.put("InteractionType", this.interactionType);
			interaction.put("TimeStamp", model.getTimeStamp());
			interaction.put("SourceObjectId", model.getSourceObjectId());

			final Map<String, Object> interactionItem = new HashMap<>();
			interactionItem.put("ItemType", model.getProductType());
			interactionItem.put("ItemId", model.getProductId());
			interaction.put("InteractionItems", interactionItem);

			final byte[] payload = this.oDataService.convertMapToJSONPayload("Interactions", interaction);
			request.setPayload(payload);

			this.oDataService.executeWithRetry(request);
		}
		catch (final IOException e)
		{
			LOG.error(
					"Error posting interaction: ScenarioId {} UserId {}, UserType {}, SourceObjectID {}, ProductID {}, ProductType {}",
					model.getScenarioId(), model.getUserId(), model.getUserType(), model.getSourceObjectId(), model.getProductId(),
					model.getProductType(), e);
			return false;
		}
		return true;
	}

	/**
	 * Save a clickthrough to database
	 *
	 * @param interactionContext
	 *           {@link InteractionContext}
	 */
	public void saveClickthrough(final InteractionContext interactionContext)
	{
		final SAPRecoClickthroughModel clickthroughModel = this.modelService.create(SAPRecoClickthroughModel.class);

		clickthroughModel.setScenarioId(interactionContext.getScenarioId());
		clickthroughModel.setUserId(this.userContextService.getUserId());
		clickthroughModel.setUserType(this.userContextService.getUserOrigin());
		clickthroughModel.setSourceObjectId(interactionContext.getSourceObjectId());
		clickthroughModel.setProductId(interactionContext.getProductId());
		clickthroughModel.setProductType(interactionContext.getProductType());
		clickthroughModel.setTimeStamp(new Date());

		this.modelService.save(clickthroughModel);
	}

	/**
	 * Send all clickthrough records via OData service
	 */
	public void sendInteractions()
	{
		int clickthroughListSize = readBatchSize;
		int successCounter = 0;
		int failureCounter = 0;
		long readTotalTime = 0;
		long sendTotalTime = 0;
		long deleteTotalTime = 0;

		while (clickthroughListSize == readBatchSize && failureCounter <= MAX_FAILURE)
		{
			final long readStartTime = System.currentTimeMillis();
			final List<SAPRecoClickthroughModel> clickthroughs = recommendationBufferService.getClickthroughs(readBatchSize);
			readTotalTime += System.currentTimeMillis() - readStartTime;

			clickthroughListSize = clickthroughs.size();
			for (int i = 0; i < clickthroughListSize && failureCounter <= MAX_FAILURE; i++)
			{
				final SAPRecoClickthroughModel clickthrough = clickthroughs.get(i);
				final long sendStartTime = System.currentTimeMillis();
				final boolean iterationSuccess = postInteraction(clickthrough);
				sendTotalTime += System.currentTimeMillis() - sendStartTime;

				if (iterationSuccess)
				{
					successCounter++;
					final long deleteStartTime = System.currentTimeMillis();
					modelService.remove(clickthrough);
					deleteTotalTime += System.currentTimeMillis() - deleteStartTime;
				}
				else
				{
					failureCounter++;
				}
			}
		}
		LOG.info("Send clickthroughs: Successful={}, Failed={}, RetrieveTime={}ms, SendTime={}ms, DeleteTime={}ms", successCounter,
				failureCounter, readTotalTime, sendTotalTime, deleteTotalTime);
	}

	@Required
	public void setInteractionType(final String interactionType)
	{
		LOG.debug("interactionType={}", interactionType);
		this.interactionType = interactionType;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	@Required
	public void setReadBatchSize(int readBatchSize)
	{
		LOG.debug("readBatchSize={}", readBatchSize);
		this.readBatchSize = readBatchSize;
	}

	@Required
	public void setRecommendationBufferService(RecommendationBufferService recommendationBufferService)
	{
		this.recommendationBufferService = recommendationBufferService;
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}	
	
}
