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
package de.hybris.platform.apiregistryservices.services.impl;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.dto.EventExportDeadLetterData;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.EventExportDeadLetterModel;
import de.hybris.platform.apiregistryservices.services.EventDlqService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultEventDlqServiceTest
{

	@InjectMocks
	private EventDlqService eventDlqService = new DefaultEventDlqService();

	@Mock
	private ModelService modelService;

	@Before
	public void setup() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);
		final EventExportDeadLetterModel letter = new EventExportDeadLetterModel();
		doReturn(letter).when(modelService).create(EventExportDeadLetterModel.class);
	}


	@Test
	public void testSendingToDlq()
	{
		final EventExportDeadLetterData data = new EventExportDeadLetterData();
		data.setError("error");
		final Date timestamp = new Date();
		data.setTimestamp(timestamp);
		data.setEventType("testName");
		final DestinationTargetModel dest = new DestinationTargetModel();
		dest.setId("testDestId");
		dest.setDestinationChannel(DestinationChannel.KYMA);

		data.setDestinationTarget(dest);
		data.setPayload("testPayload");

		eventDlqService.sendToQueue(data);

		verify(modelService, times(1)).save(anyObject());
	}
}
