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
package de.hybris.platform.secaddon.facades;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.secaddon.data.*;

import java.util.List;

import com.hybris.charon.RawResponse;


/**
 * Facade for our TicketServiceClient
 */
public interface TicketServiceFacade
{
	/**
	 * Requesting for all tickets of a customer.
	 *
	 * @param ticketId id of ticket to be returned
	 * @return TicketData with provided ticketId
	 */
	TicketData getTicketDetails(String ticketId);

	/**
	 * Requesting tickets for a specific page.
	 *
	 * @param pageableData the pageable data
	 * @return SearchPageData for tickets
	 */
	SearchPageData<TicketData> getTickets(PageableData pageableData);

	/**
	 * Requesting for all tickets of a customer.
	 *
	 * @param ticketData ticket to be created
	 * @return RawResponse
	 */
	RawResponse createTicket(TicketData ticketData);

	/**
	 * Adds a message to a ticket.
	 *
	 * @param ticketId ticket id
	 * @param transcript message to be added
	 * @return RawResponse
	 */
	RawResponse addMessage(String ticketId,Transcript transcript);

	List<TicketType> getTicketTypes();
	
	List<TicketPriority> getTicketPriorities();
}
