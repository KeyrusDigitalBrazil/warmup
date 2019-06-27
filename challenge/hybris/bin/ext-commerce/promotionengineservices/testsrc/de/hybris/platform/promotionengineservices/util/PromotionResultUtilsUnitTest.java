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
package de.hybris.platform.promotionengineservices.util;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.internal.model.order.InMemoryCartModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionResultUtilsUnitTest
{
	@InjectMocks
	private PromotionResultUtils promotionResultUtils;

	@Mock
	private CartService cartService;

	@Mock
	private InMemoryCartModel sessionCart;

	@Mock
	private CartModel cart;

	@Test
	public void testGetReferredOrder()
	{
		final PromotionResultModel promotionResult = new PromotionResultModel();
		promotionResult.setOrder(cart);
		Assert.assertEquals(cart, promotionResultUtils.getOrder(promotionResult));
	}

	@Test
	public void testGetOrderByCode()
	{
		when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.TRUE);
		final String orderCode = "00001";
		when(sessionCart.getCode()).thenReturn(orderCode);
		when(cartService.getSessionCart()).thenReturn(sessionCart);
		final PromotionResultModel promotionResult = new PromotionResultModel();
		promotionResult.setOrderCode(orderCode);
		Assert.assertEquals(sessionCart, promotionResultUtils.getOrder(promotionResult));
	}

	@Test
	public void testGetOrderHasSessionCartWithWrongCode()
	{
		when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.TRUE);
		when(sessionCart.getCode()).thenReturn("00001");
		when(cartService.getSessionCart()).thenReturn(sessionCart);
		final PromotionResultModel promotionResult = new PromotionResultModel();
		promotionResult.setOrderCode("00002");
		Assert.assertEquals(null, promotionResultUtils.getOrder(promotionResult));
	}

	@Test
	public void testGetOrderHasNoSessionCart()
	{
		when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.FALSE);
		when(cartService.getSessionCart()).thenReturn(sessionCart);
		final PromotionResultModel promotionResult = new PromotionResultModel();
		Assert.assertEquals(null, promotionResultUtils.getOrder(promotionResult));
	}
}
