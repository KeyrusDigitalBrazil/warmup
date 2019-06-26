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
package de.hybris.platform.sap.sapcreditcheck.businessobject;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;



/**
 *
 */
public interface SapCreditCheckBO
{

	/**
	 * checks if status of order is credit blocked
	 * @param orderCode
	 * @return a boolean flag indicating if the order is credit blocked
	 */
	abstract boolean checkOrderCreditBlocked(final String orderCode);

	/**
	 * 
	 * @param orderData
	 * @param soldTo
	 * @return
	 */
	abstract boolean checkCreditLimitExceeded(AbstractOrderData orderData, String soldTo);
}
