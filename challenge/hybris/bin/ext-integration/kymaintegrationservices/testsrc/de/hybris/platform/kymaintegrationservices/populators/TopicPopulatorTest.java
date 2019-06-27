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
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.kymaintegrationservices.dto.PayloadData;
import de.hybris.platform.kymaintegrationservices.dto.PropertyData;
import de.hybris.platform.kymaintegrationservices.dto.TopicData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TopicPopulatorTest
{
	public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
	public static final String TEST_PROPERTY_TITLE = "Property number 1";
	public static final String TEST_PROPERTY_DESCRIPTION = "property1";
	public static final String TEST_PROPERTY_TYPE = "string";
	public static final String TEST_PROPERTY_NAME = "TEST_PROPERTY_NAME";
	private final TopicPopulator populator = new TopicPopulator();
	private final Map<String, String> examples = new HashMap<>();
	@Mock
	private EventConfigurationModel eventConfiguration;
	@Mock
	private EventPropertyConfigurationModel eventProperty1;
	@Mock
	private EventPropertyConfigurationModel eventProperty2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		examples.put(TEST_PROPERTY_NAME, "1234");

		when(eventProperty1.getType()).thenReturn(TEST_PROPERTY_TYPE);
		when(eventProperty1.getDescription()).thenReturn(TEST_PROPERTY_DESCRIPTION);
		when(eventProperty1.getTitle()).thenReturn(TEST_PROPERTY_TITLE);
		when(eventProperty1.getExamples()).thenReturn(examples);
		when(eventProperty1.isRequired()).thenReturn(Boolean.TRUE);
		when(eventProperty1.getPropertyName()).thenReturn(TEST_PROPERTY_NAME);

		when(eventConfiguration.getDescription()).thenReturn(TEST_DESCRIPTION);
		when(eventConfiguration.getEventPropertyConfigurations()).thenReturn(Arrays.asList(eventProperty2, eventProperty1));

	}

	@Test
	public void populateTest()
	{
		final TopicData topic = new TopicData();
		populator.populate(eventConfiguration, topic);
		assertEquals(topic.getSubscribe().getSummary(), TEST_DESCRIPTION);

		final PayloadData payload = topic.getSubscribe().getPayload();
		assertEquals(payload.getType(), "object");
		assertEquals(payload.getRequired().get(0), TEST_PROPERTY_NAME);
		assertEquals(payload.getProperties().size(), 2);

		final PropertyData property = payload.getProperties().get(TEST_PROPERTY_NAME);
		assertEquals(property.getDescription(), TEST_PROPERTY_DESCRIPTION);
		assertEquals(property.getExample(), examples);
		assertEquals(property.getTitle(), TEST_PROPERTY_TITLE);
		assertEquals(property.getType(), TEST_PROPERTY_TYPE);
		assertEquals(property.getRequired(), null);
	}
}
