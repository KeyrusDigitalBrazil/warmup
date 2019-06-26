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

import de.hybris.platform.comments.model.CommentAttachmentModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketEventAttachmentData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.util.localization.Localization;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converter implementation for {@link de.hybris.platform.ticket.events.model.CsTicketEventModel} as source and
 * {@link de.hybris.platform.customerticketingfacades.data.TicketEventData} as target type.
 */

public class DefaultTicketEventPopulator<SOURCE extends CsTicketEventModel, TARGET extends TicketEventData>
		implements Populator<SOURCE, TARGET>
{
	private Map<String, StatusData> statusMapping;

	@SuppressWarnings("deprecation")
	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy hh:mm a");
		final StringBuilder textBuilder = new StringBuilder();
		final Set<CsTicketChangeEventEntryModel> entries = source.getEntries();
		if (StringUtils.isNotEmpty(source.getText()))
		{
			target.setStartDateTime(source.getCreationtime());
			target.setText(source.getText());

			if (source.getAuthor() != null && source.getAuthor() instanceof EmployeeModel)
			{
				target.setAddedByAgent(Boolean.TRUE);
				textBuilder.append(Localization.getLocalizedString("text.supporttickets.history.customer.service"));
			}
			else
			{
				final UserModel customer = source.getTicket().getCustomer();
				target.setAuthor(customer.getName());
				textBuilder.append(customer.getName());
			}

			getAttachments(source, target);

			textBuilder.append(' ').append(Localization.getLocalizedString("text.supporttickets.history.on")).append(' ');
			textBuilder.append(format.format(target.getStartDateTime())).append("\n").append(target.getText());
			target.setDisplayText(textBuilder.toString());
		}

		setModifiedFields(target, entries);
	}

	/**
	 * @param target
	 * @param entries
	 */
	protected void setModifiedFields(final TARGET target, final Set<CsTicketChangeEventEntryModel> entries)
	{
		for (final CsTicketChangeEventEntryModel e : entries)
		{
			final Map<String, List<StatusData>> modifiedFields = new HashMap<String, List<StatusData>>();
			if (CsTicketModel.STATE.equals(e.getAlteredAttribute().getQualifier()))
			{
				final List<StatusData> modifiedAttributeValues = new ArrayList<StatusData>(2);
				modifiedAttributeValues.add(getStatusMapping().get(e.getOldStringValue()));
				modifiedAttributeValues.add(getStatusMapping().get(e.getNewStringValue()));
				modifiedFields.put(e.getAlteredAttribute().getName(), modifiedAttributeValues);
				target.setModifiedFields(modifiedFields);
			}
		}
	}

	/**
	 * @param source
	 * @param target
	 */
	protected void getAttachments(final SOURCE source, final TARGET target)
	{
		if (CollectionUtils.isNotEmpty(source.getAttachments()))
		{
			final List<TicketEventAttachmentData> attachmentDataList = new ArrayList<>();
			for (final CommentAttachmentModel attachmentModel : source.getAttachments())
			{
				final MediaModel attachment = (MediaModel) attachmentModel.getItem();
				final TicketEventAttachmentData attachmentData = new TicketEventAttachmentData();
				attachmentData.setFilename(attachment.getRealFileName());
				attachmentData.setURL(attachment.getURL());
				attachmentDataList.add(attachmentData);
			}
			target.setAttachments(attachmentDataList);
		}
	}

	/**
	 * @return the statusMapping
	 */
	public Map<String, StatusData> getStatusMapping()
	{
		return statusMapping;
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
}
