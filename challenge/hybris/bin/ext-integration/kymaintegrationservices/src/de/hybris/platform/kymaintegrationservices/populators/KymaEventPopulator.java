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

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DATE_FORMAT_PROP;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DEFAULT_DATE_FORMAT;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DEFAULT_VERSION_FORMAT;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.VERSION_FORMAT_PROP;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.populators.AbstractEventPopulator;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.util.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Kyma specific implementation of event populator
 */
public class KymaEventPopulator extends AbstractEventPopulator<EventSourceData, PublishRequestData>
{
	private ObjectMapper jacksonObjectMapper;

	@Override
	public void populate(final EventSourceData eventSourceData, final PublishRequestData publishRequestData)
	{
		publishRequestData.setEventId(UUID.randomUUID().toString());
		publishRequestData.setEventType(eventSourceData.getEventConfig().getExportName());

		final String versionFormat = Config.getString(VERSION_FORMAT_PROP, DEFAULT_VERSION_FORMAT);
		publishRequestData.setEventTypeVersion(String.format(versionFormat, eventSourceData.getEventConfig().getVersion()));

		final Date eventDate = new Date(eventSourceData.getEvent().getTimestamp());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(Config.getString(DATE_FORMAT_PROP, DEFAULT_DATE_FORMAT));
		getJacksonObjectMapper().setDateFormat(dateFormat);
		publishRequestData.setEventTime(dateFormat.format(eventDate));

		final Map mappedEventValues = getValuesFromEvent(eventSourceData.getEvent(), eventSourceData.getEventConfig());
		publishRequestData.setData(mappedEventValues);
	}

	protected ObjectMapper getJacksonObjectMapper()
	{
		return jacksonObjectMapper;
	}

	@Required
	public void setJacksonObjectMapper(final ObjectMapper jacksonObjectMapper)
	{
		this.jacksonObjectMapper = jacksonObjectMapper;
	}
}
