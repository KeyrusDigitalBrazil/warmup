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
package de.hybris.platform.commerceservices.customer.strategies;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Map;


/**
 * Customer list search strategy holding methods responsible for doing the actual search for customer lists
 *
 */
public interface CustomerListSearchStrategy
{

	/**
	 * Gets customer data list based on specific implementation
	 *
	 * @param customerListUid
	 *           customer list Uid to fetch
	 * @param employeeUid
	 *           the employee Uid
	 * @param pageableData
	 *           paging information to return the data in a paginated fashion
	 * @param parameterMap
	 *           extra parameters supplied for this call
	 * @return list of customer data
	 */
	<T extends CustomerModel> SearchPageData<T> getPagedCustomers(final String customerListUid, final String employeeUid,
			final PageableData pageableData, Map<String, Object> parameterMap);

}
