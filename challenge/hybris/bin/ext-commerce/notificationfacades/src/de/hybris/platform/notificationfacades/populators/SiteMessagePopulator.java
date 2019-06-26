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
 package de.hybris.platform.notificationfacades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.notificationfacades.data.SiteMessageData;
import de.hybris.platform.notificationfacades.url.SiteMessageUrlResolver;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator to populate site message data attributes.
 */
public class SiteMessagePopulator implements Populator<SiteMessageForCustomerModel, SiteMessageData>
{

	private Map<NotificationType, SiteMessageUrlResolver> siteMessageUrlResolvers;

	@Override
	public void populate(final SiteMessageForCustomerModel source, final SiteMessageData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		Assert.notNull(source.getMessage(), "Parameter source message cannot be null.");

		final SiteMessageModel message = source.getMessage();
		target.setTitle(message.getTitle());
		target.setContent(message.getContent());
		target.setSentDate(source.getSentDate());
		target.setNotificationType(message.getNotificationType());
		target.setLink(getMessageLink(message));
	}

	protected String getMessageLink(final SiteMessageModel message)
	{
		final SiteMessageUrlResolver urlResolver = getSiteMessageUrlResolvers().get(message.getNotificationType());
		if (urlResolver == null)
		{
			return StringUtils.EMPTY;
		}
		return urlResolver.resolve(message.getExternalItem());
	}

	protected Map<NotificationType, SiteMessageUrlResolver> getSiteMessageUrlResolvers()
	{
		return siteMessageUrlResolvers;
	}

	@Required
	public void setSiteMessageUrlResolvers(final Map<NotificationType, SiteMessageUrlResolver> siteMessageUrlResolvers)
	{
		this.siteMessageUrlResolvers = siteMessageUrlResolvers;
	}

}
