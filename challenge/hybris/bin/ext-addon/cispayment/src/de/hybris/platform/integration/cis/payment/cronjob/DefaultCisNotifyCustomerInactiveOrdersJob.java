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
package de.hybris.platform.integration.cis.payment.cronjob;


import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integration.cis.payment.impl.DefaultCisOrderDao;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.util.localization.Localization;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Cronjob that finds all orders that were placed in Review and have been inactive for more than 12 hours and create a
 * ticket on them.
 */
public class DefaultCisNotifyCustomerInactiveOrdersJob extends AbstractJobPerformable
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCisNotifyCustomerInactiveOrdersJob.class);

	private TicketBusinessService ticketBusinessService;
	private DefaultCisOrderDao cisOrderDao;

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		try
		{
			final List<OrderModel> inactiveOrders = getCisOrderDao().findInactiveOrders();
			for (final OrderModel orderModel : inactiveOrders)
			{
				final String ticketTitle = Localization.getLocalizedString("message.ticket.inactiveorderinreview.title");
				final String ticketMessage = Localization.getLocalizedString("message.ticket.inactiveorderinreview.content",
						new Object[]
						{ orderModel.getCode() });
				createTicket(ticketTitle, ticketMessage, orderModel, CsTicketCategory.FRAUD, CsTicketPriority.HIGH);

				orderModel.setStatus(OrderStatus.WAIT_FRAUD_MANUAL_CHECK);
			}
			modelService.saveAll(inactiveOrders);

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred while notifying customers of inactive orders", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	protected CsTicketModel createTicket(final String subject, final String description, final OrderModel order,
			final CsTicketCategory category, final CsTicketPriority priority)
	{
		final CsTicketModel newTicket = modelService.create(CsTicketModel.class);
		newTicket.setHeadline(subject);
		newTicket.setCategory(category);
		newTicket.setPriority(priority);
		newTicket.setOrder(order);
		newTicket.setCustomer(order.getUser());

		final CsCustomerEventModel newTicketEvent = new CsCustomerEventModel();
		newTicketEvent.setText(description);

		return ticketBusinessService.createTicket(newTicket, newTicketEvent);
	}

	public TicketBusinessService getTicketBusinessService()
	{
		return ticketBusinessService;
	}

	@Required
	public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
	{
		this.ticketBusinessService = ticketBusinessService;
	}

	public DefaultCisOrderDao getCisOrderDao()
	{
		return cisOrderDao;
	}

	@Required
	public void setCisOrderDao(final DefaultCisOrderDao cisOrderDao)
	{
		this.cisOrderDao = cisOrderDao;
	}
}
