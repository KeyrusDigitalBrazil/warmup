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
package de.hybris.platform.kymaintegrationservices.populators;

import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.kymaintegrationservices.dto.PayloadData;
import de.hybris.platform.kymaintegrationservices.dto.PropertyData;
import de.hybris.platform.kymaintegrationservices.dto.SubscribeData;
import de.hybris.platform.kymaintegrationservices.dto.TopicData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Kyma specific implementation of Populator that populates event data into Topic DTO. {@link TopicData}
 */
public class TopicPopulator implements Populator<EventConfigurationModel, TopicData>
{
	@Override
	public void populate(final EventConfigurationModel source, final TopicData target)
	{
		final PayloadData payload = new PayloadData();
		payload.setType("object");

		final List<String> required = new LinkedList<>();

		final Map<String, PropertyData> properties = new HashMap<>();

		source.getEventPropertyConfigurations().forEach(propertyConfiguration -> {
			if (propertyConfiguration.isRequired())
			{
				required.add(propertyConfiguration.getPropertyName());
			}

			final PropertyData property = new PropertyData();
			property.setType(propertyConfiguration.getType());
			property.setDescription(propertyConfiguration.getDescription());
			property.setTitle(propertyConfiguration.getTitle());
			property.setExample(propertyConfiguration.getExamples());
			properties.put(propertyConfiguration.getPropertyName(), property);
		});

		payload.setRequired(required);
		payload.setProperties(properties);

		final SubscribeData subscribe = new SubscribeData();
		subscribe.setSummary(source.getDescription());
		subscribe.setPayload(payload);

		target.setSubscribe(subscribe);
	}
}
