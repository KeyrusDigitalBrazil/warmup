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
package de.hybris.platform.customerticketingfacades;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;


/**
 * interface that holds the operations for ticketing system
 */
public interface TicketFacade
{
	/**
	 * Creates ticket.
	 *
	 * @param ticketData TicketData
	 * @return TicketData or null
	 */
	@Nonnull
	TicketData createTicket(TicketData ticketData);

	/**
	 * Updates ticket
	 *
	 * @param ticketData TicketData
	 * @return TicketData
	 */
	@Nonnull
	TicketData updateTicket(TicketData ticketData);

	/**
	 * Requesting for a ticket of a customer.
	 *
	 * @param ticketId ticket id
	 * @return TicketData or null
	 */
	TicketData getTicket(String ticketId);

	/**
	 * Requesting for all tickets of a customer.
	 *
	 * @param pageableData pageable data
	 * @return not-null list
	 */
	@Nonnull
	SearchPageData<TicketData> getTickets(final PageableData pageableData);

	/**
	 * Search in cart and orders any data for association for ticket.
	 *
	 * @return non-null map, key - special code describes carts/orders are presented in value-list
	 */
	Map<String, List<TicketAssociatedData>> getAssociatedToObjects();

	/**
	 * @return a map with available ticket categories in string representation for the customer ticket creation as a key
	 *         and it's localizations as a value.
	 */
	List<TicketCategory> getTicketCategories();

}
