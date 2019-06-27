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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.secaddon.builder.TicketDataBuilder;
import de.hybris.platform.secaddon.constants.SecaddonConstants;
import de.hybris.platform.secaddon.data.*;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@UnitTest
public class DefaultSecTicketPopulatorTest
{
    @Mock
    private SessionService sessionService;
    @InjectMocks
    DefaultSecTicketPopulator secTicketPopulator = new DefaultSecTicketPopulator();

    private static final String CUSTOMER_MAPPED_ID = "customerMappedId";
    private static final String PRIORITY = "priority";
    private static final String PRIORITY_DESCRIOPTION = "priorityDescrioption";
    private static final String SHORT_DESCRIPTION = "shortDescription";
    private static final String STATUS = "status";
    private static final String STATUS_DESCRIPTION = "statusDescription";
    private static final String TYPE = "type";
    private static final String TYPE_DESCRIPTION = "typeDescription";

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPopulateTicket()
    {
        //given
        TicketData ticket = buildTicketData();
        TicketSecData ticketSec = new TicketSecData();
        given(sessionService.getAttribute(SecaddonConstants.MAPPED_CUSTOMER_ID)).willReturn(CUSTOMER_MAPPED_ID);

        //when
        secTicketPopulator.populate(ticket, ticketSec);

        //then
        assertEquals(CUSTOMER_MAPPED_ID, ticketSec.getCreatedBy());
        assertEquals(CUSTOMER_MAPPED_ID, ticketSec.getCustomerId());
        assertSecTicketData(ticketSec);
    }

    private TicketData buildTicketData()
    {
        return TicketDataBuilder.TicketDataBuilder()
                .withMetadata(new MetaData())
                .withPriority(PRIORITY)
                .withPriorityDescription(PRIORITY_DESCRIOPTION)
                .withShortDescription(SHORT_DESCRIPTION)
                .withStatusDescription(STATUS_DESCRIPTION)
                .withType(TYPE)
                .withTypeDescription(TYPE_DESCRIPTION)
                .withStatus(STATUS)
                .build();
    }

    private void assertSecTicketData(TicketSecData ticketSec)
    {
        assertEquals(PRIORITY, ticketSec.getPriority());
        assertEquals(PRIORITY_DESCRIOPTION, ticketSec.getPriorityDescription());
        assertEquals(SHORT_DESCRIPTION, ticketSec.getShortDescription());
        assertEquals(STATUS, ticketSec.getStatus());
        assertEquals(STATUS_DESCRIPTION, ticketSec.getStatusDescription());
        assertEquals(TYPE, ticketSec.getType());
        assertEquals(TYPE_DESCRIPTION, ticketSec.getTypeDescription());
    }

    @Test(expected = CustomerMappingException.class)
    public void shouldThrowCustomerMappingExceptionWhenNoMappingInSession()
    {
        //given
        TicketData ticket = new TicketData();
        TicketSecData ticketSec = new TicketSecData();

        //when
        secTicketPopulator.populate(ticket, ticketSec);
        fail("should throwCustomerMappingException");
    }

}
