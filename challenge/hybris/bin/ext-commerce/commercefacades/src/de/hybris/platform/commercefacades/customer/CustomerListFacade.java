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
package de.hybris.platform.commercefacades.customer;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.CustomerListData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;
import java.util.Map;


/**
 * Customer List facade interface which contains methods for customer listing functionality
 *
 */
public interface CustomerListFacade
{

	/**
	 * Gets list of user group data which contains what customer list an employee can interact with
	 *
	 * @param employeeUid
	 *           the employee Uid to fetch available lists for
	 * @return list of user group data
	 */
	List<UserGroupData> getCustomerListsForEmployee(String employeeUid);


	/**
	 * get list of customers based on specific customer list type
	 *
	 * @param customerListUid
	 *           the list the employee is interested in
	 * @param employeeUid
	 *           employee Id
	 * @param pageableData
	 *           pageable data for pagination purpose
	 * @param parameterMap
	 *           extra parameters supplied for the actual
	 * @return list of customers data
	 *
	 */
	<T extends CustomerData> SearchPageData<T> getPagedCustomersForCustomerListUID(String customerListUid, String employeeUid,
			PageableData pageableData, Map<String, Object> parameterMap);

	/**
	 * Gets the customer list for uid.
	 *
	 * @param customerListUid
	 *           the list the employee is interested in
	 * @param employeeUid
	 *           employee Id
	 * @return the customer list data for uid.
	 */
	CustomerListData getCustomerListForUid(String customerListUid, String employeeUid);
}
