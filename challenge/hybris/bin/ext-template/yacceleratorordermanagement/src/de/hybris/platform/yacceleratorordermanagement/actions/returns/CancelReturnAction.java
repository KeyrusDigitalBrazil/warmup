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
import de.hybris.platform.returns.dao.OrderReturnDao;
import de.hybris.platform.returns.model.OrderReturnRecordModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Cancels the {@link de.hybris.platform.returns.model.ReturnRequestModel}
 */
public class CancelReturnAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CancelReturnAction.class);

	private OrderReturnDao orderReturnDao;

	@Override
	public void executeAction(ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.debug("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		ReturnRequestModel returnRequest = process.getReturnRequest();
		returnRequest.setStatus(ReturnStatus.CANCELED);
		returnRequest.getReturnEntries().forEach(entry -> {
			entry.setStatus(ReturnStatus.CANCELED);
			getModelService().save(entry);
		});
		getModelService().save(returnRequest);

		if (returnRequest.getOrder().getReturnRequests().stream().allMatch(
				myReturnRequest -> myReturnRequest.getStatus().equals(ReturnStatus.CANCELED) || myReturnRequest.getStatus()
						.equals(ReturnStatus.COMPLETED)))
		{
			OrderReturnRecordModel returnRecord = orderReturnDao.getOrderReturnRecord(returnRequest.getOrder());
			returnRecord.setInProgress(false);
			getModelService().save(returnRecord);
		}

		LOG.debug("Process: {} transitions to success ", process.getCode());
	}

	protected OrderReturnDao getOrderReturnDao()
	{
		return orderReturnDao;
	}

	@Required
	public void setOrderReturnDao(OrderReturnDao orderReturnDao)
	{
		this.orderReturnDao = orderReturnDao;
	}
}
