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
 package de.hybris.platform.consignmenttrackingmock.service.impl;

import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A Mock Service to return trackingEvents.
 */
public class MockConsignmentTrackingService
{
	private final Map<String, List<ConsignmentEventData>> trackingEvents;

	/**
	 * default Constructor
	 */
	public MockConsignmentTrackingService()
	{
		trackingEvents = new ConcurrentHashMap<>();
	}

	public Map<String, List<ConsignmentEventData>> getTrackingEvents()
	{
		return trackingEvents;
	}

	/**
	 * Save tracking id and consignmentEventData into map
	 * 
	 * @param trackingId
	 *           the tracking number
	 * @param consignmentEvents
	 *           consignmentEvents data
	 */
	public void saveConsignmentEvents(final String trackingId, final List<ConsignmentEventData> consignmentEvents)
	{
		trackingEvents.put(trackingId, consignmentEvents);
	}

	/**
	 * Get consignmentEventData form map by tracking id
	 * 
	 * @param trackingId
	 *           the tracking number
	 * @return all the consignmentEvents corresponds to the tracking number
	 */
	public List<ConsignmentEventData> getConsignmentEventsByTrackingId(final String trackingId)
	{
		return trackingEvents.getOrDefault(trackingId, new ArrayList<>());
	}
}
