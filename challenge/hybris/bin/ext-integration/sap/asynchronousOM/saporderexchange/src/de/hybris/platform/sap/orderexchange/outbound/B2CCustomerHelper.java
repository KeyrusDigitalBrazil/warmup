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
package de.hybris.platform.sap.orderexchange.outbound;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Helper for synchronizing the B2C customer replication with the order replication process
 */
@SuppressWarnings("javadoc")
public interface B2CCustomerHelper
{
	/**
	 * Determine the SAP customer ID of the customer who created the order 
	 * @param order
	 * @return SAP Customer ID
	 */
	String determineB2CCustomer(OrderModel order);

	void processWaitingCustomerOrders(final String customerID);

}
