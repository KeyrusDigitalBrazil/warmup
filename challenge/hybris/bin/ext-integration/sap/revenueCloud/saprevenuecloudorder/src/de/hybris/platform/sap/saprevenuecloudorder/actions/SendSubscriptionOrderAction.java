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
package de.hybris.platform.sap.saprevenuecloudorder.actions;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudOrderOutboundService;
import de.hybris.platform.task.RetryLaterException;

public class SendSubscriptionOrderAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(SendSubscriptionOrderAction.class);

	private BusinessProcessService businessProcessService;
	private SapRevenueCloudOrderOutboundService sapRevenueCloudOrderOutboundService;
	private static final String SUCCESS = "success";
	private static final String RESPONSE_STATUS = "responseStatus";

	
	@Override
	public Transition executeAction(final OrderProcessModel process) throws RetryLaterException {
		OrderModel order = process.getOrder();
		getSapRevenueCloudOrderOutboundService().sendOrder(order).subscribe(responseEntityMap ->
		{
			if (isSentSuccessfully(responseEntityMap))
			{
				LOG.info("Order " + order.getCode() + " published successfully to CPI.");
				order.setRevenueCloudOrderId(getPropertyValue(responseEntityMap, "revenueCloudOrderId"));
				getModelService().save(order);
			}
			else
			{
				LOG.info("Failed to publish order " + order.getCode() + " to CPI.");
			}

		},
		error ->{
			LOG.error(error);
		}
		);	
		
		return Transition.OK;
	}

	protected boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		return (SUCCESS.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS))
				&& responseEntityMap.getStatusCode().is2xxSuccessful()) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	protected String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{
		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		final String responseKey = next.toString();
		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
		return propertyValue.toString();
	}

	/**
	 * @return the sapRevenueCloudOrderOutboundService
	 */
	public SapRevenueCloudOrderOutboundService getSapRevenueCloudOrderOutboundService() {
		return sapRevenueCloudOrderOutboundService;
	}

	/**
	 * @param sapRevenueCloudOrderOutboundService the sapRevenueCloudOrderOutboundService to set
	 */
	public void setSapRevenueCloudOrderOutboundService(
			SapRevenueCloudOrderOutboundService sapRevenueCloudOrderOutboundService) {
		this.sapRevenueCloudOrderOutboundService = sapRevenueCloudOrderOutboundService;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}

	/**
	 * @param businessProcessService the businessProcessService to set
	 */
	public void setBusinessProcessService(BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
	}
}
