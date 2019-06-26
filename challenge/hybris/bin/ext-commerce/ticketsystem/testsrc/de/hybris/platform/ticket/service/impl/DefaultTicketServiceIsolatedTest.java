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
package de.hybris.platform.ticket.service.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ticket.constants.TicketsystemConstants;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class DefaultTicketServiceIsolatedTest
{
	@InjectMocks
	private final DefaultTicketService ticketService = new DefaultTicketService();
	@Mock
	private EnumerationService enumerationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnEventReasons()
	{
		// given
		given(enumerationService.getEnumerationValues(TicketsystemConstants.TC.CSEVENTREASON)).willReturn(Collections.emptyList());

		// when
		ticketService.getEventReasons();

		// then
		verify(enumerationService, times(1)).getEnumerationValues(TicketsystemConstants.TC.CSEVENTREASON);
	}

	@Test
	public void shouldReturnInterventionTypes()
	{
		// given
		given(enumerationService.getEnumerationValues(TicketsystemConstants.TC.CSINTERVENTIONTYPE))
				.willReturn(Collections.emptyList());

		// when
		ticketService.getInterventionTypes();

		// then
		verify(enumerationService, times(1)).getEnumerationValues(TicketsystemConstants.TC.CSINTERVENTIONTYPE);
	}

	@Test
	public void shouldReturnResolutionTypes()
	{
		// given
		given(enumerationService.getEnumerationValues(TicketsystemConstants.TC.CSRESOLUTIONTYPE))
				.willReturn(Collections.emptyList());

		// when
		ticketService.getResolutionTypes();

		// then
		verify(enumerationService, times(1)).getEnumerationValues(TicketsystemConstants.TC.CSRESOLUTIONTYPE);
	}

	@Test
	public void shouldReturnTicketCategories()
	{
		// given
		given(enumerationService.getEnumerationValues(TicketsystemConstants.TC.CSTICKETCATEGORY))
				.willReturn(Collections.emptyList());

		// when
		ticketService.getTicketCategories();

		// then
		verify(enumerationService, times(1)).getEnumerationValues(TicketsystemConstants.TC.CSTICKETCATEGORY);
	}

	@Test
	public void shouldReturnTicketPriorities()
	{
		// given
		given(enumerationService.getEnumerationValues(TicketsystemConstants.TC.CSTICKETPRIORITY))
				.willReturn(Collections.emptyList());

		// when
		ticketService.getTicketPriorities();

		// then
		verify(enumerationService, times(1)).getEnumerationValues(TicketsystemConstants.TC.CSTICKETPRIORITY);
	}

	@Test
	public void shouldReturnTicketStates()
	{
		// given
		given(enumerationService.getEnumerationValues(TicketsystemConstants.TC.CSTICKETSTATE)).willReturn(Collections.emptyList());

		// when
		ticketService.getTicketStates();

		// then
		verify(enumerationService, times(1)).getEnumerationValues(TicketsystemConstants.TC.CSTICKETSTATE);
	}
}
