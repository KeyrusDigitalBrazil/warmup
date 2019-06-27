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
package de.hybris.platform.sap.ysapomsfulfillment.actions.consignment;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import de.hybris.platform.sap.ysapomsfulfillment.constants.YsapomsfulfillmentConstants;

/**
 * Update the consignment process to done and notify the corresponding order
 * process that it is completed.
 */
public class SapConsignmentProcessEndAction extends AbstractProceduralAction<SapConsignmentProcessModel> {
	private static final Logger LOG = Logger.getLogger(SapConsignmentProcessEndAction.class);

	private BusinessProcessService businessProcessService;

	protected BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
	}

	@Override
	public void executeAction(final SapConsignmentProcessModel process) {

		LOG.info(String.format("Process: %s in step %s", process.getCode(), getClass().getSimpleName()));

		process.setDone(true);
		save(process);

		LOG.info(String.format("Process: %s wrote DONE marker", process.getCode()));

		final String eventId = new StringBuilder()//
				.append(process.getParentProcess().getCode())//
				.append(YsapomsfulfillmentConstants.UNDERSCORE)//
				.append(YsapomsfulfillmentConstants.ORDER_ACTION_EVENT_NAME)//
				.toString();

		final BusinessProcessEvent event = BusinessProcessEvent.builder(eventId)
				.withChoice(YsapomsfulfillmentConstants.CONSIGNMENT_PROCESS_ENDED).build();

		getBusinessProcessService().triggerEvent(event);

		LOG.info(String.format("Process: %s fired event %s", process.getCode(),
				YsapomsfulfillmentConstants.ORDER_ACTION_EVENT_NAME));
	}

}
