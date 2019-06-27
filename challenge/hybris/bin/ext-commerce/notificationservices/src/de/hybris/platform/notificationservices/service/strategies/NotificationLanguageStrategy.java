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
package de.hybris.platform.notificationservices.service.strategies;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Optional;


/**
 * Get language strategy
 */
public interface NotificationLanguageStrategy
{
	/**
	 * If chineseprofileaddon exist use the email language setting under personal detail page else use current site
	 * language
	 * 
	 * @return language
	 */
	Optional<LanguageModel> getNotificationLanguage(CustomerModel customer);
}
