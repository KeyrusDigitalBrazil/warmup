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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.model.EscalationTaskModel;
import de.hybris.platform.b2b.services.B2BEscalationService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A task which handles order approval escalation.
 */
public class EscalationTaskRunner implements TaskRunner<EscalationTaskModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(EscalationTaskRunner.class);

	/** The b2b escalation service. */
	private B2BEscalationService b2bEscalationService;

	@Override
	public void run(final TaskService taskService, final EscalationTaskModel task) throws RetryLaterException
	{
		final OrderModel order = task.getOrder();
		b2bEscalationService.escalate(order);
	}

	@Override
	public void handleError(final TaskService taskService, final EscalationTaskModel task, final Throwable error)
	{
		LOG.error("handle error here: " + task, error);
	}

	/**
	 * Sets the b2b escalation service.
	 * 
	 * @param b2bEscalationService
	 *           the b2bEscalationService to set
	 */
	@Required
	public void setB2bEscalationService(final B2BEscalationService b2bEscalationService)
	{
		this.b2bEscalationService = b2bEscalationService;
	}
}
