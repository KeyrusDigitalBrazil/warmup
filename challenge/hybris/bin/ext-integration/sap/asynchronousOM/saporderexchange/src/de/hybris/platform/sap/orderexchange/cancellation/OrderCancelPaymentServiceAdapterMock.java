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
package de.hybris.platform.sap.orderexchange.cancellation;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;


/**
 * This class provides a payment mock to be able to do an order cancellation
 */
public class OrderCancelPaymentServiceAdapterMock implements OrderCancelPaymentServiceAdapter
{

	@Override
	public void recalculateOrderAndModifyPayments(final OrderModel order)
	{
		// The mock does nothing, just allows to proceed in the process
	}

}
