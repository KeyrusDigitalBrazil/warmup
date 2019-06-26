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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;


/**
 * Representation of OData RecommendationScenario
 *
 */
public class RecommendationScenario
{
	public static class BasketObject
	{
		@Nonnull
		protected final String basketObjectId;
		@Nonnull
		protected final String basketObjectType;

		public BasketObject(final String basketObjectType, final String basketObjectId)
		{
			this.basketObjectType = Objects.requireNonNull(basketObjectType);
			this.basketObjectId = Objects.requireNonNull(basketObjectId);
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (this.getClass() == obj.getClass())
			{
				final BasketObject other = (BasketObject) obj;
				return basketObjectId.equals(other.basketObjectId) && basketObjectType.equals(other.basketObjectType);
			}
			return false;
		}

		public String getBasketObjectId()
		{
			return basketObjectId;
		}

		public String getBasketObjectType()
		{
			return basketObjectType;
		}

		@Override
		public int hashCode()
		{
			return 31 * basketObjectId.hashCode() + basketObjectType.hashCode();
		}
	}

	public static class LeadingObject
	{
		protected final String leadingObjectId;
		protected final String leadingObjectType;

		public LeadingObject(final String leadingObjectType, final String leadingObjectId)
		{
			this.leadingObjectType = Objects.requireNonNull(leadingObjectType);
			this.leadingObjectId = Objects.requireNonNull(leadingObjectId);
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (this.getClass() == obj.getClass())
			{
				final LeadingObject other = (LeadingObject) obj;
				return leadingObjectId.equals(other.leadingObjectId) && leadingObjectType.equals(other.leadingObjectType);
			}
			return false;
		}

		public String getLeadingObjectId()
		{
			return leadingObjectId;
		}

		public String getLeadingObjectType()
		{
			return leadingObjectType;
		}

		@Override
		public int hashCode()
		{
			return 31 * leadingObjectId.hashCode() + leadingObjectType.hashCode();
		}
	}

	public static class ResultObject
	{
		protected final String resultObjectId;

		public ResultObject(ResultObject ro)
		{
			this(ro.resultObjectId);
		}

		public ResultObject(final String resultObjectId)
		{
			this.resultObjectId = resultObjectId;
		}

		public String getResultObjectId()
		{
			return resultObjectId;
		}
	}

	public static class Scenario
	{
		protected final Collection<BasketObject> basketObjects = new HashSet<>();
		protected final Collection<LeadingObject> leadingObjects = new HashSet<>();
		protected final String scenarioId;

		public Scenario(final Scenario s)
		{
			this(s.scenarioId);
			this.basketObjects.addAll(s.basketObjects);
			this.leadingObjects.addAll(s.leadingObjects);
		}

		public Scenario(final String scenarioId)
		{
			this.scenarioId = scenarioId;
		}

		public Collection<BasketObject> getBasketObjects()
		{
			return basketObjects;
		}

		public Collection<LeadingObject> getLeadingObjects()
		{
			return leadingObjects;
		}

		public String getScenarioId()
		{
			return scenarioId;
		}

		@Override
		public String toString()
		{
			final StringBuilder builder = new StringBuilder();
			builder.append("Scenario [basketObjects=");
			builder.append(basketObjects);
			builder.append(", leadingObjects=");
			builder.append(leadingObjects);
			builder.append(", scenarioId=");
			builder.append(scenarioId);
			builder.append("]");
			return builder.toString();
		}
	}

	public static class ScenarioHash
	{
		protected Date expiresOn;
		protected String hashId;
		protected String resultScope;
		protected final String scenarioId;

		public ScenarioHash(final ScenarioHash sh)
		{
			this(sh.scenarioId);
			this.expiresOn = sh.expiresOn;
			this.hashId = sh.hashId;
			this.resultScope = sh.resultScope;
		}

		public ScenarioHash(final String scenarioId)
		{
			this.scenarioId = scenarioId;
		}

		public Date getExpiresOn()
		{
			return expiresOn;
		}

		public String getHashId()
		{
			return hashId;
		}

		public String getResultScope()
		{
			return resultScope;
		}

		public String getScenarioId()
		{
			return scenarioId;
		}

		public void setExpiresOn(final Date expiresOn)
		{
			this.expiresOn = expiresOn;
		}

		public void setHashId(final String hashId)
		{
			this.hashId = hashId;
		}

		public void setResultScope(final String resultScope)
		{
			this.resultScope = resultScope;
		}
	}

	protected final List<ResultObject> resultObjects = new ArrayList<>();
	protected final List<ScenarioHash> scenarioHashes = new ArrayList<>();
	protected final List<Scenario> scenarios = new ArrayList<>();
	protected final String userId;
	protected final String userType;

	public RecommendationScenario(RecommendationScenario rs)
	{
		this(rs.userId, rs.userType);
		rs.resultObjects.stream().map(ResultObject::new).collect(Collectors.toCollection(() -> this.resultObjects));
		rs.scenarioHashes.stream().map(ScenarioHash::new).collect(Collectors.toCollection(() -> this.scenarioHashes));
		rs.scenarios.stream().map(Scenario::new).collect(Collectors.toCollection(() -> this.scenarios));
	}

	public RecommendationScenario(final String userId, final String userType)
	{
		this.userId = userId;
		this.userType = userType;
	}

	public List<ResultObject> getResultObjects()
	{
		return resultObjects;
	}

	public List<ScenarioHash> getScenarioHashes()
	{
		return scenarioHashes;
	}

	public List<Scenario> getScenarios()
	{
		return scenarios;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getUserType()
	{
		return userType;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("RecommendationScenario [resultObjects=");
		builder.append(resultObjects);
		builder.append(", scenarioHashes=");
		builder.append(scenarioHashes);
		builder.append(", scenarios=");
		builder.append(scenarios);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", userType=");
		builder.append(userType);
		builder.append("]");
		return builder.toString();
	}

	public void update(final Map<String, Object> recommendationScenario)
	{
		this.scenarioHashes.clear();
		this.resultObjects.clear();

		Optional.ofNullable(recommendationScenario.get("ScenarioHashes")) //
				.map(ODataFeed.class::cast) //
				.ifPresent(this::updateScenarioHashes);

		Optional.ofNullable(recommendationScenario.get("ResultObjects")) //
				.map(ODataFeed.class::cast) //
				.ifPresent(this::updateResultObjects);
	}

	protected void updateResultObjects(final ODataFeed resultObjects)
	{
		resultObjects.getEntries().stream().map(ODataEntry::getProperties).map(sh -> {
			final ResultObject resultObject = new ResultObject( //
					(String) sh.get("ResultObjectId"));
			return resultObject;
		}).collect(Collectors.toCollection(() -> this.resultObjects));
	}

	protected void updateScenarioHashes(final ODataFeed scenarioHashes)
	{
		scenarioHashes.getEntries().stream().map(ODataEntry::getProperties).map(sh -> {
			final ScenarioHash scenarioHash = new ScenarioHash((String) sh.get("ScenarioId"));
			scenarioHash.setHashId((String) sh.get("HashId"));
			final Date expiresOn = Optional.ofNullable(sh.get("ExpiresOn")) //
					.map(Calendar.class::cast) //
					.map(Calendar::getTime) //
					.orElse(new Date());
			scenarioHash.setExpiresOn(expiresOn);
			scenarioHash.setResultScope((String) sh.get("ResultScope"));
			return scenarioHash;
		}).collect(Collectors.toCollection(() -> this.scenarioHashes));
	}
}
