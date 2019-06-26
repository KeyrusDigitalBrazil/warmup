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
package de.hybris.platform.commerceservices.order.impl;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.dao.CommerceOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommerceOrderServiceTest
{
	@InjectMocks
	DefaultCommerceOrderService defaultCommerceOrderService = new DefaultCommerceOrderService();

	@Mock
	private CommerceOrderDao commerceOrderDao;

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfQuoteIsNull()
	{
		defaultCommerceOrderService.getOrderForQuote(null);
	}

	@Test
	public void shouldGetOrderForQuote()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(orderModel.getQuoteReference()).willReturn(quoteModel);
		given(commerceOrderDao.findOrderByQuote(quoteModel)).willReturn(orderModel);
		final OrderModel orderForQuote = defaultCommerceOrderService.getOrderForQuote(quoteModel);

		verify(commerceOrderDao).findOrderByQuote(quoteModel);
		Assert.assertEquals("passed quoteModel should be same as order.getQuoteReference", quoteModel,
				orderForQuote.getQuoteReference());
	}

	@Test
	public void shouldNotGetOrderForQuote()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(commerceOrderDao.findOrderByQuote(quoteModel)).willReturn(null);
		final OrderModel orderForQuote = defaultCommerceOrderService.getOrderForQuote(quoteModel);

		verify(commerceOrderDao).findOrderByQuote(quoteModel);
		Assert.assertNull("order should be null", orderForQuote);
	}
}
