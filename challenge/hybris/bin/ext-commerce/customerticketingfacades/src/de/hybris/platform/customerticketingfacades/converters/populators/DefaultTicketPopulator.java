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
package de.hybris.platform.customerticketingfacades.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.customerticketingfacades.constants.CustomerticketingfacadesConstants;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ticket.comparator.TicketEventsComparator;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.util.localization.Localization;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converter implementation for {@link de.hybris.platform.ticket.model.CsTicketModel} as source and
 * {@link de.hybris.platform.customerticketingfacades.data.TicketData} as target type.
 */
public class DefaultTicketPopulator<SOURCE extends CsTicketModel, TARGET extends TicketData> implements Populator<SOURCE, TARGET>
{
	private static final Logger LOG = Logger.getLogger(DefaultTicketPopulator.class);

	protected static final String LAST_UPDATED = "Updated";

	private Map<String, StatusData> statusMapping;
	private Map<StatusData, List<StatusData>> validTransitions;
	private TicketService ticketService;
	private Converter<CsTicketEventModel, TicketEventData> ticketEventConverter;

	@Override
	public void populate(final CsTicketModel source, final TicketData target)
	{

		target.setSubject(source.getHeadline());
		target.setId(source.getTicketID());
		final CsTicketState csTicketState = source.getState();
		target.setStatus(getStatusMapping().get(csTicketState.getCode()));
		target.setAvailableStatusTransitions(getValidTransitions().get(target.getStatus()));
		target.setCreationDate(source.getCreationtime());
		target.setLastModificationDate(source.getModifiedtime());
		target.setCustomerId(source.getCustomer().getUid());
		target.setTicketEvents(getTicketEvents(source));
		try
		{
			target.setTicketCategory(TicketCategory.valueOf(source.getCategory().getCode().toUpperCase()));
		}
		catch (final IllegalArgumentException ex)
		{
			LOG.info(source.getCategory().getCode().toUpperCase()
					+ " ticket category is not enabled to display for the customer ticketing");
		}
		populateAssociatedTodata(source, target);
	}

	/**
	 * Populates the associates to object data.
	 *
	 * @param source
	 * @param target
	 */
	protected void populateAssociatedTodata(final CsTicketModel source, final TicketData target)
	{
		if (source.getOrder() != null)
		{
			final AbstractOrderModel abstractOrderModel = source.getOrder();
			final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			String itemtype = abstractOrderModel.getItemtype();
			if (CartModel._TYPECODE.equals(abstractOrderModel.getItemtype())
					&& ((CartModel) abstractOrderModel).getSaveTime() != null)
			{
				itemtype = CustomerticketingfacadesConstants.SAVED_CART;
			}

			target.setAssociatedTo(Localization.getLocalizedString("text.account.supporttickets.createTicket." + itemtype) + ": "
					+ abstractOrderModel.getCode() + "; " + LAST_UPDATED + ": " + sdf.format(abstractOrderModel.getModifiedtime()));
		}
	}

	/**
	 * @param source
	 *           as CsTicketModel
	 * @return List<TicketEventData>
	 */
	protected List<TicketEventData> getTicketEvents(final CsTicketModel source)
	{
		final List<CsTicketEventModel> events = new ArrayList<CsTicketEventModel>(
				getTicketService().getTicketEventsForCustomerByTicket(source));
		Collections.sort(events, new TicketEventsComparator());
		return getTicketEventConverter().convertAll(events);
	}

	/**
	 * @return the statusMapping
	 */
	protected Map<String, StatusData> getStatusMapping()
	{
		return statusMapping;
	}

	/**
	 * @return the validTransitions
	 */
	protected Map<StatusData, List<StatusData>> getValidTransitions()
	{
		return validTransitions;
	}

	/**
	 * @return the ticketService
	 */
	protected TicketService getTicketService()
	{
		return ticketService;
	}

	/**
	 * @param statusMapping
	 *           the statusMapping to set
	 */
	@Required
	public void setStatusMapping(final Map<String, StatusData> statusMapping)
	{
		this.statusMapping = statusMapping;
	}

	/**
	 * @param validTransitions
	 *           the validTransitions to set
	 */
	@Required
	public void setValidTransitions(final Map<StatusData, List<StatusData>> validTransitions)
	{
		this.validTransitions = validTransitions;
	}

	/**
	 * @param ticketService
	 *           the ticketService to set
	 */
	@Required
	public void setTicketService(final TicketService ticketService)
	{
		this.ticketService = ticketService;
	}

	protected Converter<CsTicketEventModel, TicketEventData> getTicketEventConverter()
	{
		return ticketEventConverter;
	}

	@Required
	public void setTicketEventConverter(final Converter<CsTicketEventModel, TicketEventData> ticketEventConverter)
	{
		this.ticketEventConverter = ticketEventConverter;
	}
}
