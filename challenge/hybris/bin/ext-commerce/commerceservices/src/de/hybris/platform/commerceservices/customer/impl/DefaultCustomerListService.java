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
package de.hybris.platform.commerceservices.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.customer.CustomerListService;
import de.hybris.platform.commerceservices.model.CustomerListModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Concrete implementation for the customer list service interface
 *
 */
public class DefaultCustomerListService implements CustomerListService
{

	private UserService userService;

	@Override
	public List<CustomerListModel> getCustomerListsForEmployee(final String employeeUid)
	{
		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		final List<CustomerListModel> customerLists = new ArrayList<CustomerListModel>();

		customerLists.addAll(
				getUserService().getAllUserGroupsForUser(getUserService().getUserForUID(employeeUid), CustomerListModel.class));

		Collections.sort(customerLists,
				(customerList1, customerList2) -> customerList2.getPriority().compareTo(customerList1.getPriority()));

		return customerLists;
	}

	@Override
	public CustomerListModel getCustomerListForEmployee(final String customerListUid, final String employeeUid)
	{
		validateParameterNotNullStandardMessage("customerListUid", customerListUid);
		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		final CustomerListModel customerListModel = getUserService().getUserGroupForUID(customerListUid, CustomerListModel.class);
		if (!getUserService().isMemberOfGroup(userService.getUserForUID(employeeUid), customerListModel))
		{
			return null;
		}

		return customerListModel;
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
}
