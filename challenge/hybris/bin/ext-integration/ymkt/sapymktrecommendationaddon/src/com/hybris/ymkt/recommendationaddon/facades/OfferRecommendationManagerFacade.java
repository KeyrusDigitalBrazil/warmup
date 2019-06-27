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
package com.hybris.ymkt.recommendationaddon.facades;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendation.dao.OfferInteractionContext;
import com.hybris.ymkt.recommendation.dao.OfferRecommendation;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationContext;
import com.hybris.ymkt.recommendation.services.OfferDiscoveryService;
import com.hybris.ymkt.recommendation.services.OfferInteractionService;


/**
 * Facade for offer recommendation controller.
 */
public class OfferRecommendationManagerFacade
{
	protected OfferDiscoveryService offerDiscoveryService;
	protected OfferInteractionService offerInteractionService;

	/**
	 * Read {@link OfferRecommendation}s according to the {@link OfferRecommendationContext}.
	 * 
	 * @param context
	 *           {@link OfferRecommendationContext}
	 * @return {@link List} of {@link OfferRecommendation}
	 */
	public List<OfferRecommendation> getOfferRecommendations(final OfferRecommendationContext context)
	{
		return offerDiscoveryService.getOfferRecommendations(context);
	}

	/**
	 * Persist {@link OfferInteractionContext}.
	 * 
	 * @param offerInteractionContext
	 *           {@link OfferInteractionContext} to be persisted.
	 */
	public void saveOfferInteraction(final OfferInteractionContext offerInteractionContext)
	{
		offerInteractionService.saveOfferInteraction(offerInteractionContext);
	}

	@Required
	public void setOfferDiscoveryService(OfferDiscoveryService offerDiscoveryService)
	{
		this.offerDiscoveryService = offerDiscoveryService;
	}

	@Required
	public void setOfferInteractionService(OfferInteractionService offerInteractionService)
	{
		this.offerInteractionService = offerInteractionService;
	}

}
