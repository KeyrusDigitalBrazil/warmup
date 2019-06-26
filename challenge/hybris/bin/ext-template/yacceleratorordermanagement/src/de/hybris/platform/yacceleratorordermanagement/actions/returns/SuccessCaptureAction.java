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

package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.task.RetryLaterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Business logic to execute when payment capture was successful.
 */
public class SuccessCaptureAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SuccessCaptureAction.class);

	@Override
	public void executeAction(final ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.debug("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		// TODO: implement the logic of the success capture payment //NOSONAR
	}
}
