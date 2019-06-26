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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.kymaintegrationservices.dto.JsonPublishRequestData;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;


@IntegrationTest
public class KymaEventJsonPopulatorTest extends ServicelayerTest
{
	private static final String EVENT_TYPE = "testType";
	private static final String EVENT_VERSION = "testVersion";
	private static final String EVENT_ID = "testId";
	private static final String EVENT_DATA = "{\"testField1\":\"value1\"},\"testField2\":\"value2\"}";

	@Resource
	private ModelService modelService;

	@Resource(name = "kymaJsonEventPopulator")
	private KymaEventJsonPopulator kymaEventPopulator;

	@Resource(name = "kymaExportJacksonObjectMapper")
	private ObjectMapper objectMapper;

	private PublishRequestData publishRequestData;

	private Date eventTime = new Date();
	private Map<String, Object> data;
	@Before
	public void setUp() throws Exception
	{
		data = new HashMap<>();
		data.put("testValue1", "00123400");
		data.put("emptyData", "");
		data.put("nullData", null);
		publishRequestData = new PublishRequestData();
		publishRequestData.setEventType(EVENT_TYPE);
		publishRequestData.setEventTypeVersion(EVENT_VERSION);
		publishRequestData.setData(data);
		publishRequestData.setEventId(EVENT_ID);
		publishRequestData.setEventTime(eventTime.toString());
	}

	@Test
	public void populate() throws Exception
	{
		final JsonPublishRequestData jsonPublishRequestData = new JsonPublishRequestData();
		kymaEventPopulator.populate(publishRequestData, jsonPublishRequestData);
		assertEquals(EVENT_ID, jsonPublishRequestData.getEventId());
		assertEquals(EVENT_TYPE, jsonPublishRequestData.getEventType());
		assertEquals(EVENT_VERSION, jsonPublishRequestData.getEventTypeVersion());
		assertEquals(eventTime.toString(), jsonPublishRequestData.getEventTime());
		final String jsonData = objectMapper.writeValueAsString(jsonPublishRequestData.getData());
		assertTrue(jsonData, jsonData.contains("emptyData"));
		assertFalse(jsonData, jsonData.contains("nullData"));
	}

}
