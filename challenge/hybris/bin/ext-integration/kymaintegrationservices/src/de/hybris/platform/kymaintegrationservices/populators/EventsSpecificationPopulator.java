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

import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.kymaintegrationservices.utils.KymaApiExportHelper;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.kymaintegrationservices.dto.EventsSpecificationData;
import de.hybris.platform.kymaintegrationservices.dto.EventsSpecificationSourceData;
import de.hybris.platform.kymaintegrationservices.dto.InfoData;
import de.hybris.platform.kymaintegrationservices.dto.ServiceRegistrationData;
import de.hybris.platform.kymaintegrationservices.dto.SpecData;
import de.hybris.platform.kymaintegrationservices.dto.TopicData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Kyma specific implementation of Populator that populates All Events specification DTO.
 * {@link ServiceRegistrationData}
 */
public class EventsSpecificationPopulator implements Populator<EventsSpecificationSourceData, ServiceRegistrationData>
{
	private static final String DEFAULT_PROVIDER = "SAP Hybris";
	private static final String PROVIDER_PROP = "kymaintegrationservices.kyma-specification-provider";
	private static final String ASYNCAPI_DEFAULT = "1.0.0";
	private static final String ASYNCAPI_PROP = "kymaintegrationservices.kyma-specification-asyncapi";
	private Converter<EventConfigurationModel, TopicData> topicConverter;

	@Override
	public void populate(final EventsSpecificationSourceData source, final ServiceRegistrationData target)
	{
		final ExposedDestinationModel destination = source.getExposedDestination();
		target.setDescription(destination.getEndpoint().getDescription());
		target.setIdentifier(KymaApiExportHelper.getDestinationId(destination));
		target.setName(destination.getEndpoint().getName());
		target.setProvider(Config.getString(PROVIDER_PROP, DEFAULT_PROVIDER));
		final EventsSpecificationData events = new EventsSpecificationData();
		populateEventSpecification(destination, source.getEvents(), events);
		target.setEvents(events);
	}

	protected void populateEventSpecification(final ExposedDestinationModel destinationModel,
			final List<EventConfigurationModel> eventsList, final EventsSpecificationData target)
	{
		final Map<String, TopicData> topics = new HashMap<>();
		populateTopics(eventsList, topics);

		final InfoData info = new InfoData();
		populateInfo(destinationModel, info);

		final SpecData spec = new SpecData();
		spec.setAsyncapi(Config.getString(ASYNCAPI_PROP, ASYNCAPI_DEFAULT));
		spec.setTopics(topics);
		spec.setInfo(info);

		target.setSpec(spec);
	}

	protected void populateTopics(final List<EventConfigurationModel> eventList, final Map<String, TopicData> target)
	{
		eventList
				.forEach(event -> target.put(String.format("%s.v%d", event.getExportName(), event.getVersion()), getTopicConverter().convert(event)));
	}

	protected void populateInfo(final ExposedDestinationModel source, final InfoData target)
	{
		target.setVersion(source.getEndpoint().getVersion());
		target.setDescription(source.getEndpoint().getDescription());
		target.setTitle(source.getId());
	}

	protected Converter<EventConfigurationModel, TopicData> getTopicConverter()
	{
		return topicConverter;
	}

	@Required
	public void setTopicConverter(final Converter<EventConfigurationModel, TopicData> topicConverter)
	{
		this.topicConverter = topicConverter;
	}
}
