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
package de.hybris.platform.assistedserviceservices.events.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.assistedserviceservices.events.CustomerSupportEventService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.ticket.event.dao.CustomerSupportEventDao;
import de.hybris.platform.ticketsystem.events.SessionEvent;
import de.hybris.platform.ticketsystem.events.model.SessionEndEventModel;
import de.hybris.platform.ticketsystem.events.model.SessionEventModel;
import de.hybris.platform.ticketsystem.events.model.SessionStartEventModel;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link CustomerSupportEventService}.
 */
public class DefaultCustomerSupportEventService implements CustomerSupportEventService
{
	private static final Logger LOG = Logger.getLogger(CustomerSupportEventService.class);

	private ModelService modelService;
	private UserService userService;
	private CustomerSupportEventDao customerSupportEventDAO;
	private BaseSiteService baseSiteService;

	/**
	 * @see CustomerSupportEventService#registerSessionEvent(SessionEvent)
	 */
	@Override
	public void registerSessionEvent(final SessionEvent asmEventData)
	{
		final SessionEventModel csSessionEvent;

		validateParameterNotNullStandardMessage("asmEventData", asmEventData);

		validateParameterNotNullStandardMessage("asmEventData.Agent", asmEventData.getAgent());

		if (asmEventData.getEventType().equals(EventType.START_SESSION_EVENT))
		{
			csSessionEvent = createAndPopulateSessionEventInfo(SessionStartEventModel.class, asmEventData);

			((SessionStartEventModel) csSessionEvent).setCustomer(asmEventData.getCustomer());
		}

		else if (asmEventData.getEventType().equals(EventType.END_SESSION_EVENT))
		{
			csSessionEvent = createAndPopulateSessionEventInfo(SessionEndEventModel.class, asmEventData);

			((SessionEndEventModel) csSessionEvent).setCustomer(asmEventData.getCustomer());
		}
		else
		{
			LOG.debug(String.format("Event type [%s] has not been stored", asmEventData.getEventType()));
			return;
		}

		getModelService().save(csSessionEvent);
	}

	protected SessionEventModel createAndPopulateSessionEventInfo(final Class csSessionEventClass, final SessionEvent asmEventData)
	{

		if (asmEventData.getAgent() instanceof EmployeeModel)
		{
			final SessionEventModel csSessionEventModel = getModelService().create(csSessionEventClass);

			csSessionEventModel.setAgent((EmployeeModel) asmEventData.getAgent());
			csSessionEventModel.setEventTime(asmEventData.getCreatedAt());
			csSessionEventModel.setSessionID(UUID.randomUUID().toString());
			csSessionEventModel.setBaseSite(getBaseSiteService().getCurrentBaseSite());
			csSessionEventModel.setGroups(asmEventData.getAgentGroups());

			return csSessionEventModel;
		}
		else
		{
			throw new IllegalArgumentException("Wrong agentID value");
		}
	}

	/**
	 * @see CustomerSupportEventService#getAllEventsForAgent(EmployeeModel, EventType, Date, Date, PageableData, int)
	 */
	@Override
	public SearchPageData<SessionEventModel> getAllEventsForAgent(final EmployeeModel agent, final EventType eventType,
			final Date startDate, final Date endDate, final PageableData pageableData, final int limit)
	{
		return getCustomerSupportEventDao().findAllEventsByAgent(agent, eventType, startDate, endDate, pageableData, limit);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByEventsAndAgent(final EmployeeModel agent,
			final EventType eventType, final Date startDate, final Date endDate, final PageableData pageableData, final int limit)
	{
		return getCustomerSupportEventDao().findAllCustomersByEventsAndAgent(agent, eventType, startDate, endDate, pageableData,
				limit, true);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByEventsAndAgent(final EmployeeModel agent,
			final EventType eventType, final Date startDate, final Date endDate, final PageableData pageableData, final int limit, final boolean includeDisabledAccounts)
	{
		return getCustomerSupportEventDao().findAllCustomersByEventsAndAgent(agent, eventType, startDate, endDate, pageableData,
				limit, includeDisabledAccounts);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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

	protected CustomerSupportEventDao getCustomerSupportEventDao()
	{
		return customerSupportEventDAO;
	}

	@Required
	public void setCustomerSupportEventDao(final CustomerSupportEventDao customerSupportEventDAO)
	{
		this.customerSupportEventDAO = customerSupportEventDAO;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
