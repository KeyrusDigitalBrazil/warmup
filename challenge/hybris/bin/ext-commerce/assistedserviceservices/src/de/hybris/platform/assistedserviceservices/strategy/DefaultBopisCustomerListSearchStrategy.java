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

import de.hybris.platform.assistedserviceservices.dao.CustomerGroupDao;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the customer list search strategy for getting a list of customers that have a consignment
 * for a specific delivery PointOfService in a specific state.
 *
 * In other words it finds customers that are about to pickup an item from a specific store.
 *
 * The strategy will first query for groups for the supplied employee Uid. Second step will be making sure that these
 * groups have stores associated to them, then collecting that stores and calling:
 * 
 * @see CustomerGroupDao#findAllCustomersByConsignmentsInPointOfServices(List, PageableData)
 *
 */
public class DefaultBopisCustomerListSearchStrategy implements CustomerListSearchStrategy
{
	private UserService userService;
	private CustomerGroupDao customerGroupDao;

	private static final Logger LOG = Logger.getLogger(DefaultBopisCustomerListSearchStrategy.class);

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
			LOG.debug("Employee " + employeeUid + " does not belong to any store employee group!");
		}

		List<PointOfServiceModel> posModels = null;

		if (CollectionUtils.isNotEmpty(storeEmployeeGroups))
		{
			posModels = new ArrayList<>();
			final List<PointOfServiceModel> filteredPointOfServices = storeEmployeeGroups.stream()
					.filter(storeEmployee -> storeEmployee.getStore() != null).map(StoreEmployeeGroupModel::getStore)
					.collect(Collectors.toList());

			posModels.addAll(filteredPointOfServices);
		}

		//in case we don't have customer groups we will return empty SearchPageData
		if (CollectionUtils.isEmpty(posModels))
		{
			final SearchPageData<T> customerSearchPagedData = new SearchPageData<T>();
			final PaginationData paginationData = new PaginationData();
			customerSearchPagedData.setPagination(paginationData);
			customerSearchPagedData.setResults(new ArrayList<T>());
			return customerSearchPagedData;
		}

		return customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(posModels, pageableData);
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