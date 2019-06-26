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
package de.hybris.platform.ruleengineservices.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderUtilsTest
{
	private static final String USD = "USD";

	private OrderUtils orderUtils;
	private Currency currency;

	@Before
	public void setUp()
	{
		orderUtils = new OrderUtils();
		currency = new Currency(USD, 2);
	}

	@Test
	public void testCreateShippingChargeAbsolute()
	{
		final BigDecimal value = new BigDecimal("100");
		final OrderCharge orderCharge = orderUtils.createShippingCharge(currency, true, value);
		assertThat(orderCharge.getAmount()).isNotNull().isInstanceOf(Money.class);
		final Money money = (Money) orderCharge.getAmount();
		assertThat(money.getAmount()).isEqualByComparingTo(value);
		assertThat(money.getCurrency()).isEqualTo(currency);
	}

	@Test
	public void testCreateShippingChargePercentage()
	{
		final BigDecimal value = new BigDecimal("10");
		final OrderCharge orderCharge = orderUtils.createShippingCharge(currency, false, value);
		assertThat(orderCharge.getAmount()).isNotNull().isInstanceOf(Percentage.class);
		final Percentage percentage = (Percentage) orderCharge.getAmount();
		assertThat(percentage.getRate()).isEqualByComparingTo(value);
	}
}
