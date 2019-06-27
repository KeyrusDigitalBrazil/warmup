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
package com.hybris.ymkt.recommendation.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.ScenarioHash;


/**
 *
 */
public final class RecommendationScenarioUtils
{
	private RecommendationScenarioUtils()
	{
		//empty private constructor
	}

	public static List<String> convertBufferToList(final String list)
	{
		return Arrays.stream(list.split("\\s*,\\s*")) //
				.collect(Collectors.toList());
	}

	protected static Map<String, Object> convertBasketObjectToMap(final BasketObject bo)
	{
		final Map<String, Object> leadingObject = new HashMap<>();
		leadingObject.put("BasketObjectType", bo.getBasketObjectType());
		leadingObject.put("BasketObjectId", bo.getBasketObjectId());
		return leadingObject;
	}

	protected static Map<String, Object> convertLeadingObjectToMap(final LeadingObject lo)
	{
		final Map<String, Object> leadingObject = new HashMap<>();
		leadingObject.put("LeadingObjectType", lo.getLeadingObjectType());
		leadingObject.put("LeadingObjectId", lo.getLeadingObjectId());
		return leadingObject;
	}

	protected static Map<String, Object> convertScenarioToMap(final Scenario s)
	{
		final Map<String, Object> scenario = new HashMap<>();
		scenario.put("ScenarioId", s.getScenarioId());
		scenario.put("LeadingObjects",
				s.getLeadingObjects().stream() //
						.map(RecommendationScenarioUtils::convertLeadingObjectToMap) //
						.collect(Collectors.toList()));
		scenario.put("BasketObjects",
				s.getBasketObjects().stream() //
						.map(RecommendationScenarioUtils::convertBasketObjectToMap) //
						.collect(Collectors.toList()));
		return scenario;
	}

	protected static Map<String, Object> convertScenarioHashToMap(final ScenarioHash sh)
	{
		final Map<String, Object> scenarioHash = new HashMap<>();
		scenarioHash.put("ScenarioId", sh.getScenarioId());
		scenarioHash.put("HashId", sh.getHashId());
		return scenarioHash;
	}

	public static Map<String, Object> convertRecommendationScenarioToMap(final RecommendationScenario recommendationScenario)
	{
		final Map<String, Object> recommendationScenarioMap = new LinkedHashMap<>();
		recommendationScenarioMap.put("UserId", recommendationScenario.getUserId());
		recommendationScenarioMap.put("UserType", recommendationScenario.getUserType());
		recommendationScenarioMap.put("ExternalTracking", Boolean.TRUE);

		// User's precomputed scenario's target group.
		recommendationScenarioMap.put("ScenarioHashes",
				recommendationScenario.getScenarioHashes().stream() //
						.map(RecommendationScenarioUtils::convertScenarioHashToMap) //
						.collect(Collectors.toList()));

		recommendationScenarioMap.put("Scenarios",
				recommendationScenario.getScenarios().stream() //
						.map(RecommendationScenarioUtils::convertScenarioToMap) //
						.collect(Collectors.toList()));

		recommendationScenarioMap.put("ResultObjects", Collections.emptyList());

		return recommendationScenarioMap;
	}

}
