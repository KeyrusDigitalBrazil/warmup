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
package de.hybris.platform.cmsfacades.users.services;

import de.hybris.platform.core.model.user.UserModel;

import java.util.Set;


/**
 * Service interface to get user data.
 */
public interface CMSUserService
{

	/**
	 * Retrieves the languages ISO code for which the current user can read.
	 *
	 * @return
	 * 		the ISO codes for all readable languages.
	 */
	Set<String> getReadableLanguagesForCurrentUser();

	/**
	 * Retrieves the languages ISO code for which the current user can write.
	 *
	 * @return
	 * 		the ISO codes for all writeable languages.
	 */
	Set<String> getWriteableLanguagesForCurrentUser();

	/**
	 * Retrieves the languages ISO code for which the provided user can read.
	 *
	 * @param userModel
	 * 		- The model representing the user whose set of readable languages to retrieve
	 * @return
	 * 		the ISO codes for all readable languages.
	 */
	Set<String> getReadableLanguagesForUser(UserModel userModel);

	/**
	 * Retrieves the languages ISO code for which the provided user can write.
	 *
	 * @param userModel
	 * 		- The model representing the user whose set of writeable languages to retrieve
	 * @return
	 * 		the ISO codes for all writeable languages.
	 */
	Set<String> getWriteableLanguagesForUser(UserModel userModel);
}
