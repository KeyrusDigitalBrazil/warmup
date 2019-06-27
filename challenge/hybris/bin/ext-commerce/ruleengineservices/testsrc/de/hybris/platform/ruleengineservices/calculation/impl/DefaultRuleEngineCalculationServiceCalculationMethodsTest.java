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
package de.hybris.platform.ruleengineservices.calculation.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.AbstractCharge.ChargeType;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.platform.ruleengineservices.calculation.NumberedLineItem;
import de.hybris.platform.ruleengineservices.rao.AbstractOrderRAO;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


@UnitTest
public class DefaultRuleEngineCalculationServiceCalculationMethodsTest
{
	@Mock
	private Converter<AbstractOrderRAO, Order> abstractOrderRaoToOrderConverter;

	@InjectMocks
	private DefaultRuleEngineCalculationService service;

	private final Currency currency = new Currency("GBP", 2);
	private final AbstractOrderRAO defaultCartRao = new AbstractOrderRAO();

	@Before
	public void setUp()
	{
		service = new DefaultRuleEngineCalculationService();
		initMocks(this);
	}

	/**
	 * Test that the calculate totals method uses the converter to create the order, and calls the recalculateTotals
	 * method
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalculateTotals() throws Exception
	{
		final Order cart = new Order(currency, null);
		final DefaultRuleEngineCalculationService serviceSpy = spy(service);
		when(abstractOrderRaoToOrderConverter.convert(defaultCartRao)).thenReturn(cart);

		serviceSpy.calculateTotals(defaultCartRao);

		verify(abstractOrderRaoToOrderConverter).convert(defaultCartRao);
		verify(serviceSpy).recalculateTotals(defaultCartRao, cart);
	}

	@Test
	public void testRecalculateTotalsWithNoCartLineItems() throws Exception
	{
		final Order cart = mock(Order.class);
		final BigDecimal total = BigDecimal.valueOf(20, 2);
		final BigDecimal subtotal = BigDecimal.valueOf(10, 2);
		final BigDecimal shippingCost = BigDecimal.valueOf(10, 2);
		final BigDecimal paymentCost = BigDecimal.valueOf(10, 2);
		stubCartValues(cart, total, subtotal, shippingCost, paymentCost);

		final DefaultRuleEngineCalculationService serviceSpy = spy(service);

		serviceSpy.recalculateTotals(defaultCartRao, cart);

		assertCartRaoBasics(defaultCartRao, total, subtotal, shippingCost, paymentCost);
		verify(serviceSpy, times(0)).findLineItem(anyObject(), anyObject());

	}

	@Test
	public void testRecalculateTotalsWithIllegalCartLineItem() throws Exception
	{
		final OrderEntryRAO entryRao = createOrderEntryRAO();
		defaultCartRao.setEntries(Collections.singleton(entryRao));

		final Order cart = mock(Order.class);
		final BigDecimal total = BigDecimal.valueOf(200, 2);
		final BigDecimal subtotal = BigDecimal.valueOf(300, 2);
		final BigDecimal shippingCost = BigDecimal.valueOf(400, 2);
		final BigDecimal paymentCost = BigDecimal.valueOf(500, 2);
		stubCartValues(cart, total, subtotal, shippingCost, paymentCost);

		final BigDecimal lineCost = BigDecimal.valueOf(100, 2);

		final DefaultRuleEngineCalculationService serviceSpy = spy(service);

		try
		{
			serviceSpy.recalculateTotals(defaultCartRao, cart);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("can't find corresponding LineItem for the given orderEntryRao:" + entryRao));
		}

		//ok - need to set up some more whenbing:
		final NumberedLineItem lineItem = mock(NumberedLineItem.class);
		final Money lineItemCost = new Money(lineCost, currency);
		when(lineItem.getTotal(cart)).thenReturn(lineItemCost);
		doReturn(lineItem).when(serviceSpy).findLineItem(eq(cart), anyObject());

		serviceSpy.recalculateTotals(defaultCartRao, cart);

		assertCartRaoBasics(defaultCartRao, total, subtotal, shippingCost, paymentCost);
		verify(serviceSpy, times(2)).findLineItem(cart, entryRao);
		assertThat(entryRao.getTotalPrice(), is(lineItemCost.getAmount()));

	}

	@Test
	public void testCalculateSubTotalsWhenExcludedProductsIsEmpty() throws Exception
	{
		//setup
		final CartRAO cartRao = new CartRAO();
		cartRao.setSubTotal(BigDecimal.ZERO);
		final Collection<ProductRAO> excludedProducts = Collections.EMPTY_SET;

		//execute
		final BigDecimal calculateSubTotals = service.calculateSubTotals(cartRao, excludedProducts);

		//assert
		assertThat(calculateSubTotals, is(BigDecimal.ZERO));
	}

	@Test
	public void testCalculateSubTotalsValidationWhenCartIsNull() throws Exception
	{
		final CartRAO cartRao = null;
		try
		{
			service.calculateSubTotals(cartRao, null);
			fail("expected IllegalArgumentException");

		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("Cart must not be null."));
		}
	}

	@Test
	public void testCalculateSubTotalsWithNonMatchingExcludedProductsEntryProductIsNull() throws Exception
	{
		//setup
		final ProductRAO product = new ProductRAO();
		product.setCode("productCode");
		final ProductRAO entryProduct = null;

		final CartRAO cartRao = new CartRAO();
		final OrderEntryRAO entryRao = createOrderEntryRAO();
		entryRao.setProduct(entryProduct);
		cartRao.setEntries(Collections.singleton(entryRao));
		final Collection<ProductRAO> excludedProducts = Collections.singletonList(product);
		final DefaultRuleEngineCalculationService serviceSpy = spy(service);

		final Order cart = mock(Order.class);
		final BigDecimal total = BigDecimal.valueOf(400, 2);
		final BigDecimal subTotal = BigDecimal.valueOf(300, 2);
		final BigDecimal shippingCost = BigDecimal.valueOf(200, 2);
		final BigDecimal paymentCost = BigDecimal.valueOf(100, 2);
		stubCartValues(cart, total, subTotal, shippingCost, paymentCost);
		when(abstractOrderRaoToOrderConverter.convert(cartRao)).thenReturn(cart);

		//execute
		final BigDecimal calculateSubTotals = serviceSpy.calculateSubTotals(cartRao, excludedProducts);

		//assert
		assertThat(calculateSubTotals, is(subTotal));
		verify(serviceSpy).recalculateTotals(cartRao, cart);

	}

	/**
	 * Test the method DefaultRuleEngineCalculationService#calculateSubTotals.
	 *
	 * Note that this method will not test recalculate totals: it will only verify that the calculateSubTotals method
	 * does invoke recalculate totals!
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalculateSubTotalsWithNonMatchingExcludedProducts() throws Exception
	{
		//setup
		final ProductRAO product = new ProductRAO();
		product.setCode("productCode");
		final ProductRAO entryProduct = new ProductRAO();
		entryProduct.setCode("entryCode");

		final CartRAO cartRao = new CartRAO();
		final OrderEntryRAO entryRao = createOrderEntryRAO();
		entryRao.setProduct(entryProduct);
		cartRao.setEntries(Collections.singleton(entryRao));
		final Collection<ProductRAO> excludedProducts = Collections.singletonList(product);
		final DefaultRuleEngineCalculationService serviceSpy = spy(service);

		final Order cart = mock(Order.class);
		final BigDecimal total = BigDecimal.valueOf(400, 2);
		final BigDecimal subTotal = BigDecimal.valueOf(300, 2);
		final BigDecimal shippingCost = BigDecimal.valueOf(200, 2);
		final BigDecimal paymentCost = BigDecimal.valueOf(100, 2);
		stubCartValues(cart, total, subTotal, shippingCost, paymentCost);

		when(abstractOrderRaoToOrderConverter.convert(cartRao)).thenReturn(cart);

		//execute
		try
		{
			serviceSpy.calculateSubTotals(cartRao, excludedProducts);
			fail("Exception expected");
		}
		catch (final Exception e)
		{
			assertThat(e.getMessage(), is("can't find corresponding LineItem for the given orderEntryRao:" + entryRao));
		}


	}

	@Test
	public void testCalculateSubTotalsWithMatchingExcludedProducts() throws Exception
	{
		//setup
		final ProductRAO product = new ProductRAO();
		product.setCode("productCode");
		final ProductRAO entryProduct = product;

		final CartRAO cartRao = new CartRAO();
		final OrderEntryRAO entryRao = createOrderEntryRAO();
		entryRao.setProduct(entryProduct);
		cartRao.setEntries(Collections.singleton(entryRao));
		final Collection<ProductRAO> excludedProducts = Collections.singletonList(product);
		final DefaultRuleEngineCalculationService serviceSpy = spy(service);

		final Order cart = mock(Order.class);
		final BigDecimal total = BigDecimal.valueOf(400, 2);
		final BigDecimal subTotal = BigDecimal.valueOf(300, 2);
		final BigDecimal shippingCost = BigDecimal.valueOf(200, 2);
		final BigDecimal paymentCost = BigDecimal.valueOf(100, 2);
		stubCartValues(cart, total, subTotal, shippingCost, paymentCost);
		when(abstractOrderRaoToOrderConverter.convert(cartRao)).thenReturn(cart);

		//execute
		final BigDecimal calculateSubTotals = serviceSpy.calculateSubTotals(cartRao, excludedProducts);

		//assert
		assertThat(calculateSubTotals, is(subTotal));
		verify(serviceSpy).recalculateTotals(cartRao, cart);

	}

	private OrderEntryRAO createOrderEntryRAO()
	{
		final OrderEntryRAO entryRao = new OrderEntryRAO();
		entryRao.setEntryNumber(Integer.valueOf(1));
		return entryRao;
	}

	private void stubCartValues(final Order cart, final BigDecimal total, final BigDecimal subtotal, final BigDecimal shippingCost,
			final BigDecimal paymentCost)
	{
		when(cart.getTotal()).thenReturn(new Money(total, currency));
		when(cart.getSubTotal()).thenReturn(new Money(subtotal, currency));
		when(cart.getTotalChargeOfType(ChargeType.SHIPPING)).thenReturn(new Money(shippingCost, currency));
		when(cart.getTotalChargeOfType(ChargeType.PAYMENT)).thenReturn(new Money(paymentCost, currency));
	}

	private void assertCartRaoBasics(final AbstractOrderRAO cartRao, final BigDecimal total, final BigDecimal subtotal,
			final BigDecimal shippingCost, final BigDecimal paymentCost)
	{
		assertThat(cartRao.getDeliveryCost(), is(shippingCost));
		assertThat(cartRao.getTotal(), is(total));
		assertThat(cartRao.getSubTotal(), is(subtotal));
		assertThat(cartRao.getPaymentCost(), is(paymentCost));
	}

}
