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
package com.hybris.ymkt.recommendationbuffer.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendationbuffer.dao.RecommendationBufferDao;
import com.hybris.ymkt.recommendationbuffer.model.SAPOfferInteractionModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoClickthroughModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionAggrModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoTypeMappingModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationMappingModel;


/**
 * Default interface implementation
 *
 * @see RecommendationBufferDao
 */
public class DefaultRecommendationBufferDao implements RecommendationBufferDao
{
	protected static final String QUERY_PARAMETER_HASH_ID = "hashId";
	protected static final String QUERY_PARAMETER_LEADING_ITEMS = "leadingItems";
	protected static final String QUERY_PARAMETER_SCENARIO_ID = "scenarioId";
	protected static final String QUERY_PARAMETER_USER_ID = "userId";

	protected static final String WHERE_CLAUSE_EXPIRES_ON = "{expiresOn} < ?expiresOn";
	protected static final String WHERE_CLAUSE_HASH_ID = "{hashId} = ?hashId";
	protected static final String WHERE_CLAUSE_HASH_ID_IN = "{hashId} IN (?hashId)";
	protected static final String WHERE_CLAUSE_LEADING_ITEMS = "{leadingItems} = ?leadingItems";
	protected static final String WHERE_CLAUSE_RECO_TYPE = "{recoType} = ?recoType";
	protected static final String WHERE_CLAUSE_SCENARIO_ID = "{scenarioId} = ?scenarioId";
	protected static final String WHERE_CLAUSE_USER_ID = "{userId} = ?userId";

	protected FlexibleSearchService flexibleSearchService;

	protected FlexibleSearchQuery buildFlexibleSearchQuery(final String typecode, final String... whereCauses)
	{
		final String basicQuery = "SELECT {pk} FROM {" + typecode + "}";
		if (whereCauses.length == 0)
		{
			return new FlexibleSearchQuery(basicQuery);
		}
		final String queryOptions = Arrays.stream(whereCauses).collect(Collectors.joining(" AND ", " WHERE ", ""));
		return new FlexibleSearchQuery(basicQuery + queryOptions);
	}

	@Override
	public List<SAPRecoClickthroughModel> findClickthroughs(final int batchSize)
	{
		return this.searchAnyNoCaching(SAPRecoClickthroughModel._TYPECODE, batchSize);
	}

	protected <T> List<T> findExpiredAny(final String typecode, final Date expiresOn)
	{
		final FlexibleSearchQuery fQuery = this.buildFlexibleSearchQuery(typecode, WHERE_CLAUSE_EXPIRES_ON);
		fQuery.addQueryParameter("expiresOn", expiresOn);
		fQuery.setDisableCaching(true);
		return this.search(fQuery);
	}

	@Override
	public List<SAPRecommendationMappingModel> findExpiredRecommendationMappings(final Date expiresOn)
	{
		return this.findExpiredAny(SAPRecommendationMappingModel._TYPECODE, expiresOn);
	}

	@Override
	public List<SAPRecommendationBufferModel> findExpiredRecommendations(final Date expiresOn)
	{
		return this.findExpiredAny(SAPRecommendationBufferModel._TYPECODE, expiresOn);
	}

	@Override
	public List<SAPRecoTypeMappingModel> findExpiredRecoTypeMappings(final Date expiresOn)
	{
		return this.findExpiredAny(SAPRecoTypeMappingModel._TYPECODE, expiresOn);
	}

	@Override
	public List<SAPRecoImpressionModel> findImpressions(final int batchSize)
	{
		return this.searchAnyNoCaching(SAPRecoImpressionModel._TYPECODE, batchSize);
	}

	@Override
	public List<SAPRecoImpressionAggrModel> findImpressionsAggregated(final int batchSize)
	{
		return this.searchAnyNoCaching(SAPRecoImpressionAggrModel._TYPECODE, batchSize);
	}

	@Override
	public List<SAPOfferInteractionModel> findOfferInteractions(final int batchSize)
	{
		return this.searchAnyNoCaching(SAPOfferInteractionModel._TYPECODE, batchSize);
	}

	@Override
	public List<SAPRecommendationBufferModel> findRecommendation(final String scenarioId, final String hashId,
			final String leadingItems)
	{
		final FlexibleSearchQuery fQuery = this.buildFlexibleSearchQuery( //
				SAPRecommendationBufferModel._TYPECODE, //
				WHERE_CLAUSE_SCENARIO_ID, //
				WHERE_CLAUSE_HASH_ID_IN, //
				WHERE_CLAUSE_LEADING_ITEMS);

		fQuery.addQueryParameter(QUERY_PARAMETER_SCENARIO_ID, scenarioId);
		fQuery.addQueryParameter(QUERY_PARAMETER_HASH_ID, hashId);
		fQuery.addQueryParameter(QUERY_PARAMETER_LEADING_ITEMS, leadingItems);

		return this.search(fQuery);
	}

	@Override
	public List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId)
	{
		final FlexibleSearchQuery fQuery = this.buildFlexibleSearchQuery( //
				SAPRecommendationMappingModel._TYPECODE, //
				WHERE_CLAUSE_USER_ID, //
				WHERE_CLAUSE_SCENARIO_ID);
		fQuery.addQueryParameter(QUERY_PARAMETER_USER_ID, userId);
		fQuery.addQueryParameter(QUERY_PARAMETER_SCENARIO_ID, scenarioId);
		return this.search(fQuery);
	}

	@Override
	public List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId,
			final String hashId)
	{
		final FlexibleSearchQuery fQuery = this.buildFlexibleSearchQuery( //
				SAPRecommendationMappingModel._TYPECODE, //
				WHERE_CLAUSE_USER_ID, //
				WHERE_CLAUSE_SCENARIO_ID, //
				WHERE_CLAUSE_HASH_ID);
		fQuery.addQueryParameter(QUERY_PARAMETER_USER_ID, userId);
		fQuery.addQueryParameter(QUERY_PARAMETER_SCENARIO_ID, scenarioId);
		fQuery.addQueryParameter(QUERY_PARAMETER_HASH_ID, hashId);
		return this.search(fQuery);
	}

	@Override
	public List<SAPRecoTypeMappingModel> findRecoTypeMapping(final String recoType, final String scenarioId)
	{
		final FlexibleSearchQuery fQuery = this.buildFlexibleSearchQuery( //
				SAPRecoTypeMappingModel._TYPECODE, //
				WHERE_CLAUSE_SCENARIO_ID, //
				WHERE_CLAUSE_RECO_TYPE);
		fQuery.addQueryParameter(QUERY_PARAMETER_SCENARIO_ID, scenarioId);
		fQuery.addQueryParameter("recoType", recoType);
		return this.search(fQuery);
	}

	protected <T> List<T> search(final FlexibleSearchQuery fQuery)
	{
		return this.flexibleSearchService.<T> search(fQuery).getResult();
	}

	protected <T> List<T> searchAnyNoCaching(final String typecode, final int batchSize)
	{
		final FlexibleSearchQuery fQuery = this.buildFlexibleSearchQuery(typecode);
		fQuery.setDisableCaching(true);
		fQuery.setCount(batchSize);
		fQuery.setStart(0);
		return this.search(fQuery);
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
