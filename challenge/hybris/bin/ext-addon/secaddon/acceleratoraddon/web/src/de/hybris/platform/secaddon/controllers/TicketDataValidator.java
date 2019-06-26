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
package de.hybris.platform.secaddon.controllers;

import de.hybris.platform.secaddon.data.TicketData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TicketDataValidator implements Validator
{
    @Override
    public boolean supports(final Class<?> aClass)
    {
        return TicketData.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        TicketData ticket = (TicketData) o;
        if (StringUtils.isBlank(ticket.getShortDescription()))
        {
            errors.rejectValue("shortDescription", "ticket.error.shortDesription.invalid");
        } else if (StringUtils.isBlank(ticket.getType()))
        {
            errors.rejectValue("shortDescription", "ticket.error.ticketType.invalid");
        }
    }
}
