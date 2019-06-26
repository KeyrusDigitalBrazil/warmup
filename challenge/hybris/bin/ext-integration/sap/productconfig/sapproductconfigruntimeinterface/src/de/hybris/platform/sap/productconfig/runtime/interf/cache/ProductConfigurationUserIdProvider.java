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
package de.hybris.platform.sap.productconfig.runtime.interf.cache;

/**
 * Provides a user id for the current user
 */
public interface ProductConfigurationUserIdProvider
{
	/**
	 * Obtains an id that uniquely identifies the current user even if the session is anonymous
	 *
	 * @return user id
	 */
	String getCurrentUserId();

	/**
	 * @return User is anonymous?
	 */
	boolean isAnonymousUser();
}
