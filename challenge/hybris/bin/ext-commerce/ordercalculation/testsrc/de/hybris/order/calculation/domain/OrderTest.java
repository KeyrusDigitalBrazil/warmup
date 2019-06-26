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
package de.hybris.order.calculation.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.AbstractCharge.ChargeType;
import de.hybris.order.calculation.exception.MissingCalculationDataException;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


/**
 * 
 */
@UnitTest
public class OrderTest
{
	private final Currency euro = new Currency("EUR", 2);

	@Test
	public void testSetAndModifyLineItemCharges()
	{
		//create testdata
		final Order testobject = new Order(euro, false, null);
		final LineItem charge1 = new LineItem(new Money("10", euro));
		final LineItem charge2 = new LineItem(new Money("1.0", euro), 3);
		final LineItem charge3 = new LineItem(new Money("33", euro), 5);

		try
		{
			charge2.getOrder();
			fail("expected MissingCalculationDataException");
		}
		catch (final MissingCalculationDataException e)
		{
			e.getMessage().contains("Order for LineItemCharge");
		}

		//test different settings

		//normal order
		testobject.addLineItem(charge2);
		assertEquals(testobject, charge2.getOrder());
		testobject.addLineItem(charge1);
		assertTrue(testobject.getLineItems().size() == 2);
		assertEquals(charge2, testobject.getLineItems().get(0));
		assertEquals(charge1, testobject.getLineItems().get(1));


		//clear discounts
		try
		{
			testobject.getLineItems().clear();
		}
		catch (final UnsupportedOperationException e) //NOPMD
		{
			//fine!
		}
		testobject.clearLineItems();
		assertTrue(testobject.getLineItems().isEmpty());
		try
		{
			charge2.getOrder();
			fail("Expected MissingCalculationDataException");
		}
		catch (final MissingCalculationDataException e)
		{
			// fine
		}

		//some other operations
		final List<LineItem> list = new ArrayList<LineItem>();
		list.add(charge3);
		list.add(charge1);
		testobject.addLineItems(list);
		assertEquals(testobject, charge3.getOrder());

		assertEquals(charge1, testobject.getLineItems().get(1));
		//assertNull(charge2.getOrder());
		testobject.addLineItem(1, charge2);
		assertEquals(testobject, charge2.getOrder());
		assertTrue(testobject.getLineItems().size() == 3);
		assertEquals(charge2, testobject.getLineItems().get(1));
		assertEquals(charge1, testobject.getLineItems().get(2));

		//removing
		assertEquals(testobject, charge3.getOrder());
		testobject.removeLineItem(charge3);
		//		assertNull(charge3.getOrderCalculation());
		try
		{
			testobject.removeLineItem(charge3);
			fail("IllegalArgumentException expected");
		}
		catch (final IllegalArgumentException e)
		{
			// fine
		}
		assertEquals(testobject, charge1.getOrder());
	}

	@Test
	public void testSetAndModifyTaxes()
	{
		//create testdata
		final Order testobject = new Order(euro, false, null);
		final Tax tax1 = new Tax(Percentage.TEN);
		final Tax tax2 = new Tax(Percentage.THIRTY);
		final Tax tax3 = new Tax(Percentage.HUNDRED);

		//normal order
		testobject.addTax(tax2);
		testobject.addTax(tax1);
		assertTrue(testobject.getTaxes().size() == 2);
		assertTrue(testobject.getTaxes().contains(tax2));
		assertTrue(testobject.getTaxes().contains(tax1));


		//clear discounts
		try
		{
			testobject.getTaxes().clear();
		}
		catch (final UnsupportedOperationException e) //NOPMD
		{
			//fine!
		}
		testobject.clearTaxes();
		assertTrue(testobject.getTaxes().isEmpty());

		//some other operations
		final List<Tax> list = new ArrayList<Tax>();
		list.add(tax3);
		list.add(tax1);
		testobject.addTaxes(list);

		assertEquals(Arrays.asList(tax3, tax1), testobject.getTaxes());
		testobject.addTax(tax2);
		assertEquals(Arrays.asList(tax3, tax1, tax2), testobject.getTaxes());

		//removing
		testobject.removeTax(tax3);
		try
		{
			testobject.removeTax(tax3);
			fail("IllegalArgumentException expected");
		}
		catch (final IllegalArgumentException e)
		{
			// fine
		}
	}

	@Test
	public void testEmptyOrder()
	{
		final Money zero = new Money(euro);
		final Order order = new Order(euro, false, null);
		assertEquals(zero, order.getTotal());
		assertEquals(zero, order.getSubTotal());
		assertEquals(zero, order.getTotalDiscount());
		assertEquals(zero, order.getTotalIncludingTaxes());
		assertEquals(zero, order.getTotalCharge());
		assertEquals(zero, order.getTotalTax());
		assertEquals(zero, order.getTotalChargeOfType(ChargeType.PAYMENT));

		assertEquals(Collections.EMPTY_SET, order.getTaxesFor(null));
		final OrderCharge lic = new OrderCharge(zero);
		assertEquals(Collections.EMPTY_SET, order.getTaxesFor(lic));

		assertEquals(Collections.EMPTY_MAP, order.getTotalDiscounts());
		assertEquals(Collections.EMPTY_LIST, order.getDiscounts());

		assertEquals(Collections.EMPTY_MAP, order.getTotalCharges());
		assertEquals(Collections.EMPTY_LIST, order.getCharges());

		assertEquals(Collections.EMPTY_MAP, order.getTotalTaxes());
		assertEquals(Collections.EMPTY_LIST, order.getTaxes());

		assertEquals(Collections.EMPTY_LIST, order.getLineItems());
		//		assertNull(order.getCalculationStrategies());
	}

	@Test
	public void testSetAndModifyAdditionalOrderCharges()
	{
		//create testdata
		final Order testobject = new Order(euro, false, null);
		final OrderCharge charge1 = new OrderCharge(new Money("10", euro));
		final OrderCharge charge2 = new OrderCharge(Percentage.EIGHTY);
		final OrderCharge charge3 = new OrderCharge(new Money("33", euro));

		//test different settings

		//normal order
		testobject.addCharge(charge2);
		testobject.addCharge(charge1);
		assertTrue(testobject.getCharges().size() == 2);
		assertEquals(charge2, testobject.getCharges().get(0));
		assertEquals(charge1, testobject.getCharges().get(1));


		//clear discounts
		try
		{
			testobject.getCharges().clear();
		}
		catch (final UnsupportedOperationException e) //NOPMD
		{
			//fine!
		}
		testobject.clearCharges();
		assertTrue(testobject.getCharges().isEmpty());

		//some other operations
		final List<OrderCharge> list = new ArrayList<OrderCharge>();
		list.add(charge3);
		list.add(charge1);
		testobject.addCharges(list);

		assertEquals(charge1, testobject.getCharges().get(1));
		testobject.addCharge(1, charge2);
		assertTrue(testobject.getCharges().size() == 3);
		assertEquals(charge2, testobject.getCharges().get(1));
		assertEquals(charge1, testobject.getCharges().get(2));

		//removing
		testobject.removeCharge(charge3);
		try
		{
			testobject.removeCharge(charge3);
			fail("IllegalArgumentException expected");
		}
		catch (final IllegalArgumentException e)
		{
			// fine
		}
	}

	@Test
	public void testSetAndModifyOrderDiscounts()
	{
		//create testdata
		final Order testobject = new Order(euro, false, null);
		final OrderDiscount od1 = new OrderDiscount(Percentage.TEN);
		final OrderDiscount od2 = new OrderDiscount(Percentage.THIRTY);
		final OrderDiscount od3 = new OrderDiscount(Percentage.HUNDRED);

		//test different settings

		//normal order
		testobject.addDiscount(od2);
		testobject.addDiscount(od1);
		assertTrue(testobject.getDiscounts().size() == 2);
		assertEquals(od2, testobject.getDiscounts().get(0));
		assertEquals(od1, testobject.getDiscounts().get(1));


		//clear discounts
		try
		{
			testobject.getDiscounts().clear();
		}
		catch (final UnsupportedOperationException e) //NOPMD
		{
			//fine!
		}
		testobject.clearDiscounts();
		assertTrue(testobject.getDiscounts().isEmpty());

		//some other operations
		final List<OrderDiscount> list = new ArrayList<OrderDiscount>();
		list.add(od3);
		list.add(od1);
		testobject.addDiscounts(list);

		assertEquals(od1, testobject.getDiscounts().get(1));
		testobject.addDiscount(1, od2);
		assertTrue(testobject.getDiscounts().size() == 3);
		assertEquals(od2, testobject.getDiscounts().get(1));
		assertEquals(od1, testobject.getDiscounts().get(2));

		//removing
		testobject.removeDiscount(od3);
		try
		{
			testobject.removeDiscount(od3);
			fail("IllegalArgumentException expected");
		}
		catch (final IllegalArgumentException e)
		{
			// fine
		}
	}
}
