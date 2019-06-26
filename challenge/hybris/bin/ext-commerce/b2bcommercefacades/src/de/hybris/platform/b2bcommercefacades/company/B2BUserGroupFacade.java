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
package de.hybris.platform.b2bcommercefacades.company;

import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;


/**
 * A facade for handling user groups within b2b Commerce.
 *
 * @since 6.0
 */
public interface B2BUserGroupFacade
{
	/**
	 * Get paginated list of customers who are members of the given B2B user group.
	 *
	 * @param pageableData
	 *           pagination information for the request
	 * @param userGroupUid
	 *           the uid of the user group
	 * @return a paginated list of {@link CustomerData}
	 */
	SearchPageData<CustomerData> getPagedCustomersForUserGroup(PageableData pageableData, String userGroupUid);

	/**
	 * Updates B2B user group based on passed in data object. If no user group exists for the given uid a new user group
	 * is created.
	 *
	 * @param userGroupUid
	 *           the uid of the user group to be updated
	 * @param userGroupData
	 *           {@link B2BUserGroupData} containing the update information
	 */
	void updateUserGroup(String userGroupUid, B2BUserGroupData userGroupData);

	/**
	 * Disabled a user group by removing all members from it.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 */
	void disableUserGroup(String userGroupUid);

	/**
	 * Remove the user group with the given uid.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 */
	void removeUserGroup(String userGroupUid);

	/**
	 * Get a paginated list of B2B users.
	 *
	 * @param pageableData
	 *           pagination information for the request
	 *
	 * @return a paginated list of {@link CustomerData}
	 */
	SearchPageData<CustomerData> getPagedUserData(PageableData pageableData);

	/**
	 * Get a paginated lists of B2B user groups.
	 *
	 * @param pageableData
	 *           pagination information for the request
	 * @return a paginated list of {@link B2BUserGroupData}
	 */
	SearchPageData<B2BUserGroupData> getPagedB2BUserGroups(PageableData pageableData);

	/**
	 * Get the User Group Data with the uid
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 * @return {@link B2BUserGroupData}
	 */
	B2BUserGroupData getB2BUserGroup(String userGroupUid);

	/**
	 * Add the user with the given uid as a member of the user group.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 * @param userUid
	 *           the uid of the user
	 * @return {@link CustomerData}
	 */
	CustomerData addMemberToUserGroup(String userGroupUid, String userUid);

	/**
	 * Remove the member with the given uid from the user group.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 * @param userUid
	 *           the uid of the member
	 * @return {@link CustomerData}
	 */
	CustomerData removeMemberFromUserGroup(String userGroupUid, String userUid);

	/**
	 * Get the user group data for the given uid.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 * @return {@link UserGroupData}
	 */
	UserGroupData getUserGroupDataForUid(String userGroupUid);

	/**
	 * A list of user group codes (roles) a b2b customer can be assigned to.
	 *
	 * @return a list of {@link de.hybris.platform.core.model.user.UserGroupModel#UID} a b2b customer can be assigned to
	 */
	List<String> getUserGroups();
}
