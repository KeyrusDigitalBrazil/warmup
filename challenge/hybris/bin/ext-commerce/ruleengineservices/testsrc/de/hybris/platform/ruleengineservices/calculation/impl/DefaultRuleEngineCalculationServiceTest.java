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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.LineItemDiscount;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.strategies.CalculationStrategies;
import de.hybris.order.calculation.strategies.impl.DefaultRoundingStrategy;
import de.hybris.platform.ruleengineservices.calculation.AbstractRuleEngineTest;
import de.hybris.platform.ruleengineservices.calculation.NumberedLineItem;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.DeliveryModeRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


/**
 * Contains unit tests for the DefaultRuleEngineCalculationService.
 */
@UnitTest
public class DefaultRuleEngineCalculationServiceTest extends AbstractRuleEngineTest
{
	@Test
	public void testSimpleCalculateTotals()
	{

		// simple cart with 1 entry of quantity 2
		final CartRAO simple01 = createCartRAO("simple01", USD);
		simple01.setEntries(Collections.singleton(createOrderEntryRAO(simple01, "12.34", // NOSONAR
				USD, 2, 0)));
		getRuleEngineCalculationService().calculateTotals(simple01);
		assertEquals(new BigDecimal("24.68"), simple01.getTotal());
		assertEquals(new BigDecimal("24.68"), simple01.getSubTotal());

		// simple cart with 2 entries and delivery cost
		final CartRAO simple02 = createCartRAO("simple02", USD);
		simple02.setEntries(set(createOrderEntryRAO(simple02, "12.34", USD, 2, 0),
				createOrderEntryRAO(simple02, "23.45", USD, 3, 1))); // NOSONAR
		simple02.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple02);
		assertEquals(new BigDecimal("100.03"), simple02.getTotal()); // NOSONAR
		assertEquals(new BigDecimal("95.03"), simple02.getSubTotal()); // NOSONAR
		assertEquals(new BigDecimal("5.00"), simple02.getDeliveryCost());
	}

	@Test
	public void testChangeDeliveryMode()
	{
		// simple cart with 2 entries and payment cost
		final CartRAO simple03 = createCartRAO("simple03", USD); // NOSONAR
		simple03.setEntries(set(createOrderEntryRAO(simple03, "12.34", USD, 2, 0),
				createOrderEntryRAO(simple03, "23.45", USD, 3, 1)));
		simple03.setPaymentCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple03);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple03.getPaymentCost());

		// now assume there is already a delivery cost
		simple03.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple03);
		assertEquals(new BigDecimal("105.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());

		// now add a free shipping discount
		final DeliveryModeRAO freeDeliveryMode = new DeliveryModeRAO();
		freeDeliveryMode.setCode("FREE SHIPPING");
		freeDeliveryMode.setCost(new BigDecimal("0.00"));
		getRuleEngineCalculationService().changeDeliveryMode(simple03, freeDeliveryMode);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("0.00"), simple03.getDeliveryCost());

		// now just recalculate to make sure nothing changes
		getRuleEngineCalculationService().calculateTotals(simple03);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("0.00"), simple03.getDeliveryCost());

		// now add a not-so-free shipping discount
		final DeliveryModeRAO notFreeDeliveryMode = new DeliveryModeRAO();
		notFreeDeliveryMode.setCode("PREMIUM-OVERNIGHT");
		notFreeDeliveryMode.setCost(new BigDecimal("20.00"));
		getRuleEngineCalculationService().changeDeliveryMode(simple03, notFreeDeliveryMode);
		assertEquals(new BigDecimal("120.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("20.00"), simple03.getDeliveryCost());

		// now back to free shipping discount (I could do this all day)
		getRuleEngineCalculationService().changeDeliveryMode(simple03, freeDeliveryMode);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("0.00"), simple03.getDeliveryCost());

		// now just recalculate to make sure nothing changes
		getRuleEngineCalculationService().calculateTotals(simple03);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("0.00"), simple03.getDeliveryCost());
	}


	@Test
	public void testAddOrderLevelDiscountPercentage()
	{
		// simple cart with 2 entries and delivery cost
		final CartRAO simple03 = createCartRAO("simple03", USD);
		simple03.setEntries(set(createOrderEntryRAO(simple03, "12.34", USD, 2, 0),
				createOrderEntryRAO(simple03, "23.45", USD, 3, 1)));
		simple03.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple03);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple03.getDeliveryCost());

		// now add a 10% oder-level discount
		// 10% of sub total = 9.50
		// total is 100.03 - 9.50 = 90.53
		getRuleEngineCalculationService().addOrderLevelDiscount(simple03, false, new BigDecimal("10.00")); // NOSONAR
		assertEquals(new BigDecimal("5.00"), simple03.getDeliveryCost());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("90.53"), simple03.getTotal());
	}

	@Test
	public void testAddOrderLevelDiscountAbsolute()
	{
		// simple cart with 2 entries and delivery cost
		final CartRAO simple03 = createCartRAO("simple03", USD);
		simple03.setEntries(set(createOrderEntryRAO(simple03, "12.34", USD, 2, 0),
				createOrderEntryRAO(simple03, "23.45", USD, 3, 1)));
		simple03.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple03);
		assertEquals(new BigDecimal("100.03"), simple03.getTotal());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple03.getDeliveryCost());

		// now add a 10USD oder-level discount
		// total is 100.03 - 10 = 90.03
		getRuleEngineCalculationService().addOrderLevelDiscount(simple03, true, new BigDecimal("10.00"));
		assertEquals(new BigDecimal("5.00"), simple03.getDeliveryCost());
		assertEquals(new BigDecimal("95.03"), simple03.getSubTotal());
		assertEquals(new BigDecimal("90.03"), simple03.getTotal());
	}

	@Test
	public void testAddOrderEntryLevelDiscountPercentage()
	{
		// simple cart with 2 entries and delivery cost
		final CartRAO simple04 = createCartRAO("simple04", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(simple04, "12.34", USD, 2, 0);
		simple04.setEntries(set(orderEntry1, createOrderEntryRAO(simple04, "23.45", USD, 3, 1)));
		simple04.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple04);
		assertEquals(new BigDecimal("100.03"), simple04.getTotal());
		assertEquals(new BigDecimal("95.03"), simple04.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple04.getDeliveryCost());

		// now add a 10% oder-entry-level discount
		// subtotal is: 12.34*2-rounded_down(12.34*2*0.1) + 23.45*3 = 24.68 - rounded_down(2.468) + 70.35 = 24.68-2.46+70.35=92.57
		// total is: 12.34*2-rounded_down(12.34*2*0.1) + 23.45*3 + 5 = 24.68 - rounded_down(2.468) + 70.35 + 5 = 24.68-2.46+70.35+5 = 97.57
		getRuleEngineCalculationService().addOrderEntryLevelDiscount(orderEntry1, false, new BigDecimal("10.00"));
		assertEquals(new BigDecimal("5.00"), simple04.getDeliveryCost());
		assertEquals(new BigDecimal("92.57"), simple04.getSubTotal());
		assertEquals(new BigDecimal("97.57"), simple04.getTotal());
	}

	@Test
	public void testAddOrderEntryLevelDoubleDiscountPercentage()
	{
		// simple cart with 1 entry
		final CartRAO simple06 = createCartRAO("simple06", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(simple06, "31.02", USD, 1, 0);
		simple06.setEntries(set(orderEntry1));
		simple06.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple06);
		assertEquals(new BigDecimal("36.02"), simple06.getTotal());
		assertEquals(new BigDecimal("31.02"), simple06.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple06.getDeliveryCost());

		// now add a 25% order-entry-level discount
		// subtotal is: 31.02-rounded_down(31.02*0.25) = 23.27
		getRuleEngineCalculationService().addOrderEntryLevelDiscountStackable(orderEntry1, false, new BigDecimal("25.00"));
		// now add a 10% order-entry-level discount
		// subtotal is: 23.27-rounded_down(23.27*0.10) = 20.95
		getRuleEngineCalculationService().addOrderEntryLevelDiscount(orderEntry1, false, new BigDecimal("10.00"));

		assertEquals(new BigDecimal("5.00"), simple06.getDeliveryCost());
		assertEquals(new BigDecimal("20.95"), simple06.getSubTotal());
		assertEquals(new BigDecimal("25.95"), simple06.getTotal());
	}

	@Test
	public void testAddOrderEntryLevelDoubleDiscountPercentage2()
	{
		// simple cart with 1 entry
		final CartRAO simple07 = createCartRAO("simple07", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(simple07, "31.02", USD, 1, 0);
		simple07.setEntries(set(orderEntry1));
		simple07.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple07);
		assertEquals(new BigDecimal("36.02"), simple07.getTotal());
		assertEquals(new BigDecimal("31.02"), simple07.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple07.getDeliveryCost());

		// now add a 10% order-entry-level discount
		// subtotal is: 31.02-rounded_down(31.02*0.1) = 27.92
		getRuleEngineCalculationService().addOrderEntryLevelDiscount(orderEntry1, false, new BigDecimal("10.00"));
		// now add a 25% order-entry-level discount
		// subtotal is: 27.92-rounded_down(27.92*0.25) = 20.94
		getRuleEngineCalculationService().addOrderEntryLevelDiscountStackable(orderEntry1, false, new BigDecimal("25.00"));

		assertEquals(new BigDecimal("5.00"), simple07.getDeliveryCost());
		assertEquals(new BigDecimal("20.94"), simple07.getSubTotal());
		assertEquals(new BigDecimal("25.94"), simple07.getTotal());
	}

	@Test
	public void testAddOrderEntryLevelDiscountAbsolute()
	{
		// simple cart with 2 entries and delivery cost
		final CartRAO simple05 = createCartRAO("simple05", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(simple05, "12.34", USD, 2, 0);
		final OrderEntryRAO orderEntry2 = createOrderEntryRAO(simple05, "23.45", USD, 3, 1);

		simple05.setEntries(set(orderEntry1, orderEntry2));
		simple05.setDeliveryCost(new BigDecimal("5.00"));
		getRuleEngineCalculationService().calculateTotals(simple05);
		assertEquals(new BigDecimal("100.03"), simple05.getTotal());
		assertEquals(new BigDecimal("95.03"), simple05.getSubTotal());
		assertEquals(new BigDecimal("5.00"), simple05.getDeliveryCost());

		// now add a 10USD oder-entry-level discount
		// subtotal is: (12.34-10)*2 + 23.45*3 = 75.03
		// total is: (12.34-10)*2 + 23.45*3 + 5 = 80.03
		getRuleEngineCalculationService().addOrderEntryLevelDiscount(orderEntry1, true, new BigDecimal("10.00"));
		assertEquals(new BigDecimal("5.00"), simple05.getDeliveryCost());
		assertEquals(new BigDecimal("75.03"), simple05.getSubTotal());
		assertEquals(new BigDecimal("80.03"), simple05.getTotal());
	}


	@Test
	public void testAddOrderEntryLevelFixedPrice()
	{
		// simple cart with 2 entries and delivery cost
		final CartRAO cartRao1 = createCartRAO("cart01", USD); // NOSONAR
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cartRao1, "12.50", USD, 1, 0); //NOSONAR
		cartRao1.setEntries(set(orderEntry1, createOrderEntryRAO(cartRao1, "20.25", USD, 2, 1)));
		cartRao1.setDeliveryCost(new BigDecimal("5.00"));

		getRuleEngineCalculationService().calculateTotals(cartRao1);
		assertEquals(new BigDecimal("58.00"), cartRao1.getTotal()); // NOSONAR
		assertEquals(new BigDecimal("53.00"), cartRao1.getSubTotal()); // NOSONAR
		assertEquals(new BigDecimal("5.00"), cartRao1.getDeliveryCost());

		getRuleEngineCalculationService().addFixedPriceEntryDiscount(orderEntry1, new BigDecimal("10.00"));
		assertEquals(new BigDecimal("55.50"), cartRao1.getTotal());
		assertEquals(new BigDecimal("50.50"), cartRao1.getSubTotal());
		assertEquals(new BigDecimal("5.00"), cartRao1.getDeliveryCost());
	}


	@Test
	public void testAddOrderEntryLevelFixedPriceError()
	{
		// fixed price amount is greater than the base price for this item so no discount should be created
		final CartRAO cartRao1 = createCartRAO("cart01", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cartRao1, "12.50", USD, 1, 0);
		cartRao1.setEntries(set(orderEntry1, createOrderEntryRAO(cartRao1, "20.25", USD, 2, 1)));
		cartRao1.setDeliveryCost(new BigDecimal("5.00"));

		getRuleEngineCalculationService().calculateTotals(cartRao1);
		assertEquals(new BigDecimal("58.00"), cartRao1.getTotal());
		assertEquals(new BigDecimal("53.00"), cartRao1.getSubTotal());
		assertEquals(new BigDecimal("5.00"), cartRao1.getDeliveryCost());

		getRuleEngineCalculationService().addFixedPriceEntryDiscount(orderEntry1, new BigDecimal("50"));

		//no discount should be applied because fixed price was higher that original price
		assertEquals(new BigDecimal("58.00"), cartRao1.getTotal());
		assertEquals(new BigDecimal("53.00"), cartRao1.getSubTotal());
		assertEquals(new BigDecimal("5.00"), cartRao1.getDeliveryCost());
	}

	@Test
	public void testExcludedProducts()
	{
		final List<ProductRAO> excludedProducts = newArrayList();
		final ProductRAO prodRao1 = new ProductRAO();
		prodRao1.setCode("13579");
		excludedProducts.add(prodRao1);

		final CartRAO cartRao1 = createCartRAO("cart01", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cartRao1, "12.50", USD, 1, 0);
		orderEntry1.getProduct().setCode("13579");
		final OrderEntryRAO orderEntry2 = createOrderEntryRAO(cartRao1, "24.00", USD, 1, 1);
		orderEntry2.getProduct().setCode("24680");
		cartRao1.setEntries(set(orderEntry1, orderEntry2));
		cartRao1.setDeliveryCost(new BigDecimal("5.00"));

		getRuleEngineCalculationService().calculateTotals(cartRao1);
		assertEquals(new BigDecimal("41.50"), cartRao1.getTotal());
		assertEquals(new BigDecimal("36.50"), cartRao1.getSubTotal());
		assertEquals(new BigDecimal("5.00"), cartRao1.getDeliveryCost());

		final BigDecimal result = getRuleEngineCalculationService().calculateSubTotals(cartRao1, excludedProducts);

		//sub total without the excluded products
		assertEquals(new BigDecimal("24.00"), result);

		//Original cartRao is unchanged
		assertEquals(new BigDecimal("41.50"), cartRao1.getTotal());
		assertEquals(new BigDecimal("36.50"), cartRao1.getSubTotal());
		assertEquals(new BigDecimal("5.00"), cartRao1.getDeliveryCost());

	}

	@Test
	public void testConvertPercentageDiscountToAbsoluteDiscountDiscountAvailableItems()
	{
		BigDecimal percentageAmount = BigDecimal.valueOf(0.0);
		final Currency usdCurrency = Currency.valueOf("USD", 2);
		NumberedLineItem orderLineItem = new NumberedLineItem(new Money("12.50", usdCurrency), 2);
		BigDecimal adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(
				percentageAmount, 2, orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("0");

		percentageAmount = BigDecimal.valueOf(100.0);
		adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(percentageAmount, 2,
				orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("25");

		percentageAmount = BigDecimal.valueOf(100.0);
		adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(percentageAmount, 1,
				orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("12.5");

		percentageAmount = BigDecimal.valueOf(50.0);
		adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(percentageAmount, 1,
				orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("6.25");

		percentageAmount = BigDecimal.valueOf(50.0);
		orderLineItem = new NumberedLineItem(new Money("12.50", usdCurrency), 2);
		final LineItemDiscount lineItemDiscount = new LineItemDiscount(new Money("2.0", usdCurrency), true, 1);
		orderLineItem.addDiscount(lineItemDiscount);
		adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(percentageAmount, 1,
				orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("6.25");
	}

	@Test
	public void testConvertPercentageDiscountToAbsoluteDiscountDiscountConsumedItems()
	{
		final Currency usdCurrency = Currency.valueOf("USD", 2);

		final BigDecimal percentageAmount = BigDecimal.valueOf(50.0);
		NumberedLineItem orderLineItem = new NumberedLineItem(new Money("12.50", usdCurrency), 2);

		final CalculationStrategies calculationStrategies = new CalculationStrategies();
		calculationStrategies.setRoundingStrategy(new DefaultRoundingStrategy());

		final Order order = new Order(usdCurrency, calculationStrategies);
		orderLineItem.setOrder(order);
		LineItemDiscount lineItemDiscount = new LineItemDiscount(new Money("2.0", usdCurrency), true, 2);
		orderLineItem.addDiscount(lineItemDiscount);
		BigDecimal adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(
				percentageAmount, 1, orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("5.25");

		adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(percentageAmount, 2,
				orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("10.5");

		orderLineItem = new NumberedLineItem(new Money("12.50", usdCurrency), 2);
		orderLineItem.setOrder(order);
		lineItemDiscount = new LineItemDiscount(new Money("2.0", usdCurrency), true, 1);
		orderLineItem.addDiscount(lineItemDiscount);
		adjustedAbsoluteAmount = getRuleEngineCalculationService().convertPercentageDiscountToAbsoluteDiscount(percentageAmount, 2,
				orderLineItem);
		assertThat(adjustedAbsoluteAmount.stripTrailingZeros()).isEqualTo("11.5");
	}


}
