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
package de.hybris.platform.couponservices.services.impl;

import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_ALGORITHM_DEFAULT_VALUE;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_ALGORITHM_PROPERTY;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_ALPHABET_LENGTH_DEFAULT_VALUE;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_ALPHABET_LENGTH_PROPERTY;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_GLOBAL_CHARACTERSET_DEFAULT_VALUE;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_GLOBAL_CHARACTERSET_PROPERTY;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.couponservices.couponcodegeneration.CouponCodeCipherTextGenerationStrategy;
import de.hybris.platform.couponservices.couponcodegeneration.CouponCodeClearTextGenerationStrategy;
import de.hybris.platform.couponservices.couponcodegeneration.CouponCodeGenerationException;
import de.hybris.platform.couponservices.couponcodegeneration.impl.DefaultCouponCodesGenerator;
import de.hybris.platform.couponservices.model.CodeGenerationConfigurationModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for coupon code generation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponCodeGenerationServiceUnitTest
{

	private static final String COUPON_ALPHABET = "0123456789ABCDEF";

	private static final String COUPON_SIGNATURE = "wddu0O6HiHxOUEh/zbFMOQ==";

	private static final String COUPON_PREFIX = "PREFIX";

	private static final String COUPON_CODE_SEPARATOR = "-";

	private static final String PREFIX_DELIM = COUPON_PREFIX + COUPON_CODE_SEPARATOR;

	@InjectMocks
	private DefaultCouponCodeGenerationService couponCodeGenerationService;
	@InjectMocks
	private DefaultCouponCodesGenerator couponCodesGenerator;

	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private MultiCodeCouponModel coupon;

	@Mock
	private CodeGenerationConfigurationModel codeGenerationConfiguration;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private CouponCodeClearTextGenerationStrategy clearTextStrategy;

	@Mock
	private CouponCodeCipherTextGenerationStrategy cipherTextStrategy;

	@Mock
	private ModelService modelService;

	@Mock
	private MediaService mediaService;

	@Before
	public void setUp() throws Exception
	{
		couponCodeGenerationService.setCodeSeparatorPattern("[-#]");
		couponCodeGenerationService.setCouponCodesGenerator(couponCodesGenerator);
		when(coupon.getAlphabet()).thenReturn(COUPON_ALPHABET);
		when(coupon.getSignature()).thenReturn(COUPON_SIGNATURE);
		when(coupon.getCodeGenerationConfiguration()).thenReturn(codeGenerationConfiguration);
		when(coupon.getCouponId()).thenReturn(COUPON_PREFIX);
		when(codeGenerationConfiguration.getCodeSeparator()).thenReturn(COUPON_CODE_SEPARATOR);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(COUPON_CODE_GENERATION_ALGORITHM_PROPERTY, COUPON_CODE_GENERATION_ALGORITHM_DEFAULT_VALUE))
				.thenReturn(COUPON_CODE_GENERATION_ALGORITHM_DEFAULT_VALUE);
		when(Integer.valueOf(configuration.getInt(COUPON_CODE_GENERATION_ALPHABET_LENGTH_PROPERTY,
				COUPON_CODE_GENERATION_ALPHABET_LENGTH_DEFAULT_VALUE)))
						.thenReturn(Integer.valueOf(COUPON_CODE_GENERATION_ALPHABET_LENGTH_DEFAULT_VALUE));
		when(configuration.getString(COUPON_CODE_GENERATION_GLOBAL_CHARACTERSET_PROPERTY,
				COUPON_CODE_GENERATION_GLOBAL_CHARACTERSET_DEFAULT_VALUE))
						.thenReturn(COUPON_CODE_GENERATION_GLOBAL_CHARACTERSET_DEFAULT_VALUE);
		when(configuration.getInteger(eq("couponservices.code.generation.batch.size"), Integer.valueOf(anyInt())))
				.thenReturn(Integer.valueOf(1000));
		couponCodesGenerator.afterPropertiesSet();
		couponCodeGenerationService.afterPropertiesSet();

	}

	@Test
	public void testCreateAlphabet()
	{
		couponCodeGenerationService = spy(couponCodeGenerationService);
		final String alphabet = couponCodeGenerationService.generateCouponAlphabet();
		assertNotNull(alphabet);
		assertEquals(COUPON_CODE_GENERATION_ALPHABET_LENGTH_DEFAULT_VALUE, alphabet.length());
	}

	@Test(expected = CouponCodeGenerationException.class)
	public void testCreateAlphabetWhenCodeSeparatorIsPartOfAlphabet()
	{
		couponCodeGenerationService.setCodeSeparatorPattern("[A]");
		couponCodeGenerationService.afterPropertiesSet();
		couponCodeGenerationService = spy(couponCodeGenerationService);
		couponCodeGenerationService.generateCouponAlphabet();
	}

	@Test
	public void testIsValidCodeSeparatorNull()
	{
		couponCodeGenerationService.setCodeSeparatorPattern("[-#_;\\|\\+\\*\\.]");
		couponCodeGenerationService.afterPropertiesSet();

		assertThat(couponCodeGenerationService.isValidCodeSeparator(null)).isFalse();
	}

	@Test
	public void testIsValidCodeSeparatorLengthMoreThen1()
	{
		couponCodeGenerationService.setCodeSeparatorPattern("[-#_;\\|\\+\\*\\.]");
		couponCodeGenerationService.afterPropertiesSet();

		assertThat(couponCodeGenerationService.isValidCodeSeparator("||")).isFalse();
	}

	@Test
	public void testIsValidCodeSeparatorWrongChar()
	{
		couponCodeGenerationService.setCodeSeparatorPattern("[-#_;\\|\\+\\*\\.]");
		couponCodeGenerationService.afterPropertiesSet();

		assertThat(couponCodeGenerationService.isValidCodeSeparator("A")).isFalse();
	}

	@Test
	public void testIsValidCodeSeparatorOk()
	{
		couponCodeGenerationService.setCodeSeparatorPattern("[-#_;\\|\\+\\*\\.]");
		couponCodeGenerationService.afterPropertiesSet();

		assertThat(couponCodeGenerationService.isValidCodeSeparator("-")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator("#")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator("|")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator(";")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator("_")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator("+")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator("*")).isTrue();
		assertThat(couponCodeGenerationService.isValidCodeSeparator(".")).isTrue();
	}

	@Test
	public void testWrongLengths() throws CouponCodeGenerationException
	{
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartCount())).thenReturn(Integer.valueOf(1));

		int exceptionCounter = 0;
		for (int i = -100; i < 100; i++)
		{
			when(Integer.valueOf(codeGenerationConfiguration.getCouponPartLength())).thenReturn(Integer.valueOf(i));
			try
			{
				final int clearTextLength = couponCodeGenerationService.getClearTextLength(coupon);
				final int cipherTextLength = couponCodeGenerationService.getCipherTextLength(coupon);
				final String clearText = text(clearTextLength, 'A');
				final String cipherText = text(cipherTextLength, 'C');
				when(clearTextStrategy.generateClearText(coupon, clearTextLength)).thenReturn(clearText);
				when(cipherTextStrategy.generateCipherText(coupon, clearText, cipherTextLength)).thenReturn(cipherText);
				couponCodeGenerationService.generateCouponCode(coupon);
			}
			catch (final CouponCodeGenerationException e)
			{
				exceptionCounter++;
			}
		}
		assertEquals(190, exceptionCounter);
	}


	@Test
	public void testExtractCouponPrefix() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 8, COUPON_CODE_SEPARATOR);

		final String prefix = couponCodeGenerationService.extractCouponPrefix(code);
		assertEquals(COUPON_PREFIX, prefix);
	}

	@Test
	public void testPrefixGeneration() throws CouponCodeGenerationException
	{
		when(coupon.getCouponId()).thenReturn(COUPON_PREFIX);
		when(codeGenerationConfiguration.getCodeSeparator()).thenReturn("#");
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartCount())).thenReturn(Integer.valueOf(4));
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartLength())).thenReturn(Integer.valueOf(4));
		final int clearTextLength = couponCodeGenerationService.getClearTextLength(coupon);
		final int cipherTextLength = couponCodeGenerationService.getCipherTextLength(coupon);
		final String clearText = text(clearTextLength, 'A');
		final String cipherText = text(cipherTextLength, 'C');
		when(clearTextStrategy.generateClearText(coupon, clearTextLength)).thenReturn(clearText);
		when(cipherTextStrategy.generateCipherText(coupon, clearText, cipherTextLength)).thenReturn(cipherText);
		couponCodeGenerationService = spy(couponCodeGenerationService);

		final String code = couponCodeGenerationService.generateCouponCode(coupon);
		assertEquals("generated code doesn't match", "PREFIX#AAAA#AAAA#CCCC#CCCC", code);

	}


	// used as template method for all the variations of testing differnt lengths
	protected String generateCouponLengthAndDelimitersYxZ(final int parts, final int lengths, final String separator)
			throws CouponCodeGenerationException
	{
		when(codeGenerationConfiguration.getCodeSeparator()).thenReturn(separator);
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartCount())).thenReturn(Integer.valueOf(parts));
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartLength())).thenReturn(Integer.valueOf(lengths));
		final int clearTextLength = couponCodeGenerationService.getClearTextLength(coupon);
		final int cipherTextLength = couponCodeGenerationService.getCipherTextLength(coupon);
		final String clearText = text(clearTextLength, 'A');
		final String cipherText = text(cipherTextLength, 'C');
		when(clearTextStrategy.generateClearText(coupon, clearTextLength)).thenReturn(clearText);
		when(cipherTextStrategy.generateCipherText(coupon, clearText, cipherTextLength)).thenReturn(cipherText);
		couponCodeGenerationService = spy(couponCodeGenerationService);
		return couponCodeGenerationService.generateCouponCode(coupon);
	}



	///////////////
	// LENGTH 4  //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AACC", code);

		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-C-C", code);

		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}


	///////////////
	// LENGTH 8  //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x8() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 8, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAACCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters8x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(8, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-A-A-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}


	///////////////
	// LENGTH 12 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x12() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 12, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAACCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x6() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 6, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAA-CCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters3x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(3, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AACC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x3() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 3, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAA-AAA-CCC-CCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters6x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(6, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters12x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(12, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-A-A-A-A-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}



	///////////////
	// LENGTH 16 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x16() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 16, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x8() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 8, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAA-CCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters8x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(8, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters16x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(16, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}



	///////////////
	// LENGTH 20 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x20() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 20, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x10() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 10, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACC-CCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x5() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 5, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAA-AAACC-CCCCC-CCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters5x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(5, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters10x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(10, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters20x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(20, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}



	///////////////
	// LENGTH 24 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x24() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 24, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x12() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 12, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCC-CCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters3x8() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(3, 8, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAA-CCCCCCCC-CCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x6() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 6, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAA-AACCCC-CCCCCC-CCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters6x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(6, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters8x3() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(8, 3, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAA-AAA-AAC-CCC-CCC-CCC-CCC-CCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters12x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(12, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC-CC-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters24x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(24, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}


	///////////////
	// LENGTH 28 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x28() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 28, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x14() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 14, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCC-CCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x7() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 7, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAA-ACCCCCC-CCCCCCC-CCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters7x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(7, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC-CCCC-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters14x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(14, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters28x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(28, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C",
				code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}



	///////////////
	// LENGTH 32 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x32() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 32, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x16() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 16, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCC-CCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x8() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 8, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAA-CCCCCCCC-CCCCCCCC-CCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters8x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(8, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC-CCCC-CCCC-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters16x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(16, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters32x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(32, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!",
				PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}



	///////////////
	// LENGTH 36 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x36() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 36, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x18() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 18, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCC-CCCCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters3x12() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(3, 12, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCC-CCCCCCCCCCCC-CCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x9() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 9, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAAC-CCCCCCCCC-CCCCCCCCC-CCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters6x6() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(6, 6, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAA-AACCCC-CCCCCC-CCCCCC-CCCCCC-CCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters9x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(9, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC-CCCC-CCCC-CCCC-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters12x3() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(12, 3, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAA-AAA-AAC-CCC-CCC-CCC-CCC-CCC-CCC-CCC-CCC-CCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters18x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(18, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters36x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(36, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!",
				PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}



	///////////////
	// LENGTH 40 //
	///////////////
	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters1x40() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(1, 40, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters2x20() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(2, 20, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACCCCCCCCCCCC-CCCCCCCCCCCCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}


	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters4x10() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(4, 10, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAACC-CCCCCCCCCC-CCCCCCCCCC-CCCCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters5x8() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(5, 8, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAAAAA-CCCCCCCC-CCCCCCCC-CCCCCCCC-CCCCCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters8x5() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(8, 5, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAAA-AAACC-CCCCC-CCCCC-CCCCC-CCCCC-CCCCC-CCCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters10x4() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(10, 4, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AAAA-AAAA-CCCC-CCCC-CCCC-CCCC-CCCC-CCCC-CCCC-CCCC", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters20x2() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(20, 2, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!", PREFIX_DELIM + "AA-AA-AA-AA-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC-CC",
				code);
	}

	@Test
	public void testGenerateAndVerifyCouponLengthAndDelimiters40x1() throws CouponCodeGenerationException
	{
		final String code = generateCouponLengthAndDelimitersYxZ(40, 1, COUPON_CODE_SEPARATOR);
		assertEquals("generated code doesn't match!",
				PREFIX_DELIM + "A-A-A-A-A-A-A-A-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C-C", code);
		final boolean result = couponCodeGenerationService.verifyCouponCode(coupon, code);
		assertTrue("code verification failed for" + code, result);
	}

	@Test
	public void testGenerateCouponCodes() throws CouponCodeGenerationException
	{
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartCount())).thenReturn(Integer.valueOf(1));
		when(Integer.valueOf(codeGenerationConfiguration.getCouponPartLength())).thenReturn(Integer.valueOf(12));
		final int clearTextLength = couponCodeGenerationService.getClearTextLength(coupon);
		final int cipherTextLength = couponCodeGenerationService.getCipherTextLength(coupon);
		final String clearText = text(clearTextLength, 'A');
		final String cipherText = text(cipherTextLength, 'C');
		when(clearTextStrategy.generateClearText(coupon, clearTextLength)).thenReturn(clearText);
		when(cipherTextStrategy.generateCipherText(coupon, clearText, cipherTextLength)).thenReturn(cipherText);
		final int couponsToGenerate = 100;
		couponCodesGenerator = spy(couponCodesGenerator);
		couponCodeGenerationService.setCouponCodesGenerator(couponCodesGenerator);
		couponCodeGenerationService.setKeyGenerator(keyGenerator);
		final Optional<MediaModel> generatedMediaModel = couponCodeGenerationService.generateCouponCodes(coupon, couponsToGenerate);

		verify(couponCodesGenerator, Mockito.times(couponsToGenerate)).generateNextCouponCode(coupon);
		verify(keyGenerator, Mockito.times(1)).generate();
		verify(modelService, Mockito.times(1)).save(coupon);
		verify(modelService, Mockito.times(1)).save(generatedMediaModel.get());
		verify(mediaService, Mockito.times(1)).setStreamForMedia(Mockito.eq(generatedMediaModel.get()),
				Mockito.any(InputStream.class), Mockito.eq(generatedMediaModel.get().getCode() + ".csv"), Mockito.eq("text/csv"));

	}

	protected String text(final int length, final char character)
	{
		final StringBuilder builder = new StringBuilder();
		while (builder.length() < length)
		{
			builder.append(character);
		}
		return builder.toString();
	}
}
