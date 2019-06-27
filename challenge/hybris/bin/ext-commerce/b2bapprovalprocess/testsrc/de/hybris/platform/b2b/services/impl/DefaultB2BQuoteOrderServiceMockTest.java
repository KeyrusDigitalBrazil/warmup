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

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.strategies.PlaceQuoteOrderStrategy;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


/**
 * Created by IntelliJ IDEA. User: deniszontak Date: 5/12/11 Time: 10:57 AM To change this template use File | Settings
 * | File Templates.
 */
@UnitTest
public class DefaultB2BQuoteOrderServiceMockTest extends HybrisMokitoTest
{
	private final DefaultB2BSaleQuoteService defaultB2BQuoteOrderService = new DefaultB2BSaleQuoteService();

	@Mock
	private PlaceQuoteOrderStrategy placeQuoteOrderStrategy;

	@Before
	public void setUp()
	{
		defaultB2BQuoteOrderService.setPlaceQuoteOrderStrategy(placeQuoteOrderStrategy);
	}

	@Test
	public void testPlaceQuoteOrder() throws Exception
	{
		final OrderModel mockOrderModel = mock(OrderModel.class);
		defaultB2BQuoteOrderService.placeQuoteOrder(mockOrderModel);
	}

	@Test
	public void testPlaceOrderFromRejectedQuote() throws Exception
	{
		final OrderModel mockOrderModel = mock(OrderModel.class);
		placeQuoteOrderStrategy.placeOrderFromRejectedQuote(mockOrderModel);
	}
}
