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

package de.hybris.platform.apiregistryservices.router;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;


@IntegrationTest
public class EventSourceDataRouterTest extends ServicelayerTest
{
	private static final String CHANNEL_NAME = DestinationChannel.KYMA.getCode();

	private EventSourceDataRouter eventSourceDataRouter;

	@Resource
	private ModelService modelService;

	@Resource
	private Map<String, String> eventRoutingMap;

	@Before
	public void setUp()
	{
		eventRoutingMap.put(CHANNEL_NAME, DestinationChannel.KYMA.getCode());
		eventSourceDataRouter = new EventSourceDataRouter();
		eventSourceDataRouter.setEventRoutingMap(eventRoutingMap);
	}

	@Test
	public void testRouteForKymaChannel()
	{
		final EventSourceData eventSourceData = new EventSourceData();
		final EventConfigurationModel eventConfigurationModel = modelService.create(EventConfigurationModel.class);
		final DestinationTargetModel target = new DestinationTargetModel();
		target.setDestinationChannel(DestinationChannel.KYMA);
		target.setId(CHANNEL_NAME);
		eventConfigurationModel.setDestinationTarget(target);
		eventSourceData.setEventConfig(eventConfigurationModel);
		final Message<EventSourceData> message = MessageBuilder.withPayload(eventSourceData).build();

		assertEquals(DestinationChannel.KYMA.getCode(), eventSourceDataRouter.route(message));
	}
}
