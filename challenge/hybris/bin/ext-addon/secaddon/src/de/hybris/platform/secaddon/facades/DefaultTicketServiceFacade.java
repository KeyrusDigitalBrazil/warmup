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

import com.hybris.charon.exp.HttpException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.secaddon.constants.SecaddonConstants;
import de.hybris.platform.secaddon.data.*;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.secaddon.services.TicketServiceClientAdapter;

import java.util.List;
import java.util.Optional;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.RawResponse;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.MAPPED_CUSTOMER_ID;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.VISIBILITY_PUBLIC;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

/**
 * Implementation for {@link TicketServiceFacade}. Delivers main functionality
 * for TicketServiceClient.
 */
public class DefaultTicketServiceFacade implements TicketServiceFacade {
	private TicketServiceClientAdapter ticketServiceClientAdapter;
	private StoreSessionFacade storeSessionFacade;
	private Converter<TicketData, TicketSecData> secTicketConverter;
	private Converter<Transcript, TranscriptSec> secTranscriptConverter;
	private SessionService sessionService;
	private CustomerFacade customerFacade;
	private static final Logger LOG = Logger.getLogger(DefaultTicketServiceFacade.class);

	@Override
	public TicketData getTicketDetails(final String ticketId) {
		TicketData ticket = getTicket(ticketId);
		ticket.setOwner(getDisplayName());
		filterPublicTranscript(ticket);
		return ticket;
	}

	protected String getDisplayName() {
		CustomerData currentCustomer = customerFacade.getCurrentCustomer();
		String displayNameFormat = "%s %s";
		return String.format(displayNameFormat, currentCustomer.getFirstName(), currentCustomer.getLastName());
	}

	protected TicketData getTicket(String ticketId) {
		final String lang = getCurrentLanguage();
		return getTicketServiceClientAdapter().getTicketDetails(lang, ticketId).toBlocking().first();
	}

	protected void filterPublicTranscript(TicketData ticket) {
		if (ticket.getTranscript() != null) {
			List<Transcript> publicTranscript = ticket.getTranscript().stream()
					.filter(transcript -> VISIBILITY_PUBLIC.equals(transcript.getVisibility())).collect(toList());
			publicTranscript.stream().forEach(transcript -> transcript.getOwner().setDisplayName(getDisplayName()));
			ticket.setTranscript(publicTranscript);
		}
	}

	@Override
	public SearchPageData<TicketData> getTickets(final PageableData pageableData) {
		String mappedCustomerIdQuery = getMappedCustomerIdQuery();
		final String code = getCurrentLanguage();
		final RawResponse<List<TicketData>> response = getTicketServiceClientAdapter()
				.getTickets(code, pageableData.getSort(), pageableData.getCurrentPage(), pageableData.getPageSize(),
						mappedCustomerIdQuery)
				.toBlocking().first();

		final SearchPageData searchPageData = prepareSearchPageData(pageableData, response);

		return searchPageData;
	}

	protected String getMappedCustomerIdQuery() {
		String mappedCustomerId = sessionService.getAttribute(MAPPED_CUSTOMER_ID);
		if (StringUtils.isBlank(mappedCustomerId)) {
			throw new CustomerMappingException();
		}
		return "customerId:\"" + mappedCustomerId + "\"";
	}

	protected SearchPageData<TicketData> prepareSearchPageData(final PageableData pageableData,
			final RawResponse<List<TicketData>> response) {
		final List<TicketData> ticketList = response.content().toBlocking().first();

		final Optional<Integer> totalNumberOfTickets = response.headerInt(SecaddonConstants.HYBRIS_COUNT);

		final SearchPageData<TicketData> searchPageData = new SearchPageData<TicketData>();
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(pageableData.getCurrentPage());
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setTotalNumberOfResults(totalNumberOfTickets.get().intValue());
		paginationData.setSort(pageableData.getSort());

		searchPageData.setPagination(paginationData);
		searchPageData.setResults(ticketList);

		return searchPageData;
	}

	@Override
	public List<TicketType> getTicketTypes() {
		final String code = getCurrentLanguage();
		List<TicketType> ticketTypes = getTicketServiceClientAdapter().getTicketTypes(code).toBlocking().first();

		ticketTypes = ticketTypes.stream().filter(ticket -> ticket.getActive().booleanValue() != false)
				.collect(toList());

		return ticketTypes;
	}

	@Override
	public List<TicketPriority> getTicketPriorities() {
		final String code = getCurrentLanguage();
		List<TicketPriority> ticketPriorities = getTicketServiceClientAdapter().getTicketPriorities(code).toBlocking()
				.first();
		ticketPriorities = ticketPriorities.stream().filter(ticket -> ticket.getActive().booleanValue() != false)
				.collect(toList());

		return ticketPriorities;
	}

	@Override
	public RawResponse createTicket(final TicketData ticketData) {
		try {
			final String code = getCurrentLanguage();
			TicketSecData ticketSecData = secTicketConverter.convert(ticketData);
			return getTicketServiceClientAdapter().createTicket(code, ticketSecData).toBlocking().first();
		} catch (final HttpException err) // NOSONAR
		{
			LOG.error(
					"Exception happened during communicating with YaaS while trying to create a ticket. Error: " + err);
			if (null != err.getServerMessage()) {
				err.getServerMessage()
						.subscribe(message -> LOG.error(
								"Exception happened during communicating with YaaS while trying to create a ticket "
										+ message));
			}
			return null;
		}
	}

	@Override
	public RawResponse addMessage(String ticketId, Transcript transcript) {
		checkArgument(ticketId != null, "Ticket ID must not be null");
		try {
			final String lang = getCurrentLanguage();
			TranscriptSec transcriptSec = secTranscriptConverter.convert(transcript);
			return getTicketServiceClientAdapter().addMessage(lang, ticketId, transcriptSec).toBlocking().single();
		} catch (final HttpException err) // NOSONAR
		{
			LOG.error("Exception happened during communicating with YaaS while trying to add message to a ticket ["
					+ ticketId + "]. Error: " + err);
			if (null != err.getServerMessage()) {
				err.getServerMessage().subscribe(message -> LOG.error(
						"Exception happened during communicating with YaaS while trying to add message to a ticket ["
								+ ticketId + "]. " + message));
			}
			return null;
		}

	}

	protected String getCurrentLanguage() {
		return storeSessionFacade.getCurrentLanguage().getIsocode();
	}

	protected TicketServiceClientAdapter getTicketServiceClientAdapter() {
		return ticketServiceClientAdapter;
	}

	@Required
	public void setTicketServiceClientAdapter(final TicketServiceClientAdapter ticketServiceClientAdapter) {
		this.ticketServiceClientAdapter = ticketServiceClientAdapter;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade) {
		this.storeSessionFacade = storeSessionFacade;
	}

	@Required
	public void setSecTicketConverter(Converter<TicketData, TicketSecData> ticketSecConverter) {
		this.secTicketConverter = ticketSecConverter;
	}

	@Required
	public void setSecTranscriptConverter(Converter<Transcript, TranscriptSec> transcriptSecConverter) {
		this.secTranscriptConverter = transcriptSecConverter;
	}

	@Required
	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public void setCustomerFacade(CustomerFacade customerFacade) {
		this.customerFacade = customerFacade;
	}
}
