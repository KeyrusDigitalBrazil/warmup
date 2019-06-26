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
package de.hybris.platform.secaddon.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.secaddon.data.TicketData;
import de.hybris.platform.secaddon.data.TicketSecData;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


import static de.hybris.platform.secaddon.constants.SecaddonConstants.MAPPED_CUSTOMER_ID;

public class DefaultSecTicketPopulator implements Populator<TicketData, TicketSecData>
{

    private SessionService sessionService;

    @Override
    public void populate(TicketData ticketData, TicketSecData ticketSecData) throws ConversionException
    {
        String customerMappedId = getCustomerMappedId();
        ticketSecData.setCreatedBy(customerMappedId);
        ticketSecData.setCustomerId(customerMappedId);
        ticketSecData.setMetadata(ticketData.getMetadata());
        ticketSecData.setPriority(ticketData.getPriority());
        ticketSecData.setPriorityDescription(ticketData.getPriorityDescription());
        ticketSecData.setShortDescription(ticketData.getShortDescription());
        ticketSecData.setStatus(ticketData.getStatus());
        ticketSecData.setStatusDescription(ticketData.getStatusDescription());
        ticketSecData.setType(ticketData.getType());
        ticketSecData.setTypeDescription(ticketData.getTypeDescription());
    }

    protected String getCustomerMappedId()
    {
        String mappedCustomerId = sessionService.getAttribute(MAPPED_CUSTOMER_ID);
        if (StringUtils.isBlank(mappedCustomerId))
        {
            throw new CustomerMappingException();
        }
        return sessionService.getAttribute(MAPPED_CUSTOMER_ID);
    }

    @Required
    public void setSessionService(SessionService sessionService)
    {
        this.sessionService = sessionService;
    }

}
