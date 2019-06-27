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
package de.hybris.platform.acceleratorservices.site;

import de.hybris.platform.acceleratorservices.site.strategies.SiteChannelValidationStrategy;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract site event listener providing that provides a method to determine if a SiteChannel should be handled.
 */
public abstract class AbstractAcceleratorSiteEventListener<T extends AbstractEvent> extends AbstractSiteEventListener<T>
{
	private static final Logger LOG = Logger.getLogger(AbstractAcceleratorSiteEventListener.class);

	private SiteChannelValidationStrategy siteChannelValidationStrategy;

	protected abstract SiteChannel getSiteChannelForEvent(final T event);

	@Override
	protected boolean shouldHandleEvent(final T event)
	{
		final SiteChannel siteChannel = getSiteChannelForEvent(event);
		final boolean siteChannelSupported = getSiteChannelValidationStrategy().validateSiteChannel(siteChannel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Handle event [%s] for site channel [%s]: [%s]", event.getClass().getSimpleName(),
					siteChannel.getCode(), Boolean.valueOf(siteChannelSupported)));
		}

		return siteChannelSupported;
	}

	protected SiteChannelValidationStrategy getSiteChannelValidationStrategy()
	{
		return siteChannelValidationStrategy;
	}

	@Required
	public void setSiteChannelValidationStrategy(final SiteChannelValidationStrategy siteChannelValidationStrategy)
	{
		this.siteChannelValidationStrategy = siteChannelValidationStrategy;
	}
}
