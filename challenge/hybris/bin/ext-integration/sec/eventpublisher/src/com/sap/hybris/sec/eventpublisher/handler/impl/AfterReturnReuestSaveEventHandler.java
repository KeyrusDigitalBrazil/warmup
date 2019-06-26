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
package com.sap.hybris.sec.eventpublisher.handler.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.order.ReturnRequest;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.tx.AfterSaveEvent;



public class AfterReturnReuestSaveEventHandler extends DefaultSaveEventHandler
{
	private static final Logger LOGGER = LogManager.getLogger(AfterReturnReuestSaveEventHandler.class);

	private String randomCode;
	private Populator<ReturnRequestModel, ReturnRequest> returnRequestPopulator;

	@Override
	public void handleEvent(final AfterSaveEvent event)
	{
		final PK pk = event.getPk();
		if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
				&& (getModelService().get(pk) instanceof ReturnRequestModel))
		{
			final ReturnRequestModel returnRequestModel = (ReturnRequestModel) getModelService().get(pk);
			try
			{
				createOrUpdateRetrunRequest(returnRequestModel, event.getType());

			}
			catch (Exception e)
			{
				LOGGER.error("Failed to publish Order event", e);
			}
		}
	}

	/**
	 * @param resData
	 * @return boolean
	 */
	protected boolean isWebSocketReplyEligible(final AfterSaveEvent event)
	{
		return (event.getType() == AfterSaveEvent.CREATE) || (event.getType() == AfterSaveEvent.UPDATE);

	}

	public String getCode()
	{
		final String copy = randomCode;
		randomCode = null;
		return copy;
	}

	/**
	 * @param orderModel
	 */
	private ResponseData createOrUpdateRetrunRequest(final ReturnRequestModel returnRequestModel, final int eventType)
			throws Exception
	{
		final ReturnRequest returnRequest = new ReturnRequest();
		if (eventType == AfterSaveEvent.CREATE)
		{
			returnRequest.setEventStatus(EventpublisherConstants.RETURN_CREATED);
		}
		else
		{
			returnRequest.setEventStatus(EventpublisherConstants.RETURN_UPDATED);
		}
		getReturnRequestPopulator().populate(returnRequestModel, returnRequest);
		return getPublisher().publishJson(getFinalJson(returnRequestModel, returnRequest.toString()), returnRequestModel.getItemtype());


	}

	/**
	 * @return the orderPopulator
	 */
	public Populator<ReturnRequestModel, ReturnRequest> getReturnRequestPopulator()
	{
		return returnRequestPopulator;
	}


	/**
	 * @param returnRequestPopulator
	 *           the orderPopulator to set
	 */
	public void setReturnRequestPopulator(final Populator<ReturnRequestModel, ReturnRequest> returnRequestPopulator)
	{
		this.returnRequestPopulator = returnRequestPopulator;
	}

}
