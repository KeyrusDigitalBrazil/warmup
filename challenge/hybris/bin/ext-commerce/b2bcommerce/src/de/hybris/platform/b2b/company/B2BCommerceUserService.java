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
package de.hybris.platform.b2b.company;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;


/**
 * A service for user management within b2b commerce
 */
public interface B2BCommerceUserService
{
	/**
	 * Gets list of {@link SearchPageData} for pagination given the required pagination parameters with
	 * {@link PageableData}
	 *
	 * @param pageableData
	 *           Pagination information
	 * @return List of paginated {@link B2BCustomerModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedCustomers(PageableData pageableData);

	/**
	 * Gets list of {@link SearchPageData} for pagination given the required pagination parameters with
	 * {@link PageableData}
	 *
	 * @param pageableData
	 *           Pagination information
	 * @param userGroupUids
	 *           Unique identifiers for {@link B2BUserGroupModel}
	 * @return List of paginated {@link B2BCustomerModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedCustomersByGroupMembership(PageableData pageableData, String... userGroupUids);

	/**
	 * Gets a pageable list of b2b customers that belong to the supplied groups. The results are filtered by a search
	 * term.
	 *
	 * @param pageableData
	 *           Pagination information
	 * @param searchTerm
	 *           Search string that is used to filter the results using the customer name or his unit name. A match can
	 *           occur in either the customer name or his unit name for the customer to be returned in the results.
	 * @param userGroupUids
	 *           Unique identifiers for {@link B2BUserGroupModel}
	 * @return List of paginated {@link B2BCustomerModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedCustomersBySearchTermAndGroupMembership(PageableData pageableData, String searchTerm,
			String... userGroupUids);

	/**
	 * Remove user role for a given user and return the updated {@link B2BCustomerModel} object
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param role
	 *           A unique identifier for {@link UserGroupModel} representing a user groups to which the user belongs
	 * @return Updated {@link B2BCustomerModel} object with user groups removed matching the role
	 */
	B2BCustomerModel removeUserRole(String user, String role);

	/**
	 * Add user role for a given user and return the updated {@link B2BCustomerModel} object
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param role
	 *           A unique identifier for {@link UserGroupModel} representing a user groups to which the user belongs
	 * @return Updated {@link B2BCustomerModel} object with user groups added matching the role
	 */
	B2BCustomerModel addUserRole(String user, String role);

	/**
	 * Add Usergroups {@link B2BCustomerModel} object for a given unique id of a customer
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           A unique identifier for {@link B2BUserGroupModel} representing a user group
	 * @return Updated {@link B2BUserGroupModel} object with user group added for the given unique identifier for
	 *         {@link B2BUserGroupModel}
	 */
	B2BUserGroupModel addB2BUserGroupToCustomer(String user, String usergroup);

	/**
	 * Deselects (removes) usergroup from a customer.
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           A unique identifier for {@link B2BUserGroupModel} representing a user group
	 * @return Updated {@link B2BUserGroupModel} object with user group.
	 */
	B2BUserGroupModel deselectB2BUserGroupFromCustomer(String user, String usergroup);

	/**
	 * Removes usergroup from a customer.
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           A unique identifier for {@link B2BUserGroupModel} representing a user group
	 */
	void removeB2BUserGroupFromCustomerGroups(String user, String usergroup);

	/**
	 * Get parent unit {@link B2BUnitModel} for a given unique id of a customer
	 *
	 * @param uid
	 *           A unique id for @link B2BCustomerModel} object of a customer
	 * @return Parent unit {@link B2BUnitModel} object for a given unique id of customer
	 *
	 */
	<T extends B2BUnitModel> T getParentUnitForCustomer(String uid);

	/**
	 * Disable a customer given its unique id
	 *
	 * @param uid
	 *           A unique id for @link B2BCustomerModel} representing a user
	 */
	void disableCustomer(String uid);

	/**
	 * Enable customer given its unique id
	 *
	 * @param uid
	 *           A unique id for @link B2BCustomerModel} representing a user
	 */
	void enableCustomer(String uid);
}
