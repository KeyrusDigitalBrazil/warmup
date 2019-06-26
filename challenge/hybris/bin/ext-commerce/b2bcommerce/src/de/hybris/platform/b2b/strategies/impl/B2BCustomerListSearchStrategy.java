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
package de.hybris.platform.b2b.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.OrgUnitDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceSearchUtils;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the customer list search strategy for getting a list of b2b customers assigned to an
 * employee through their common org unit association.
 *
 * The strategy will first query for the Sales Unit of which the employee is a member.
 *
 * Second step is to get the list of B2BUnits that are members of the same Sales Unit as the employee.
 *
 * In the end the strategy returns the list of all b2b customers that are members of the B2BUnits found in the previous
 * step.
 *
 */
public class B2BCustomerListSearchStrategy implements CustomerListSearchStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(B2BCustomerListSearchStrategy.class);
	protected static final String QUERY = "query";

	private UserService userService;
	private B2BCommerceUserService b2bCommerceUserService;
	private OrgUnitDao orgUnitDao;

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CustomerModel> SearchPageData<T> getPagedCustomers(final String customerListUid, final String employeeUid,
			final PageableData pageableData, final Map<String, Object> parameterMap)
	{
		validateParameterNotNullStandardMessage("pageableData", pageableData);
		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		// find employee's org units
		final EmployeeModel employeeModel = getUserService().getUserForUID(employeeUid, EmployeeModel.class);
		final List<OrgUnitModel> employeeOrgUnits = employeeModel.getGroups().stream()
				.filter(unit -> unit.getClass().equals(OrgUnitModel.class)).map(unit -> (OrgUnitModel) unit)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(employeeOrgUnits))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Employee [{}] does not belong to any OrgUnit!", employeeUid);
			}
			return CommerceSearchUtils.createEmptySearchPageData();
		}

		// find all orgUnits in each orgUnit branch
		final List<OrgUnitModel> orgUnits = getOrgUnitDao()
				.findAllUnits(employeeOrgUnits, CommerceSearchUtils.getAllOnOnePagePageableData()).getResults();
		final String[] orgUnitsUids = orgUnits.stream().map(OrgUnitModel::getUid).toArray(String[]::new);

		// find all b2b units of the orgUnits
		final List<B2BUnitModel> customerAccounts = (List<B2BUnitModel>) getOrgUnitDao()
				.findMembersOfType(B2BUnitModel.class, CommerceSearchUtils.getAllOnOnePagePageableData(), orgUnitsUids).getResults()
				.stream().filter(child -> child.getClass().equals(B2BUnitModel.class)).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(customerAccounts))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Employee [{}] is not associated to any B2BUnit!", employeeUid);
			}
			return CommerceSearchUtils.createEmptySearchPageData();
		}

		// find all b2b units in each b2bUnit branch
		final List<B2BUnitModel> allCustomerAccounts = getOrgUnitDao()
				.findAllUnits(customerAccounts, CommerceSearchUtils.getAllOnOnePagePageableData()).getResults();

		// find all customers of the b2b units
		final String customerOrganizationUidArray[] = allCustomerAccounts.stream().map(B2BUnitModel::getUid).toArray(String[]::new);
		final String searchTerm = getSearchTermParameter(parameterMap);
		if (StringUtils.isBlank(searchTerm))
		{
			return (SearchPageData<T>) getB2bCommerceUserService().getPagedCustomersByGroupMembership(pageableData,
					customerOrganizationUidArray);
		}
		else
		{
			return (SearchPageData<T>) getB2bCommerceUserService().getPagedCustomersBySearchTermAndGroupMembership(pageableData,
					searchTerm, customerOrganizationUidArray);
		}
	}

	protected String getSearchTermParameter(final Map<String, Object> parameterMap)
	{
		if (parameterMap == null)
		{
			return null;
		}
		return (String) parameterMap.get(QUERY);
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

	protected B2BCommerceUserService getB2bCommerceUserService()
	{
		return b2bCommerceUserService;
	}

	@Required
	public void setB2bCommerceUserService(final B2BCommerceUserService b2bCommerceUserService)
	{
		this.b2bCommerceUserService = b2bCommerceUserService;
	}

	protected OrgUnitDao getOrgUnitDao()
	{
		return orgUnitDao;
	}

	@Required
	public void setOrgUnitDao(final OrgUnitDao orgUnitDao)
	{
		this.orgUnitDao = orgUnitDao;
	}
}
