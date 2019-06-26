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
package de.hybris.platform.customerticketingfacades.customerticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.jalo.AbstractTicketsystemTest;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for the Customer Ticket Facade
 *
 */
public class CustomerTicketingFacadeIntegrationTest extends AbstractTicketsystemTest
{
	private static final String SUBJECT = "Ticket subject";
	private static final String NOTE = "Hello";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "statusMapping")
	private Map<String, StatusData> statusMapping;

	@Resource(name = "validTransitions")
	private Map<StatusData, List<StatusData>> validTransitions;

	@Resource(name = "ticket_open")
	private StatusData open;

	@Resource(name = "defaultTicketFacade")
	private TicketFacade ticketFacade;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		importCsv("/customerticketingfacades/test/testCustomerTicketing.impex", "UTF-8");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSite, true);

		userService.setCurrentUser(testUser);
	}

	@Test
	public void testCreateTicket()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(NOTE);
		ticketData.setTicketCategory(TicketCategory.ENQUIRY);
		ticketData.setCustomerId(testUser.getUid());
		ticketData.setStatus(open);

		final TicketData ticketData1 = ticketFacade.createTicket(ticketData);
		assertNotNull(ticketData1.getId());

		assertEquals(ticketData1.getStatus().getId(), open.getId());
		assertEquals(ticketData1.getSubject(), SUBJECT);

		final TicketData ticket = ticketFacade.getTicket(ticketData1.getId());

		assertNotNull(ticket);
		assertEquals(ticket.getSubject(), SUBJECT);
		if (ticket.getTicketEvents() == null || ticket.getTicketEvents().isEmpty())
		{
			assertTrue(ticket.getMessageHistory().contains(NOTE));
		}
		else
		{
			assertTrue(ticket.getTicketEvents().get(0).getText().contains(NOTE));
		}
	}

	@Test
	public void testGetTicketsForCustomerOrderByModifiedTime()
	{
		final TicketData ticketDataOne = new TicketData();
		ticketDataOne.setSubject(SUBJECT);
		ticketDataOne.setMessage(NOTE);
		ticketDataOne.setTicketCategory(TicketCategory.COMPLAINT);
		ticketDataOne.setCustomerId(testUser.getUid());

		final TicketData ticketData1 = ticketFacade.createTicket(ticketDataOne);
		assertNotNull(ticketData1.getId());


		final TicketData ticketDataTwo = new TicketData();
		ticketDataTwo.setSubject(SUBJECT);
		ticketDataTwo.setMessage(NOTE);
		ticketDataTwo.setTicketCategory(TicketCategory.ENQUIRY);
		ticketDataTwo.setCustomerId(testUser.getUid());

		final TicketData ticketData2 = ticketFacade.createTicket(ticketDataTwo);
		assertNotNull(ticketData2.getId());

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(5);
		pageableData.setCurrentPage(0);
		pageableData.setSort("LastChangeDateTime");

		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);

		// first one must be after second one and so on. So latest on bottom, newest on top
		assertTrue(
				tickets.getResults().get(0).getLastModificationDate().after(tickets.getResults().get(1).getLastModificationDate()));
	}
}
