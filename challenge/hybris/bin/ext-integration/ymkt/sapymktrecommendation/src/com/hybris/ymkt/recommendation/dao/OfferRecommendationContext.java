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

/**
 * Holds data needed to make offer recommendation request
 */
public class OfferRecommendationContext extends RecommendationContext
{
	protected String contentPosition;

	public String getContentPosition()
	{
		return contentPosition;
	}

	public void setContentPosition(final String contentPosition)
	{
		this.contentPosition = contentPosition;
	}

}
