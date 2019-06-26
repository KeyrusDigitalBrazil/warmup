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
package de.hybris.platform.assistedservicestorefront.customer360.populators;

import de.hybris.platform.assistedservicefacades.util.AssistedServiceUtils;
import de.hybris.platform.assistedservicestorefront.customer360.GeneralActivityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.TICKET_TEXT;


/**
 * CustomerTicketModel -> GeenralActivityTicket populator
 *
 */
public class GeneralActivityTicketPopulator implements Populator<CsTicketModel, GeneralActivityData>
{
	private BaseSiteService baseSiteService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CsTicketModel ticketModel, final GeneralActivityData ticketData)
	{
		ticketData.setType(TICKET_TEXT);
		ticketData.setId(ticketModel.getTicketID());
		ticketData.setStatus(ticketModel.getState().getCode());
		ticketData.setCreated(ticketModel.getCreationtime());
		ticketData.setUpdated(ticketModel.getModifiedtime());
		ticketData.setDescription(ticketModel.getHeadline());
		ticketData.setUrl(AssistedServiceUtils.populateTicketUrl(ticketModel, getBaseSiteService().getCurrentBaseSite()));
		ticketData.setCategory(ticketModel.getCategory() == null ? "---" : ticketModel.getCategory().toString());

	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
