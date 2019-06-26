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
package com.hybris.backoffice.excel.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelSheetNamingStrategyTest
{
	@InjectMocks
	private DefaultExcelSheetNamingStrategy strategy;
	@Mock
	private Workbook workbook;

	@Test
	public void verifyNameHasCorrectLength()
	{
		final String maxCharName = RandomStringUtils.randomAlphabetic(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME);

		final String name = strategy.generateName(workbook, maxCharName);

		assertThat(name).hasSize(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME);
		assertThat(name).isEqualTo(maxCharName);
	}

	@Test
	public void verifyTooLongNameTruncated()
	{
		final String maxCharName = RandomStringUtils.randomAlphabetic(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME + 4);
		final String firstTrySuffix = DefaultExcelSheetNamingStrategy.SHEET_NUMBER_SEPARATOR.concat("1");

		final String name = strategy.generateName(workbook, maxCharName);

		assertThat(name).hasSize(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME);
		final int endOfOriginalName = DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME - firstTrySuffix.length();
		assertThat(name).isEqualTo(maxCharName.substring(0, endOfOriginalName).concat(firstTrySuffix));
	}

	@Test
	public void verifyNameIsCheckedInWorkbook()
	{
		final String maxCharName = RandomStringUtils.randomAlphabetic(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME + 4);
		final String firstTrySuffix = DefaultExcelSheetNamingStrategy.SHEET_NUMBER_SEPARATOR.concat("1");
		final int endOfOriginalName = DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME - firstTrySuffix.length();
		final String firstTryName = maxCharName.substring(0, endOfOriginalName).concat(firstTrySuffix);
		final Sheet sheet = Mockito.mock(Sheet.class);

		when(workbook.getSheet(firstTryName)).thenReturn(sheet);
		final String name = strategy.generateName(workbook, maxCharName);

		assertThat(name).hasSize(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME);
		final String secondTrySuffix = DefaultExcelSheetNamingStrategy.SHEET_NUMBER_SEPARATOR.concat("2");
		final int endOfOriginalNameSecondTry = DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME - secondTrySuffix.length();
		assertThat(name).isEqualTo(maxCharName.substring(0, endOfOriginalNameSecondTry).concat(secondTrySuffix));
	}

	@Test
	public void verifyNameWithSuffixExists()
	{
		final String maxCharName = RandomStringUtils.randomAlphabetic(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME - 1);
		final Sheet sheet = Mockito.mock(Sheet.class);

		when(workbook.getSheet(maxCharName)).thenReturn(sheet);
		final String name = strategy.generateName(workbook, maxCharName);

		assertThat(name).hasSize(DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME);
		final String secondTrySuffix = DefaultExcelSheetNamingStrategy.SHEET_NUMBER_SEPARATOR.concat("1");
		final int endOfOriginalNameSecondTry = DefaultExcelSheetNamingStrategy.MAX_LENGTH_SHEET_NAME - secondTrySuffix.length();
		assertThat(name).isEqualTo(maxCharName.substring(0, endOfOriginalNameSecondTry).concat(secondTrySuffix));
	}

	@Test
	public void verifyShortNameWhichExists()
	{
		final String firstTrySuffix = DefaultExcelSheetNamingStrategy.SHEET_NUMBER_SEPARATOR.concat("1");
		final Sheet sheet = Mockito.mock(Sheet.class);

		when(workbook.getSheet(ProductModel._TYPECODE)).thenReturn(sheet);
		final String name = strategy.generateName(workbook, ProductModel._TYPECODE);

		final String expectedName = ProductModel._TYPECODE.concat(firstTrySuffix);

		assertThat(name).hasSize(expectedName.length());
		assertThat(name).isEqualTo(expectedName);
	}

}
