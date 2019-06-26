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
package com.hybris.ymkt.recommendation.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;


/**
 * Representation of OData RecommendationScenario
 *
 */
public class OfferRecommendationScenario
{
	public static class ContextParam
	{
		protected final int contextId;
		protected final String name;
		protected final String value;

		public ContextParam(final int contextId, final String name, final String value)
		{
			this.contextId = contextId;
			this.name = name;
			this.value = value;
		}

		public int getContextId()
		{
			return contextId;
		}

		public String getValue()
		{
			return value;
		}

		public String getName()
		{
			return name;
		}
	}

	public static class Result
	{
		protected final String offerId;
		protected final String targetLink;
		protected final String targetDescription;
		protected final String contentId;
		protected final String contentSource;
		protected final String contentDescription;
		protected BigDecimal score;

		public Result(final String offerId, final String targetLink, final String targetDescription, final String contentId,
				final String contentSource, final String contentDescription)
		{
			this.offerId = offerId;
			this.targetLink = targetLink;
			this.targetDescription = targetDescription;
			this.contentId = contentId;
			this.contentSource = contentSource;
			this.contentDescription = contentDescription;
		}

		public String getOfferId()
		{
			return offerId;
		}

		public String getTargetLink()
		{
			return targetLink;
		}

		public String getTargetDescription()
		{
			return targetDescription;
		}

		public String getContentId()
		{
			return contentId;
		}

		public String getContentSource()
		{
			return contentSource;
		}

		public String getContentDescription()
		{
			return contentDescription;
		}

		public BigDecimal getScore()
		{
			return score;
		}

		public void setScore(final BigDecimal score)
		{
			this.score = score;
		}
	}

	protected final String userId;
	protected final String userOriginId;
	protected final String recommendationScenarioId;
	protected final Collection<BasketObject> basketObjects = new HashSet<>();
	protected final Collection<LeadingObject> leadingObjects = new HashSet<>();
	protected final List<ContextParam> contextParams = new ArrayList<>();
	protected final List<Result> results = new ArrayList<>();

	public OfferRecommendationScenario(final String userId, final String userOriginId, final String recommendationScenarioId)
	{
		this.userId = userId;
		this.userOriginId = userOriginId;
		this.recommendationScenarioId = recommendationScenarioId;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getUserOriginId()
	{
		return userOriginId;
	}

	public String getRecommendationScenarioId()
	{
		return recommendationScenarioId;
	}

	public Collection<BasketObject> getBasketObjects()
	{
		return basketObjects;
	}

	public Collection<LeadingObject> getLeadingObjects()
	{
		return leadingObjects;
	}

	public List<ContextParam> getContextParams()
	{
		return contextParams;
	}

	public List<Result> getResults()
	{
		return results;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("OfferRecommendationScenario [recommendationScenarioId=");
		builder.append(recommendationScenarioId);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", userOriginId=");
		builder.append(userOriginId);
		builder.append(", basketObjects=");
		builder.append(basketObjects);
		builder.append(", leadingObjects=");
		builder.append(leadingObjects);
		builder.append(", contextParams=");
		builder.append(contextParams);
		builder.append(", results=");
		builder.append(results);
		builder.append("]");
		return builder.toString();
	}

	public void update(final Map<String, Object> recommendationScenario)
	{
		this.results.clear();

		Optional.ofNullable(recommendationScenario.get("Results")) //
				.map(ODataFeed.class::cast) //
				.ifPresent(this::updateResults);
	}

	protected void updateResults(final ODataFeed resultObjectsFeed)
	{
		resultObjectsFeed.getEntries().stream().map(ODataEntry::getProperties).map(sh -> {
			final Result result = new Result( //
					(String) sh.get("OfferId"), //
					(String) sh.get("TargetLink"), //
					(String) sh.get("TargetDescription"), //
					(String) sh.get("ContentId"), //
					(String) sh.get("ContentSource"), //
					(String) sh.get("ContentDescription"));
			result.setScore((BigDecimal) sh.get("Score"));
			return result;
		}).collect(Collectors.toCollection(() -> this.results));
	}

}
