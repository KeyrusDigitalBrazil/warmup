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
import de.hybris.eventtracking.model.events.CartViewEvent;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author stevo.slavic
 *
 */
public class CartViewEventPopulator extends AbstractTrackingEventGenericPopulator
{

	public CartViewEventPopulator(final ObjectMapper mapper)
	{
		super(mapper);
	}

	/**
	 * @see de.hybris.eventtracking.services.populators.GenericPopulator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CartViewEvent.class.isAssignableFrom(clazz);
	}

	/**
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Map<String, Object> trackingEventData, final AbstractTrackingEvent trackingEvent)
			throws ConversionException
	{
		
		((CartViewEvent) trackingEvent).setEventType("CartViewEvent");	
	}

}
