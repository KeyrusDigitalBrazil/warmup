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

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DEFAULT_VERSION_FORMAT;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.VERSION_FORMAT_PROP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class KymaEventPopulatorTest extends ServicelayerTest
{
	private static final String ORDER_CODE_KEY = "orderCode";
	private static final String ORDER_TOTAL_PRICE_KEY = "orderTotalPrice";
	private static final String ORDER_CODE = "Test orderCode";
	private static final Double ORDER_TOTAL_PRICE = 123.4;

	@Resource
	private ModelService modelService;

	@Resource(name = "kymaEventPopulator")
	private KymaEventPopulator kymaEventPopulator;

	private EventSourceData eventSourceData;

	@Before
	public void setUp() throws Exception
	{
		final EventConfigurationModel configuration = modelService.create(EventConfigurationModel.class);
		configuration.setEventClass(SubmitOrderEvent.class.getCanonicalName());
		configuration.setVersion(1);
		configuration.setExportName("ECSubmitOrderEvent");
		configuration.setEventPropertyConfigurations(Arrays.asList(buildEventPCM("event.order.code", ORDER_CODE_KEY),
				buildEventPCM("event.order.totalPrice", ORDER_TOTAL_PRICE_KEY)));

		final OrderModel order = modelService.create(OrderModel.class);
		order.setCode(ORDER_CODE);
		order.setTotalPrice(ORDER_TOTAL_PRICE);


		final SubmitOrderEvent event = new SubmitOrderEvent();
		event.setOrder(order);

		eventSourceData = new EventSourceData();
		eventSourceData.setEventConfig(configuration);
		eventSourceData.setEvent(event);
	}

	@Test
	public void populate() throws Exception
	{
		final PublishRequestData publishRequestData = new PublishRequestData();

		kymaEventPopulator.populate(eventSourceData, publishRequestData);

		assertTrue(StringUtils.isNotBlank(publishRequestData.getEventId()));

		final String versionFormat = Config.getString(VERSION_FORMAT_PROP, DEFAULT_VERSION_FORMAT);
		final String expectedEventVersion = String.format(versionFormat, eventSourceData.getEventConfig().getVersion());
		assertTrue(publishRequestData.getEventTypeVersion().equals(expectedEventVersion));
		assertEquals(publishRequestData.getEventType(), eventSourceData.getEventConfig().getExportName());
	}

	protected EventPropertyConfigurationModel buildEventPCM(final String mapping, final String name)
	{
		final EventPropertyConfigurationModel eventPCM = new EventPropertyConfigurationModel();
		eventPCM.setPropertyMapping(mapping);
		eventPCM.setPropertyName(name);
		eventPCM.setType("string");
		eventPCM.setTitle("test");
		return eventPCM;
	}
}
