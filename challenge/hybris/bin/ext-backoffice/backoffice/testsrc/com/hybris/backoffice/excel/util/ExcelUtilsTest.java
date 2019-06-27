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
package com.hybris.backoffice.excel.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;


public class ExcelUtilsTest
{
	@Test
	public void testWithEscape()
	{
		final String[] tokens = ExcelUtils.extractExcelCellTokens(":10 eur:::liters:[Mon Dec 11 05:22:00 CET 2017]:test:");
		assertThat(tokens.length).isEqualTo(8);
		assertThat(tokens[0]).isEmpty();
		assertThat(tokens[1]).isEqualTo("10 eur");
		assertThat(tokens[2]).isEmpty();
		assertThat(tokens[3]).isEmpty();
		assertThat(tokens[4]).isEqualTo("liters");
		assertThat(tokens[5]).isEqualTo("Mon Dec 11 05:22:00 CET 2017");
		assertThat(tokens[6]).isEqualTo("test");
		assertThat(tokens[7]).isEmpty();
	}

	@Test
	public void testTrimValues()
	{
		final String[] tokens = ExcelUtils.extractExcelCellTokens(":10 eur : :: pieces:[Mon Dec 11 05:22:00 CET 2017]:test :");
		assertThat(tokens.length).isEqualTo(8);
		assertThat(tokens[0]).isEmpty();
		assertThat(tokens[1]).isEqualTo("10 eur");
		assertThat(tokens[2]).isEmpty();
		assertThat(tokens[3]).isEmpty();
		assertThat(tokens[4]).isEqualTo("pieces");
		assertThat(tokens[5]).isEqualTo("Mon Dec 11 05:22:00 CET 2017");
		assertThat(tokens[6]).isEqualTo("test");
		assertThat(tokens[7]).isEmpty();
	}

	@Test
	public void testEmptyEscapeGroup()
	{
		final String[] tokens = ExcelUtils.extractExcelCellTokens("pieces:[]: ");
		assertThat(tokens.length).isEqualTo(3);
		assertThat(tokens[0]).isEqualTo("pieces");
		assertThat(tokens[1]).isEmpty();
		assertThat(tokens[2]).isEmpty();
	}

	@Test
	public void testMoreEscapeGroups()
	{
		final String[] tokens = ExcelUtils.extractExcelCellTokens("kilos:[:fd:]:[:aa]:[aa:] ");
		assertThat(tokens.length).isEqualTo(4);
		assertThat(tokens[0]).isEqualTo("kilos");
		assertThat(tokens[1]).isEqualTo(":fd:");
		assertThat(tokens[2]).isEqualTo(":aa");
		assertThat(tokens[3]).isEqualTo("aa:");
	}

}
