/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.tracking.impl;

import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItemKey;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingWriter;

import java.util.Collections;
import java.util.Map.Entry;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;

import org.apache.log4j.Logger;


/**
 * Simple TrackingWriter implementation, that just dumps immediately all history data into the Log file.
 */
public class TrackingToLogWriterImpl implements TrackingWriter
{
	static final String JSON_PARAM_VALUE = "value";
	static final String JSON_PARAM_NAME = "name";
	static final String JSON_PARAMETERS = "params";
	static final String JSON_DATETIME = "dts";
	static final String JSON_EVENT_TYPE = "evt";
	static final String JSON_SESSION_ID = "sid";
	static final String JSON_CONFIG_ID = "cid";

	private static final Logger LOG = Logger.getLogger(TrackingToLogWriterImpl.class);
	private static final TrackingItem EMPTY_ITEM;

	private static JsonProvider jsonProvider;

	static
	{
		EMPTY_ITEM = new TrackingItem();
		EMPTY_ITEM.setParameters(Collections.emptyMap());
		// expensive call, due to reflection and class loading, however the provider itself is considered Thread Safe.
		// so it is sufficient to intstantiate only one instance and share it.
		jsonProvider = JsonProvider.provider();
	}

	@Override
	public void trackingItemCreated(final TrackingItem item)
	{
		if (LOG.isInfoEnabled())
		{
			final String itemCreatedMessage = getItemMessage(item);
			LOG.info(itemCreatedMessage);
		}
	}

	protected String getSessionMessage(final TrackingItemKey key)
	{
		final JsonObjectBuilder builder = jsonProvider.createObjectBuilder();
		itemKeyToJson(key, builder);
		itemToJson(EMPTY_ITEM, builder);
		return builder.build().toString();
	}

	protected String getItemMessage(final TrackingItem item)
	{
		final JsonObjectBuilder builder = jsonProvider.createObjectBuilder();
		itemKeyToJson(item.getTrackingItemKey(), builder);
		itemToJson(item, builder);
		return builder.build().toString();
	}

	protected void itemKeyToJson(final TrackingItemKey key, final JsonObjectBuilder builder)
	{
		builder.add(JSON_SESSION_ID, key.getSessionId());
		builder.add(JSON_CONFIG_ID, key.getConfigId());
		builder.add(JSON_DATETIME, key.getTimestamp().toString());
		builder.add(JSON_EVENT_TYPE, key.getEventType().toString());
	}

	protected void itemToJson(final TrackingItem item, final JsonObjectBuilder builder)
	{
		final JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
		for (final Entry<String, String> parameter : item.getParameters().entrySet())
		{
			arrayBuilder.add(jsonProvider.createObjectBuilder().add(JSON_PARAM_NAME, parameter.getKey()).add(JSON_PARAM_VALUE,
					parameter.getValue()));
		}
		builder.add(JSON_PARAMETERS, arrayBuilder);
	}
}
