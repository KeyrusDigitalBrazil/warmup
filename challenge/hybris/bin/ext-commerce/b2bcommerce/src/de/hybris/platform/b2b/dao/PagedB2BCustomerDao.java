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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


public interface PagedB2BCustomerDao<M> extends PagedGenericDao<M>
{
	/**
	 * Paged search to find {@link B2BCustomerModel} by User Group.
	 *
	 * @param pageableData
	 *           The page data to be filled with the results.
	 * @param userGroupId
	 *           The uid of the desired user group.
	 * @return Customers found in search.
	 */
	SearchPageData<B2BCustomerModel> findPagedCustomersByGroupMembership(PageableData pageableData, String... userGroupId);

	/**
	 * Paged search to find {@link B2BCustomerModel} by User Group.
	 *
	 * @param pageableData
	 *           The page data to be filled with the results.
	 * @param searchTerm
	 *           Search string that is used to filter the results using the customer name or his unit name. A match can
	 *           occur in either the customer name or his unit name for the customer to be returned in the results.
	 * @param userGroupId
	 *           The uid of the desired user group.
	 * @return Customers found in search.
	 */
	SearchPageData<B2BCustomerModel> findPagedCustomersBySearchTermAndGroupMembership(PageableData pageableData, String searchTerm,
			String... userGroupId);

	/**
	 * Paged search to find {@link B2BCustomerModel} by B2BUnit and User Groups.
	 *
	 * @param pageableData
	 *           The page data to be filled with the results.
	 * @param unit
	 *           The uid of the desired B2BUnit.
	 * @param userGroupId
	 *           The uids of the desired user groups.
	 * @return Customers found in search.
	 */
	SearchPageData<B2BCustomerModel> findPagedCustomersForUnitByGroupMembership(PageableData pageableData, String unit,
			String... userGroupId);

	/**
	 * Paged search to find {@link B2BCustomerModel} by B2BUnit or User Groups.
	 * 
	 * @param pageableData
	 *           The page data to be filled with the results.
	 * @param unit
	 *           The uid of the desired B2BUnit.
	 * @param userGroupId
	 *           The uids of the desired user groups.
	 * @return Customers found in search.
	 */
	SearchPageData<B2BCustomerModel> findPagedApproversForUnitByGroupMembership(PageableData pageableData, String unit,
			String... userGroupId);

	/**
	 * aged search to find {@link B2BCustomerModel} by B2BUnit.
	 * 
	 * @param pageableData
	 *           The page data to be filled with the results.
	 * @param unit
	 *           The uid of the desired B2BUnit.
	 * @return Customers found in search.
	 */
	SearchPageData<B2BCustomerModel> findPagedCustomersForUnit(PageableData pageableData, String unit);
}
