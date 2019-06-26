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
package de.hybris.platform.customerticketingfacades.converters.populators;


import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class DefaultTicketEventPopulatorTest extends ServicelayerTest
{

	protected static final String CUSTOMER_NAME = "customer-name";
	protected static final String COMPLETED = "COMPLETED";
	protected static final String OPEN = "OPEN";
	protected static final String INPROCESS = "INPROCESS";

	protected static final String KEY_CLOSED = "Closed";
	protected static final String KEY_NEW = "New";
	protected static final String KEY_OPEN = "Open";

	protected static final String TEXT1 = "TEXT1";

	protected Date today;
	protected Date yesterday;
	protected Map<String, StatusData> statusMap;

	@InjectMocks
	private DefaultTicketEventPopulator ticketEventpopulator;

	@Mock
	protected CsTicketEventModel csTicketEventOneModel;

	@Mock
	protected CsTicketModel csTicketModel;

	@Mock
	protected CustomerModel customerModel;

	@Before
	public void setup()
	{
		ticketEventpopulator = new DefaultTicketEventPopulator();
		MockitoAnnotations.initMocks(this);
		ticketEventpopulator.setStatusMapping(buildStatusMap());

		Mockito.when(csTicketEventOneModel.getTicket()).thenReturn(csTicketModel);
		Mockito.when(csTicketModel.getCustomer()).thenReturn(customerModel);
		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);
	}

	/**
	 * This test should populate ticket model event entries.
	 */
	@Test
	public void shouldPopulateEventEntries()
	{
		final Date date = new Date();
		Mockito.when(csTicketEventOneModel.getText()).thenReturn(TEXT1);
		Mockito.when(csTicketEventOneModel.getCreationtime()).thenReturn(today);

		final CsTicketChangeEventEntryModel entryOne = Mockito.mock(CsTicketChangeEventEntryModel.class);
		final AttributeDescriptorModel attributeDescriptorOne = Mockito.mock(AttributeDescriptorModel.class);
		Mockito.when(entryOne.getAlteredAttribute()).thenReturn(attributeDescriptorOne);
		Mockito.when(attributeDescriptorOne.getQualifier()).thenReturn(CsTicketModel.STATE);
		Mockito.when(attributeDescriptorOne.getName()).thenReturn(COMPLETED);
		Mockito.when(entryOne.getOldStringValue()).thenReturn(KEY_NEW);
		Mockito.when(entryOne.getNewStringValue()).thenReturn(KEY_CLOSED);
		Mockito.when(csTicketEventOneModel.getCreationtime()).thenReturn(date);

		final CsTicketChangeEventEntryModel entryTwo = Mockito.mock(CsTicketChangeEventEntryModel.class);
		final AttributeDescriptorModel attributeDescriptorTwo = Mockito.mock(AttributeDescriptorModel.class);
		Mockito.when(entryTwo.getAlteredAttribute()).thenReturn(attributeDescriptorTwo);
		Mockito.when(attributeDescriptorTwo.getQualifier()).thenReturn(CsTicketModel.STATE);
		Mockito.when(attributeDescriptorTwo.getName()).thenReturn(OPEN);
		Mockito.when(entryTwo.getOldStringValue()).thenReturn(KEY_CLOSED);
		Mockito.when(entryTwo.getNewStringValue()).thenReturn(KEY_OPEN);

		final LinkedHashSet<CsTicketChangeEventEntryModel> entriesSet = Sets.newLinkedHashSet();
		entriesSet.add(entryOne);
		entriesSet.add(entryTwo);

		Mockito.when(csTicketEventOneModel.getEntries()).thenReturn(entriesSet);

		final TicketEventData ticketEventData = new TicketEventData();
		ticketEventpopulator.populate(csTicketEventOneModel, ticketEventData);

		Assert.assertEquals(OPEN, ticketEventData.getModifiedFields().keySet().iterator().next());
		Assert.assertEquals(createStatus(COMPLETED), ticketEventData.getModifiedFields().get(OPEN).get(0));
		Assert.assertEquals(createStatus(INPROCESS), ticketEventData.getModifiedFields().get(OPEN).get(1));
	}

	protected Map<String, StatusData> buildStatusMap()
	{
		final Map<String, StatusData> statusDataMap = Maps.newHashMap();

		final StatusData openStatus = createStatus(OPEN);
		final StatusData inProgressStatus = createStatus(INPROCESS);
		final StatusData completedStatus = createStatus(COMPLETED);

		statusDataMap.put(KEY_NEW, openStatus);
		statusDataMap.put(KEY_OPEN, inProgressStatus);
		statusDataMap.put(KEY_CLOSED, completedStatus);

		return statusDataMap;
	}

	protected StatusData createStatus(final String statusString)
	{
		if (statusMap == null)
		{
			statusMap = Maps.newHashMap();
		}
		if (statusMap.containsKey(statusString))
		{
			return statusMap.get(statusString);
		}

		final StatusData status = new StatusData();
		status.setId(statusString.toUpperCase());

		statusMap.put(statusString, status);

		return status;
	}
}
