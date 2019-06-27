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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.events.CustomerSupportEventService;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.ticketsystem.events.SessionEvent;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;


@IntegrationTest
public class DefaultRecentlyStartedSessionCustomerListSearchStrategyTest extends ServicelayerTest
{
	private PageableData pageableData;

	@Resource
	private UserService userService;

	@Resource
	private CustomerSupportEventService customerSupportEventService;

	@Resource
	private DefaultRecentlyStartedSessionCustomerListSearchStrategy defaultRecentlyStartedSessionCustomerListSearchStrategy;

	@Before
	public void setup() throws Exception
	{
		pageableData = new PageableData();
		pageableData.setPageSize(5);
		importCsv("/assistedserviceservices/test/recent_data.impex", "UTF-8");
	}

	@Test
	public void recentCustomerListSearchStrategyTest()
	{
		EmployeeModel asagent = userService.getUserForUID("asagent", EmployeeModel.class);
		CustomerModel user1 = userService.getUserForUID("user1", CustomerModel.class);
		CustomerModel user2 = userService.getUserForUID("user2", CustomerModel.class);
		CustomerModel user3 = userService.getUserForUID("user3", CustomerModel.class);
		CustomerModel user4 = userService.getUserForUID("user4", CustomerModel.class);

		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user3, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user4, EventType.START_SESSION_EVENT));

		final SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(2, customers.getResults().size());
	}

	@Test
	public void recentCustomerListSearchStrategyTestWithExceedPage()
	{
		EmployeeModel asagent = userService.getUserForUID("asagent", EmployeeModel.class);
		CustomerModel user2 = userService.getUserForUID("user2", CustomerModel.class);
		CustomerModel user1 = userService.getUserForUID("user1", CustomerModel.class);
		CustomerModel user3 = userService.getUserForUID("user3", CustomerModel.class);
		CustomerModel user4 = userService.getUserForUID("user4", CustomerModel.class);
		CustomerModel user5 = userService.getUserForUID("user5", CustomerModel.class);
		CustomerModel user6 = userService.getUserForUID("user6", CustomerModel.class);
		CustomerModel user7 = userService.getUserForUID("user7", CustomerModel.class);

		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user3, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user4, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user5, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user6, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user7, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user7, EventType.START_SESSION_EVENT));

		final SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(5, customers.getResults().size());
	}

	@Test
	public void emptyRecentCustomerListSearchStrategyTest()
	{
		final SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(0, customers.getResults().size());
	}

	protected SessionEvent createSessionEvent(UserModel agent, UserModel customer, EventType type)
	{
		final SessionEvent asmStartSessionEventData = new SessionEvent();
		asmStartSessionEventData.setAgent(agent);
		asmStartSessionEventData.setCustomer(customer);
		asmStartSessionEventData.setCreatedAt(new Date());
		asmStartSessionEventData.setAgentGroups(new ArrayList<>(agent.getGroups()));
		asmStartSessionEventData.setEventType(type);
		return asmStartSessionEventData;
	}
}
