/*
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
package de.hybris.platform.apiregistryservices.populators;

import de.hybris.platform.apiregistryservices.event.DynamicProcessEvent;
import de.hybris.platform.apiregistryservices.utils.EventExportUtils;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * Abstract class for events converters. Implements getValuesFromEvent method, which extract event's data into a simple
 * map, mappings are defined in EventConfigurationModel.mappingConfiguration
 */
public abstract class AbstractEventPopulator<S extends EventSourceData, T> implements Populator<S, T>
{
	private static final Logger LOGGER = Logger.getLogger(AbstractEventPopulator.class);

	/**
	 * Method applies mappingConfiguration on event data
	 *
	 * @param event
	 * @param ecModel
	 * @return mapped data
	 */
	protected Map<String, Object> getValuesFromEvent(final AbstractEvent event, final EventConfigurationModel ecModel)
	{
		return ecModel.getEventPropertyConfigurations().stream()
			.map(eventPropertyConfig -> fetchProperty(event, eventPropertyConfig))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private static AbstractMap.SimpleEntry<String, Object> fetchProperty(final AbstractEvent event,
			final EventPropertyConfigurationModel eventPropertyConfiguration)
	{
		final String delimiter = EventExportUtils.getDelimiter();
		Object value = null;
		if (StringUtils.isNotBlank(eventPropertyConfiguration.getPropertyMapping()))
		{
			try
			{
				// removed 'event.'
				final String path = eventPropertyConfiguration.getPropertyMapping().split(delimiter, 2)[1];

				Object result = event;
				if (event instanceof DynamicProcessEvent)
				{
					result = ((DynamicProcessEvent) event).getBusinessProcess();
				}

				for (final String field : path.split(delimiter))
				{
					result = readField(result, field, event);
				}
				value = result;
			}
			catch (IndexOutOfBoundsException e)
			{
				LOGGER.error(String.format("Could not split properties of event: %s",
						eventPropertyConfiguration.getEventConfiguration().getEventClass()), e);
			}
		}
		return new AbstractMap.SimpleEntry<>(eventPropertyConfiguration.getPropertyName(), value);
	}

	/**
	 * that method tries to read the property from the event and its fields
	 *
	 * @param event
	 * @param path
	 * @param abstractEvent
	 * @return
	 */
	private static Object readField(final Object event, final String path, final AbstractEvent abstractEvent)
	{
		try
		{
			return PropertyUtils.getProperty(event, path);
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassCastException e)
		{
			LOGGER.info("Could not read property: " + path + " from class: " + event + " for event: "
					+ abstractEvent.getClass().getCanonicalName(), e);
		}
		return null;
	}
}
