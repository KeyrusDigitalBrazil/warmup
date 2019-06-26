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
package de.hybris.platform.secaddon.services;

import de.hybris.platform.secaddon.data.*;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;

// TODO figure out if it possible to add global header(language)
@OAuth
@Http(value = "ticketServiceClient")
public interface TicketServiceClient
{
	@GET
	@Path("/${tenant}/serviceTickets/{ticketId}")
	Observable<TicketData> getTicketDetails(
		@HeaderParam("hybris-languages") String lang,
		@PathParam("ticketId") String ticketId);

	@GET
	@Path("/${tenant}/serviceTickets")
	Observable<RawResponse<List<TicketData>>> getTickets(
		@HeaderParam("hybris-languages") String lang,
		@QueryParam("sort") String sort,
		@QueryParam("pageNumber") int pageNumber,
		@QueryParam("pageSize") int pageSize,
		@QueryParam("q") String customerId);

	@POST
	@Path("/${tenant}/serviceTickets")
	@Produces(MediaType.APPLICATION_JSON)
	Observable<RawResponse> createTicket(
			@HeaderParam("hybris-languages") String lang,
			TicketSecData ticketData);
	
	@POST
	@Path("/${tenant}/serviceTickets/{ticketId}/conversations")
	@Produces(MediaType.APPLICATION_JSON)
	Observable<RawResponse> addMessage(
			@HeaderParam("hybris-languages") String lang,
			@PathParam("ticketId") String ticketId,
			TranscriptSec transcript);

	@GET
	@Path("/${tenant}/serviceTicketTypes")
	Observable<List<TicketType>> getTicketTypes(@HeaderParam("hybris-languages") String lang);
	
	@GET
	@Path("/${tenant}/serviceTicketPriorities")
	Observable<List<TicketPriority>> getTicketPriorities(@HeaderParam("hybris-languages") String lang);
}
