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
package de.hybris.platform.ticket.interceptors;


import static de.hybris.platform.testframework.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CsCustomerEventPrepareInterceptorTest extends ServicelayerTest
{
	public static final String HEADLINE = "headline";
	public static final String TEXT = "text";
	public static final String ID = "id";
	public static final CsTicketCategory ENQUIRY = CsTicketCategory.ENQUIRY;
	public static final CsTicketPriority HIGH = CsTicketPriority.HIGH;

	@Resource
	private TicketBusinessService ticketBusinessService;
	@Resource
	private TicketService ticketService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/testCsCustomerEvent.impex", "utf-8");
	}

	@Test
	public void shouldPopulateCsCustomerEvent()
	{
		//given
		//when
		final CsTicketModel ticket = ticketBusinessService.createTicket(createCsTicketParameter());
		final CsCustomerEventModel csCustomerEvent = (CsCustomerEventModel) (ticketService.getEventsForTicket(ticket).get(0));
		//then
		assertEquals(HEADLINE, csCustomerEvent.getSubject());
	}

	protected CsTicketParameter createCsTicketParameter()
	{
		final CsTicketParameter ticketParameter = new CsTicketParameter();
		ticketParameter.setPriority(HIGH);
		ticketParameter.setCategory(ENQUIRY);
		ticketParameter.setHeadline(HEADLINE);
		ticketParameter.setInterventionType(CsInterventionType.TICKETMESSAGE);
		ticketParameter.setCreationNotes(TEXT);
		return ticketParameter;
	}

}
