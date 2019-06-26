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
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;


/**
 * @author stevo.slavic
 *
 */
public class TrackingEventToCsvDataConverter extends AbstractPopulatingConverter<AbstractTrackingEvent, TrackingEventCsvData>
{

	public TrackingEventToCsvDataConverter()
	{
		this.setTargetClass(TrackingEventCsvData.class);
		
	}
}
