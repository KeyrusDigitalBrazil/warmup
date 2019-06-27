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
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.NotesComparator;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.localization.Localization;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class C4C2YTicketEventsPopulator<SOURCE extends ServiceRequestData, TARGET extends TicketData>
		implements Populator<SOURCE, TARGET>
{

	private CustomerFacade customerFacade;
	private ConfigurationService configurationService;

	private static final String STRING_AGENT = "hybris";

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		if (CollectionUtils.isNotEmpty(source.getNotes()))
		{
			final List<TicketEventData> collected = source.getNotes().stream().filter(n -> n.getParentObjectID() != null)
					.sorted(new NotesComparator()).map(note -> {
						final TicketEventData ticketEventData = new TicketEventData();

						ticketEventData.setStartDateTime(parseDate(note.getCreatedOn()));
						ticketEventData.setText(note.getText());
						final String createdBy = getCreatedBy(note);
						if (getConfigurationService().getConfiguration()
								.getString(Customerticketingc4cintegrationConstants.SUPPORT_TICKET_DISPLAY_NAME, STRING_AGENT)
								.equalsIgnoreCase(note.getCreatedBy()))
						{
							ticketEventData.setAuthor(createdBy);
						}
						else
						{
							ticketEventData.setAddedByAgent(Boolean.TRUE);
						}


						final StringBuilder textBuilder = new StringBuilder(createdBy);
						textBuilder.append(" ").append(Localization.getLocalizedString("text.account.supporttickets.updateTicket.on"))
								.append(" ").append(note.getCreatedOn()).append("\n").append(note.getText());
						ticketEventData.setDisplayText(textBuilder.toString());


						ticketEventData.setDisplayText(textBuilder.toString());

						return ticketEventData;
					}).collect(Collectors.toList());
			target.setTicketEvents(collected);
		}
	}

	protected Date parseDate(final String date)
	{
		if (StringUtils.isEmpty(date))
		{
			return null;
		}
		final Pattern p = Pattern.compile("[0-9]+");
		final Matcher m = p.matcher(date);
		long longDate = 0;
		while (m.find())
		{
			final String dateString = m.group();
			longDate = StringUtils.isNotEmpty(dateString) ? Long.parseLong(dateString) : 0;
		}
		return (longDate != 0) ? new Date(longDate) : null;
	}

	protected String getCreatedBy(final Note note)
	{
		final StringBuilder builder = new StringBuilder();
		return getConfigurationService().getConfiguration()
				.getString(Customerticketingc4cintegrationConstants.SUPPORT_TICKET_DISPLAY_NAME, STRING_AGENT)
				.equalsIgnoreCase(note.getCreatedBy())
						? builder.append(getCustomerFacade().getCurrentCustomer().getFirstName()).append(' ')
								.append(getCustomerFacade().getCurrentCustomer().getLastName()).toString()
						: Localization.getLocalizedString(Customerticketingc4cintegrationConstants.SUPPORT_TICKET_AGENT_NAME);
	}

	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	@Required
	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
