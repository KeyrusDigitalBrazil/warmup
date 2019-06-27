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
package de.hybris.platform.notificationservices.dao;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;

import java.util.List;


/**
 * DAO to provide methods for SiteMessage querying.
 */
public interface SiteMessageDao
{

	/**
	 * Finds paginated site message for given parameters
	 *
	 * @param customer
	 *           the specific customer
	 * @param type
	 *           the specific site message type
	 * @param searchPageData
	 *           paginated parameters
	 * @return paginated search result
	 */
	SearchPageData<SiteMessageForCustomerModel> findPaginatedMessagesByType(CustomerModel customer, SiteMessageType type,
			SearchPageData searchPageData);

	/**
	 * Finds all paginated site message
	 *
	 * @param customer
	 *           the specific customer
	 * 
	 * @param searchPageData
	 *           paginated parameters
	 * @return paginated search result
	 */
	SearchPageData<SiteMessageForCustomerModel> findPaginatedMessages(CustomerModel customer, SearchPageData searchPageData);

	/**
	 * Finds all site message of the customer
	 * 
	 * @param customer
	 *           the specific customer
	 * @return site message list
	 */
	List<SiteMessageForCustomerModel> findSiteMessagesForCustomer(CustomerModel customer);
}
