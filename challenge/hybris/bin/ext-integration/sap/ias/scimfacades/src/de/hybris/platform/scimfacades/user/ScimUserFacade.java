/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimfacades.user;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;

import java.util.List;


/**
 * Facade to carry out user operations
 */
public interface ScimUserFacade
{

	/**
	 * Create user from scim user
	 *
	 * @param scimUser
	 *           the scim user object
	 * @return ScimUser object
	 */
	ScimUser createUser(ScimUser scimUser);

	/**
	 * Update user for userId
	 *
	 * @param userId
	 *           the user id
	 * @param scimUser
	 *           the scim user object
	 * @return ScimUser object
	 */
	ScimUser updateUser(String userId, ScimUser scimUser);

	/**
	 * Get scim user for userId
	 *
	 * @param userId
	 *           the user id
	 * @return ScimUser object
	 */
	ScimUser getUser(String userId);

	/**
	 * Get all scim users
	 *
	 * @param userId
	 *           the user id
	 * @return List<ScimUser> of scim users
	 */
	List<ScimUser> getUsers(String userId);

	/**
	 * Delete user
	 *
	 * @param userId
	 *           the user id
	 * @return boolean true if successful deleted otherwise false
	 */
	boolean deleteUser(String userId);

	/**
	 * Get user for scimUser id
	 *
	 * @param scimUserId
	 *           the scim user id
	 * @return UserModel user model object
	 */
	UserModel getUserForScimUserId(String scimUserId);

}
