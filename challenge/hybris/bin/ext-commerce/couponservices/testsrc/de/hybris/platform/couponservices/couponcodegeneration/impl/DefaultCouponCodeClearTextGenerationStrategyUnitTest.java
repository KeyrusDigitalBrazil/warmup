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
package de.hybris.platform.couponservices.couponcodegeneration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.couponcodegeneration.CouponCodeGenerationException;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponCodeClearTextGenerationStrategyUnitTest
{

	@InjectMocks
	private DefaultCouponCodeClearTextGenerationStrategy strategy;

	@Mock
	private MultiCodeCouponModel coupon;

	private final static String ALPHABET = "0123456789ABCDEF";

	@Before
	public void setUp() throws Exception
	{
		when(coupon.getAlphabet()).thenReturn(ALPHABET);
		strategy.afterPropertiesSet();
	}

	@Test
	public void testWrongInputLengths() throws CouponCodeGenerationException
	{
		//only length 2,4,6,8 are valid
		int exceptionCounter = 0;

		for (int i = -100; i < 100; i++)
		{
			try
			{
				strategy.generateClearText(coupon, i);
			}
			catch (final SystemException se)
			{
				exceptionCounter++;
			}
		}
		assertEquals(196, exceptionCounter);
	}

	@Test
	public void testGenerateAndInverseOperationForSpecialCases() throws CouponCodeGenerationException
	{
		// test that creating a code and inverting back to the seed number works.
		// we test just a couple of codes, starting from 0
		long start = 0;
		final long totalRuns = 100L;
		doTestGenerateAndInverseOperation(start, totalRuns, 2);
		doTestGenerateAndInverseOperation(start, totalRuns, 4);
		doTestGenerateAndInverseOperation(start, totalRuns, 6);
		doTestGenerateAndInverseOperation(start, totalRuns, 8);

		// and also around the  Integer.MAX_VALUE range (requires length 8)
		start = Integer.MAX_VALUE - 1000;
		doTestGenerateAndInverseOperation(start, totalRuns, 8);
	}

	protected void doTestGenerateAndInverseOperation(final long start, final long totalRuns, final int clearTextLength)
			throws CouponCodeGenerationException
	{
		for (long seed = start; seed < start + totalRuns; seed++)
		{
			when(coupon.getCouponCodeNumber()).thenReturn(Long.valueOf(seed));
			final String code = strategy.generateClearText(coupon, clearTextLength);
			final long inverse = strategy.getCouponCodeNumberForClearText(coupon, code);
			assertEquals("inversion failed for code:" + code + ", seed:" + seed, seed, inverse);
		}
	}

	@Test(expected = CouponCodeGenerationException.class)
	public void testClearTextGenerationFailsWhenLimitIsReachedLength2() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(256L, 2);
	}

	@Test(expected = CouponCodeGenerationException.class)
	public void testClearTextGenerationFailsWhenLimitIsReachedLength4() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(65536L, 4);

	}

	@Test(expected = CouponCodeGenerationException.class)
	public void testClearTextGenerationFailsWhenLimitIsReachedLength6() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(16777216L, 6);

	}

	@Test(expected = CouponCodeGenerationException.class)
	public void testClearTextGenerationFailsWhenLimitIsReachedLength8() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(4294967296L, 8);
	}

	@Test
	public void testClearTextGenerationSucceedsBeforeLimitIsReachedLength2() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(255L, 8);
	}

	@Test
	public void testClearTextGenerationSucceedsBeforeLimitIsReachedLength4() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(65535L, 8);

	}

	@Test
	public void testClearTextGenerationSucceedsBeforeLimitIsReachedLength6() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(16777215L, 8);

	}

	@Test
	public void testClearTextGenerationSucceedsBeforeLimitIsReachedLength8() throws CouponCodeGenerationException
	{
		doTestClearTextGeneration(4294967295L, 8);
	}


	protected void doTestClearTextGeneration(final long couponCodeNumber, final int length) throws CouponCodeGenerationException
	{
		when(coupon.getCouponCodeNumber()).thenReturn(Long.valueOf(couponCodeNumber));
		strategy.generateClearText(coupon, length);
	}

	@Test
	public void testCodeForLength2() throws CouponCodeGenerationException
	{
		final String code = strategy.generateClearText(coupon, 2);
		assertEquals(2, code.length());
		// with fixed alphabet and seed 0 the cleartext is predictable
		assertEquals("37", code);
	}

	@Test
	public void testCodeForLength4() throws CouponCodeGenerationException
	{
		final String code = strategy.generateClearText(coupon, 4);
		assertEquals(4, code.length());
		// with fixed alphabet and seed 0 the cleartext is predictable
		assertEquals("3759", code);
	}

	@Test
	public void testCodeForLength6() throws CouponCodeGenerationException
	{
		final String code = strategy.generateClearText(coupon, 6);
		assertEquals(6, code.length());
		// with fixed alphabet and seed 0 the cleartext is predictable
		assertEquals("37597B", code);
	}

	@Test
	public void testCodeForLength8() throws CouponCodeGenerationException
	{
		final String code = strategy.generateClearText(coupon, 8);
		assertEquals(8, code.length());
		// with fixed alphabet and seed 0 the cleartext is predictable
		assertEquals("37597BBF", code);
	}


	@Test
	public void testNoClearTextDuplicatesLength2() throws CouponCodeGenerationException
	{
		checkForDuplicateClearTextWithLength(2, 0L);
	}

	//@Test
	//this is a very long running test, so its disabled by default
	public void testNoClearTextDuplicatesLength4() throws CouponCodeGenerationException
	{
		checkForDuplicateClearTextWithLength(4, 0L);
	}

	//@Test
	//this is a very long running (and memory intensive) test, so its disabled by default
	public void testNoClearTextDuplicatesLength6() throws CouponCodeGenerationException
	{
		checkForDuplicateClearTextWithLength(6, 0L);
	}

	//@Test
	//this is a very long running (and memory intensive) test, so its disabled by default
	// in fact, it will probably run out of memory before completing
	public void testNoClearTextDuplicatesLength8() throws CouponCodeGenerationException
	{
		checkForDuplicateClearTextWithLength(8, 0L);
	}


	/**
	 * runs through all possible seed values with a default 16-char alphabet (based on clear-text length: length 2 -->
	 * 16^2 = 256, 16^4 = 65536 ...) and checks that for each distinct seed value the resulting cleartext doesn't lead to
	 * duplicates.
	 */
	protected void checkForDuplicateClearTextWithLength(final int length, long seed) throws CouponCodeGenerationException
	{
		// determine limit based on input length
		final long limit = (length == 2 ? 256L : (length == 4 ? 65536L : (length == 6 ? 16777216L : (length == 8 ? 4294967296L
				: -1L))));

		if (limit == -1L)
		{
			fail("only values for 2,4,6,8 are allowed.");
		}

		final Set<String> codes = new HashSet<>();

		for (long i = 0; i < limit; i++, seed++)
		{
			final Long key = Long.valueOf(seed);
			when(coupon.getCouponCodeNumber()).thenReturn(key);
			final String code = strategy.generateClearText(coupon, length);
			codes.add(code);
			if (codes.size() != (i + 1))
			{
				fail("duplicate code:" + code + " at count:" + i);
			}
		}
	}
}
