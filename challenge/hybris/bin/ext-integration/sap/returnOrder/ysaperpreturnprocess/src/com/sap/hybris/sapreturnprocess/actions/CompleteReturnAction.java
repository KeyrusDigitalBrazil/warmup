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
package com.sap.hybris.sapreturnprocess.actions;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.OrderReturnRecordHandler;
import de.hybris.platform.returns.model.OrderReturnRecordModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Update the {@link ReturnRequestModel} status to COMPLETED and finalize the corresponding {@link OrderReturnRecordModel} for this {@link ReturnRequestModel}<br/>
 */
public class CompleteReturnAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(CompleteReturnAction.class);
	private OrderReturnRecordHandler orderReturnRecordsHandler;

	@Override
	public void executeAction(final ReturnProcessModel process)
	{
		LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();
		returnRequest.setStatus(ReturnStatus.COMPLETED);
		returnRequest.getReturnEntries().stream().forEach(entry ->
		{
			entry.setStatus(ReturnStatus.COMPLETED);
			getModelService().save(entry);
		});
		getModelService().save(returnRequest);

		getOrderReturnRecordsHandler().finalizeOrderReturnRecordForReturnRequest(returnRequest);
	}


	protected OrderReturnRecordHandler getOrderReturnRecordsHandler()
	{
		return orderReturnRecordsHandler;
	}

	@Required
	public void setOrderReturnRecordsHandler(OrderReturnRecordHandler orderReturnRecordsHandler)
	{
		this.orderReturnRecordsHandler = orderReturnRecordsHandler;
	}
}
