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
package de.hybris.platform.kymaintegrationservices.populators.custom;

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DATE_FORMAT_PROP;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DEFAULT_DATE_FORMAT;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.populators.AbstractEventPopulator;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.util.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;


public class SubmittingOrderEventPopulator extends AbstractEventPopulator<EventSourceData, PublishRequestData>
{
	private ObjectMapper jacksonObjectMapper;

	@Override
	public void populate(final EventSourceData sourceData, final PublishRequestData publishRequestData)
	{
		final Map<String, Object> mappedEventValues = new HashMap<>();
		mappedEventValues.put("orderCode", "00000001");

		publishRequestData.setEventId(UUID.randomUUID().toString());
		publishRequestData.setEventTypeVersion(String.valueOf(sourceData.getEventConfig().getVersion()));
		publishRequestData.setEventType(sourceData.getEventConfig().getExportName());
		final Date eventDate = new Date(sourceData.getEvent().getTimestamp());
		final String dateFormat = Config.getString(DATE_FORMAT_PROP, DEFAULT_DATE_FORMAT);
		publishRequestData.setEventTime(new SimpleDateFormat(dateFormat).format(eventDate));
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
