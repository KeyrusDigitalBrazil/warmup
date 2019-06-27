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
package de.hybris.platform.assistedservicestorefront.customer360.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicestorefront.customer360.GeneralActivityData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Model provider implementation for Support Tickets fragment.
 */
public class TicketsModelProvider implements FragmentModelProvider<List<GeneralActivityData>>
{
	protected static final int MAX_AMOUNT = 10;
	private UserService userService;
	private TicketService ticketService;
	private Converter<CsTicketModel, GeneralActivityData> ticketConverter;

	@Override
	public List<GeneralActivityData> getModel(final Map<String, String> parameters)
	{

		final CustomerModel user = (CustomerModel) getUserService().getCurrentUser();
		final List<CsTicketModel> ticketModel = getTicketService().getTicketsForCustomer(user);

		return ticketModel.stream().sorted(Comparator.comparing(CsTicketModel::getModifiedtime).reversed())
				.limit(getEventNumberLimit()).map(getTicketConverter()::convert).collect(Collectors.toList());
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected TicketService getTicketService()
	{
		return ticketService;
	}

	@Required
	public void setTicketService(final TicketService ticketService)
	{
		this.ticketService = ticketService;
	}

	/**
	 * @return the ticketConverter
	 */
	public Converter<CsTicketModel, GeneralActivityData> getTicketConverter()
	{
		return ticketConverter;
	}

	/**
	 * @param ticketConverter
	 *           the ticketConverter to set
	 */
	@Required
	public void setTicketConverter(final Converter<CsTicketModel, GeneralActivityData> ticketConverter)
	{
		this.ticketConverter = ticketConverter;
	}

	protected int getEventNumberLimit()
	{
		return MAX_AMOUNT;
	}
}