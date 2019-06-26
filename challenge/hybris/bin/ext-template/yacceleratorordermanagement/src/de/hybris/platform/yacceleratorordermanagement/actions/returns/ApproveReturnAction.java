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

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


/**
 * Approves the {@link de.hybris.platform.returns.model.ReturnRequestModel} by updating the {@link ReturnRequestModel#STATUS}
 * recalculate the return if quantity approved is not same as quantity requested originally and redirects the process.
 */
public class ApproveReturnAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ApproveReturnAction.class);

	@Override
	public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		ReturnRequestModel returnRequest = process.getReturnRequest();
		returnRequest.setStatus(ReturnStatus.WAIT);
		returnRequest.getReturnEntries().forEach(entry -> {
			entry.setStatus(ReturnStatus.WAIT);
			getModelService().save(entry);
		});
		getModelService().saveAll(returnRequest);
		LOG.debug("Process: {} transitions to printReturnLabelAction", process.getCode());
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

}
