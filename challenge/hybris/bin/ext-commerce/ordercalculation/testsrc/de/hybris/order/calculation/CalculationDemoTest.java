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
package de.hybris.order.calculation;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.order.calculation.domain.AbstractCharge.ChargeType;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.LineItemCharge;
import de.hybris.order.calculation.domain.LineItemDiscount;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.domain.OrderDiscount;
import de.hybris.order.calculation.domain.Tax;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;
import de.hybris.order.calculation.strategies.CalculationStrategies;
import de.hybris.order.calculation.strategies.impl.DefaultRoundingStrategy;
import de.hybris.order.calculation.strategies.impl.DefaultTaxRoundingStrategy;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


@DemoTest
public class CalculationDemoTest
{
	private Currency euro;
	private CalculationStrategies strategies;
	private Tax tax19, tax7;

	@Before
	public void showInitialization()
	{
		euro = new Currency("EUR", 2);
		strategies = new CalculationStrategies();
		strategies.setRoundingStrategy(new DefaultRoundingStrategy());
		strategies.setTaxRoundingStrategy(new DefaultTaxRoundingStrategy());
		tax19 = new Tax(new Percentage(19));
		tax7 = new Tax(new Percentage(7));
	}

	@Test
	public void testPlain()
	{
		final Order order = new Order(euro, strategies);

		order.addLineItems(//
				new LineItem(new Money("1.33", euro), 5), //
				new LineItem(new Money("19.99", euro)), //
				new LineItem(new Money("5.55", euro), 3));

		assertEquals(new Money("43.29", euro), order.getSubTotal());
		assertEquals(new Money("43.29", euro), order.getTotal());
	}

	@Test
	public void testVATGross()
	{
		final Order order = new Order(euro, strategies);

		order.addLineItems(//
				new LineItem(new Money("1.33", euro), 5), //
				new LineItem(new Money("19.99", euro)), //
				new LineItem(new Money("5.55", euro), 3));

		final Tax tax19 = new Tax(new Percentage(19));
		final Tax tax7 = new Tax(new Percentage(7));
		order.addTax(tax7);
		order.addTax(tax19);

		tax19.addTargets(order.getLineItems().get(0), order.getLineItems().get(1));
		tax7.addTarget(order.getLineItems().get(2));

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("4.25", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("43.29", euro), order.getTotal());
	}

	@Test
	public void testVATnet()
	{
		final Order order = new Order(euro, true, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("5.06", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.16", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("43.29", euro), order.getTotal());
		assertEquals(new Money("49.51", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testOrderChargesPlain()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		order.addCharge(new OrderCharge(new Money("5.99", euro), ChargeType.SHIPPING));

		assertEquals(new Money("43.29", euro), order.getSubTotal());
		assertEquals(new Money("5.99", euro), order.getTotalChargeOfType(ChargeType.SHIPPING));
		assertEquals(new Money("49.28", euro), order.getTotal());
		assertEquals(new Money("49.28", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testOrderChargesVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		order.addCharge(new OrderCharge(new Money("5.99", euro), ChargeType.SHIPPING));

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("4.84", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.23", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("5.99", euro), order.getTotalCharge());

		assertEquals(new Money("49.28", euro), order.getTotal());
		assertEquals(new Money("49.28", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testOrderChargesVATnet()
	{
		final Order order = new Order(euro, true, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		order.addCharge(new OrderCharge(new Money("5.99", euro), ChargeType.SHIPPING));

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("5.76", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.32", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("5.99", euro), order.getTotalCharge());

		assertEquals(new Money("49.28", euro), order.getTotal());
		assertEquals(new Money("56.36", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testOrderDiscountsPlain()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		order.addDiscount(new OrderDiscount(new Money("5", euro)));

		assertEquals(new Money("43.29", euro), order.getSubTotal());
		assertEquals(new Money("5", euro), order.getTotalDiscount());
		assertEquals(new Money("38.29", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testOrderDiscountVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		order.addDiscount(new OrderDiscount(new Money("5", euro)));

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("3.76", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("0.96", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("5.00", euro), order.getTotalDiscount());

		assertEquals(new Money("38.29", euro), order.getTotal());
		assertEquals(new Money("38.29", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testOrderDiscountVATnet()
	{
		final Order order = new Order(euro, true, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		order.addDiscount(new OrderDiscount(new Money("5", euro)));

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("5.00", euro), order.getTotalDiscount());

		assertEquals(new Money("4.47", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.03", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("38.29", euro), order.getTotal());
		assertEquals(new Money("43.79", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testMixedOrderDiscountsAndChargesVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		order.addDiscount(new OrderDiscount(new Money("5", euro)));
		order.addCharge(new OrderCharge(new Money("10", euro), ChargeType.SHIPPING));

		assertEquals(new Money("43.29", euro), order.getSubTotal());
		assertEquals(new Money("5.00", euro), order.getTotalDiscount());
		assertEquals(new Money("10.00", euro), order.getTotalChargeOfType(ChargeType.SHIPPING));

		assertEquals(new Money("4.74", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.21", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("48.29", euro), order.getTotal());
		assertEquals(new Money("48.29", euro), order.getTotalIncludingTaxes());
	}

	@Test
	public void testLineItemDiscountPlain()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		final LineItemDiscount discount1 = new LineItemDiscount(Percentage.TEN, true, 5);
		final LineItemDiscount discount2 = new LineItemDiscount(new Money("5", euro), true, 10);

		lineitem1.addDiscount(discount1);
		lineitem2.addDiscount(discount2);

		assertEquals(new Money("5.98", euro), lineitem1.getTotal(null));
		assertEquals(new Money("14.99", euro), lineitem2.getTotal(null));
		assertEquals(new Money("16.65", euro), lineitem3.getTotal(null));

		assertEquals(new Money("0.67", euro), lineitem1.getTotalDiscounts().get(discount1));
		assertEquals(new Money("5", euro), lineitem2.getTotalDiscounts().get(discount2));

		assertEquals(new Money("37.62", euro), order.getSubTotal());
		assertEquals(new Money("37.62", euro), order.getTotal());
	}

	@Test
	public void testLineItemDiscountVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		final LineItemDiscount discount1 = new LineItemDiscount(Percentage.TEN, true, 5);
		final LineItemDiscount discount2 = new LineItemDiscount(new Money("5", euro), true, 10);

		lineitem1.addDiscount(discount1);
		lineitem2.addDiscount(discount2);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		assertEquals(new Money("3.34", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("37.62", euro), order.getSubTotal());
		assertEquals(new Money("37.62", euro), order.getTotal());
	}

	@Test
	public void testLineItemChargesVAT()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		lineitem1.addCharge(new LineItemCharge(new Money("5", euro), true));

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		assertEquals(new Money("8.24", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("68.29", euro), order.getSubTotal());
		assertEquals(new Money("68.29", euro), order.getTotal());
	}

	@Test
	public void testLineItemForFreeVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		lineitem3.setGiveAwayUnits(1);

		assertEquals(new Money("11.10", euro), lineitem3.getTotal(null));

		assertEquals(new Money("4.25", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("0.72", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("37.74", euro), order.getSubTotal());
		assertEquals(new Money("37.74", euro), order.getTotal());
	}

	@Test
	public void testLineItemForFreeAndDiscountVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		lineitem3.setGiveAwayUnits(1);
		lineitem3.addDiscount(new LineItemDiscount(Percentage.TEN));

		assertEquals(new Money("9.99", euro), lineitem3.getTotal(null));

		assertEquals(new Money("4.25", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("0.65", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("36.63", euro), order.getSubTotal());
		assertEquals(new Money("36.63", euro), order.getTotal());
	}

	@Test
	public void testLimitedQuantityLineItemDiscountVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		final LineItemDiscount discount1 = new LineItemDiscount(new Money("1", euro), true, 2);
		final LineItemDiscount discount3 = new LineItemDiscount(Percentage.TEN, true, 1);
		lineitem1.addDiscount(discount1);
		lineitem3.addDiscount(discount3);

		assertEquals(new Money("4.65", euro), lineitem1.getTotal(null));
		assertEquals(new Money("19.99", euro), lineitem2.getTotal(null));
		assertEquals(new Money("16.09", euro), lineitem3.getTotal(null));

		assertEquals(new Money("2", euro), lineitem1.getTotalDiscounts().get(discount1));
		assertEquals(new Money("0.56", euro), lineitem3.getTotalDiscounts().get(discount3));

		assertEquals(new Money("3.93", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.05", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("40.73", euro), order.getSubTotal());
		assertEquals(new Money("40.73", euro), order.getTotal());
	}

	@Test
	public void testAbsoluteLineItemDiscountsAndChargesVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		final LineItemCharge giftwrapping = new LineItemCharge(new Money("5", euro));
		final LineItemDiscount couponXYZ = new LineItemDiscount(new Money("2.5", euro));
		final LineItemDiscount couponABC = new LineItemDiscount(new Money("5", euro));

		lineitem1.addCharge(giftwrapping);
		lineitem2.addCharge(giftwrapping);
		lineitem2.addDiscount(couponXYZ);
		lineitem3.addDiscount(couponABC);

		assertEquals(new Money("11.65", euro), lineitem1.getTotal(null));
		assertEquals(new Money("22.49", euro), lineitem2.getTotal(null));
		assertEquals(new Money("11.65", euro), lineitem3.getTotal(null));

		assertEquals(new Money("5", euro), lineitem1.getTotalCharges().get(giftwrapping));
		assertEquals(new Money("5", euro), lineitem2.getTotalCharges().get(giftwrapping));

		assertEquals(new Money("2.5", euro), lineitem2.getTotalDiscounts().get(couponXYZ));
		assertEquals(new Money("5", euro), lineitem3.getTotalDiscounts().get(couponABC));

		assertEquals(new Money("45.79", euro), order.getSubTotal());
		assertEquals(new Money("45.79", euro), order.getTotal());

		assertEquals(new Money("5.45", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("0.76", euro), order.getTotalTaxFor(tax7));
	}

	@Test
	public void testAbsoluteLineItemChargeAndAllForFree()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		final LineItemCharge giftwrapping = new LineItemCharge(new Money("5", euro));
		lineitem1.setGiveAwayUnits(5);
		lineitem1.addCharge(giftwrapping);

		assertEquals(new Money("5", euro), lineitem1.getTotal(null));

		assertEquals(new Money("41.64", euro), order.getSubTotal());

		assertEquals(new Money("3.98", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));
	}

	@Test
	public void testDiscountOnProductCombination()
	{
		final Order order = new Order(euro, strategies);

		final LineItem laptop = new LineItem(new Money("599", euro), 2);
		final LineItem bag = new LineItem(new Money("59", euro), 1);
		order.addLineItems(laptop, bag);


		final LineItemDiscount bundleDiscount = new LineItemDiscount(Percentage.TEN, true, 1);
		laptop.addDiscount(bundleDiscount);
		bag.addDiscount(bundleDiscount);

		assertEquals(new Money("1138.10", euro), laptop.getTotal(null));
		assertEquals(new Money("53.10", euro), bag.getTotal(null));
		assertEquals(new Money("59.9", euro), laptop.getTotalDiscounts().get(bundleDiscount));
		assertEquals(new Money("5.9", euro), bag.getTotalDiscounts().get(bundleDiscount));

		assertEquals(new Money("1191.20", euro), order.getTotal());
	}

	@Test
	public void testFixedPriceBundle()
	{

		final Order order = new Order(euro, strategies);

		final LineItem screen = new LineItem(new Money("1199.00", euro), 1);
		final LineItem wallMount = new LineItem(new Money("109.00", euro), 1);
		final LineItem dvd = new LineItem(new Money("19.99", euro));
		order.addLineItems(screen, wallMount, dvd);

		final Money screenSubTotal = screen.getSubTotal();
		final Money wallMountSubTotal = wallMount.getSubTotal();
		final Money dvdSubTotal = dvd.getSubTotal();

		final List<Percentage> priceDistribution = Money.getPercentages(screenSubTotal, wallMountSubTotal, dvdSubTotal);
		assertEquals(Percentage.valueOf("91", "8", "1"), priceDistribution);

		final Money overallDiscount = Money.sum(screenSubTotal, wallMountSubTotal, dvdSubTotal).subtract(new Money("999.00", euro));
		assertEquals(new Money("328.99", euro), overallDiscount);

		final List<Money> splitUpDiscount = overallDiscount.split(priceDistribution);
		assertEquals(Money.valueOf(euro, "299.39", "26.32", "3.28"), splitUpDiscount);

		final Money screenOff = splitUpDiscount.get(0);
		final LineItemDiscount bundleScreenDiscount = new LineItemDiscount(screenOff, true, 1);
		screen.addDiscount(bundleScreenDiscount);

		final Money wallMountOff = splitUpDiscount.get(1);
		final LineItemDiscount bundleWallMountDiscount = new LineItemDiscount(wallMountOff, true, 1);
		wallMount.addDiscount(bundleWallMountDiscount);

		final Money dvdOff = splitUpDiscount.get(2);
		final LineItemDiscount bundleDVDDiscount = new LineItemDiscount(dvdOff, true, 1);
		dvd.addDiscount(bundleDVDDiscount);

		assertEquals(new Money("899.61", euro), screen.getTotal(null));
		assertEquals(new Money("82.68", euro), wallMount.getTotal(null));
		assertEquals(new Money("16.71", euro), dvd.getTotal(null));

		assertEquals(screenOff, screen.getTotalDiscounts().get(bundleScreenDiscount));
		assertEquals(wallMountOff, wallMount.getTotalDiscounts().get(bundleWallMountDiscount));
		assertEquals(dvdOff, dvd.getTotalDiscounts().get(bundleDVDDiscount));

		assertEquals(new Money("999.00", euro), order.getTotal());
	}

	@Test
	public void testFreeOrderShippingVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		final OrderCharge shippingCharge = new OrderCharge(new Money("5.99", euro), ChargeType.SHIPPING);
		order.addCharge(shippingCharge);
		shippingCharge.setDisabled(true);

		assertEquals(new Money("43.29", euro), order.getSubTotal());
		assertEquals(Money.zero(euro), order.getTotalChargeOfType(ChargeType.SHIPPING));
		assertEquals(new Money("43.29", euro), order.getTotal());
		assertEquals(new Money("43.29", euro), order.getTotalIncludingTaxes());

		assertEquals(new Money("4.25", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));
	}

	@Test
	public void testFreeItemShippingVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		final LineItemCharge charge1 = new LineItemCharge(new Money("4.90", euro));
		final LineItemCharge charge2 = new LineItemCharge(new Money("4.90", euro));
		final LineItemCharge charge3 = new LineItemCharge(new Money("4.90", euro));

		lineitem1.addCharge(charge1);
		lineitem2.addCharge(charge2);
		lineitem3.addCharge(charge3);

		charge1.setDisabled(true);
		charge3.setDisabled(true);

		assertEquals(new Money("6.65", euro), lineitem1.getTotal(null));
		assertEquals(new Money("24.89", euro), lineitem2.getTotal(null));
		assertEquals(new Money("16.65", euro), lineitem3.getTotal(null));

		assertEquals(new Money("48.19", euro), order.getSubTotal());
		assertEquals(new Money("48.19", euro), order.getTotal());

		assertEquals(new Money("5.03", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));
	}

	@Test
	public void testFixedTaxOrderChargesVATgross()
	{
		final Order order = new Order(euro, strategies);

		final LineItem lineitem1 = new LineItem(new Money("1.33", euro), 5);
		final LineItem lineitem2 = new LineItem(new Money("19.99", euro));
		final LineItem lineitem3 = new LineItem(new Money("5.55", euro), 3);
		order.addLineItems(lineitem1, lineitem2, lineitem3);

		tax19.addTargets(lineitem1, lineitem2);
		tax7.addTarget(lineitem3);
		order.addTaxes(tax7, tax19);

		final OrderCharge shippingCharge = new OrderCharge(new Money("5.99", euro), ChargeType.SHIPPING);
		order.addCharge(shippingCharge);

		tax19.addTarget(shippingCharge);

		assertEquals(new Money("43.29", euro), order.getSubTotal());

		assertEquals(new Money("5.20", euro), order.getTotalTaxFor(tax19));
		assertEquals(new Money("1.08", euro), order.getTotalTaxFor(tax7));

		assertEquals(new Money("5.99", euro), order.getTotalCharge());

		assertEquals(new Money("49.28", euro), order.getTotal());
		assertEquals(new Money("49.28", euro), order.getTotalIncludingTaxes());
	}
}
