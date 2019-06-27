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
package de.hybris.platform.apiregistryservices.event.impl;

import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;
import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.event.DynamicProcessEvent;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.utils.EventExportUtils;
import de.hybris.platform.servicelayer.event.EventSender;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.tx.Transaction;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.support.MutableMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.util.List;


/**
 * One of additional event senders
 * Map fired AbstractEvent to existing EventConfigurationModels, wrap it in EventSourceData
 * and send to spring integration channel, default is eventSourceDataChannel.
 */
public class ExportEventSender implements EventSender
{
    private MessageChannel channel;
    private EventConfigurationDao eventConfigurationDao;


    @Override
    public void sendEvent(final AbstractEvent abstractEvent)
    {
        if (!EventExportUtils.isEventExportActive())
        {
            return;
        }

        for (final String blacklisted : EventExportUtils.getBlacklist())
        {
            if (abstractEvent.getClass().getCanonicalName().equalsIgnoreCase(blacklisted)
                  || abstractEvent.getClass().getPackage().getName().equalsIgnoreCase(blacklisted))
            {
                return;
            }
        }

        if (!Transaction.isInCommitOrRollback())
        {
            final List<EventConfigurationModel> configurationModels = getEventConfigurationDao()
                  .findActiveEventConfigsByClass(getEventClass(abstractEvent));

            for (final EventConfigurationModel ecm : configurationModels)
            {
                final EventSourceData data = new EventSourceData();
                data.setEventConfig(ecm);
                data.setEvent(abstractEvent);
                getChannel().send(wrapData(data));
            }
        }
    }

    protected String getEventClass(final AbstractEvent abstractEvent)
    {
        final String eventClass;

        if (abstractEvent instanceof DynamicProcessEvent)
        {
            eventClass = ((DynamicProcessEvent) abstractEvent).getBusinessEvent();
        }
        else
        {
            eventClass = abstractEvent.getClass().getCanonicalName();
        }

        return eventClass;
    }

    protected Message wrapData(final EventSourceData data)
    {
        final MutableMessage message = new MutableMessage(data);
        message.getHeaders().put(MessageHeaders.REPLY_CHANNEL, "errorChannel");
        message.getHeaders().put(MessageHeaders.ERROR_CHANNEL, "errorChannel");
        return message;
    }

    protected MessageChannel getChannel()
    {
        return channel;
    }

    @Required
    public void setChannel(final MessageChannel channel)
    {
        this.channel = channel;
    }

    protected EventConfigurationDao getEventConfigurationDao()
    {
        return eventConfigurationDao;
    }

    @Required
    public void setEventConfigurationDao(final EventConfigurationDao eventConfigurationDao)
    {
        this.eventConfigurationDao = eventConfigurationDao;
    }
}
