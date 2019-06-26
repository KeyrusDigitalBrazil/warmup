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
package de.hybris.order.calculation.money;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.exception.AmountException;
import de.hybris.order.calculation.exception.CurrenciesAreNotEqualException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class MoneyTest
{
	private final Currency euro = new Currency("EUR", 2);

	@Test
	public void testCreateMoney() throws Exception
	{
		//those should work
		checkCreateMoneyProcess(BigDecimal.ZERO, 0, BigInteger.valueOf(0), false);
		checkCreateMoneyProcess(BigDecimal.ZERO, 10, BigInteger.valueOf(0), false);
		checkCreateMoneyProcess(BigDecimal.ONE, 0, BigInteger.valueOf(1), false);
		checkCreateMoneyProcess(BigDecimal.ONE, 3, BigInteger.valueOf(1000), false);
		checkCreateMoneyProcess(BigDecimal.valueOf(3.5), 3, BigInteger.valueOf(3500), false);
		checkCreateMoneyProcess(BigDecimal.valueOf(3.5), 1, BigInteger.valueOf(35), false);
		checkCreateMoneyProcess(new BigDecimal("1.000"), 0, BigInteger.valueOf(1), false);
		checkCreateMoneyProcess(new BigDecimal("1.2345"), 4, BigInteger.valueOf(12345), false);
		checkCreateMoneyProcess(new BigDecimal("1.2345"), 5, BigInteger.valueOf(123450), false);

		//working negative value
		checkCreateMoneyProcess(new BigDecimal("-1.2"), 5, BigInteger.valueOf(-120000), false);

		checkCreateMoneyProcess(new BigDecimal("1.2"), 0, BigInteger.valueOf(1), false);
		checkCreateMoneyProcess(new BigDecimal("1.000001"), 2, BigInteger.valueOf(100), false);

		//those should NOT work
		checkCreateMoneyProcess(new BigDecimal("1.2"), -1, null, true);

		//check other constructors
		assertEquals(new Money(new BigDecimal("1.2"), euro), new Money("1.20", euro));
		assertEquals(new Money(new BigDecimal("1.2"), euro), new Money(120, euro));
	}

	private void checkCreateMoneyProcess(final BigDecimal amount, final int digits, final BigInteger unscaledValue,
			final boolean shouldFail) throws Exception
	{
		try
		{
			final Currency dummyCurr = new Currency("dummyCurrency", digits);
			final Money money = new Money(amount, dummyCurr);
			if (shouldFail)
			{
				fail("expected  ArithmeticException!");
			}
			assertEquals(unscaledValue, money.getAmount().unscaledValue());
		}
		catch (final Exception e)
		{
			if (!shouldFail)
			{
				throw e;
			}
		}
	}

	@Test
	public void testCreateMoneyShortWriting()
	{
		final Money money = new Money(".1", euro);
		assertEquals(new Money("0.1", euro), money);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckCurrencyWithNull()
	{
		final Money amount1 = new Money(BigDecimal.valueOf(3.45), new Currency("TietzeT", 4));
		amount1.assertCurreniesAreEqual((Currency) null);
	}

	@Test
	public void testCheckCurrencyEqual()
	{
		final Money amount1 = new Money(BigDecimal.valueOf(3.45), new Currency("TietzeT", 4));
		final Money amount2 = new Money(BigDecimal.valueOf(3343.45), new Currency("TietzeT", 4));

		amount1.assertCurreniesAreEqual(amount2.getCurrency());
		amount2.assertCurreniesAreEqual(amount1);

		final Currency xxx = new Currency("xXx", 3);
		final Money amount3 = new Money(BigDecimal.valueOf(322.45), xxx);
		final Money amount4 = new Money(BigDecimal.valueOf(33343.45), xxx);

		amount3.assertCurreniesAreEqual(amount4.getCurrency());
		amount4.assertCurreniesAreEqual(amount3);
		amount3.assertCurreniesAreEqual(amount3.getCurrency());
	}

	@Test
	public void testCheckCurrencyDifferent()
	{
		final Money amount1 = new Money(BigDecimal.valueOf(3.45), new Currency("TietzeT", 4));
		final Money amount2 = new Money(BigDecimal.valueOf(3343.45), new Currency("TietzeT", 3));
		final Money amount3 = new Money(BigDecimal.valueOf(3343.45), new Currency("xxx", 3));

		checkCurrAndFail(amount1, amount2);
		checkCurrAndFail(amount2, amount1);
		checkCurrAndFail(amount2, amount3);
		checkCurrAndFail(amount3, amount2);
		checkCurrAndFail(amount1, amount3);
		checkCurrAndFail(amount3, amount1);

	}

	private void checkCurrAndFail(final Money amt1, final Money amt2)
	{
		try
		{
			amt1.assertCurreniesAreEqual(amt2.getCurrency());
			fail("expected CurrenciesAreNotEqualException");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine
		}
		catch (final Exception e2)
		{
			fail("got unexpected exception: " + e2);
		}
	}


	@Test
	public void testAddMoneySuccess()
	{
		final Currency curr = new Currency("EUR", 25);
		final Money val1 = new Money(new BigDecimal(2), curr);
		final Money val2 = new Money(new BigDecimal(3), curr);
		final Money expectedRes = new Money(new BigDecimal(5), curr);
		final Money actualRes = val1.add(val2);

		assertEquals(expectedRes, actualRes);
		assertFalse(expectedRes.hashCode() == actualRes.hashCode());

		//BIG NUMBERS NOW!!!!!

		final Money val3 = new Money(BigDecimal.valueOf(8250325.12), curr);
		final Money val4 = new Money(BigDecimal.valueOf(4321456.31), curr);
		final Money expectedRes2 = new Money(BigDecimal.valueOf(12571781.43), curr);
		assertEquals(expectedRes2, val3.add(val4));

		final Money val5 = new Money(new BigDecimal("8273872368712658763457862348566489.7162578164578825032512"), curr);
		final Money val6 = new Money(new BigDecimal("8762347526136571645982560956723521.8374618726457432145631"), curr);
		final Money expectedRes3 = new Money(new BigDecimal("17036219894849230409440423305290011.5537196891036257178143"), curr);
		assertEquals(expectedRes3, val5.add(val6));
	}

	@Test
	public void testAddMoneyFailure()
	{
		final Money val3 = new Money(BigDecimal.valueOf(8250325.12), new Currency("xXx", 2));
		final Money val4 = new Money(BigDecimal.valueOf(4321456.31), new Currency("XxX", 3));

		try
		{
			val3.add(val4);
			fail("expected CurrenciesAreNotEqualException here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine
		}
		catch (final Exception e)
		{
			fail("unexpected exception: " + e);
		}
	}

	@Test
	public void testSubtract() throws CurrenciesAreNotEqualException
	{
		final Currency curr = new Currency("EUR", 25);
		final Money val1 = new Money(new BigDecimal(5), curr);
		final Money val2 = new Money(new BigDecimal(2), curr);
		final Money expectedRes = new Money(new BigDecimal(3), curr);
		final Money actualRes = val1.subtract(val2);

		assertEquals(expectedRes, actualRes);
		assertFalse(expectedRes.hashCode() == actualRes.hashCode());

		final Money expectedRes2 = new Money(new BigDecimal(-3), curr);
		assertEquals(expectedRes2, val2.subtract(val1));

		final Money val5 = new Money(new BigDecimal("8273872368712658763457862348566489.7162578164578825032512"), curr);
		final Money val6 = new Money(new BigDecimal("8273872368712658763457862348566489.7162578164578825032511"), curr);
		final Money expectedRes3 = new Money(new BigDecimal("0.0000000000000000000001"), curr);
		assertEquals(expectedRes3, val5.subtract(val6));
	}

	@Test
	public void testSubtractMoneyFailure()
	{
		final Money val3 = new Money(BigDecimal.valueOf(8250325.12), new Currency("xXx", 2));
		final Money val4 = new Money(BigDecimal.valueOf(4321456.31), new Currency("XxX", 3));

		try
		{
			val3.subtract(val4);
			fail("expected CurrenciesAreNotEqualException here");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine
		}
		catch (final Exception e)
		{
			fail("unexpected exception: " + e);
		}
	}

	@Test
	public void testMoneyEquals()
	{
		final Money val1 = new Money(BigDecimal.valueOf(8250325.12), new Currency("xXx", 2));
		final Money val2 = new Money(BigDecimal.valueOf(8250325.120000), new Currency("xXx", 2));
		final Money val3 = new Money(BigDecimal.valueOf(8250325.12), euro);
		final Money val4 = new Money(BigDecimal.valueOf(8250325.12), new Currency("EUR", 3));

		assertTrue(val1.equals(val2));
		assertTrue(val2.equals(val1));
		assertTrue(val1.equals(val1));
		assertFalse(val1.equals(val3));
		assertFalse(val1.equals(val4));
		assertFalse(val4.equals(val3));
	}

	@Test
	public void testFirstSplittingMethodSuccess()
	{
		final Money val1 = new Money(new BigDecimal(5), euro);

		final List<Money> res1 = val1.split();
		assertEquals(1, res1.size());
		assertEquals(val1, res1.get(0));
		assertFalse(val1.hashCode() == res1.get(0).hashCode());

		//val1.split(null); does not compile anyway:)

		final List<Money> res2 = val1.split(new Percentage(BigDecimal.valueOf(10)));
		assertEquals(2, res2.size());
		assertEquals(new BigDecimal("0.50"), res2.get(0).getAmount());
		assertEquals(new BigDecimal("4.50"), res2.get(1).getAmount());

		final List<Money> res3 = val1.split(new Percentage(BigDecimal.valueOf(100)));
		assertEquals(1, res3.size());
		assertEquals(val1, res3.get(0));

		try
		{
			val1.split(new Percentage(BigDecimal.valueOf(101)));
			fail("expected MoneyException");
		}
		catch (final AmountException e)
		{
			//fine
		}
		catch (final Exception e)
		{
			fail("did not expect this exception: " + e);
		}

		final Money val2 = new Money(new BigDecimal(100), euro);
		final List<Money> res4 = val2.split(new Percentage(BigDecimal.valueOf(33)), new Percentage(BigDecimal.valueOf(33)));
		assertEquals(3, res4.size());
		assertEquals(new BigDecimal("33.00"), res4.get(0).getAmount());
		assertEquals(new BigDecimal("33.00"), res4.get(1).getAmount());
		assertEquals(new BigDecimal("34.00"), res4.get(2).getAmount());
	}

	@Test
	public void testFirstSplitMethodWithRounding()
	{
		final Money val1 = new Money(new BigDecimal("0.05"), euro);

		final List<Money> res1 = val1.split(new Percentage(30));
		assertEquals(2, res1.size());
		assertEquals(new BigDecimal("0.02"), res1.get(0).getAmount());
		assertEquals(new BigDecimal("0.03"), res1.get(1).getAmount());

		final List<Money> res2 = val1.split(new Percentage(70));
		assertEquals(2, res1.size());
		assertEquals(new BigDecimal("0.04"), res2.get(0).getAmount());
		assertEquals(new BigDecimal("0.01"), res2.get(1).getAmount());

	}

	@Test
	public void testSplitSmallestAmount()
	{
		final Money val1 = new Money(new BigDecimal("0.01"), euro);
		final List<Money> res1 = val1.split(Percentage.TEN);
		final List<Money> res2 = val1.split(Percentage.TWENTY);
		final List<Money> res3 = val1.split(new Percentage(49));
		final List<Money> res4 = val1.split(new Percentage(50));
		final List<Money> res5 = val1.split(new Percentage(99));
		final List<Money> res6 = val1.split(Percentage.HUNDRED);
		final List<Money> res7 = val1.split(Percentage.TWENTYFIVE, Percentage.TWENTYFIVE, Percentage.TWENTYFIVE);

		//10% - 90%
		assertEquals(2, res1.size());
		assertEquals(new BigDecimal("0.01"), res1.get(0).getAmount());
		assertEquals(new BigDecimal("0.00"), res1.get(1).getAmount());

		//20% - 80%
		assertEquals(2, res2.size());
		assertEquals(new BigDecimal("0.01"), res2.get(0).getAmount());
		assertEquals(new BigDecimal("0.00"), res2.get(1).getAmount());

		//49% - 51%
		assertEquals(2, res3.size());
		assertEquals(new BigDecimal("0.01"), res3.get(0).getAmount());
		assertEquals(new BigDecimal("0.00"), res3.get(1).getAmount());

		//50% - 50%
		assertEquals(2, res4.size());
		assertEquals(new BigDecimal("0.01"), res4.get(0).getAmount());
		assertEquals(new BigDecimal("0.00"), res4.get(1).getAmount());

		//99% - 1%
		assertEquals(2, res5.size());
		assertEquals(new BigDecimal("0.01"), res5.get(0).getAmount());
		assertEquals(new BigDecimal("0.00"), res5.get(1).getAmount());

		//100%
		assertEquals(1, res6.size());
		assertEquals(new BigDecimal("0.01"), res6.get(0).getAmount());

		//25% - 25% - 25% - 25%
		assertEquals(4, res7.size());
		assertEquals(new BigDecimal("0.01"), res7.get(0).getAmount());
		assertEquals(new BigDecimal("0.00"), res7.get(1).getAmount());
		assertEquals(new BigDecimal("0.00"), res7.get(2).getAmount());
		assertEquals(new BigDecimal("0.00"), res7.get(3).getAmount());
	}

	@Test
	public void splitAndShare()
	{
		final Money val1 = new Money(new BigDecimal("0.96"), euro);
		final List<Money> res1 = val1.split(Percentage.TEN, Percentage.TEN, Percentage.TEN, Percentage.TEN, Percentage.TEN,
				Percentage.TEN, Percentage.TEN, Percentage.TEN, Percentage.TEN, Percentage.TEN);
		assertEquals(10, res1.size());
		assertEquals(new BigDecimal("0.10"), res1.get(0).getAmount());
		assertEquals(new BigDecimal("0.10"), res1.get(1).getAmount());
		assertEquals(new BigDecimal("0.10"), res1.get(2).getAmount());
		assertEquals(new BigDecimal("0.10"), res1.get(3).getAmount());
		assertEquals(new BigDecimal("0.10"), res1.get(4).getAmount());
		assertEquals(new BigDecimal("0.10"), res1.get(5).getAmount());
		assertEquals(new BigDecimal("0.09"), res1.get(6).getAmount());
		assertEquals(new BigDecimal("0.09"), res1.get(7).getAmount());
		assertEquals(new BigDecimal("0.09"), res1.get(8).getAmount());
		assertEquals(new BigDecimal("0.09"), res1.get(9).getAmount());
	}

	@Test
	public void testOtherSplitMethod()
	{
		final Money val1 = new Money(new BigDecimal("0.02"), euro);
		final List<Money> res1 = val1.split(Arrays.asList(new Percentage(50), new Percentage(50)));

		//50% - 50%
		assertEquals(2, res1.size());
		assertEquals(new BigDecimal("0.01"), res1.get(0).getAmount());
		assertEquals(new BigDecimal("0.01"), res1.get(1).getAmount());
	}

	@Test
	public void testFalsePercentList()
	{
		final Money val1 = new Money(new BigDecimal("0.02"), euro);

		try
		{
			val1.split(Arrays.asList(new Percentage(50)));
			fail("a MoneyException should be thrown here");
		}
		catch (final AmountException e)
		{
			assertTrue(e.getMessage().contains("less than"));
		}
		catch (final Exception e)
		{
			fail("Got unexpected exception: " + e);
		}

		try
		{
			val1.split(Arrays.asList(new Percentage(150)));
			fail("a MoneyException should be thrown here");
		}
		catch (final AmountException e)
		{
			assertTrue(e.getMessage().contains("greater than"));
		}
		catch (final Exception e)
		{
			fail("Got unexpected exception: " + e);
		}
	}

	@Test
	public void testEmptyPercentList()
	{
		final Money val1 = new Money(new BigDecimal("0.02"), euro);

		try
		{
			val1.split(Collections.EMPTY_LIST);
			fail("a IllegalArgumentException should be thrown here");
		}
		catch (final IllegalArgumentException e)
		{
			//fine here
		}
		catch (final Exception e)
		{
			fail("Got unexpected exception: " + e);
		}
	}

	@Test
	public void testZeroPercentList()
	{
		final Money val1 = new Money(new BigDecimal("34.02"), euro);
		final List<Money> res1 = val1.split(new Percentage(0));
		assertEquals(2, res1.size());
		assertEquals(new BigDecimal("0.00"), res1.get(0).getAmount());
		assertEquals(new BigDecimal("34.02"), res1.get(1).getAmount());
	}

	@Test
	public void testSumMoney()
	{
		final Money val1 = new Money("20.3", euro);
		final Money val2 = new Money("100", euro);
		final Money val3 = new Money("5", euro);
		assertEquals(new Money("125.3", euro), Money.sum(val1, val2, val3));

		final Money neg = new Money("-20.3", euro);
		assertEquals(new Money("0", euro), Money.sum(val1, neg));
		assertEquals(val1, Money.sum(val1));

		final Money otherMoney = new Money("5", new Currency("$", 2));
		try
		{
			Money.sum(otherMoney, val1);
			fail("Expected CurrenciesAreNotEqualException");
		}
		catch (final CurrenciesAreNotEqualException e)
		{
			//fine
		}

		try
		{
			Money.sum();
			fail("Expected MoneyException");
		}
		catch (final AmountException e)
		{
			//fine
		}
	}

	@Test(expected = AmountException.class)
	public void testSumMoneyCollection()
	{
		Money.sum(Collections.EMPTY_SET);
	}

	@Test
	public void testSort()
	{
		List<Money> moneyList = Money.valueOf(euro, "12.33", "1999.99", "1.11", "0.01");
		Money.sortDescending(moneyList);
		assertEquals(Money.valueOf(euro, "1999.99", "12.33", "1.11", "0.01"), moneyList);

		moneyList = Money.valueOf(euro, "12.33", "1999.99", "1.11", "0.01");
		Money.sortAscending(moneyList);
		assertEquals(Money.valueOf(euro, "0.01", "1.11", "12.33", "1999.99"), moneyList);

		final Map<String, Money> moneyMap = new HashMap<String, Money>();
		moneyMap.put("Axel", new Money("1.99", euro));
		moneyMap.put("Marcel", new Money("2.50", euro));
		moneyMap.put("Andreas", new Money("0.99", euro));

		assertEquals(Arrays.asList("Andreas", "Axel", "Marcel"), Money.sortByMoneyAscending(moneyMap));
		assertEquals(Arrays.asList("Marcel", "Axel", "Andreas"), Money.sortByMoneyDescending(moneyMap));

		// TODO test exceptions on NULL or alien currency
	}

	@Test
	public void testMoneyToPercentage()
	{
		List<Money> moneyList = Money.valueOf(euro, "12.33", "1999.99", "1.11", "0.01");

		assertEquals(Percentage.valueOf(0, 100, 0, 0), Money.getPercentages(moneyList, 0));
		assertEquals(Percentage.valueOf("0.6", "99.4", "0.0", "0.0"), Money.getPercentages(moneyList, 1));
		assertEquals(Percentage.valueOf("0.61", "99.34", "0.05", "0.00"), Money.getPercentages(moneyList, 2));
		assertEquals(Percentage.valueOf("0.613", "99.332", "0.055", "0.000"), Money.getPercentages(moneyList, 3));
		assertEquals(Percentage.valueOf("0.6124", "99.3320", "0.0552", "0.0004"), Money.getPercentages(moneyList, 4));

		moneyList = Money.valueOf(euro, "0.01", "12.33", "1999.99", "1.11");

		assertEquals(Percentage.valueOf(0, 0, 100, 0), Money.getPercentages(moneyList, 0));
		assertEquals(Percentage.valueOf("0.0", "0.6", "99.4", "0.0"), Money.getPercentages(moneyList, 1));
		assertEquals(Percentage.valueOf("0.00", "0.61", "99.34", "0.05"), Money.getPercentages(moneyList, 2));
		assertEquals(Percentage.valueOf("0.000", "0.613", "99.332", "0.055"), Money.getPercentages(moneyList, 3));
		assertEquals(Percentage.valueOf("0.0004", "0.6124", "99.3320", "0.0552"), Money.getPercentages(moneyList, 4));
	}
}
