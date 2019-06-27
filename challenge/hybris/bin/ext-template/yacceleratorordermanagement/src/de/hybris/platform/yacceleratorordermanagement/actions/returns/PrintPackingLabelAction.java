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
 * Creates the packing label for the {@link de.hybris.platform.returns.model.ReturnRequestModel}
 */
public class PrintPackingLabelAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(PrintPackingLabelAction.class);

	@Override
	public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		// TODO: implement the logic for creating the packing label for Returns //NOSONAR
	}
}
