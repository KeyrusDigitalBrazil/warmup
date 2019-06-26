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

package de.hybris.platform.kymaintegrationservices.utils;

import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;

import org.apache.commons.lang.SystemUtils;


/**
 * Helper class for event export
 */
public class KymaEventExportUtils
{
	public static final String EVENTS_SERVICE_ID = "kymaintegrationservices.kyma_events_service_id";
	public static final String MAX_CONSECUTIVE_RETRIES = "kymaintegrationservices.event.task.max.consecutive.retry";
	public static final String MAX_RETRIES = "kymaintegrationservices.event.task.max.retry";
	public static final String EVENT_RETRY_DELAY = "kymaintegrationservices.event.task.retry.delay";

	public static final String VALIDATION_ERROR_KEY = "kymaintegrationservices.event.error.validation";
	public static final String DEFAULT_VALIDATION_ERROR = "validation_violation";

	public static final String VERSION_FORMAT_PROP = "kymaintegrationservices.kyma-event-version-format";
	public static final String DEFAULT_VERSION_FORMAT = "v%d";

	public static final String DATE_FORMAT_PROP = "kymaintegrationservices.kyma-date-format";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private KymaEventExportUtils()
	{
	}

	/**
	 * Utils method to get String representation of publishRequestData
	 *
	 * @param publishRequestData publish event request
	 * @return String representation of the publishRequestData
	 */
	public static String eventPayloadToString(final PublishRequestData publishRequestData)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("getEventId: ").append(publishRequestData.getEventId());
		stringBuilder.append(SystemUtils.LINE_SEPARATOR);
		stringBuilder.append("getEventTypeVersion: ").append(publishRequestData.getEventTypeVersion());
		stringBuilder.append(SystemUtils.LINE_SEPARATOR);
		stringBuilder.append("getEventType: ").append(publishRequestData.getEventType());
		return stringBuilder.toString();
	}
}
