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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.services.B2BSaleQuoteService;
import de.hybris.platform.b2b.strategies.PlaceQuoteOrderStrategy;
import de.hybris.platform.core.model.order.OrderModel;


/**
 * @deprecated Since 6.3. Please see quote functionality from commerce.
 *
 * Default implementation for {@link B2BSaleQuoteService}
 * 
 * @spring.bean b2bSaleQuoteService
 */
@Deprecated
public class DefaultB2BSaleQuoteService implements B2BSaleQuoteService
{

	private PlaceQuoteOrderStrategy placeQuoteOrderStrategy;


	@Override
	public void placeQuoteOrder(final OrderModel order)
	{
		getPlaceQuoteOrderStrategy().placeQuoteOrder(order);
	}

	@Override
	public void placeOrderFromRejectedQuote(final OrderModel order)
	{
		getPlaceQuoteOrderStrategy().placeOrderFromRejectedQuote(order);
	}

	protected PlaceQuoteOrderStrategy getPlaceQuoteOrderStrategy()
	{
		return placeQuoteOrderStrategy;
	}


	public void setPlaceQuoteOrderStrategy(final PlaceQuoteOrderStrategy placeQuoteOrderStrategy)
	{
		this.placeQuoteOrderStrategy = placeQuoteOrderStrategy;
	}

}
