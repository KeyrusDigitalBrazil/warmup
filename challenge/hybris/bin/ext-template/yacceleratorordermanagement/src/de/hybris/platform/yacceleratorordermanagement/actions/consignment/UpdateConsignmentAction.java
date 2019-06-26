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
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Updates a consignment status to a given status.
 */
public class UpdateConsignmentAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(UpdateConsignmentAction.class);

	private ConsignmentStatus status;

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());
		final ConsignmentModel consignment = process.getConsignment();
		consignment.setStatus(status);
		save(consignment);
	}

	protected ConsignmentStatus getStatus()
	{
		return status;
	}

	@Required
	public void setStatus(final ConsignmentStatus status)
	{
		this.status = status;
	}
}
