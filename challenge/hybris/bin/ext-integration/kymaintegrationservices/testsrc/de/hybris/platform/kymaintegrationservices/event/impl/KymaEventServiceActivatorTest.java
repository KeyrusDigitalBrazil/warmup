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
package de.hybris.platform.kymaintegrationservices.event.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.enums.EventMappingType;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.strategies.EventEmitStrategy;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.integration.support.MutableMessage;
import org.springframework.messaging.Message;


@UnitTest
public class KymaEventServiceActivatorTest
{
	private final KymaEventServiceActivator activator = new KymaEventServiceActivator();
	@Mock
	private EventEmitStrategy eventEmitStrategy;
	@Mock
	private Converter<EventSourceData, PublishRequestData> converter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.doNothing().when(eventEmitStrategy).sendEvent(any());
		when(converter.convert(any())).thenReturn(new PublishRequestData());
		activator.setEventEmitStrategy(eventEmitStrategy);
		activator.setKymaEventConverter(converter);
	}

	@Test
	public void publishRequestDataTest()
	{
		final Message message = new MutableMessage(new PublishRequestData());
		activator.handle(message);
		Mockito.verify(eventEmitStrategy).sendEvent(message);
	}

	@Test
	public void eventSourceDataTest()
	{
		final EventConfigurationModel configuration = mock(EventConfigurationModel.class);
		when(configuration.getMappingType()).thenReturn(EventMappingType.GENERIC);

		final EventSourceData data = new EventSourceData();
		data.setEventConfig(configuration);
		activator.handle(new MutableMessage(data));

		Mockito.verify(converter).convert(any());
		Mockito.verify(eventEmitStrategy).sendEvent(any());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unsupportedTest()
	{
		final Message message = new MutableMessage("Test");
		activator.handle(message);
	}
}
