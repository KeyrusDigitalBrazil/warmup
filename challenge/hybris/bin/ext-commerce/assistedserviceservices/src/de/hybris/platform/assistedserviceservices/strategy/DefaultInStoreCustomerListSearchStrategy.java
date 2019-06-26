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
package de.hybris.platform.assistedserviceservices.strategy;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedserviceservices.dao.CustomerGroupDao;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the customer list search strategy for getting current customers in store
 *
 * The strategy will first query for groups for the supplied employee Uid which would result in list of store employees
 * groups that this agent belongs to
 *
 * Second step will be making sure that these groups have stores associated to them, then loop over these groups and get
 * the customers who are member of these stores
 *
 * finding customers belonging to store will be done based on a naming convention which would be like appending the
 * prefix "POS_" before the store upper case name and then retrieving customers that are members of these groups
 */
public class DefaultInStoreCustomerListSearchStrategy implements CustomerListSearchStrategy
{
	private UserService userService;
	private CustomerGroupDao customerGroupDao;

	private static final Logger LOG = LogManager.getLogger(DefaultInStoreCustomerListSearchStrategy.class);

	@Override
	public <T extends CustomerModel> SearchPageData<T> getPagedCustomers(final String customerListUid, final String employeeUid,
			final PageableData pageableData, final Map<String, Object> parameterMap)
	{
		validateParameterNotNullStandardMessage("customerListUid", customerListUid);

		validateParameterNotNullStandardMessage("pageableData", pageableData);

		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		final Set<StoreEmployeeGroupModel> storeEmployeeGroups = getUserService()
				.getAllUserGroupsForUser(getUserService().getUserForUID(employeeUid), StoreEmployeeGroupModel.class);

		if (LOG.isDebugEnabled() && CollectionUtils.isEmpty(storeEmployeeGroups))
		{
			LOG.debug("Employee [{}] does not belong to any store employee group!", employeeUid);
		}

		List<UserGroupModel> customerGroups = null;

		if (!CollectionUtils.isEmpty(storeEmployeeGroups))
		{
			customerGroups = new ArrayList<UserGroupModel>();
			final List<StoreEmployeeGroupModel> validStoreEmployeeGroups = storeEmployeeGroups.stream()
					.filter(storeEmployee -> storeEmployee.getStore() != null).collect(Collectors.toList());

			for (final StoreEmployeeGroupModel validStoreEmployeeGroup : validStoreEmployeeGroups)
			{
				final UserGroupModel userGroup = getUserService()
						.getUserGroupForUID(Config.getString(AssistedserviceservicesConstants.DEFAULT_CUSTOMER_GROUP_PREFIX_KEY,
								AssistedserviceservicesConstants.DEFAULT_CUSTOMER_GROUP_PREFIX)
								+ validStoreEmployeeGroup.getStore().getName().toUpperCase());

				customerGroups.add(userGroup);
			}
		}
		//in case we dont have customer groups we will return empty SearchPageData
		if (CollectionUtils.isEmpty(customerGroups))
		{
			final SearchPageData<T> customerSearchPagedData = new SearchPageData<T>();
			final PaginationData paginationData = new PaginationData();
			customerSearchPagedData.setPagination(paginationData);

			return customerSearchPagedData;
		}

		return customerGroupDao.findAllCustomersByGroups(customerGroups, pageableData);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CustomerGroupDao getCustomerGroupDao()
	{
		return customerGroupDao;
	}

	@Required
	public void setCustomerGroupDao(final CustomerGroupDao customerGroupDao)
	{
		this.customerGroupDao = customerGroupDao;
	}
}