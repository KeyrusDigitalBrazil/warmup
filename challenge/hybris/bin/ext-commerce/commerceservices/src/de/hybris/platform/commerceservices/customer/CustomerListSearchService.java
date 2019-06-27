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
package de.hybris.platform.commerceservices.customer;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Map;


/**
 * Customer List Service Search interface holding service layer methods for dealing with Customers
 *
 */
public interface CustomerListSearchService
{
	/**
	 * Get paginated customers for specific customer list
	 *
	 * @param customerListUid
	 *           customer list UID
	 * @param employeeUid
	 *           employee ID
	 * @param pageableData
	 *           paging information
	 * @param parameterMap
	 *           extra parameters to be provided
	 * @return customer model search page data
	 */
	<T extends CustomerModel> SearchPageData<T> getPagedCustomers(final String customerListUid, final String employeeUid,
			final PageableData pageableData, final Map<String, Object> parameterMap);

}
