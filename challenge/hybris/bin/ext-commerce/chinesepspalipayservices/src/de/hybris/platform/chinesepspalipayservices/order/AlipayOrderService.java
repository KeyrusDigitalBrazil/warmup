/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesepspalipayservices.order;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Provides method to get order
 */
public interface AlipayOrderService
{
	/**
	 * Gets order by order code
	 *
	 * @param code
	 *           the order code
	 * @return OrderModel if order found or returns null otherwise
	 */
	OrderModel getOrderByCode(final String code);

}
