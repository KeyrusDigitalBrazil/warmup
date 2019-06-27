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

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.yacceleratorordermanagement.constants.YAcceleratorOrderManagementConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Update the consignment process to done and notify the corresponding order process that it is completed.
 */
public class ConsignmentProcessEndAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ConsignmentProcessEndAction.class);

	private BusinessProcessService businessProcessService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		process.setDone(true);
		save(process);
		LOG.debug("Process: {} wrote DONE marker", process.getCode() );

		final String eventId =
				process.getParentProcess().getCode() + "_" + YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME;

		final BusinessProcessEvent event = BusinessProcessEvent.builder(eventId).withChoice("consignmentProcessEnded").build();
		getBusinessProcessService().triggerEvent(event);

		LOG.debug("Process: {} fired event {}", process.getCode(), YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME);
	}
}
