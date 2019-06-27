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

package de.hybris.platform.apiregistryservices.action;


import de.hybris.platform.apiregistryservices.event.DynamicProcessEvent;
import de.hybris.platform.apiregistryservices.exceptions.BusinessEventParameterMissingException;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.processengine.definition.ActionDefinitionContext;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * The business process action which provides to extend business process with an external system
 */
public class DynamicProcessEventAction extends AbstractAction
{
	private static final Logger LOG = LoggerFactory.getLogger(DynamicProcessEventAction.class);
	private static final String EVENT_KEY ="BUSINESSEVENT";
	private EventService eventService;

	public enum Transition
	{
		OK;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final DynamicProcessEventAction.Transition transitions : DynamicProcessEventAction.Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final BusinessProcessModel process) throws RetryLaterException, Exception
	{

		final ActionDefinitionContext currentActionDefinitionContext = getCurrentActionDefinitionContext();
		final Optional<String> businessEvent = currentActionDefinitionContext.getParameter(EVENT_KEY);

		if(businessEvent.isPresent())
		{
			final DynamicProcessEvent dynamicProcessEvent = new DynamicProcessEvent();
			dynamicProcessEvent.setBusinessEvent(businessEvent.get());
			dynamicProcessEvent.setBusinessProcess(process);
			eventService.publishEvent(dynamicProcessEvent);
			LOG.debug("The dynamic process event [{}] is published for business process [{}].", businessEvent, process.getCode());

			return Transition.OK.toString();
		}

		final String errorMessage = String.format("The business event is not found for the business process [%s]!", process.getCode());
		throw new BusinessEventParameterMissingException(errorMessage);
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
