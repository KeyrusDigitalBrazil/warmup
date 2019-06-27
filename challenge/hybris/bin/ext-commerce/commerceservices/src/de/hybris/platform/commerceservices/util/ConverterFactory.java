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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import org.apache.log4j.Logger;


/**
 * @deprecated Since 6.0. Use {@link de.hybris.platform.converters.impl.AbstractPopulatingConverter} directly instead
 */
@Deprecated
public class ConverterFactory<SOURCE, TARGET, P extends Populator>
{
	//Logger for anonymous inner class
	private static final Logger LOG = Logger.getLogger(AbstractPopulatingConverter.class);

	public AbstractPopulatingConverter<SOURCE, TARGET> create(final Class<TARGET> targetClass, final P... populators)
	{
		return new AbstractPopulatingConverter<SOURCE, TARGET>()
		{
			@Override
			protected TARGET createTarget()
			{
				try
				{
					return targetClass.newInstance();
				}
				catch (final InstantiationException | IllegalAccessException e)
				{
					LOG.fatal(e);
				}
				return null;
			}

			@Override
			public void populate(final SOURCE source, final TARGET target)
			{
				for (final Populator populator : populators)
				{
					populator.populate(source, target);
				}
			}
		};
	}
}
