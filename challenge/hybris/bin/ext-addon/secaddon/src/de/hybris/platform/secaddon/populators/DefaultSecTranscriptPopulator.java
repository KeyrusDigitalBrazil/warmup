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

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.secaddon.data.*;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.secaddon.constants.SecaddonConstants.MAPPED_CUSTOMER_ID;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.VISIBILITY_PUBLIC;

public class DefaultSecTranscriptPopulator implements Populator<Transcript, TranscriptSec>
{

    private SessionService sessionService;
    private CustomerFacade customerFacade;

    @Override
    public void populate(Transcript transcript, TranscriptSec transcriptSec) throws ConversionException
    {
        transcriptSec.setDescription(transcript.getDescription());
        transcriptSec.setVisibility(VISIBILITY_PUBLIC);
        populateOwner(transcriptSec);
    }

    protected void populateOwner(TranscriptSec transcriptSec)
    {
        OwnerSec owner = new OwnerSec();
        owner.setOwnerId(getOwnerId());
        owner.setEmail(getEmail());
        owner.setDisplayName(getDisplayName());
        owner.setIsCustomer(Boolean.TRUE);
        transcriptSec.setOwner(owner);
    }

    protected String getEmail()
    {
        return customerFacade.getCurrentCustomer().getDisplayUid();
    }

    protected String getDisplayName()
    {
        CustomerData currentCustomer = customerFacade.getCurrentCustomer();
        String firstName = currentCustomer.getFirstName();
        String lastName = currentCustomer.getLastName();
        String displayNameFormat = "%s %s";
        return String.format(displayNameFormat, firstName, lastName);
    }

    protected String getOwnerId()
    {
        String mappedCustomerId = sessionService.getAttribute(MAPPED_CUSTOMER_ID);
        if (StringUtils.isBlank(mappedCustomerId))
        {
            throw new CustomerMappingException();
        }
        return mappedCustomerId;
    }

    @Required
    public void setSessionService(SessionService sessionService)
    {
        this.sessionService = sessionService;
    }

    @Required
    public void setCustomerFacade(CustomerFacade customerFacade)
    {
        this.customerFacade = customerFacade;
    }
}
