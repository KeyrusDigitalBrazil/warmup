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
package de.hybris.platform.stocknotificationservices.sitemsg.processor.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.notificationservices.processor.Processor;
import de.hybris.platform.notificationservices.strategies.SendSiteMessageStrategy;
import de.hybris.platform.stocknotificationservices.constants.StocknotificationservicesConstants;
import static java.util.Objects.nonNull;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Processor to send back-in-stock notification via Site Message.
 */
public class StockNotificationSiteMessageProcessor implements Processor
{

	private static final Logger LOG = Logger.getLogger(StockNotificationSiteMessageProcessor.class);

	private Map<SiteMessageType, SendSiteMessageStrategy> sendSiteMessageStrategies;

	@Override
	public void process(final CustomerModel customer, final Map<String, ? extends ItemModel> dataMap)
	{
		final SiteMessageModel message = (SiteMessageModel) dataMap.get(StocknotificationservicesConstants.SITE_MESSAGE);

		sendMessage(customer, message);
		LOG.info("Send site message(uid = " + message.getUid() + ")[BACK_IN_STOCK] finished");
	}

	protected void sendMessage(final CustomerModel customer, final SiteMessageModel message)
	{
		final SendSiteMessageStrategy strategy = getSendSiteMessageStrategies().get(message.getType());
		if (nonNull(strategy))
		{
			strategy.sendMessage(customer, message);
		}
		else
		{
			LOG.warn("No SendSiteMessageStrategy found, message[uid=" + message.getUid() + ", type=" + message.getType()
					+ ", notificationType=" + message.getNotificationType() + "] won't be sent.");
		}

	}

	protected Map<SiteMessageType, SendSiteMessageStrategy> getSendSiteMessageStrategies()
	{
		return sendSiteMessageStrategies;
	}

	@Required
	public void setSendSiteMessageStrategies(final Map<SiteMessageType, SendSiteMessageStrategy> sendSiteMessageStrategies)
	{
		this.sendSiteMessageStrategies = sendSiteMessageStrategies;
	}

}
