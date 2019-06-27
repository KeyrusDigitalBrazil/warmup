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

import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * A facade for user management within b2b commerce.
 *
 * @since 6.0
 */
public interface B2BUserFacade
{
	/**
	 * Returns {@link B2BUnitData} for given customer uid.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @return found {@link B2BUnitData}
	 */
	B2BUnitData getParentUnitForCustomer(String customerUid);

	/**
	 * Get Paginated list of customers.
	 *
	 * @param pageableData
	 *           Pagination Data
	 * @return A paginated list of customers
	 */
	SearchPageData<CustomerData> getPagedCustomers(PageableData pageableData);

	/**
	 * Update customer model {@link de.hybris.platform.b2b.model.B2BCustomerModel}.
	 *
	 * @param customer
	 *           The Customer Data {@link CustomerData}
	 */
	void updateCustomer(CustomerData customer);

	/**
	 * Enable a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 */
	void enableCustomer(String customerUid);

	/**
	 * Disable a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 */
	void disableCustomer(String customerUid);

	/**
	 * Reset the customer password.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param updatedPassword
	 */
	void resetCustomerPassword(String customerUid, String updatedPassword);

	/**
	 * Remove the role from a user.
	 *
	 * @param userUid
	 *           the uid of the user
	 * @param roleUid
	 *           the uid of the role to be removed
	 * @return Returns the {@link de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData}
	 */
	B2BSelectionData removeUserRole(String userUid, String roleUid);

	/**
	 * Adds a role to a user.
	 *
	 * @param userUid
	 *           the uid of the user
	 * @param roleUid
	 *           the uid of the role to be removed
	 * @return Returns the {@link de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData}
	 */
	B2BSelectionData addUserRole(String userUid, String roleUid);

	/**
	 * Get Paginated list of B2B User groups the customer belongs to.
	 *
	 * @param pageableData
	 *           Pageable Data
	 * @param customerUid
	 *           the uid of the customer
	 * @return Returns the {@link de.hybris.platform.commerceservices.search.pagedata.SearchPageData}
	 */
	SearchPageData<B2BUserGroupData> getPagedB2BUserGroupsForCustomer(PageableData pageableData, String customerUid);

	/**
	 * Add b2b user group to a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param userGroupUid
	 *           the uid od the user group
	 * @return Returns {@link de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData}
	 */
	B2BSelectionData addB2BUserGroupToCustomer(String customerUid, String userGroupUid);

	/**
	 * Remove b2b user group from a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param userGroupUid
	 *           the uid od the user group
	 */
	void removeB2BUserGroupFromCustomerGroups(String customerUid, String userGroupUid);

	/**
	 * Deselects b2b user group from a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param userGroupUid
	 *           the uid od the user group
	 * @return Returns {@link de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData}
	 */
	B2BSelectionData deselectB2BUserGroupFromCustomer(String customerUid, String userGroupUid);

	/**
	 * Returns a b2b customer for the given uid.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @return a customer data object
	 */
	CustomerData getCustomerForUid(String customerUid);
}
