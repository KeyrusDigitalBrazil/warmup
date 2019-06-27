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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DefaultExcelDateUtilsTest
{
	private final DefaultExcelDateUtils extractDateUtils = new DefaultExcelDateUtils();
	private TimeZone defaultTimeZone;

	@Before
	public void setup()
	{
		defaultTimeZone = java.util.TimeZone.getDefault();
	}

	@After
	public void tearDown()
	{
		TimeZone.setDefault(defaultTimeZone);
	}

	@Test
	public void testTestExtractRange()
	{
		final Pair<String, String> range = extractDateUtils.extractDateRange("123 to 200");
		assertThat(range.getLeft()).isEqualTo("123");
		assertThat(range.getRight()).isEqualTo("200");
	}

	@Test
	public void testExtractRangeWhitespaces()
	{
		final Pair<String, String> range = extractDateUtils.extractDateRange("123    to	 200");
		assertThat(range.getLeft()).isEqualTo("123");
		assertThat(range.getRight()).isEqualTo("200");
	}

	@Test
	public void testExportDateWhenServerIsInUTC()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		final ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 10, 23, 10, 46, 0, 0, ZoneId.systemDefault());
		final Date dateFrom = Date.from(zonedDateTime.toInstant());
		assertThat(extractDateUtils.exportDate(dateFrom)).isEqualTo("23.10.2017 10:46:00");
	}

	@Test
	public void testExportDateWhenServerInCET()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
		final ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 10, 23, 10, 46, 0, 0, ZoneId.systemDefault());
		final Date dateFrom = Date.from(zonedDateTime.toInstant());
		assertThat(extractDateUtils.exportDate(dateFrom)).isEqualTo("23.10.2017 08:46:00");
	}

	@Test
	public void testImportDateWhenServerIsInUTC()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		assertThat(extractDateUtils.importDate("23.10.2017 10:46:00")).isEqualTo("23.10.2017 10:46:00");
	}

	@Test
	public void testImportDateWhenServerInCET()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
		assertThat(extractDateUtils.importDate("23.10.2017 10:46:00")).isEqualTo("23.10.2017 12:46:00");
	}
}
