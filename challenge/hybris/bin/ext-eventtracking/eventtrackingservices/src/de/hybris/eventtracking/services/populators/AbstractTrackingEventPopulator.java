/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.eventtracking.services.populators;

import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.services.constants.TrackingEventJsonFields;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author stevo.slavic
 *
 */
public class AbstractTrackingEventPopulator extends AbstractTrackingEventGenericPopulator
{	
	public AbstractTrackingEventPopulator(final ObjectMapper mapper)
	{
		super(mapper);
	}

	/**
	 * @see de.hybris.eventtracking.services.populators.GenericPopulator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AbstractTrackingEvent.class.isAssignableFrom(clazz);
	}

	/**
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Map<String, Object> trackingEventData, final AbstractTrackingEvent trackingEvent)
			throws ConversionException
	{
		final String interactionTimestamp = (String) trackingEventData.get(TrackingEventJsonFields.COMMON_TIMESTAMP.getKey());
		final String pageUrl = (String) trackingEventData.get(TrackingEventJsonFields.COMMON_URL.getKey());
		final String sessionId = (String) trackingEventData.get(TrackingEventJsonFields.COMMON_SESSION_ID.getKey());
		final String userId = (String) trackingEventData.get(TrackingEventJsonFields.COMMON_USER_ID.getKey());
		final String userEmail = (String) trackingEventData.get(TrackingEventJsonFields.COMMON_USER_EMAIL.getKey());
		final String piwikId = (String) trackingEventData.get(TrackingEventJsonFields.COMMON_PIWIK_ID.getKey());
		final String refUrl = (String) trackingEventData.get(TrackingEventJsonFields.REF_URL.getKey());
		final String baseSiteId = (String)trackingEventData.get(TrackingEventJsonFields.BASE_SITE_ID.getKey());
		
		trackingEvent.setPageUrl(pageUrl);
		trackingEvent.setInteractionTimestamp(interactionTimestamp);
		trackingEvent.setSessionId(sessionId);
		trackingEvent.setUserId(userId);
		trackingEvent.setUserEmail(userEmail);
		trackingEvent.setPiwikId(piwikId);
		trackingEvent.setRefUrl(refUrl);
		trackingEvent.setBaseSiteId(baseSiteId);
		
		trackingEvent.setIdsite((String)trackingEventData.get(TrackingEventJsonFields.IDSITE.getKey()));
		trackingEvent.setRes((String)trackingEventData.get(TrackingEventJsonFields.SCREEN_RESOLUTION.getKey()));
		trackingEvent.setCvar((String)trackingEventData.get(TrackingEventJsonFields.COMMON_CVAR_PAGE.getKey()));
		trackingEvent.setData((String) trackingEventData.get(TrackingEventJsonFields.DATA.getKey()));
		trackingEvent.setSearch_cat((String) trackingEventData.get(TrackingEventJsonFields.SEARCH_CATEGORY.getKey()));
		trackingEvent.setSearch_count((String)trackingEventData.get(TrackingEventJsonFields.SEARCH_COUNT.getKey()));
		trackingEvent.setEc_id((String)trackingEventData.get(TrackingEventJsonFields.COMMERCE_ORDER_ID.getKey()));
		trackingEvent.setEc_items((String)trackingEventData.get(TrackingEventJsonFields.COMMERCE_CART_ITEMS.getKey()));
		trackingEvent.setEc_st((String)trackingEventData.get(TrackingEventJsonFields.COMMERCE_ST.getKey()));
		trackingEvent.setEc_tx((String)trackingEventData.get(TrackingEventJsonFields.COMMERCE_TX.getKey()));
		trackingEvent.setEc_dt((String)trackingEventData.get(TrackingEventJsonFields.COMMERCE_DT.getKey()));
	}

}
