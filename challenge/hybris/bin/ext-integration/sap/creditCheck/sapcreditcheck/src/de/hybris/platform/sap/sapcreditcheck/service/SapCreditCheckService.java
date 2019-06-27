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
package de.hybris.platform.sap.sapcreditcheck.service;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData; 


/**
 * SapCreditCheckService interface
 */
public interface SapCreditCheckService 
{
	/**
	 *check if credit limit is exceeded
         * @param order AbstractOrderData
         * @return true if the credit limit has been exceeded
	 */
	abstract boolean checkCreditLimitExceeded(AbstractOrderData order);
	
	
	/**
	 * Check if the order is blocked in ERP due to exceeding credit limit
         * @param orderCode String order code
	 * @return true if order is credit blocked
	 */
	abstract boolean checkOrderCreditBlocked(String orderCode);
	
}
