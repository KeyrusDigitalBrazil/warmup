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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.util.Config;

import de.hybris.platform.util.Utilities;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import com.fasterxml.jackson.databind.ObjectMapper;


@UnitTest
public class SubmittingOrderEventPopulatorTest
{
	private final SubmittingOrderEventPopulator populator = new SubmittingOrderEventPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		populator.setJacksonObjectMapper(new ObjectMapper());

		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();

		Config.setParameter(DATE_FORMAT_PROP, DEFAULT_DATE_FORMAT);
	}

	@Test
	public void populate()
	{
		final PublishRequestData target = new PublishRequestData();
		final EventSourceData source = new EventSourceData();
		source.setEvent(new AfterInitializationEndEvent());
		final EventConfigurationModel model = mock(EventConfigurationModel.class);
		Mockito.when(model.getVersion()).thenReturn(1);
		Mockito.when(model.getExportName()).thenReturn("export");
		source.setEventConfig(model);
		populator.populate(source, target);

		assertTrue(target.getData() != null);
		assertTrue("no orderCode key in data", target.getData().toString().contains("orderCode"));
		assertTrue("no orderCode value in data", target.getData().toString().contains("00000001"));
		assertTrue(target.getEventType().contains("export"));
		assertTrue(target.getEventTypeVersion().contains("1"));
	}

}
