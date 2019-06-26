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
package de.hybris.platform.notificationservices.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationservices.dao.SiteMessageDao;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.formatters.SiteMessageContentFormatter;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.notificationservices.service.SiteMessageService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SiteMessageService}
 */
public class DefaultSiteMessageService implements SiteMessageService
{

	private static final Logger LOG = Logger.getLogger(DefaultSiteMessageService.class);

	private SiteMessageDao siteMessageDao;
	private ModelService modelService;
	private I18NService i18nService;
	private KeyGenerator siteMessageUidGenerator;
	private Map<SiteMessageType, SiteMessageContentFormatter> siteMessageContentFormatters;

	@Override
	public SearchPageData<SiteMessageForCustomerModel> getPaginatedMessagesForType(final CustomerModel customer, final SiteMessageType type,
			final SearchPageData searchPageData)
	{
		return siteMessageDao.findPaginatedMessagesByType(customer, type, searchPageData);
	}

	@Override
	public SearchPageData<SiteMessageForCustomerModel> getPaginatedMessagesForCustomer(final CustomerModel customer,
			final SearchPageData searchPageData)
	{
		return siteMessageDao.findPaginatedMessages(customer, searchPageData);
	}

	@Override
	public SiteMessageModel createMessage(final String title, final String content, final SiteMessageType type,
			final ItemModel externalItem, final NotificationType notificationType, final Locale locale)
	{
		final SiteMessageModel message = getModelService().create(SiteMessageModel.class);
		final Locale locale2 = locale == null ? getI18nService().getCurrentLocale() : locale;

		message.setUid(getSiteMessageUidGenerator().generate().toString());
		message.setTitle(title, locale2);
		message.setContent(formatContent(type, content), locale2);
		message.setType(type);
		message.setExternalItem(externalItem);
		message.setNotificationType(notificationType);
		getModelService().save(message);

		return message;
	}

	@Override
	public List<SiteMessageForCustomerModel> getSiteMessagesForCustomer(final CustomerModel customer)
	{
		return siteMessageDao.findSiteMessagesForCustomer(customer);
	}

	protected String formatContent(final SiteMessageType type, final String content)
	{
		final SiteMessageContentFormatter formatter = getSiteMessageContentFormatters().get(type);
		if (formatter == null)
		{
			LOG.warn("No formatter found for given site message type[" + type + "], will return original message content.");
			return content;
		}
		return formatter.format(content);
	}

	protected SiteMessageDao getSiteMessageDao()
	{
		return siteMessageDao;
	}

	@Required
	public void setSiteMessageDao(final SiteMessageDao siteMessageDao)
	{
		this.siteMessageDao = siteMessageDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected KeyGenerator getSiteMessageUidGenerator()
	{
		return siteMessageUidGenerator;
	}

	@Required
	public void setSiteMessageUidGenerator(final KeyGenerator siteMessageUidGenerator)
	{
		this.siteMessageUidGenerator = siteMessageUidGenerator;
	}

	protected Map<SiteMessageType, SiteMessageContentFormatter> getSiteMessageContentFormatters()
	{
		return siteMessageContentFormatters;
	}

	@Required
	public void setSiteMessageContentFormatters(
			final Map<SiteMessageType, SiteMessageContentFormatter> siteMessageContentFormatters)
	{
		this.siteMessageContentFormatters = siteMessageContentFormatters;
	}

}
