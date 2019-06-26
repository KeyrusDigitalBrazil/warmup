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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.order.calculation.domain.AbstractCharge.ChargeType;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.LineItemCharge;
import de.hybris.order.calculation.domain.LineItemDiscount;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.domain.OrderDiscount;
import de.hybris.order.calculation.domain.Tax;
import de.hybris.order.calculation.exception.CurrenciesAreNotEqualException;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;
import de.hybris.order.calculation.strategies.CalculationStrategies;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * tests the net/gross tax calculation
 * 
 */
@IntegrationTest
public class TaxIntegrationTest extends ServicelayerTest
{
	@Resource
	private CalculationStrategies calculationStrategies;

	private final Currency euro = new Currency("EUR", 2);
	private final Currency dollar = new Currency("USD", 2);

	private LineItem lineItem1;
	private LineItem lineItem2;
	private LineItem lineItem3;

	private Tax tax19;
	private Tax tax7;

	private Order ocEuroNet, ocEuroGross;


	@Before
	public void setup()
	{
		ocEuroNet = new Order(euro, true, calculationStrategies);
		ocEuroGross = new Order(euro, false, calculationStrategies);
		assertNotNull(ocEuroNet);
		assertNotNull(ocEuroGross);

		tax19 = new Tax(new Percentage(19));
		tax7 = new Tax(new Percentage(7));

		lineItem1 = new LineItem(new Money("52.99", euro), 1);
		lineItem2 = new LineItem(new Money("15.37", euro), 5);
		lineItem3 = new LineItem(new Money("24.89", euro), 10);

		tax19.addTarget(lineItem1);
		tax19.addTarget(lineItem3);
		tax7.addTarget(lineItem2);
	}

	@Test
	public void testCalculateEverythingWithZeroAmounts()
	{
		final Money zeroMoney = new Money(euro);
		final Percentage zeroPercent = new Percentage(0);

		final Order oNet = new Order(euro, true, calculationStrategies);
		final Order oGross = new Order(euro, false, calculationStrategies);

		//add order charges
		final OrderCharge ocMoney = new OrderCharge(zeroMoney);
		final OrderCharge ocPercentage = new OrderCharge(zeroPercent, ChargeType.SHIPPING);
		oNet.addCharge(ocMoney);
		oNet.addCharge(ocPercentage);
		oGross.addCharge(ocMoney);
		oGross.addCharge(ocPercentage);

		//add order discounts
		final OrderDiscount odMoney = new OrderDiscount(zeroMoney);
		final OrderDiscount odPecent = new OrderDiscount(zeroPercent);
		oNet.addDiscount(odPecent);
		oNet.addDiscount(odMoney);
		oGross.addDiscount(odPecent);
		oGross.addDiscount(odMoney);

		//Lineitem with discount and charge
		final LineItem lineItem = new LineItem(zeroMoney, 0);
		final LineItemCharge licMoney = new LineItemCharge(zeroMoney);
		final LineItemCharge licPercent = new LineItemCharge(zeroPercent);
		final LineItemDiscount lidMoney = new LineItemDiscount(zeroMoney);
		final LineItemDiscount lidPercent = new LineItemDiscount(zeroPercent);
		lineItem.addCharges(Arrays.asList(licMoney, licPercent));
		lineItem.addDiscounts(Arrays.asList(lidMoney, lidPercent));
		oNet.addLineItem(lineItem);
		oGross.addLineItem(lineItem);

		//taxes - here with value
		final Tax zeroMoneyTax = new Tax(zeroMoney);
		final Tax zeroPercentTax = new Tax(zeroPercent);

		final Tax tenPercentTax = new Tax(Percentage.TEN);
		final Tax tenMoneyTax = new Tax(new Money("10", euro));

		oNet.addTaxes(Arrays.asList(zeroMoneyTax, zeroPercentTax, tenPercentTax));
		oGross.addTaxes(Arrays.asList(zeroMoneyTax, zeroPercentTax, tenPercentTax));
		zeroMoneyTax.addTargets(Arrays.asList(lineItem, ocMoney));
		zeroPercentTax.addTarget(ocPercentage);
		tenPercentTax.addTargets(Arrays.asList(lineItem, ocMoney, ocPercentage));
		tenMoneyTax.addTarget(lineItem);

		checkCorrectOrderCalculation(oGross, "0", "0", "0", "0", "0", "0", "0", "0");
		checkCorrectOrderCalculation(oNet, "0", "0", "0", "0", "0", "0", "0", "0");
		checkCorrectLineItemCalculation(lineItem, "0", "0", "0", "0", 0);
	}

	@Test
	public void testGrossCalculationWithTaxRates()
	{
		ocEuroGross.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroGross.addTax(tax19);
		ocEuroGross.addTax(tax7);

		checkCorrectOrderCalculation(ocEuroGross, "378.74", "378.74", "0", "0", "378.74", "53.22", "0", "0");
		checkCorrectLineItemCalculation(lineItem1, "52.99", "52.99", "0", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "76.85", "0", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "248.90", "0", "0", 10);
		checkGermanTaxes(ocEuroGross, "5.02", "48.20");
	}

	@Test
	public void testNetCalculationWithTaxRates()
	{
		ocEuroNet.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroNet.addTax(tax19);
		ocEuroNet.addTax(tax7);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "52.99", "0", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "76.85", "0", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "248.90", "0", "0", 10);
		checkGermanTaxes(ocEuroNet, "5.37", "57.35");
		checkCorrectOrderCalculation(ocEuroNet, "378.74", "378.74", "0", "0", "441.46", "62.72", "0", "0");
	}

	@Test
	public void testNetWithAdditionalLineItemCharges()
	{
		ocEuroNet.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroNet.addTax(tax19);
		ocEuroNet.addTax(tax7);

		//add additional charge to some line items and calculate
		final LineItemCharge apc10percent = new LineItemCharge(Percentage.TEN);
		final LineItemCharge apc10euro = new LineItemCharge(new Money("10", euro));

		lineItem1.addCharge(apc10euro);
		lineItem2.addCharge(apc10percent);
		lineItem3.addCharge(apc10percent);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "62.99", "10", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "84.54", "7.69", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "273.79", "24.89", "0", 10);
		checkGermanTaxes(ocEuroNet, "5.91", "63.98");
		checkCorrectOrderCalculation(ocEuroNet, "421.32", "421.32", "0", "0", "491.21", "69.89", "0", "0");
	}

	@Test
	public void testGrossWithAdditionalLineItemCharges()
	{
		ocEuroGross.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroGross.addTax(tax19);
		ocEuroGross.addTax(tax7);

		//add additional charge to some line items and calculate
		final LineItemCharge apc10percent = new LineItemCharge(Percentage.TEN);
		final LineItemCharge apc10euro = new LineItemCharge(new Money("10", euro));

		lineItem1.addCharge(apc10euro);
		lineItem2.addCharge(apc10percent);
		lineItem3.addCharge(apc10percent);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "62.99", "10", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "84.54", "7.69", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "273.79", "24.89", "0", 10);
		checkGermanTaxes(ocEuroGross, "5.53", "53.77");
		checkCorrectOrderCalculation(ocEuroGross, "421.32", "421.32", "0", "0", "421.32", "59.3", "0", "0");

	}

	@Test
	public void testNetWithLineItemDiscounts()
	{
		ocEuroNet.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroNet.addTax(tax19);
		ocEuroNet.addTax(tax7);

		final LineItem additionalLineItem = new LineItem(new Money("100", euro), 10);
		ocEuroNet.addLineItem(additionalLineItem);
		tax19.addTarget(additionalLineItem);

		final LineItemDiscount fivePercentDiscount = new LineItemDiscount(new Percentage(5));
		final LineItemDiscount fiveEuroDiscount = new LineItemDiscount(new Money("5", euro));
		final LineItemDiscount fiftyCentOnOneItem = new LineItemDiscount(new Money("0.5", euro), true, 1);
		final LineItemDiscount minus10EuroFor5Items = new LineItemDiscount(new Money("10", euro), true, 5);

		lineItem1.addDiscount(fiveEuroDiscount);
		lineItem1.addDiscount(fivePercentDiscount);
		lineItem2.setGiveAwayUnits(2);
		lineItem3.addDiscount(fiftyCentOnOneItem);
		additionalLineItem.setGiveAwayUnits(1);
		additionalLineItem.addDiscount(minus10EuroFor5Items);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "45.59", "0", "7.4", 1);
		checkCorrectLineItemCalculation(lineItem2, "46.11", "46.11", "0", "0", 3);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "248.40", "0", "0.5", 10);
		checkCorrectLineItemCalculation(additionalLineItem, "900", "850", "0", "50", 9);
		checkGermanTaxes(ocEuroNet, "3.22", "217.35");
		checkCorrectOrderCalculation(ocEuroNet, "1190.10", "1190.10", "0", "0", "1410.67", "220.57", "0", "0");

	}

	@Test
	public void testNetWithOrderCharges()
	{
		ocEuroNet.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroNet.addTax(tax19);
		ocEuroNet.addTax(tax7);

		final OrderCharge shippingFee = new OrderCharge(new Money("25", euro), ChargeType.SHIPPING);
		final OrderCharge paymentFee = new OrderCharge(Percentage.TEN, ChargeType.PAYMENT);

		ocEuroNet.addCharge(shippingFee);
		ocEuroNet.addCharge(paymentFee);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "52.99", "0", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "76.85", "0", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "248.90", "0", "0", 10);
		checkGermanTaxes(ocEuroNet, "6.30", "67.25");
		checkCorrectOrderCalculation(ocEuroNet, "378.74", "444.11", "0", "65.37", "517.66", "73.55", "25", "40.37");
	}

	@Test
	public void testGrossWithOrderCharges()
	{
		ocEuroGross.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroGross.addTax(tax19);
		ocEuroGross.addTax(tax7);

		final OrderCharge shippingFee = new OrderCharge(new Money("30", euro));
		ocEuroGross.addCharge(shippingFee);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "52.99", "0", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "76.85", "0", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "248.90", "0", "0", 10);

		//		checkGermanTaxes(ocEuroGross, "5.03", "48.20"); //TODO XXX WRONG?!? no shippin/payment tax!
		//		checkCorrectOrderCalculation(ocEuroGross, subtotal, total, totalOrderDiscount, totalOrderCharge, totalIncludingTaxes,
		//				totalTaxes, shippingCosts, paymentCosts);
	}

	@Test
	public void testNetWithOrderDiscounts()
	{
		ocEuroNet.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroNet.addTax(tax19);
		ocEuroNet.addTax(tax7);

		final Collection<Tax> taxColl1 = ocEuroNet.getTaxesFor(lineItem1);
		final Collection<Tax> taxColl2 = ocEuroNet.getTaxesFor(lineItem2);
		final Collection<Tax> taxColl3 = ocEuroNet.getTaxesFor(lineItem3);
		assertTrue(taxColl1.contains(tax19));
		assertFalse(taxColl1.contains(tax7));
		assertTrue(taxColl2.contains(tax7));
		assertFalse(taxColl2.contains(tax19));
		assertTrue(taxColl3.contains(tax19));
		assertFalse(taxColl3.contains(tax7));

		assertTrue(ocEuroNet.hasAssignedTaxes(lineItem1));
		assertTrue(ocEuroNet.hasAssignedTaxes(lineItem2));
		assertTrue(ocEuroNet.hasAssignedTaxes(lineItem3));


		final OrderDiscount firstCustomer = new OrderDiscount(new Money("10", euro));
		ocEuroNet.addDiscount(firstCustomer);
		final OrderDiscount friday10percentOff = new OrderDiscount(Percentage.TEN);
		ocEuroNet.addDiscount(0, friday10percentOff);

		checkCorrectLineItemCalculation(lineItem1, "52.99", "52.99", "0", "0", 1);
		checkCorrectLineItemCalculation(lineItem2, "76.85", "76.85", "0", "0", 5);
		checkCorrectLineItemCalculation(lineItem3, "248.90", "248.90", "0", "0", 10);
		checkGermanTaxes(ocEuroNet, "4.69", "50.10");
		checkCorrectOrderCalculation(ocEuroNet, "378.74", "330.87", "47.87", "0", "385.66", "54.79", "0", "0");
	}

	@Test
	public void veryBigCalculationWithEverythingInNet()
	{
		final LineItem productONE = new LineItem(new Money("29.89", euro), 13);
		final LineItem productZWO = new LineItem(new Money("16.67", euro), 10);
		productZWO.setGiveAwayUnits(3);
		final LineItem productTHREE = new LineItem(new Money("899", euro), 2);
		final LineItem productFOUR = new LineItem(new Money("45.99", euro), 1);
		productFOUR.setGiveAwayUnits(1);

		ocEuroNet.addLineItems(Arrays.asList(productONE, productZWO, productTHREE, productFOUR));
		//TODO XXX fill me
	}

	private void checkGermanTaxes(final Order order, final String sevenPercent, final String nineteenPercent)
	{
		assertEquals("invalid 7% tax:", new Money(sevenPercent, order.getCurrency()), order.getTotalTaxes().get(tax7));
		assertEquals("invalid 19% tax:", new Money(nineteenPercent, order.getCurrency()), order.getTotalTaxes().get(tax19));
	}

	private void checkCorrectLineItemCalculation(final LineItem lineItem, final String subtotal, final String taxableTotal,
			final String totalCharges, final String totalDiscount, final int calculatedNumberOfUnits)
	{
		assertEquals("invalid subtotal:", new Money(subtotal, lineItem.getOrder().getCurrency()), lineItem.getSubTotal());
		assertEquals("invalid taxableTotal:", new Money(taxableTotal, lineItem.getOrder().getCurrency()), lineItem.getTotal(null));
		assertEquals("invalid totalCharges:", new Money(totalCharges, lineItem.getOrder().getCurrency()), lineItem.getTotalCharge());
		assertEquals("invalid totalDiscount:", new Money(totalDiscount, lineItem.getOrder().getCurrency()),
				lineItem.getTotalDiscount());
		assertEquals("invalid calculated number of units:", calculatedNumberOfUnits, lineItem.getNumberOfUnitsForCalculation());
	}

	private void checkCorrectOrderCalculation(final Order order, final String subtotal, final String total,
			final String totalOrderDiscount, final String totalOrderCharge, final String totalIncludingTaxes,
			final String totalTaxes, final String shippingCosts, final String paymentCosts)
	{
		assertEquals("invalid subtotal:", new Money(subtotal, order.getCurrency()), order.getSubTotal());
		assertEquals("invalid total:", new Money(total, order.getCurrency()), order.getTotal());
		assertEquals("invalid totalOrderDiscount:", new Money(totalOrderDiscount, order.getCurrency()), order.getTotalDiscount());
		assertEquals("invalid totalOrderCharges:", new Money(totalOrderCharge, order.getCurrency()), order.getTotalCharge());
		assertEquals("invalid totalIncludingTaxes:", new Money(totalIncludingTaxes, order.getCurrency()),
				order.getTotalIncludingTaxes());
		assertEquals("invalid totalTaxes:", new Money(totalTaxes, order.getCurrency()), order.getTotalTax());
		assertEquals("invalid shipping OrderCharge:", new Money(shippingCosts, order.getCurrency()),
				order.getTotalChargeOfType(ChargeType.SHIPPING));
		assertEquals("invalid payment OrderCharge:", new Money(paymentCosts, order.getCurrency()),
				order.getTotalChargeOfType(ChargeType.PAYMENT));
	}

	@Test
	public void testTaxCalculationWithDifferentCurrencies()
	{
		ocEuroGross.addLineItems(Arrays.asList(lineItem1, lineItem2, lineItem3));
		ocEuroGross.addTax(tax19);
		final Tax dollarTax = new Tax(new Money("0", dollar));
		try
		{
			ocEuroGross.addTax(dollarTax);
			fail("Expected CurrenciesAreNotEqualException here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine
		}
	}

	//TODO: calc with tax on some units only

}
