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
package de.hybris.platform.cmsfacades.users;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserData;

/**
 * Facade for searching information about users.
 */
public interface UserFacade
{
	/**
	 * Get a single user.
	 * @param uid
	 * 			- the identifier of the user to retrieve
	 * @return user found.
	 * @throws CMSItemNotFoundException
	 *            when the user could not be found
	 */
	UserData getUserById(String uid) throws CMSItemNotFoundException;
}
