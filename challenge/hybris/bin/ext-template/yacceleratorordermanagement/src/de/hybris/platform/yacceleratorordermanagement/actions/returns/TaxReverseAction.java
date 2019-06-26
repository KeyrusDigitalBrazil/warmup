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
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.util.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Reverse tax calculation and update the {@link ReturnRequestModel} status to TAX_REVERSAL_FAILED or TAX_REVERSED.
 */
public class TaxReverseAction extends AbstractSimpleDecisionAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(TaxReverseAction.class);
	public static final String TAX_REVERSE_FORCE_FAILURE = "yacceleratorordermanagement.reverse.tax.force.failure";

	@Override
	public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.debug("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();

		// TODO: implement tax reverse //NOSONAR

		final boolean testFailCapture = Config.getBoolean(TAX_REVERSE_FORCE_FAILURE, false);
		if (testFailCapture)
		{
			setReturnRequestStatus(returnRequest,ReturnStatus.TAX_REVERSAL_FAILED);
			return Transition.NOK;
		} else
		{
			setReturnRequestStatus(returnRequest,ReturnStatus.TAX_REVERSED);
			return Transition.OK;
		}
	}

	/**
	 * Update the return status for all return entries in {@link ReturnRequestModel}
	 *
	 * @param returnRequest
	 *           - the return request
	 * @param status
	 *           - the return status
	 */
	protected void setReturnRequestStatus(final ReturnRequestModel returnRequest, final ReturnStatus status)
	{
		returnRequest.setStatus(status);
		returnRequest.getReturnEntries().forEach(entry -> {
			entry.setStatus(status);
			getModelService().save(entry);
		});
		getModelService().save(returnRequest);
	}

}
