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

import de.hybris.eventtracking.model.events.AbstractCartAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.publisher.csv.model.TrackingEventCsvData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * @author stevo.slavic
 *
 */
public class AbstractCartAwareTrackingEventToCsvDataPopulator implements Populator<AbstractTrackingEvent, TrackingEventCsvData>
{

	/**
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractTrackingEvent source, final TrackingEventCsvData target) throws ConversionException
	{
		if (AbstractCartAwareTrackingEvent.class.isAssignableFrom(source.getClass()))
		{
			target.setCartId(((AbstractCartAwareTrackingEvent) source).getCartId());
			target.setEventType(((AbstractCartAwareTrackingEvent) source).getEventType());
		}
	}

}
