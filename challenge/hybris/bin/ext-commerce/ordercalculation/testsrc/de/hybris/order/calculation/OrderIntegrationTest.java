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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.LineItemCharge;
import de.hybris.order.calculation.domain.LineItemDiscount;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.domain.OrderDiscount;
import de.hybris.order.calculation.domain.Tax;
import de.hybris.order.calculation.exception.CalculationException;
import de.hybris.order.calculation.exception.CurrenciesAreNotEqualException;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;
import de.hybris.order.calculation.strategies.CalculationStrategies;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Order calculation tests
 */
@IntegrationTest
public class OrderIntegrationTest extends ServicelayerBaseTest
{
	private final Currency euro = new Currency("EUR", 2);

	@Resource
	private CalculationStrategies calculationStrategies;

	@Test
	public void testCalculateDiscountValues()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		//create testdata
		final LineItem testobject = new LineItem(new Money("100", euro), 1);
		final LineItemDiscount tenEuroDiscount = new LineItemDiscount(new Money("10", euro));
		final LineItemDiscount tenPercentDiscount = new LineItemDiscount(new Percentage(10));
		testobject.setOrder(ocDummy);

		//test first way

		testobject.addDiscount(tenEuroDiscount);
		testobject.addDiscount(tenPercentDiscount);
		//100E -10E -10% (=9E) = 81E

		assertEquals(new Money("10", euro), testobject.getTotalDiscounts().get(tenEuroDiscount));
		assertEquals(new Money("9", euro), testobject.getTotalDiscounts().get(tenPercentDiscount));
		assertEquals(new Money("81", euro), testobject.getTotal(null));

		testobject.clearDiscounts();

		// other order now
		testobject.addDiscount(tenPercentDiscount);
		testobject.addDiscount(tenEuroDiscount);
		//100E -10% (=10E) -10E = 80E

		assertEquals(new Money("10", euro), testobject.getTotalDiscounts().get(tenEuroDiscount));
		assertEquals(new Money("10", euro), testobject.getTotalDiscounts().get(tenPercentDiscount));
		assertEquals(new Money("80", euro), testobject.getTotal(null));

		testobject.clearDiscounts();
	}

	@Test
	public void testCalculatePercentDiscountOnSomeUnitsOnly()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		//create testdata
		final LineItem testobject = new LineItem(new Money("10", euro), 10);
		final LineItemDiscount hundredPercentDiscountOn5Items = new LineItemDiscount(Percentage.HUNDRED, true, 5);

		testobject.setOrder(ocDummy);
		testobject.addDiscount(hundredPercentDiscountOn5Items);

		//		assertEquals(new Money("50", euro), hundredPercentDiscountOn5Items.getTotal());
		assertEquals(new Money("50", euro), testobject.getTotal(null));
	}

	@Test
	public void testCalculationWithDifferentCurrencies()
	{
		final Currency dollar = new Currency("USD", 2);
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem entry1 = new LineItem(new Money("100", euro), 1);
		final LineItem entry2 = new LineItem(new Money("100", dollar), 1);
		ocDummy.addLineItem(entry1);

		try
		{
			ocDummy.addLineItem(entry2);
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine
		}

		ocDummy.getSubTotal();

		//working because no taxes/discounts/additionalcosts
		ocDummy.getTotalTax();
		ocDummy.getTotalDiscounts();
		ocDummy.getTotalCharges();

		//adding tax now
		final Tax tax = new Tax(Percentage.FIFTY);
		tax.addTarget(entry1);
		tax.addTarget(entry2);
		ocDummy.addTax(tax);
		try
		{
			ocDummy.getTotalTax();
			fail("expected problem with different currencies here");
		}
		catch (final CalculationException e)
		{
			//fine
		}
	}

	@Test
	public void testDifferentCurrencyInAdditionalChargesForLineItemsAndOrder()
	{
		final Currency dollar = new Currency("USD", 2);
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem entry1 = new LineItem(new Money("100", euro), 1);
		final LineItemCharge apc = new LineItemCharge(new Money("20", dollar));
		ocDummy.addLineItem(entry1);

		try
		{
			entry1.addCharge(apc);
			fail("expected problem with different currencies here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine here
		}

		ocDummy.getTotalCharges();


		final OrderCharge aop = new OrderCharge(new Money("20", dollar));
		try
		{
			ocDummy.addCharge(aop);
			fail("expected problem with different currencies here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine here
		}
	}

	@Test
	public void testDifferentCurrencyInDiscountsForLineItemsAndOrder()
	{
		final Currency dollar = new Currency("USD", 2);
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem entry1 = new LineItem(new Money("100", euro), 1);
		ocDummy.addLineItem(entry1);

		ocDummy.getTotalDiscounts();
		entry1.getTotalDiscounts();

		final LineItemDiscount prodDisc = new LineItemDiscount(new Money("10", dollar));
		final OrderDiscount orderDisc = new OrderDiscount(new Money("10", dollar));

		try
		{
			entry1.addDiscount(prodDisc);
			fail("expected problem with different currencies here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine here
		}


		try
		{
			ocDummy.addDiscount(orderDisc);
			fail("expected problem with different currencies here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine here
		}
	}

	@Test
	public void testCalculateOrderDiscounts()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem entry1 = new LineItem(new Money("100", euro), 1);
		ocDummy.addLineItem(entry1);

		final OrderDiscount tenPercent = new OrderDiscount(Percentage.TEN);
		final OrderDiscount tenEuros = new OrderDiscount(new Money(BigDecimal.valueOf(10), euro));

		ocDummy.addDiscount(tenPercent);
		ocDummy.addDiscount(tenEuros);

		Map<OrderDiscount, Money> map = ocDummy.getTotalDiscounts();
		assertTrue(map.size() == 2);
		assertEquals(new Money(1000, euro), map.get(tenPercent)); //1000cent = 10euro
		assertEquals(new Money("10", euro), map.get(tenEuros));
		assertEquals(new Money("20", euro), ocDummy.getTotalDiscount());
		assertEquals(new Money("100", euro), ocDummy.getSubTotal());
		assertEquals(new Money("80", euro), ocDummy.getTotal());

		ocDummy.clearDiscounts();
		assertTrue(ocDummy.getDiscounts().isEmpty());

		ocDummy.addDiscount(tenEuros);
		ocDummy.addDiscount(tenPercent);

		map = ocDummy.getTotalDiscounts();
		assertTrue(map.size() == 2);
		assertEquals(new Money("9", euro), map.get(tenPercent));
		assertEquals(new Money(BigDecimal.valueOf(10), euro), map.get(tenEuros));
		assertEquals(new Money("19", euro), ocDummy.getTotalDiscount());
		assertEquals(new Money("81", euro), ocDummy.getTotal());
	}

	@Test
	public void testCalculateAdditionalOrderCharges()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem entry1 = new LineItem(new Money("100", euro), 1);
		ocDummy.addLineItem(entry1);

		final OrderCharge aoc1 = new OrderCharge(new Money("20", euro));
		final OrderCharge aoc2 = new OrderCharge(Percentage.TWENTY);
		final OrderCharge aoc3 = new OrderCharge(new Money("100", euro));
		aoc3.setDisabled(true);

		ocDummy.addCharges(Arrays.asList(aoc1, aoc2, aoc3));

		final Map<OrderCharge, Money> map = ocDummy.getTotalCharges();
		assertTrue(map.size() == 3);

	}

	@Test
	public void testGiveAwayDiscount()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem testobject = new LineItem(new Money("10", euro), 10); //10 * 10E = 100E
		ocDummy.addLineItem(testobject);

		final LineItemDiscount tenEuroDiscount = new LineItemDiscount(new Money("10", euro));
		final LineItemDiscount tenPercentDiscount = new LineItemDiscount(new Percentage(10));


		assertEquals(new Money("100", euro), testobject.getTotal(null)); //10*10 without discounts
		assertEquals(new Money("100", euro), testobject.getSubTotal());//10*10

		//add first the non give away discounts -
		testobject.addDiscounts(Arrays.asList(tenEuroDiscount, tenPercentDiscount));

		assertEquals(new Money("10", euro), testobject.getTotalDiscounts().get(tenEuroDiscount));
		assertEquals(new Money("9", euro), testobject.getTotalDiscounts().get(tenPercentDiscount));

		assertEquals(new Money("100", euro), testobject.getSubTotal()); //10*10
		assertEquals(new Money("81", euro), testobject.getTotal(null)); //10*10 - 10E -9E


		testobject.setGiveAwayUnits(2);
		assertEquals(new Money("80", euro), testobject.getSubTotal()); //8*10
		assertEquals(new Money("63", euro), testobject.getTotal(null)); //8*10 - 10E - 7E

		assertEquals(new Money("10", euro), testobject.getTotalDiscounts().get(tenEuroDiscount));
		assertEquals(new Money("7", euro), testobject.getTotalDiscounts().get(tenPercentDiscount));
	}

	@Test
	public void testPercentDiscountsPerUnit()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem testobject = new LineItem(new Money("10", euro), 10); //10 * 10E = 100E
		ocDummy.addLineItem(testobject);

		final LineItemDiscount lid = new LineItemDiscount(Percentage.TWENTY, true, 100);
		testobject.addDiscount(lid);

		assertEquals(new Money("20", euro), testobject.getTotalDiscounts().get(lid));
		assertEquals(new Money("100", euro), testobject.getSubTotal());
		assertEquals(new Money("80", euro), testobject.getTotal(null));

		testobject.setGiveAwayUnits(10);
		assertEquals(new Money("0", euro), testobject.getTotalDiscounts().get(lid));
		assertEquals(new Money("0", euro), testobject.getSubTotal());
		assertEquals(new Money("0", euro), testobject.getTotal(null));
	}

	@Test
	public void testMoneyDiscountsPerUnit()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem testobject = new LineItem(new Money("10", euro), 10); //10 * 10E = 100E
		ocDummy.addLineItem(testobject);

		final LineItemDiscount lid = new LineItemDiscount(new Money("1", euro), true, 100);
		testobject.addDiscount(lid);

		assertEquals(new Money("10", euro), testobject.getTotalDiscounts().get(lid));
		assertEquals(new Money("100", euro), testobject.getSubTotal());
		assertEquals(new Money("90", euro), testobject.getTotal(null));
	}

	@Test
	public void testMoneyChargePerUnit()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem testobject = new LineItem(new Money("10", euro), 10); //10 * 10E = 100E
		ocDummy.addLineItem(testobject);

		final LineItemCharge lic = new LineItemCharge(new Money("7.34", euro), true, 7);
		testobject.addCharge(lic);

		assertEquals(new Money("51.38", euro), testobject.getTotalCharges().get(lic));
		assertEquals(new Money("100", euro), testobject.getSubTotal());
		assertEquals(new Money("151.38", euro), testobject.getTotal(null));

		testobject.setGiveAwayUnits(10);
		assertEquals(new Money("51.38", euro), testobject.getTotalCharges().get(lic));
		assertEquals(new Money("0", euro), testobject.getSubTotal());
		assertEquals(new Money("51.38", euro), testobject.getTotal(null));
	}

	@Test
	public void testPercentChargePerUnit()
	{
		final Order ocDummy = new Order(euro, false, calculationStrategies);
		final LineItem testobject = new LineItem(new Money("10", euro), 10); //10 * 10E = 100E
		ocDummy.addLineItem(testobject);

		final LineItemCharge lic = new LineItemCharge(Percentage.TWENTYFIVE, true, 17);
		testobject.addCharge(lic);
		final LineItemCharge dontcharged = new LineItemCharge(new Money("1000", euro), true, 1000);
		testobject.addCharge(dontcharged);
		dontcharged.setDisabled(true);

		assertEquals(new Money("0", euro), testobject.getTotalCharges().get(dontcharged));
		assertEquals(new Money("25", euro), testobject.getTotalCharges().get(lic));
		assertEquals(new Money("100", euro), testobject.getSubTotal());
		assertEquals(new Money("125", euro), testobject.getTotal(null));
	}
}
