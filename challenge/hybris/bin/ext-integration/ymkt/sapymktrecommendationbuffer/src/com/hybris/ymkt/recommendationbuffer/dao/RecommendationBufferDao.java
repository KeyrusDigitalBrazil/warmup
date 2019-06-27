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
package com.hybris.ymkt.recommendationbuffer.dao;

import java.util.Date;
import java.util.List;

import com.hybris.ymkt.recommendationbuffer.model.SAPOfferInteractionModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoClickthroughModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionAggrModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoTypeMappingModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationMappingModel;


/**
 * An interface for SAP Recommendation DAO
 */
public interface RecommendationBufferDao
{
	/**
	 * @param batchSize
	 * @return {@link List} of expired {@link SAPRecoClickthroughModel}
	 */
	List<SAPRecoClickthroughModel> findClickthroughs(int batchSize);

	/**
	 * @param expiresOn
	 * @return {@link List} of expired {@link SAPRecommendationMappingModel}
	 */
	List<SAPRecommendationMappingModel> findExpiredRecommendationMappings(Date expiresOn);

	/**
	 * @param expiresOn
	 * @return {@link List} of expired {@link SAPRecommendationBufferModel}
	 */
	List<SAPRecommendationBufferModel> findExpiredRecommendations(Date expiresOn);

	/**
	 * @param expiresOn
	 * @return {@link List} of expired {@link SAPRecoTypeMappingModel}
	 */
	List<SAPRecoTypeMappingModel> findExpiredRecoTypeMappings(Date expiresOn);

	/**
	 * @param batchSize
	 * @return {@link List} of {@link SAPRecoImpressionModel}
	 */
	List<SAPRecoImpressionModel> findImpressions(int batchSize);

	/**
	 * @param batchSize
	 * @return {@link List} of {@link SAPRecoImpressionAggrModel}
	 */
	List<SAPRecoImpressionAggrModel> findImpressionsAggregated(int batchSize);

	/**
	 * @param scenarioId
	 * @param hashId
	 * @param leadingItems
	 * @return {@link List} of {@link SAPRecommendationBufferModel}
	 */
	List<SAPRecommendationBufferModel> findRecommendation(final String scenarioId, final String hashId,
			final String leadingItems);

	/**
	 * @param userId
	 * @param scenarioId
	 * @return {@link List} of {@link SAPRecommendationMappingModel}
	 */
	List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId);

	/**
	 * @param userId
	 * @param scenarioId
	 * @param hashId
	 * @return {@link List} of {@link SAPRecommendationMappingModel}
	 */
	List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId,
			final String hashId);

	/**
	 * @param recoType
	 * @param scenarioId
	 * @return {@link List} of {@link SAPRecoTypeMappingModel}
	 */
	List<SAPRecoTypeMappingModel> findRecoTypeMapping(String recoType, String scenarioId);

	/**
	 * @param batchSize
	 * @return {@link List} of {@link SAPOfferInteractionModel}
	 */
	List<SAPOfferInteractionModel> findOfferInteractions(int batchSize);
}
