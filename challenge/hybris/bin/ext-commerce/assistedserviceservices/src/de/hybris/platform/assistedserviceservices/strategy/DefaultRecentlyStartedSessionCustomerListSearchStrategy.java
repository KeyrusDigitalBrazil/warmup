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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedserviceservices.events.CustomerSupportEventService;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.util.Config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Concrete implementation for ASM recent sessions
 *
 */
public class DefaultRecentlyStartedSessionCustomerListSearchStrategy implements CustomerListSearchStrategy
{

	private CustomerSupportEventService customerSupportEventService;
	private UserService userService;

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CustomerModel> SearchPageData<T> getPagedCustomers(final String customerListUid, final String employeeUid,
			final PageableData pageableData, final Map<String, Object> parameterMap)
	{
		validateParameterNotNullStandardMessage("customerListUid", customerListUid);

		validateParameterNotNullStandardMessage("pageableData", pageableData);

		validateParameterNotNullStandardMessage("employeeUid", employeeUid);

		final EmployeeModel currentASMAgent = userService.getUserForUID(employeeUid, EmployeeModel.class);

		validateParameterNotNull(currentASMAgent, String.format("Employee with uid '%1$s' cannot be resolved", employeeUid));

		return getCustomerSupportEventService().findAllCustomersByEventsAndAgent(currentASMAgent, EventType.START_SESSION_EVENT,
				null, null, pageableData, Config.getInt(AssistedserviceservicesConstants.DEFAULT_RECENT_SESSIONS_LIMIT_KEY,
						AssistedserviceservicesConstants.DEFAULT_RECENT_SESSIONS_LIMIT), false);
	}

	protected CustomerSupportEventService getCustomerSupportEventService()
	{
		return customerSupportEventService;
	}

	@Required
	public void setCustomerSupportEventService(final CustomerSupportEventService customerSupportEventService)
	{
		this.customerSupportEventService = customerSupportEventService;
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
