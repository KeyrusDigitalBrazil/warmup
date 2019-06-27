/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.platform.orderprocessing.events.ConsignmentProcessingEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Common functionality extracted for actions that are dealing with consignment workflow
 */
public abstract class AbstractConsignmentAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractConsignmentAction.class);
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	private EventService eventService;

	/**
	 * Retrieves a {@link ConsignmentProcessingEvent} out of the consignment process model
	 *
	 * @param process
	 * 		the {@link ConsignmentProcessModel}
	 * @return the newly retrieved {@link ConsignmentProcessingEvent}
	 */
	protected ConsignmentProcessingEvent getEvent(final ConsignmentProcessModel process)
	{
		return new ConsignmentProcessingEvent(process);
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

	protected WarehousingConsignmentWorkflowService getWarehousingConsignmentWorkflowService()
	{
		return warehousingConsignmentWorkflowService;
	}

	@Required
	public void setWarehousingConsignmentWorkflowService( final WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService)
	{
		this.warehousingConsignmentWorkflowService = warehousingConsignmentWorkflowService;
	}

}
