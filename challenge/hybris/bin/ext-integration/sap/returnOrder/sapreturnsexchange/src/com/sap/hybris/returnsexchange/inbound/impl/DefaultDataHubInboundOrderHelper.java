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
package com.sap.hybris.returnsexchange.inbound.impl;

import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.constants.SapreturnsexchangeConstants;
import com.sap.hybris.returnsexchange.inbound.DataHubInboundOrderHelper;


/**
 *
 */
public class DefaultDataHubInboundOrderHelper implements DataHubInboundOrderHelper
{
	private BusinessProcessService businessProcessService;
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@SuppressWarnings("javadoc")
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Override
	public void processOrderConfirmationFromDataHub(final String orderNumber)
	{

		String eventName = null;
		if (!orderNumber.isEmpty())
		{
			eventName = SapreturnsexchangeConstants.RETURNORDER_CONFIRMATION_EVENT + orderNumber;
		}
		getBusinessProcessService().triggerEvent(eventName);
	}

	@Override
	public void processOrderDeliveryNotififcationFromDataHub(final String orderNumber, final String delivInfo)
	{
		final String incomingDeliveryDocumentNo = determineDeliveryDocumentNo(delivInfo);
		String eventName = null;
		if (incomingDeliveryDocumentNo != null && !incomingDeliveryDocumentNo.isEmpty())
		{
			final ReturnRequestModel requestModel = readReturnOrder(orderNumber);

			List<String> alreadyArrivedDelDocNumbers = new ArrayList<>(requestModel.getDeliveryDocNumbers());
			if (alreadyArrivedDelDocNumbers == null || alreadyArrivedDelDocNumbers.isEmpty())
			{
				alreadyArrivedDelDocNumbers = new ArrayList<String>();
			}
			if(!alreadyArrivedDelDocNumbers.contains(incomingDeliveryDocumentNo)){
				alreadyArrivedDelDocNumbers.add(incomingDeliveryDocumentNo);
			}

			requestModel.setDeliveryDocNumbers(alreadyArrivedDelDocNumbers);

			getModelService().save(requestModel);
			eventName = SapreturnsexchangeConstants.RETURNORDER_GOOD_EVENT + orderNumber;
		}
		getBusinessProcessService().triggerEvent(eventName);
	}

	public String determineDeliveryDocumentNo(final String deliveryInfo)
	{
		String result = null;
		final int delviLength = deliveryInfo.length();
		final int delivIndex = deliveryInfo.indexOf(SapreturnsexchangeConstants.SEPERATING_SYMBOL);
		if (delivIndex >= 0)
		{
			result = deliveryInfo.substring(delivIndex, delviLength);
		}

		return result;
	}

	protected ReturnRequestModel readReturnOrder(final String returnOrderCode)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {rr:pk} FROM {ReturnRequest AS rr} WHERE  {rr.code} = ?code");
		flexibleSearchQuery.addQueryParameter("code", returnOrderCode);

		final ReturnRequestModel returnOrder = getFlexibleSearchService().searchUnique(flexibleSearchQuery);
		if (returnOrder == null)
		{
			final String msg = "Error while IDoc processing. Called with not existing order for order code : " + returnOrderCode;
			throw new IllegalArgumentException(msg);
		}
		return returnOrder;
	}
}
