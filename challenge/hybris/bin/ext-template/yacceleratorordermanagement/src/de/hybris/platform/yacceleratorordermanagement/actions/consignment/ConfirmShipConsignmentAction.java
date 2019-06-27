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

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;


/**
 * Update the {@link ConsignmentModel} status to {@link ConsignmentStatus#SHIPPED}.
 */
public class ConfirmShipConsignmentAction extends AbstractConsignmentAction
{

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());
		final ConsignmentModel consignment = process.getConsignment();
		consignment.setStatus(ConsignmentStatus.SHIPPED);
		save(consignment);

		getEventService().publishEvent(getEvent(process));
	}

}
