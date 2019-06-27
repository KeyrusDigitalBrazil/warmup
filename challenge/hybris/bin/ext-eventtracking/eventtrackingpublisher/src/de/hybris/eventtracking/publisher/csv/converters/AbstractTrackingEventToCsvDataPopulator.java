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
package de.hybris.eventtracking.publisher.csv.converters;

import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.publisher.csv.model.TrackingEventCsvData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * @author stevo.slavic
 *
 */
public class AbstractTrackingEventToCsvDataPopulator implements Populator<AbstractTrackingEvent, TrackingEventCsvData>
{

	/**
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractTrackingEvent source, final TrackingEventCsvData target) throws ConversionException
	{
		target.setTimestamp(source.getInteractionTimestamp());
		target.setUrl(source.getPageUrl());
		target.setSessionId(source.getSessionId());
		target.setUserId(source.getUserId());
		target.setUserEmail(source.getUserEmail());
		target.setEventType(source.getEventType());
		target.setPiwikId(source.getPiwikId());
		target.setRefUrl(source.getRefUrl());
		target.setIdsite(source.getIdsite());
        target.setRes(source.getRes());
        target.setCvar(source.getCvar());
        target.setData(source.getData());
        target.setSearch_cat(source.getSearch_cat());
        target.setSearch_count(source.getSearch_count());
        target.setEc_id(source.getEc_id());
        target.setEc_items(source.getEc_items());
        target.setEc_st(source.getEc_st());
        target.setEc_tx(source.getEc_tx());
        target.setEc_dt(source.getEc_dt());
        target.setBaseSiteId(source.getBaseSiteId());
	}

}
