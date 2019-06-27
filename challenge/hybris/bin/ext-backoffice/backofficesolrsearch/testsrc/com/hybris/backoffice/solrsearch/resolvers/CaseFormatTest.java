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
package com.hybris.backoffice.solrsearch.resolvers;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CaseFormatTest
{
	private static final String TEST_STRING = "Test";

	@Test
	public void testLowerCase()
	{
		Assertions.assertThat(new CaseFormat(CaseFormat.Case.TO_LOWER).format(TEST_STRING)).isEqualTo(TEST_STRING.toLowerCase());
	}

	@Test
	public void testUpperCase()
	{
		Assertions.assertThat(new CaseFormat(CaseFormat.Case.TO_UPPER).format(TEST_STRING)).isEqualTo(TEST_STRING.toUpperCase());
	}

	@Test
	public void testSwapCase()
	{
		Assertions.assertThat(new CaseFormat(CaseFormat.Case.SWAP).format(TEST_STRING))
				.isEqualTo(StringUtils.swapCase(TEST_STRING));
	}

	@Test
	public void testNull()
	{
		Assertions.assertThat(new CaseFormat(CaseFormat.Case.SWAP).format(null)).isEmpty();
	}

	@Test
	public void testParse() throws ParseException
	{
		Assertions.assertThat(new CaseFormat(CaseFormat.Case.SWAP).parseObject(TEST_STRING)).isEqualTo(TEST_STRING);
	}

	@Test
	public void testParseNull() throws ParseException
	{
		Assertions.assertThat(new CaseFormat(CaseFormat.Case.SWAP).parseObject(null)).isEqualTo(null);
	}
}
