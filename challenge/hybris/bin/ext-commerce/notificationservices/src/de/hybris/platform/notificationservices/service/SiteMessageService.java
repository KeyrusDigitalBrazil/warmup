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
package de.hybris.platform.notificationservices.service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;

import java.util.List;
import java.util.Locale;


/**
 * Service to provide methods for site message
 */
public interface SiteMessageService
{

	/**
	 * Gets paginated site messages for the given type
	 *
	 * @param customer
	 *           the specific customer
	 * @param type
	 *           the given message type
	 * @param searchPageData
	 *           pagination parameters
	 * @return paginated result
	 */
	SearchPageData<SiteMessageForCustomerModel> getPaginatedMessagesForType(CustomerModel customer, SiteMessageType type,
			SearchPageData searchPageData);

	/**
	 * Gets all paginated site messages
	 *
	 * @param customer
	 *           the specific customer
	 * @param searchPageData
	 *           pagination parameters
	 * @return paginated result
	 */
	SearchPageData<SiteMessageForCustomerModel> getPaginatedMessagesForCustomer(CustomerModel customer, SearchPageData searchPageData);

	/**
	 * Creates a site message by given title, link, content, message type, notification type and locale
	 *
	 * @param title
	 *           the message title
	 * @param content
	 *           the message content
	 * @param type
	 *           the message type
	 * @param externalItem
	 *           the related item model
	 * @param notificationType
	 *           the notification type
	 * @param locale
	 *           the locale, if null, default current locale
	 * @return the created message model
	 */
	SiteMessageModel createMessage(String title, String content, SiteMessageType type, ItemModel externalItem,
			NotificationType notificationType, Locale locale);

	/**
	 * Gets all site message of the customer
	 * 
	 * @param customer
	 *           the specific customer
	 * @return list of site message for customer
	 */
	List<SiteMessageForCustomerModel> getSiteMessagesForCustomer(CustomerModel customer);
}
