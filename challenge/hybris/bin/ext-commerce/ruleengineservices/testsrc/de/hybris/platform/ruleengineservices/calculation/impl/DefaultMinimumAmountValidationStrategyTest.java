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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.LineItemDiscount;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.domain.OrderDiscount;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.strategies.CalculationStrategies;
import de.hybris.order.calculation.strategies.impl.DefaultRoundingStrategy;
import de.hybris.order.calculation.strategies.impl.DefaultTaxRoundingStrategy;
import de.hybris.platform.ruleengineservices.calculation.NumberedLineItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for {@link DefaultMinimumAmountValidationStrategy}.
 *
 */
@UnitTest
public class DefaultMinimumAmountValidationStrategyTest
{
	private DefaultMinimumAmountValidationStrategy minimumAmountValidationStrategy;
	private Currency currency;

	@Before
	public void setUp()
	{
		minimumAmountValidationStrategy = new DefaultMinimumAmountValidationStrategy();
		currency = new Currency("USD", 2);
	}

	protected Order createOrder()
	{
		final CalculationStrategies strategies = new CalculationStrategies();
		strategies.setRoundingStrategy(new DefaultRoundingStrategy());
		strategies.setTaxRoundingStrategy(new DefaultTaxRoundingStrategy());
		final Order order = new Order(currency, strategies);

		final OrderCharge shippingCharge = new OrderCharge(new Money(new BigDecimal("5.00"), currency));
		order.addCharge(shippingCharge);
		final OrderCharge paymentCharge = new OrderCharge(new Money(new BigDecimal("5.00"), currency));
		order.addCharge(paymentCharge);

		final List<LineItem> lineItems = new ArrayList<>();
		final NumberedLineItem lineItem = new NumberedLineItem(new Money(new BigDecimal("10.00"), currency), 1);
		lineItems.add(lineItem);
		order.addLineItems(lineItems);

		return order;
	}

	@Test
	public void testIsOrderLowerLimitValid()
	{
		Assert.assertTrue(minimumAmountValidationStrategy.isOrderLowerLimitValid(createOrder(), new OrderDiscount(new Money(
				new BigDecimal("5.00"), currency))));
	}

	@Test
	public void testIsOrderLowerLimitNotValid()
	{
		Assert.assertFalse(minimumAmountValidationStrategy.isOrderLowerLimitValid(createOrder(), new OrderDiscount(new Money(
				new BigDecimal("11.00"), currency))));
	}

	@Test
	public void testIsLineItemLowerLimitValid()
	{
		Assert.assertTrue(minimumAmountValidationStrategy.isLineItemLowerLimitValid(createOrder().getLineItems().get(0),
				new LineItemDiscount(new Money(new BigDecimal("5.00"), currency))));
	}

	@Test
	public void testIsLineItemLowerLimitNotValid()
	{
		Assert.assertFalse(minimumAmountValidationStrategy.isLineItemLowerLimitValid(createOrder().getLineItems().get(0),
				new LineItemDiscount(new Money(new BigDecimal("11.00"), currency))));
	}

	@Test
	public void testIsLineItemLowerLimitNotValidAfterOrderDiscountApplied()
	{
		final Order createOrder = createOrder();
		createOrder.addDiscount(new OrderDiscount(new Money(new BigDecimal("7.00"), currency)));
		Assert.assertFalse(minimumAmountValidationStrategy.isLineItemLowerLimitValid(createOrder.getLineItems().get(0),
				new LineItemDiscount(new Money(new BigDecimal("5.00"), currency))));
	}
}
