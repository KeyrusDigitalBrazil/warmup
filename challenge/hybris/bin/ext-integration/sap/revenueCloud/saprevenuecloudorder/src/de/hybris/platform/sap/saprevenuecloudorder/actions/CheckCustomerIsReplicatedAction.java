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

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerOutboundService;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;

/**
 * Check if customer got replicated to external system.
 */
public class CheckCustomerIsReplicatedAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

	private SapRevenueCloudCustomerOutboundService sapRevenueCloudCustomerOutboundService;

	@Override
	public de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition executeAction(
			OrderProcessModel process) throws RetryLaterException, Exception {
		OrderModel order = process.getOrder();
		if (order != null) {
			CustomerModel customerModel = (CustomerModel) order.getUser();
			Boolean isReplicated = customerModel.getSapIsReplicated();
			if (Boolean.TRUE.equals(isReplicated)) {
				return Transition.OK;
			} 
			else {
				Customer customerJson = new Customer();
				customerJson.setCustomerId(customerModel.getCustomerID());
				getSapRevenueCloudCustomerOutboundService().publishCustomerUpdate(customerJson);
				return Transition.OK;
			}
		}
		return Transition.NOK;
	}

	/**
	 * @return the sapRevenueCloudCustomerOutboundService
	 */
	public SapRevenueCloudCustomerOutboundService getSapRevenueCloudCustomerOutboundService() {
		return sapRevenueCloudCustomerOutboundService;
	}

	/**
	 * @param sapRevenueCloudCustomerOutboundService the sapRevenueCloudCustomerOutboundService to set
	 */
	public void setSapRevenueCloudCustomerOutboundService(
			SapRevenueCloudCustomerOutboundService sapRevenueCloudCustomerOutboundService) {
		this.sapRevenueCloudCustomerOutboundService = sapRevenueCloudCustomerOutboundService;
	}

	
}
