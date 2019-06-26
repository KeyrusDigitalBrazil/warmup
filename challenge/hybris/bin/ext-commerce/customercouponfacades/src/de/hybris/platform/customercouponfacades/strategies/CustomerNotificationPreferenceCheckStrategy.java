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
package de.hybris.platform.customercouponfacades.strategies;

/**
 * Checks if the current customer subscribe any type of notification when saving a coupon notification
 */
public interface CustomerNotificationPreferenceCheckStrategy
{
	/**
	 * Checks if the current customer subscribe any type of notification
	 *
	 * @return true if the customer has subscribed to at least one notification type and false otherwise
	 */
	Boolean checkCustomerNotificationPreference();
}
