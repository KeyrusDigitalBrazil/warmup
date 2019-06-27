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

import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_ALGORITHM_DEFAULT_VALUE;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_ALGORITHM_PROPERTY;
import static java.lang.String.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xml.internal.security.utils.Base64;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponCodeCipherTextGenerationStrategyUnitTest
{
	private static final Logger log = LoggerFactory.getLogger(DefaultCouponCodeCipherTextGenerationStrategyUnitTest.class);

	@InjectMocks
	private DefaultCouponCodeCipherTextGenerationStrategy strategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private MultiCodeCouponModel coupon;

	private final static String ALPHABET = "0123456789ABCDEF";

	private final static String CLEARTEXT = "11111111";

	private final static String SIGNATURE = "VVRwdTQLcf9moC1kmuEa7g==";

	@Before
	public void setUp() throws Exception
	{
		when(coupon.getAlphabet()).thenReturn(ALPHABET);
		when(coupon.getSignature()).thenReturn(SIGNATURE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(COUPON_CODE_GENERATION_ALGORITHM_PROPERTY, COUPON_CODE_GENERATION_ALGORITHM_DEFAULT_VALUE))
				.thenReturn(COUPON_CODE_GENERATION_ALGORITHM_DEFAULT_VALUE);
		strategy.afterPropertiesSet();
	}

	@Test
	public void testWrongInputLengths()
	{
		//only length 2,4,6,8,12,16,20,24,28 or 32 are valid
		int exceptionCounter = 0;

		for (int i = -100; i < 100; i++)
		{
			try
			{
				strategy.generateCipherText(coupon, CLEARTEXT, i);
			}
			catch (final SystemException se)
			{
				exceptionCounter++;
			}
		}
		assertEquals(190, exceptionCounter);
	}

	@Test
	public void testCodeForLength2()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 2);
		// with fixed alphabet, cleartext and signature, the ciphertext is predictable
		assertEquals("BC", code);
	}

	@Test
	public void testCodeForLength4()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 4);
		assertEquals("BC49", code);
	}

	@Test
	public void testCodeForLength6()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 6);
		assertEquals("BC4930", code);
	}

	@Test
	public void testCodeForLength8()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 8);
		assertEquals("BC4930F4", code);
	}

	@Test
	public void testCodeForLength12()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 12);
		assertEquals("BC4930F46022", code);
	}

	@Test
	public void testCodeForLength16()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 16);
		assertEquals("BC4930F460227938", code);
	}

	@Test
	public void testCodeForLength20()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 20);
		assertEquals("BC4930F460227938B413", code);
	}

	@Test
	public void testCodeForLength24()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 24);
		assertEquals("BC4930F460227938B413CD6A", code);
	}

	@Test
	public void testCodeForLength28()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 28);
		assertEquals("BC4930F460227938B413CD6AA347", code);
	}

	@Test
	public void testCodeForLength32()
	{
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 32);
		assertEquals("BC4930F460227938B413CD6AA3477510", code);
	}

	protected String doTestForGivenLength(final MultiCodeCouponModel coupon, final String clearText, final int length)
	{
		final String code = strategy.generateCipherText(coupon, clearText, length);
		assertEquals("expected length " + length, length, code.length());
		return code;
	}

	@Test
	public void testWithDifferentAlphabet()
	{
		// shift alphabet by 1, so that compared with the default one of this test:
		// (0-->1, .. 3-->4, ... A-->B,... :
		final String ALPHABET = "123456789ABCDEF0";
		when(coupon.getAlphabet()).thenReturn(ALPHABET);
		final String code = doTestForGivenLength(coupon, CLEARTEXT, 32);
		// assert that the result is also just shifted by 1
		// CD5A410571338A49C524DE7BB4588621
		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// BC4930F460227938B413CD6AA3477510
		assertEquals("CD5A410571338A49C524DE7BB4588621", code);
	}

	//@Test
	// this test is disabled by default as technically a signature could be identical and cause this test to fail
	public void testWithDifferentSignatures()
	{
		final int COUNT = 3000;
		final int LENGTH = 32;
		final String CLEARTEXT = "01234567";
		final HashMap<String, String> cipherTexts = new HashMap<>();
		final SecureRandom random = new SecureRandom();

		// generate 16byte signatures each time
		for (int i = 0; i < COUNT; i++)
		{
			final byte[] signature = new byte[16];
			random.nextBytes(signature);
			final String base64Signature = Base64.encode(signature);
			when(coupon.getSignature()).thenReturn(base64Signature);
			final String cipherText = strategy.generateCipherText(coupon, CLEARTEXT, LENGTH);
			if (cipherTexts.containsKey(cipherText))
			{
				fail("duplicate ciphertext: " + cipherText + " for signatures: " + base64Signature + "and "
						+ cipherTexts.get(cipherText));

			}
			cipherTexts.put(cipherText, base64Signature);
		}
	}

	//@Test
	// this test is disabled by default, as technically two generated cleartexts could result in the same ciphertext.
	public void testWithDifferentClearTexts()
	{
		final int START = 0;
		final int COUNT = 1000000;
		final int LENGTH = 12;
		final List<String> cipherTexts = new ArrayList<>(COUNT);

		int duplicateCount = 0;
		// construct cleartext as simple 8 char hex string
		for (int i = START; i < COUNT; i++)
		{
			final String clearText = StringUtils.leftPad(Integer.toHexString(i).toUpperCase(), 8, "0");
			final String cipherText = strategy.generateCipherText(coupon, clearText, LENGTH);
			if (cipherTexts.contains(cipherText))
			{
				final String duplicateClearText = StringUtils
						.leftPad(Integer.toHexString(cipherTexts.indexOf(cipherText)).toUpperCase(), 8, "0");
				log.info("same ciphertext for different clearTexts: cipherText: {}, clearText: {}, clearText: {}", cipherText,
						clearText, duplicateClearText);
				duplicateCount++;
			}
			cipherTexts.add(cipherText);
			if (i % 10000 == 0)
			{
				log.info("{} done", valueOf(i));
			}
		}
		log.info("duplicates: {}", valueOf(duplicateCount));
	}


}
