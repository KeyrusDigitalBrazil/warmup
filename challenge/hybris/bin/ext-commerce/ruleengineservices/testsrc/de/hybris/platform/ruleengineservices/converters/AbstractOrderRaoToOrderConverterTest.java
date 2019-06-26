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
package de.hybris.platform.ruleengineservices.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.AbstractCharge.ChargeType;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.money.Money;
import de.hybris.platform.ruleengineservices.calculation.AbstractRuleEngineTest;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class AbstractOrderRaoToOrderConverterTest extends AbstractRuleEngineTest
{
	@Test
	public void testCartToOrderConversion()
	{
		final CartRAO cart = createCartRAO("cart_code", USD);
		final DiscountRAO orderDiscount = createDiscount(cart, "1.00", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cart, "12.34", USD, 2, 0);
		final DiscountRAO orderEntry1Discount = createDiscount(orderEntry1, "1.34", USD);
		cart.setEntries(set(orderEntry1, createOrderEntryRAO(cart, "23.45", USD, 3, 1)));
		final BigDecimal deliveryCost = new BigDecimal("5.00");
		cart.setDeliveryCost(deliveryCost);
		final BigDecimal paymentCost = new BigDecimal("3.00");
		cart.setPaymentCost(paymentCost);

		final Order conversionResult = getCartRaoToOrderConverter().convert(cart);
		// check currency:
		Assert.assertEquals(USD, conversionResult.getCurrency().getIsoCode());
		Assert.assertEquals(2, conversionResult.getCharges().size());
		// check charges:
		boolean shippingChargePresent = false, paymentChargePresent = false;
		for (final OrderCharge charge : conversionResult.getCharges())
		{
			if (ChargeType.SHIPPING.equals(charge.getChargeType()))
			{
				shippingChargePresent = true;
				Assert.assertEquals(deliveryCost, ((Money) charge.getAmount()).getAmount());
			}
			if (ChargeType.PAYMENT.equals(charge.getChargeType()))
			{
				paymentChargePresent = true;
				Assert.assertEquals(paymentCost, ((Money) charge.getAmount()).getAmount());
			}
		}
		Assert.assertTrue("Payment charge is absent!", paymentChargePresent);
		Assert.assertTrue("Shipping charge is absent!", shippingChargePresent);
		// check discounts:
		Assert.assertEquals(1, conversionResult.getDiscounts().size());
		final Money discountMoney = (Money) conversionResult.getDiscounts().get(0).getAmount();
		Assert.assertEquals(orderDiscount.getValue(), discountMoney.getAmount());
		Assert.assertEquals(orderDiscount.getCurrencyIsoCode(), discountMoney.getCurrency().getIsoCode());
		// check line items:
		Assert.assertEquals(2, conversionResult.getLineItems().size());
		boolean checkedEntry1 = false;
		for (final LineItem li : conversionResult.getLineItems())
		{
			if (li.getNumberOfUnits() == orderEntry1.getQuantity()
					&& li.getBasePrice().getAmount().equals(orderEntry1.getBasePrice())
					&& li.getBasePrice().getCurrency().getIsoCode().equals(orderEntry1.getCurrencyIsoCode()))
			{
				checkedEntry1 = true;
				// line item discounts:
				Assert.assertEquals(1, li.getDiscounts().size());
				final Money discountEntryMoney = (Money) li.getDiscounts().get(0).getAmount();
				Assert.assertEquals(orderEntry1Discount.getValue(), discountEntryMoney.getAmount());
				Assert.assertEquals(orderEntry1Discount.getCurrencyIsoCode(), discountEntryMoney.getCurrency().getIsoCode());
			}
		}
		Assert.assertTrue("Order entry 1 not found!", checkedEntry1);
	}
}
