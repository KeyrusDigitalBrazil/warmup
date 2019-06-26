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
package de.hybris.platform.acceleratorservices.payment.utils.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@UnitTest
public class DefaultAcceleratorDigestUtilsTest
{
	private static final String digestValue = "974TWTR/5e+bOOC9gDW7Vh4XXog=";
	private static final String SOME_TEST_KEY = "someTestKey";
	private static final String SOME_TEST_DATA = "someTestData";
	private DefaultAcceleratorDigestUtils utils;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception
	{
		utils = new DefaultAcceleratorDigestUtils();
		utils.setMacAlgorithm("HmacSHA1");
	}

	@Test
	public void testGetPublicDigest() throws InvalidKeyException, NoSuchAlgorithmException
	{

		final String result = utils.getPublicDigest(SOME_TEST_DATA, SOME_TEST_KEY);
		final String definedDigest = digestValue;
		Assertions.assertThat(result.equalsIgnoreCase(definedDigest)).isTrue();
	}

	@Test
	public void testGetPublicDigestForNullCustomValues() throws InvalidKeyException, NoSuchAlgorithmException
	{

		thrown.expect(NullPointerException.class);
		utils.getPublicDigest(SOME_TEST_DATA, null);
	}

	@Test
	public void testGetPublicDigestForNullKey() throws InvalidKeyException, NoSuchAlgorithmException
	{
		thrown.expect(NullPointerException.class);
		utils.getPublicDigest(null, SOME_TEST_KEY);
	}

}
