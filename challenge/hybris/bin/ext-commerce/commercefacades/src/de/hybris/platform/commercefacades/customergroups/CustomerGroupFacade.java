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
package de.hybris.platform.commercefacades.customergroups;

import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;

import java.util.List;
import java.util.Set;


/**
 * Facade for management of customer groups - that is user groups which are sub group of user group with id defined via
 * {@link #setBaseCustomerGroupId(String)}. Typically customer group id = 'customergroup'
 *
 */
public interface CustomerGroupFacade
{
	/**
	 * Create customer group (direct sub group of 'customergroup') with given uid and localized name in current locale
	 *
	 * @param uid
	 *           the customer group uid
	 * @param localizedName
	 *           the customer group localized name
	 */
	void createCustomerGroup(String uid, String localizedName);

	/**
	 * Assign user to customer group
	 *
	 * @param customerGroupid
	 *           customer group uid
	 * @param userId
	 *           user uid
	 */
	void addUserToCustomerGroup(String customerGroupid, String userId);

	/**
	 * Remove user from customer group
	 *
	 * @param customerGroupid
	 *           customer group uid
	 * @param userId
	 *           user uid
	 */
	void removeUserFromCustomerGroup(String customerGroupid, String userId);

	/**
	 * Returns all customers groups for the current user.
	 * 
	 * @return all customer groups of a current customer
	 */
	List<UserGroupData> getCustomerGroupsForCurrentUser();

	/**
	 * Gets a user's customer groups
	 *
	 * @param uid
	 *           the user's uid
	 * @return all customer groups of a given customer
	 */
	List<UserGroupData> getCustomerGroupsForUser(String uid);

	/**
	 * Returns user group with uid 'customergroup' and all it's direct subgroups
	 *
	 * @param pageOption
	 *           - result paging option.
	 * @return All customer groups as {@link UserGroupDataList}.
	 */
	UserGroupDataList getAllCustomerGroups(PageOption pageOption);

	/**
	 * Returns customer group (a sub-group of 'cutomergroup') by uid.
	 *
	 * @param uid
	 *           the customer group uid
	 * @param options
	 *           a {@link Set} of required {@link UserGroupOption}s
	 * @return the customer group
	 */
	UserGroupData getCustomerGroup(String uid, Set<UserGroupOption> options);
}
