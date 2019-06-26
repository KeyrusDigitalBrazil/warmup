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

import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.ImpressionContext;
import com.hybris.ymkt.recommendation.utils.ImpressionCounters;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionAggrModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * This service is responsible to record/track, aggregate and send recommendations seen/printed to the users.
 */
public class ImpressionService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImpressionService.class);

	protected static final int MAX_FAILURE = 3;

	protected long aggregationTimeWindow;
	protected ModelService modelService;
	protected ODataService oDataService;
	protected int readBatchSize;
	protected RecommendationBufferService recommendationBufferService;
	protected int sendBatchSize;

	/**
	 * Aggregate individual impressions, save the aggregated impressions and delete the individual impressions.
	 * Exceptions must be handles by the caller.
	 */
	public void aggregateImpressions()
	{
		int impressionsListSize = this.readBatchSize;
		int impressionsTotal = 0;
		int aggregatedImpressionsTotal = 0;
		long retrieveTotalTime = 0;
		long aggregateTotalTime = 0;
		long saveTotalTime = 0;
		long deleteTotalTime = 0;

		while (impressionsListSize == this.readBatchSize)
		{
			final long retrieveStartTime = System.currentTimeMillis();
			final List<SAPRecoImpressionModel> impressionsList = recommendationBufferService.getImpressions(this.readBatchSize);
			retrieveTotalTime += System.currentTimeMillis() - retrieveStartTime;
			impressionsListSize = impressionsList.size();
			impressionsTotal += impressionsList.size();

			final long aggregateStartTime = System.currentTimeMillis();
			final List<SAPRecoImpressionAggrModel> aggregatedImpressionsList = aggregateImpressionsByScenario(impressionsList);
			aggregateTotalTime += System.currentTimeMillis() - aggregateStartTime;
			aggregatedImpressionsTotal += aggregatedImpressionsList.size();

			final long saveStartTime = System.currentTimeMillis();
			modelService.saveAll(aggregatedImpressionsList);
			saveTotalTime += System.currentTimeMillis() - saveStartTime;

			final long deleteStartTime = System.currentTimeMillis();
			modelService.removeAll(impressionsList);
			deleteTotalTime += System.currentTimeMillis() - deleteStartTime;
		}

		LOGGER.info(
				"Aggregate impressions: Impressions={}, AggregatedImpressions={}, RetrieveTime={}ms, AggregateTime={}ms, SaveTime={}ms, DeleteTime={}ms",
				impressionsTotal, aggregatedImpressionsTotal, retrieveTotalTime, aggregateTotalTime, saveTotalTime, deleteTotalTime);
	}

	/**
	 * Aggregates impressions by scenario and aggregationTimeStamp<br>
	 *
	 * Aggregation timestamp is the midpoint of the time window based on the current time<br>
	 * e.g.<br>
	 * Current time = 1:04:32.512<br>
	 * Time window = 5 minutes<br>
	 * AggregatedTimeStamp = 1:02:30 (midpoint between 1:00 and 1:05)
	 *
	 * @param impressionsList
	 *           {@link List} of {@link SAPRecoImpressionModel}
	 * @return aggregatedImpressionsList
	 */
	protected List<SAPRecoImpressionAggrModel> aggregateImpressionsByScenario(final List<SAPRecoImpressionModel> impressionsList)
	{
		final Map<Map<String, Long>, ImpressionCounters> impressionMap = new HashMap<>();
		final List<SAPRecoImpressionAggrModel> aggregatedImpressionsList = new ArrayList<>();

		//go through the impressions list and aggregate into map
		for (final SAPRecoImpressionModel listItem : impressionsList)
		{
			final long aggregationTimeStamp = calcMidTimeStamp(listItem.getTimeStamp().getTime());
			final Map<String, Long> impressionKey = Collections.singletonMap(listItem.getScenarioId(), aggregationTimeStamp);

			final ImpressionCounters impressionCounter = impressionMap.computeIfAbsent(impressionKey, i -> new ImpressionCounters());
			impressionCounter.addToImpressionCount(listItem.getImpressionCount());
			impressionCounter.addToItemCount(listItem.getItemCount());
		}

		//convert map to list of SAPRecoImpressionAggrModel
		for (final Map.Entry<Map<String, Long>, ImpressionCounters> entry : impressionMap.entrySet())
		{
			final SAPRecoImpressionAggrModel aggrImpression = new SAPRecoImpressionAggrModel();

			aggrImpression.setScenarioId(entry.getKey().keySet().iterator().next());
			aggrImpression.setTimeStamp(new Date(entry.getKey().values().iterator().next().longValue()));
			aggrImpression.setImpressionCount(entry.getValue().getImpressionCount());
			aggrImpression.setItemCount(entry.getValue().getItemCount());
			aggregatedImpressionsList.add(aggrImpression);

			LOGGER.debug("Aggregated impression: scenarioId={}, timeStamp={}, impressionCount={}, itemCount={}",
					aggrImpression.getScenarioId(), aggrImpression.getTimeStamp().getTime(), aggrImpression.getImpressionCount(),
					aggrImpression.getItemCount());
		}

		return aggregatedImpressionsList;
	}

	protected long calcMidTimeStamp(final long datetime)
	{
		return datetime - datetime % aggregationTimeWindow + aggregationTimeWindow / 2;
	}

	/**
	 * Send aggregated impression via oData
	 *
	 * @param aggregatedImpression
	 *           {@link SAPRecoImpressionAggrModel}
	 * @return true if posting was a success, false otherwise.
	 */
	public boolean postImpression(final SAPRecoImpressionAggrModel aggregatedImpression)
	{
		try
		{
			final Map<String, Object> data = new LinkedHashMap<>();
			data.put("ScenarioId", aggregatedImpression.getScenarioId());
			data.put("ImpressionCount", aggregatedImpression.getImpressionCount());
			data.put("ItemCount", aggregatedImpression.getItemCount());
			data.put("TimeStamp", aggregatedImpression.getTimeStamp());

			final Map<String, String> parameters = this.oDataService.convertMapToURIParameters("PostImpressions", data);

			final URL url = this.oDataService.createURL("PostImpressions", parameters);
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST", url);
			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);

			this.oDataService.executeWithRetry(request);

			return true;
		}
		catch (final IOException e)
		{
			LOGGER.error("Post Impression error scenarioId=" + aggregatedImpression.getScenarioId() + " impressionCount="
					+ aggregatedImpression.getImpressionCount() + " itemCount=" + aggregatedImpression.getItemCount(), e);
			return false;
		}
	}

	/**
	 * Save individual impressions when UI component is viewed
	 *
	 * @param impressionContext
	 *           the impression data to save
	 */
	public void saveImpression(final ImpressionContext impressionContext)
	{
		try
		{
			final SAPRecoImpressionModel impressionModel = modelService.create(SAPRecoImpressionModel.class);

			impressionModel.setScenarioId(impressionContext.getScenarioId());
			impressionModel.setImpressionCount(impressionContext.getImpressionCount());
			impressionModel.setItemCount(impressionContext.getItemCount());
			impressionModel.setTimeStamp(impressionContext.getTimeStamp());
			modelService.save(impressionModel);
		}
		catch (final ModelSavingException e)
		{
			LOGGER.error("An error occurred while saving impression with " + impressionContext, e);
		}
	}

	/**
	 * Get aggregated impressions for sending via oData.<br>
	 * If successfully sent, delete it. If sending failed, keep it for next time
	 */
	public void sendAggregatedImpressions()
	{
		int impressionsListSize = readBatchSize;
		int successCounter = 0;
		int failureCounter = 0;
		long retrieveTime = 0;
		long sendTime = 0;
		long deleteTime = 0;

		while (impressionsListSize == readBatchSize && failureCounter <= MAX_FAILURE)
		{
			final long retrieveStartTime = System.currentTimeMillis();
			final List<SAPRecoImpressionAggrModel> aggregatedImpressionList = recommendationBufferService
					.getAggregatedImpressions(sendBatchSize);
			retrieveTime += System.currentTimeMillis() - retrieveStartTime;

			impressionsListSize = aggregatedImpressionList.size();

			for (int i = 0; i < impressionsListSize && failureCounter <= MAX_FAILURE; i++)
			{
				final long sendStartTime = System.currentTimeMillis();
				final boolean postStatus = postImpression(aggregatedImpressionList.get(i));
				sendTime += System.currentTimeMillis() - sendStartTime;

				if (postStatus)
				{
					final long deleteStartTime = System.currentTimeMillis();
					modelService.remove(aggregatedImpressionList.get(i));
					deleteTime += System.currentTimeMillis() - deleteStartTime;
					successCounter++;
				}
				else
				{
					failureCounter++;
				}
			}
		}

		LOGGER.info("Send aggregated impressions: Successful={}, Failed={}, RetrieveTime={}ms, SendTime={}ms, DeleteTime={}ms",
				successCounter, failureCounter, retrieveTime, sendTime, deleteTime);
	}

	@Required
	public void setAggregationTimeWindow(final long aggregationTimeWindow)
	{
		LOGGER.debug("aggregationTimeWindow={}", aggregationTimeWindow);
		this.aggregationTimeWindow = aggregationTimeWindow;
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
	public void setReadBatchSize(final int readBatchSize)
	{
		LOGGER.debug("readBatchSize={}", readBatchSize);
		this.readBatchSize = readBatchSize;
	}

	@Required
	public void setRecommendationBufferService(final RecommendationBufferService recommendationBufferService)
	{
		this.recommendationBufferService = recommendationBufferService;
	}

	@Required
	public void setSendBatchSize(final int sendBatchSize)
	{
		LOGGER.debug("sendBatchSize={}", sendBatchSize);
		this.sendBatchSize = sendBatchSize;
	}
}

