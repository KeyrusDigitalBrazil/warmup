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
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.personalization.services.TargetGroupService;


/**
 * This class will create segments in the personalization of SmartEdit by retrieving target group values from yMKT. This
 * feature will only work for Logged In users that have the personalization consent approved. COOKIE_ID users is
 * excluded here, because the API does not support "Shareable" origin IDs
 */
public class TargetGroupSegmentsProvider implements UserSegmentsProvider
{
	/** null if the segment provider can't provide new data, and doesn't want the existing data to be changed. */
	private static final List<SegmentMappingData> CAN_T_PROVIDE_NEW_DATA = null;

	private static final Logger LOG = LoggerFactory.getLogger(TargetGroupSegmentsProvider.class);

	protected String segmentPrefix;
	protected boolean targetGroupEnabled;
	protected TargetGroupService targetGroupService;
	protected UserContextService userContextService;
	protected UserService userService;

	protected SegmentMappingData convert(final String targetGroupId)
	{
		final SegmentMappingData segment = new SegmentMappingData();
		segment.setCode(this.segmentPrefix.concat(targetGroupId));
		segment.setAffinity(BigDecimal.ONE);
		return segment;
	}

	@Override
	public List<SegmentMappingData> getUserSegments(final UserModel user)
	{

		// feature activation in properties or
		// incognito (includes logged in user without consent) or
		// excluding anonymous (as the service does not work for shareable origin Id for COOKIE_ID) 
		if (!this.targetGroupEnabled || this.userContextService.isIncognitoUser() || this.userService.isAnonymousUser(user))
		{
			LOG.debug("create segment enabled = {}, user is incognito = {}, user is anonymous = {}", this.targetGroupEnabled,
					this.userContextService.isIncognitoUser(), this.userService.isAnonymousUser(user));
			return CAN_T_PROVIDE_NEW_DATA;
		}

		LOG.debug("Call of getUserSegments with user={}", user);

		if (user instanceof CustomerModel)
		{
			try
			{
				final CustomerModel customer = (CustomerModel) user;
				final String customerID = customer.getCustomerID();

				LOG.debug("Call of customerID with user={}", customerID);

				final String userOrigin = this.userContextService.getUserOrigin();
				final List<UUID> guids = this.targetGroupService.getCustomerTargetGroupsGUIDs(customerID, userOrigin);

				return this.targetGroupService.getCustomerTargetGroupIds(guids) //
						.stream().map(this::convert).collect(Collectors.toList());
			}
			catch (IOException e)
			{
				LOG.error("Error in InitiativeService using " + user, e);
				return CAN_T_PROVIDE_NEW_DATA;
			}
		}
		return Collections.emptyList();
	}

	@Required
	public void setSegmentPrefix(final String segmentPrefix)
	{
		LOG.debug("segmentPrefix={}", segmentPrefix);
		this.segmentPrefix = segmentPrefix;
	}

	@Required
	public void setTargetGroupEnabled(final boolean targetGroupEnabled)
	{
		LOG.debug("targetGroupEnabled={}", targetGroupEnabled);
		this.targetGroupEnabled = targetGroupEnabled;
	}

	@Required
	public void setTargetGroupService(final TargetGroupService targetGroupService)
	{
		this.targetGroupService = targetGroupService;
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