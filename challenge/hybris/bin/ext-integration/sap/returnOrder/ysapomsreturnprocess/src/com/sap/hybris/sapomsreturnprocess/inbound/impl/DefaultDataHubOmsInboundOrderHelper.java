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
package com.sap.hybris.sapomsreturnprocess.inbound.impl;

import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapmodel.model.SAPReturnRequestsModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.constants.SapreturnsexchangeConstants;
import com.sap.hybris.returnsexchange.inbound.impl.DefaultDataHubInboundOrderHelper;
import com.sap.hybris.sapomsreturnprocess.constants.YsapomsreturnprocessConstants;
import com.sap.hybris.sapomsreturnprocess.enums.SAPReturnRequestOrderStatus;
import com.sap.hybris.returnsexchange.inbound.DataHubInboundCancelOrderHelper;


public class DefaultDataHubOmsInboundOrderHelper extends DefaultDataHubInboundOrderHelper implements DataHubInboundCancelOrderHelper
{

	private static final Logger LOGGER = Logger.getLogger(DefaultDataHubOmsInboundOrderHelper.class);

	private BusinessProcessService businessProcessService;
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;

	@Override
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public ModelService getModelService()
	{
		return modelService;
	}

	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	@SuppressWarnings("javadoc")
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Override
	@SuppressWarnings("javadoc")
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Override
	public void processOrderConfirmationFromDataHub(final String sapReturnRequest)
	{
		final SAPReturnRequestsModel sapReturnRequestModel = readReturnOrderFromSAPOrder(sapReturnRequest);
		final String orderNumber = sapReturnRequestModel.getReturnRequest().getCode();
		sapReturnRequestModel.setSapReturnRequestOrderStatus(SAPReturnRequestOrderStatus.CONFIRMED_FROM_BACKEND);
		getModelService().save(sapReturnRequestModel);

		String eventName = null;
		if (!orderNumber.isEmpty())
		{
			eventName = SapreturnsexchangeConstants.RETURNORDER_CONFIRMATION_EVENT + orderNumber;
		}
		getBusinessProcessService().triggerEvent(
				BusinessProcessEvent.builder(eventName).withChoice("confirmedReturnFromBackend").build());
	}
	
	@Override
	public void processCancelOrderConfirmationFromDataHub(final String sapReturnRequest)  
	{
		final SAPReturnRequestsModel sapReturnRequestModel = readReturnOrderFromSAPOrder(sapReturnRequest);
		final String orderNumber = sapReturnRequestModel.getReturnRequest().getCode();
		sapReturnRequestModel.setSapReturnRequestOrderStatus(SAPReturnRequestOrderStatus.CANCELLED_FROM_BACKEND);
		getModelService().save(sapReturnRequestModel);

		String eventName = null;
		if (!orderNumber.isEmpty())
		{
			eventName = SapreturnsexchangeConstants.RETURNORDER_CANCELLATION_CONFIRMATION_EVENT + orderNumber;
		}
		getBusinessProcessService().triggerEvent(eventName);
	}
	
	@Override
	public void processOrderDeliveryNotififcationFromDataHub(final String orderCode, final String delivInfo)
	{
		final SAPReturnRequestsModel sapReturnRequest = readReturnOrderFromSAPOrder(orderCode);
		sapReturnRequest.setIsDelivered(true);
		final String deliveryDocumentNo = determineDeliveryDocumentNo(delivInfo);
		final String returnRequestNumber = sapReturnRequest.getReturnRequest().getCode();
		final String sapReturnRequestNumber = sapReturnRequest.getCode();
		String eventName = null;
		if (deliveryDocumentNo != null && !deliveryDocumentNo.isEmpty())
		{
			final ReturnRequestModel requestModel = readReturnOrder(returnRequestNumber);
			updateDeliveryDocumentNumber(deliveryDocumentNo, sapReturnRequestNumber, requestModel);
			eventName = SapreturnsexchangeConstants.RETURNORDER_GOOD_EVENT + returnRequestNumber;

			getBusinessProcessService().triggerEvent(
					BusinessProcessEvent.builder(eventName).withChoice("receivedNotificationForGoods").build());
			LOGGER.info(eventName + " has been triggered");
		}
		else
		{
			LOGGER.info("No Delivery Document Received from BackEnd system for SAP return request : " + sapReturnRequestNumber);
		}
		getModelService().save(sapReturnRequest);
	}



	protected void updateDeliveryDocumentNumber(final String deliveryDocumentNo, final String sapReturnRequestNumber,
			final ReturnRequestModel requestModel)
	{
		List<String> delDocNumbers = new ArrayList(requestModel.getDeliveryDocNumbers());
		if (delDocNumbers == null || delDocNumbers.isEmpty())
		{
			delDocNumbers = new ArrayList<String>();
		}
		delDocNumbers.add(sapReturnRequestNumber + YsapomsreturnprocessConstants.PIPE + deliveryDocumentNo);
		requestModel.setDeliveryDocNumbers(delDocNumbers);
		getModelService().save(requestModel);




	}


	private SAPReturnRequestsModel readReturnOrderFromSAPOrder(final String sapOrderCode)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {o:pk} FROM {SAPReturnRequests AS o} WHERE  {o.code} = ?code");

		flexibleSearchQuery.addQueryParameter("code", sapOrderCode);

		final SAPReturnRequestsModel sapOrder = getFlexibleSearchService().searchUnique(flexibleSearchQuery);

		if (sapOrder == null)
		{

			throw new IllegalArgumentException(String
					.format("Error while processing inbound IDoc with SAP order number [%s] that does not exist!", sapOrderCode));
		}
		sapOrder.setSapReturnRequestOrderStatus(SAPReturnRequestOrderStatus.CONFIRMED_FROM_BACKEND);
		getModelService().save(sapOrder);

		return sapOrder;
	}



}
