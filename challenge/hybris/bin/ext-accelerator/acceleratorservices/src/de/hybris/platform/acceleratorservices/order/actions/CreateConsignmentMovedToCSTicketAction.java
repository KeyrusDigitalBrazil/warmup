/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorservices.order.actions;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import de.hybris.platform.util.localization.Localization;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Creates a ticket for customers services saying that pickup order has not been collected for specific amount of time.
 */
public class CreateConsignmentMovedToCSTicketAction extends AbstractAction<ConsignmentProcessModel>
{
	protected TicketBusinessService ticketBusinessService;

	@Override
	public String execute(final ConsignmentProcessModel process) throws RetryLaterException, Exception
	{
		final String ticketTitle = Localization.getLocalizedString("message.ticket.order.not.collected.title");
		final String ticketMessage = Localization.getLocalizedString("message.ticket.order.not.collected.content", new Object[]
		{ process.getConsignment().getOrder().getCode() });
		final CsTicketModel csTicketModel = createTicket(ticketTitle, ticketMessage, process.getConsignment().getOrder(),
				CsTicketCategory.PROBLEM, CsTicketPriority.HIGH);
		return (csTicketModel == null) ? "NOK" : "OK";
	}

	@Override
	public Set<String> getTransitions()
	{
		return AbstractAction.createTransitions("OK", "NOK");
	}


	protected CsTicketModel createTicket(final String subject, final String description, final AbstractOrderModel order,
			final CsTicketCategory category, final CsTicketPriority priority)
	{
		final CsTicketParameter csTicketParameter = new CsTicketParameter();
		csTicketParameter.setHeadline(subject);
		csTicketParameter.setCategory(category);
		csTicketParameter.setPriority(priority);
		csTicketParameter.setAssociatedTo(order);
		csTicketParameter.setCustomer(order.getUser());
		csTicketParameter.setCreationNotes(description);

		return getTicketBusinessService().createTicket(csTicketParameter);
	}


	protected TicketBusinessService getTicketBusinessService()
	{
		return ticketBusinessService;
	}

	@Required
	public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
	{
		this.ticketBusinessService = ticketBusinessService;
	}
}
