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
package de.hybris.platform.b2b.strategies;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * @deprecated Since 6.3. Please see quote functionality from commerce.
 */
@Deprecated
public interface PlaceQuoteOrderStrategy
{
	public void placeQuoteOrder(final OrderModel order);

	public void placeOrderFromRejectedQuote(OrderModel order);
}
