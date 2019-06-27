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
package de.hybris.platform.assistedserviceservices.dao;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;


/**
 * Customer Group DAO
 */
public interface CustomerGroupDao
{

	/**
	 * Get paginated customers for specific customer list
	 *
	 * @param groupsUid
	 *           groups that we want to get customers for
	 * @param pageableData
	 *           paging information
	 * @return customer model search page data
	 */
	<T extends CustomerModel> SearchPageData<T> findAllCustomersByGroups(List<UserGroupModel> groupsUid,
			PageableData pageableData);

	/**
	 * Get paginated customers for specific pos-list where customer have a consignment.
	 *
	 * @param pointOfServiceModels
	 *           POS-es with employee belongs to
	 * @param pageableData
	 *           paging information
	 * @return customer model search page data
	 */
	<T extends CustomerModel> SearchPageData<T> findAllCustomersByConsignmentsInPointOfServices(List<PointOfServiceModel> pointOfServiceModels,
																								PageableData pageableData);
}
