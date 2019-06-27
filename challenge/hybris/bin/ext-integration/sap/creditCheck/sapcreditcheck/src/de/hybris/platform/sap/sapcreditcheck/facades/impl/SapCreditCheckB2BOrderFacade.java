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
package de.hybris.platform.sap.sapcreditcheck.facades.impl;

import java.util.IllegalFormatException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BOrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcreditcheck.exceptions.SapCreditCheckException;
import de.hybris.platform.sap.sapcreditcheck.service.SapCreditCheckService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
/**
 * SapCreditCheckB2BOrderFacade for saporder related facilities
 */
public class SapCreditCheckB2BOrderFacade extends DefaultB2BOrderFacade{ 

	static final private Logger sapLogger = Logger.getLogger(SapCreditCheckB2BOrderFacade.class.getName());
	private static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with guid %s not found for current user in current BaseStore";
	private SapCreditCheckService sapCreditCheckService;// NOPMD
	
	public SapCreditCheckService getSapCreditCheckService() {
		return sapCreditCheckService;
	}
	
	@Required
	public void setSapCreditCheckService(SapCreditCheckService sapCreditCheckService) {
		this.sapCreditCheckService = sapCreditCheckService;
	}

	@Override
	public OrderData getOrderDetailsForCode(String code) {
		
		OrderData orderData = null;
		try
		{
			final OrderModel orderModel = getCustomerAccountService().getOrderForCode(
					(CustomerModel) getUserService().getCurrentUser(), code, getBaseStoreService().getCurrentBaseStore());
			orderData = getOrderConverter().convert(orderModel);
		}
		catch (final ModelNotFoundException e)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, code));
		}
		
	   boolean creditBlockStatus = true; 
       try {
    	   creditBlockStatus = getSapCreditCheckService().checkOrderCreditBlocked(code); 
       }
       catch (final SapCreditCheckException | IllegalFormatException ex){
	    	sapLogger.error(String.format("Unable to check the credit status for the order %s from the ERP backend! ", code),ex);				
	   }
       catch(final Exception ex){
    	   sapLogger.error(String.format("Unable to check the credit status for the order %s from the ERP backend! ", code),ex);
       }
       
       if (creditBlockStatus) {
			orderData.setStatus(OrderStatus.PENDING_APPROVAL_FROM_MERCHANT);
			orderData.setStatusDisplay("pending.merchant.approval");
		}
		return orderData;
	}
		
}
