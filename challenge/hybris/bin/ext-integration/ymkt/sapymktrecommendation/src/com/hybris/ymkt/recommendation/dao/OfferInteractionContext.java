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
import java.util.Date;
import java.util.List;


/**
 * This class is used to represent the payload that will be sent to ymkt
 */
public class OfferInteractionContext
{

	public static class Interaction
	{
		private String contactId;
		private String contactIdOrigin;
		private String interactionType;
		private final List<Offer> offers = new ArrayList<>();

		public String getContactId()
		{
			return contactId;
		}

		public String getContactIdOrigin()
		{
			return contactIdOrigin;
		}

		public String getInteractionType()
		{
			return interactionType;
		}

		public List<Offer> getOffers()
		{
			return offers;
		}
		public void setContactId(String contactId)
		{
			this.contactId = contactId;
		}

		public void setContactIdOrigin(String contactIdOrigin)
		{
			this.contactIdOrigin = contactIdOrigin;
		}

		public void setInteractionType(String interactionType)
		{
			this.interactionType = interactionType;
		}

	}

	public static class Offer
	{
		private String contentItemId;
		private String id;
		private String recommendationScenarioId;

		public String getContentItemId()
		{
			return contentItemId;
		}

		public String getId()
		{
			return id;
		}

		public String getRecommendationScenarioId()
		{
			return recommendationScenarioId;
		}

		public void setContentItemId(String contentItemId)
		{
			this.contentItemId = contentItemId;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public void setRecommendationScenarioId(String recommendationScenarioId)
		{
			this.recommendationScenarioId = recommendationScenarioId;
		}

	}

	private final List<Interaction> interactions = new ArrayList<>();
	private Date timestamp;

	public List<Interaction> getInteractions()
	{
		return interactions;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}
}
