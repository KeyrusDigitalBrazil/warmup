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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.tracking.EventType;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItemKey;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class TrackingToLogWriterImplTest
{

	private static final Logger LOG = Logger.getLogger(TrackingToLogWriterImplTest.class);

	private TrackingToLogWriterImpl classUnderTest;
	private TrackingItemKey key;
	private TrackingItem item;


	@Before
	public void setUp()
	{
		LOG.setLevel(Level.DEBUG);
		classUnderTest = new TrackingToLogWriterImpl();

		key = new TrackingItemKey();
		key.setConfigId("configId123");
		key.setEventType(EventType.CREATE_CONFIGURATION);
		key.setSessionId("sessionId456");
		key.setTimestamp(LocalDateTime.now());

		item = new TrackingItem();
		item.setTrackingItemKey(key);
		final Map<String, String> parameters = new HashMap<>();
		item.setParameters(parameters);
		parameters.put("name1", "value1");
		parameters.put("nameWith \",{[", "valueWith \",]}");
	}

	@Test
	public void testGetSessionCreatedMessage()
	{
		final String message = classUnderTest.getSessionMessage(key);

		LOG.debug(message);

		final JsonObject object = messageToJsonObject(message);
		assertEquals(key.getConfigId(), object.getString(TrackingToLogWriterImpl.JSON_CONFIG_ID));
		assertEquals(key.getSessionId(), object.getString(TrackingToLogWriterImpl.JSON_SESSION_ID));
		assertEquals(key.getEventType(), EventType.valueOf(object.getString(TrackingToLogWriterImpl.JSON_EVENT_TYPE)));
		assertEquals(key.getTimestamp(), LocalDateTime.parse(object.getString(TrackingToLogWriterImpl.JSON_DATETIME)));
		assertEquals(0, object.getJsonArray(TrackingToLogWriterImpl.JSON_PARAMETERS).size());
	}

	@Test
	public void testGetItemMessage()
	{
		final String message = classUnderTest.getItemMessage(item);

		LOG.debug(message);

		final JsonObject object = messageToJsonObject(message);
		assertEquals(key.getConfigId(), object.getString(TrackingToLogWriterImpl.JSON_CONFIG_ID));
		assertEquals(key.getSessionId(), object.getString(TrackingToLogWriterImpl.JSON_SESSION_ID));
		assertEquals(key.getEventType(), EventType.valueOf(object.getString(TrackingToLogWriterImpl.JSON_EVENT_TYPE)));
		assertEquals(key.getTimestamp(), LocalDateTime.parse(object.getString(TrackingToLogWriterImpl.JSON_DATETIME)));
		final JsonArray paramJsonArray = object.getJsonArray(TrackingToLogWriterImpl.JSON_PARAMETERS);
		assertEquals(2, paramJsonArray.size());
		int index = 0;
		for (final Entry<String, String> entry : item.getParameters().entrySet())
		{
			assertEquals(entry.getKey(), paramJsonArray.getJsonObject(index).getString(TrackingToLogWriterImpl.JSON_PARAM_NAME));
			assertEquals(entry.getValue(), paramJsonArray.getJsonObject(index).getString(TrackingToLogWriterImpl.JSON_PARAM_VALUE));
			index++;
		}
	}

	protected JsonObject messageToJsonObject(final String message)
	{
		final JsonReader jsonReader = Json.createReader(new StringReader(message));
		final JsonObject object = jsonReader.readObject();
		jsonReader.close();
		return object;
	}
}
