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
package de.hybris.platform.cmsfacades.usergroups;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserGroupData;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * Facade for searching and getting information about available user groups.
 */
public interface UserGroupFacade
{

	/**
	 * Get a single user group.
	 *
	 * @param uid
	 *           - the identifier of the user group to retrieve
	 * @return user group
	 * @throws CMSItemNotFoundException
	 *            when the user group could not be found
	 */
	UserGroupData getUserGroupById(String uid) throws CMSItemNotFoundException;

	/**
	 * Method to find user groups using a free-text form. It also supports pagination.
	 *
	 * @param text
	 *           The free-text string to be used on the product search
	 * @param pageableData
	 *           the pagination object
	 * @return the search result object.
	 */
	SearchResult<UserGroupData> findUserGroups(String text, PageableData pageableData);

}
