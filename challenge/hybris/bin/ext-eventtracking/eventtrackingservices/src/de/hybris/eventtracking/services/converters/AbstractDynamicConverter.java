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
package de.hybris.eventtracking.services.converters;

import de.hybris.eventtracking.services.exceptions.EventTrackingInternalException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;


/**
 * @author stevo.slavic
 *
 */
public abstract class AbstractDynamicConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET>, Populator<SOURCE, TARGET>
{

	private static final Logger LOG = Logger.getLogger(AbstractDynamicConverter.class);
	private final TypeResolver<SOURCE, TARGET> typeResolver;

	public AbstractDynamicConverter(final TypeResolver<SOURCE, TARGET> typeResolver)
	{
		this.typeResolver = typeResolver;
	}

	@Override
	public TARGET convert(final SOURCE source) throws ConversionException
	{
		final TARGET target = createTargetFromSource(source);
		if (target != null)
		{
			populate(source, target);
		}
		return target;
	}

	@Override
	public TARGET convert(final SOURCE source, final TARGET prototype) throws ConversionException
	{
		LOG.warn("Do not call this method - only call the single argument method  #convert(Object)");
		return convert(source);
	}

	protected TARGET createTargetFromSource(final SOURCE source)
	{
		final Class<? extends TARGET> targetClass = typeResolver.resolveType(source);

		return createTarget(targetClass);
	}

	protected TARGET createTarget(final Class<? extends TARGET> targetClass)
	{
		try
		{
			return targetClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new EventTrackingInternalException("Unexpected error occurred.",e);
		}
	}
}
