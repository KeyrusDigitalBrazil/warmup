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
package com.hybris.ymkt.segmentation.evaluators;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;
import com.hybris.ymkt.segmentation.model.CMSYmktCampaignRestrictionModel;
import com.hybris.ymkt.segmentation.services.InitiativeService;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery.TileFilterCategory;


/**
 *
 */
public class CampaignRestrictionEvaluator implements CMSRestrictionEvaluator<CMSYmktCampaignRestrictionModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CampaignRestrictionEvaluator.class);

	protected static final String SESSION_PREFIX = "ymktSegmentationCampaignRestriction_";

	protected InitiativeService initiativeService;
	protected SessionService sessionService;
	protected UserContextService userContextService;
	
	protected void addCampaignsToSession(final List<String> campaignsList)
	{
		final String contactId = initiativeService.getInteractionContactId();
		sessionService.setAttribute(SESSION_PREFIX + contactId, campaignsList);
		LOG.debug("Campaigns added to session for contactId={}, campaigns={}", contactId, campaignsList);
	}

	/**
	 * This evaluator checks if the restriction is applicable.<br>
	 * The restriction is true when the user belongs in a target group associated with the selected campaign<br>
	 * The memberOfCampaign flag can be used to negate the restriction<br>
	 * e.g.<br>
	 * memberOfCampaign=true -> Restriction evaluates to true when user belongs to selected campaign<br>
	 * memberOfCampaign=false -> Restriction evaluates to false when user belongs to selected campaign<br>
	 *
	 * @param campaignRestriction
	 * @param context
	 * @return true if user belongs to campaign
	 */
	@Override
	public boolean evaluate(final CMSYmktCampaignRestrictionModel campaignRestriction, final RestrictionData context)
	{
		Objects.requireNonNull(campaignRestriction, "campaignRestriction");

		boolean userBelongsToCampaign = false;
		List<String> campaigns = Collections.emptyList();
		final boolean isMemberOfCampaign = campaignRestriction.getMemberOfCampaign();

		if (!userContextService.isIncognitoUser())
		{
			//Get campaigns from session or back end
			final Optional<List<String>> optionalSessionCampaigns = this.getCampaignsFromSession();
			campaigns = optionalSessionCampaigns.orElseGet(this::getCampaignsFromBackend);
			userBelongsToCampaign = campaigns.contains(campaignRestriction.getCampaign());
		}

		final boolean evaluatorResult = !(userBelongsToCampaign ^ isMemberOfCampaign);

		LOG.debug("Evaluate with userCampaigns={}, userInCampaign={}, isMemberOfCampaign={}, evaluatorResult={}", campaigns,
				userBelongsToCampaign, isMemberOfCampaign, evaluatorResult);

		return evaluatorResult;
	}

	/**
	 * Retrieve user's campaigns from back end system and adds them to user session
	 * 
	 * @return A list of campaigns
	 */
	protected List<String> getCampaignsFromBackend()
	{
		try
		{
			final InitiativeQuery query = new InitiativeQuery.Builder() //
					.setTileFilterCategories(TileFilterCategory.ACTIVE) //
					.filterByUserContext(true) //
					.build();

			final List<String> campaigns = initiativeService.getInitiatives(query).stream() //
					.map(SAPInitiative::getId) //
					.map(String::intern) //
					.collect(Collectors.toList());

			this.addCampaignsToSession(campaigns);
			return campaigns;
		}
		catch (final IOException e)
		{
			LOG.error("Error retrieving campaigns for user " + initiativeService.getInteractionContactId(), e);
			return Collections.emptyList();
		}
	}

	protected Optional<List<String>> getCampaignsFromSession()
	{
		final String contactId = initiativeService.getInteractionContactId();
		final List<String> sessionCampaigns = sessionService.getAttribute(SESSION_PREFIX + contactId);
		return Optional.ofNullable(sessionCampaigns);
	}

	@Required
	public void setInitiativeService(final InitiativeService initiativeService)
	{
		this.initiativeService = initiativeService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}	
}
