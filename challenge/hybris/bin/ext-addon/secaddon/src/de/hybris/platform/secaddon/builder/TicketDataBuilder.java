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
package de.hybris.platform.secaddon.builder;


import de.hybris.platform.secaddon.data.MetaData;
import de.hybris.platform.secaddon.data.TicketData;
import de.hybris.platform.secaddon.data.Transcript;

import java.util.List;

public class TicketDataBuilder
{
    private String id;
    private String ticketNumber;
    private String shortDescription;
    private String priorityDescription;
    private String priority;
    private String statusDescription;
    private String status;
    private String typeDescription;
    private String type;
    private MetaData metadata;
    private List<Transcript> transcript;


    public static TicketDataBuilder TicketDataBuilder()
    {
        return new TicketDataBuilder();
    }

    public TicketDataBuilder withId(String id)
    {
        this.id = id;
        return this;
    }

    public TicketDataBuilder withTicketNumber(String ticketNumber)
    {
        this.ticketNumber = ticketNumber;
        return this;
    }

    public TicketDataBuilder withShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
        return this;
    }

    public TicketDataBuilder withPriorityDescription(String priorityDescription)
    {
        this.priorityDescription = priorityDescription;
        return this;
    }

    public TicketDataBuilder withPriority(String priority)
    {
        this.priority = priority;
        return this;
    }

    public TicketDataBuilder withStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
        return this;
    }

    public TicketDataBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    public TicketDataBuilder withTypeDescription(String typeDescription)
    {
        this.typeDescription = typeDescription;
        return this;
    }

    public TicketDataBuilder withType(String type)
    {
        this.type = type;
        return this;
    }

    public TicketDataBuilder withMetadata(MetaData metadata)
    {
        this.metadata = metadata;
        return this;
    }

    public TicketDataBuilder withTranscript(List<Transcript> transcript)
    {
        this.transcript = transcript;
        return this;
    }

    public TicketData build()
    {
        TicketData ticket = new TicketData();
        ticket.setId(id);
        ticket.setTicketNumber(ticketNumber);
        ticket.setShortDescription(shortDescription);
        ticket.setStatus(status);
        ticket.setPriorityDescription(priorityDescription);
        ticket.setPriority(priority);
        ticket.setStatusDescription(statusDescription);
        ticket.setType(type);
        ticket.setTypeDescription(typeDescription);
        ticket.setMetadata(metadata);
        ticket.setTranscript(transcript);
        return ticket;
    }

}
