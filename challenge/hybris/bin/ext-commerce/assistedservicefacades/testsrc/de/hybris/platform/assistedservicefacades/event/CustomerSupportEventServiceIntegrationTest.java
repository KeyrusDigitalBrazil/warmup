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
package de.hybris.platform.assistedservicefacades.event;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedserviceservices.events.CustomerSupportEventService;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Date;

import javax.annotation.Resource;

import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.ticketsystem.events.SessionEvent;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class CustomerSupportEventServiceIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String USER_UID = "test.customer@asm.com";
	private static final String ASAGENT_UID = "asagent";
	private static final String ASAGENT_PWD = "1234";

	@Resource
	private CustomerSupportEventService customerSupportEventService;
	@Resource
	private UserService userService;
	@Resource
	private AssistedServiceFacade assistedServiceFacade;
	@Resource
	private BaseSiteService baseSiteService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/assistedservicefacades/test/event.impex", "UTF-8");
		baseSiteService.setCurrentBaseSite("testSite", true);
	}

	@Test
	public void testCreateStartEvent()
	{
		final SessionEvent event = new SessionEvent();
		event.setCreatedAt(new Date());
		event.setAgent(userService.getUserForUID(ASAGENT_UID));
		event.setCustomer(userService.getUserForUID(USER_UID));
		event.setEventType(EventType.START_SESSION_EVENT);
		customerSupportEventService.registerSessionEvent(event);

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(10);

		assertTrue(customerSupportEventService.getAllEventsForAgent((EmployeeModel) userService.getUserForUID(ASAGENT_UID),
				EventType.END_SESSION_EVENT, null, null, pageableData,10).getResults().size() == 1);
	}

	@Test
	public void testCreateStopEvent()
	{
		final SessionEvent event = new SessionEvent();
		event.setCreatedAt(new Date());
		event.setAgent(userService.getUserForUID(ASAGENT_UID));
		event.setCustomer(userService.getUserForUID(USER_UID));
		event.setEventType(EventType.END_SESSION_EVENT);
		customerSupportEventService.registerSessionEvent(event);

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(10);

		assertTrue(customerSupportEventService.getAllEventsForAgent((EmployeeModel) userService.getUserForUID(ASAGENT_UID),
				EventType.END_SESSION_EVENT, null, null, pageableData,10).getResults().size() == 1);
	}

	@Test
	public void testStartAsmSession()
	{
		try
		{
			assistedServiceFacade.launchAssistedServiceMode();
			assistedServiceFacade.loginAssistedServiceAgent(ASAGENT_UID, ASAGENT_PWD);

			assistedServiceFacade.emulateCustomer(USER_UID, null);

		}
		catch (final AssistedServiceException e)
		{
			e.printStackTrace();
			Logger.getLogger(CustomerSupportEventServiceIntegrationTest.class).info(e);
		}

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(10);

		assertTrue(customerSupportEventService.getAllEventsForAgent((EmployeeModel) userService.getUserForUID(ASAGENT_UID),
				EventType.START_SESSION_EVENT, null, null, pageableData,10).getResults().size() == 1);
	}

	@Test
	public void testStartAndStopAsmSession()
	{
		try
		{
			assistedServiceFacade.launchAssistedServiceMode();
			assistedServiceFacade.loginAssistedServiceAgent(ASAGENT_UID, ASAGENT_PWD);

			assistedServiceFacade.emulateCustomer(USER_UID, null);

			assistedServiceFacade.stopEmulateCustomer();

		}
		catch (final AssistedServiceException e)
		{
			e.printStackTrace();
			Logger.getLogger(CustomerSupportEventServiceIntegrationTest.class).info(e);
		}

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(10);

		assertTrue(customerSupportEventService.getAllEventsForAgent((EmployeeModel) userService.getUserForUID(ASAGENT_UID),
				EventType.EVENTS, null, null, pageableData,10).getResults().size() == 2);
	}
}
