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
/**
 *
 */
package com.hybris.ymkt.personalization.segment;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationintegration.segment.UserSegmentsProvider;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.constants.SapymktcommonConstants;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;
import com.hybris.ymkt.segmentation.services.InitiativeService;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery.TileFilterCategory;


/**
 * This class will create segments in the personalization of SmartEdit by retrieving initiative (campaign) values from
 * yMKT. This feature will only work for users, including COOKIE_ID users, that gave consent to personalize.
 */
public class InitiativeUserSegmentsProvider implements UserSegmentsProvider
{
	/** null if the segment provider can't provide new data, and doesn't want the existing data to be changed. */
	private static final List<SegmentMappingData> CAN_T_PROVIDE_NEW_DATA = null;

	private static final Logger LOG = LoggerFactory.getLogger(InitiativeUserSegmentsProvider.class);

	protected boolean campaignEnabled;
	protected InitiativeService initiativeService;
	protected String segmentPrefix;
	protected SessionService sessionService;
	protected UserContextService userContextService;
	protected UserService userService;

	protected SegmentMappingData convert(final SAPInitiative initiative)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(segmentPrefix);
		sb.append(initiative.getId());

		final SegmentMappingData segment = new SegmentMappingData();
		segment.setCode(sb.toString());
		segment.setAffinity(BigDecimal.ONE);
		return segment;
	}

	@Override
	public List<SegmentMappingData> getUserSegments(final UserModel user)
	{

		// feature activation in properties
		if (!campaignEnabled || userContextService.isIncognitoUser())
		{
			LOG.debug("create segment enabled = {}, user is incognito = {}", campaignEnabled, userContextService.isIncognitoUser());
			return CAN_T_PROVIDE_NEW_DATA;
		}

		LOG.debug("Call of getUserSegments with user={}", user);

		if (user instanceof CustomerModel)
		{
			try
			{
				final CustomerModel customer = (CustomerModel) user;
				final String userOriginId = userContextService.getUserOrigin();

				final String customerID = userService.isAnonymousUser(user)
						? this.sessionService.getAttribute(SapymktcommonConstants.PERSONALIZATION_PIWIK_ID_SESSION_KEY)
						: customer.getCustomerID();

				final InitiativeQuery query = new InitiativeQuery.Builder() //
						.setTileFilterCategories(TileFilterCategory.ACTIVE) //
						.setContactOrigins(userOriginId) //
						.setContacts(customerID).build();

				return this.initiativeService.getInitiatives(query).stream() //
						.map(this::convert) //
						.collect(Collectors.toList());
			}
			catch (final IOException e)
			{
				LOG.error("Error in InitiativeService using " + user, e);
				return CAN_T_PROVIDE_NEW_DATA;
			}
		}
		return Collections.emptyList();
	}

	@Required
	public void setCampaignEnabled(final boolean campaignEnabled)
	{
		LOG.debug("campaignEnabled={}", campaignEnabled);
		this.campaignEnabled = campaignEnabled;
	}

	@Required
	public void setInitiativeService(final InitiativeService initiativeService)
	{
		this.initiativeService = initiativeService;
	}

	@Required
	public void setSegmentPrefix(final String segmentPrefix)
	{
		LOG.debug("segmentPrefix={}", segmentPrefix);
		this.segmentPrefix = segmentPrefix;
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

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}