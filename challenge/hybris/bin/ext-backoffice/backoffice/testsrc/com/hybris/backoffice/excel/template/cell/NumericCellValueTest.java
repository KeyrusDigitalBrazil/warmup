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
package com.hybris.backoffice.excel.template.cell;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.util.ExcelDateUtils;


@RunWith(MockitoJUnitRunner.class)
public class NumericCellValueTest extends AbstractCellValueTest
{

	@Mock
	private ExcelDateUtils excelDateUtils;
	@Spy
	private NumericCellValue numericCellValue = new NumericCellValue();

	@Before
	public void setUp()
	{
		numericCellValue.setExcelDateUtils(excelDateUtils);
	}

	@Test
	@Override
	public void shouldGivenTypeCellBeHandled()
	{
		assertTrue(numericCellValue.canHandle(CellType.NUMERIC));
	}

	@Test
	@Override
	public void shouldGivenTypeCellNotBeHandled()
	{
		assertFalse(numericCellValue.canHandle(CellType.FORMULA));
	}

	@Test
	@Override
	public void shouldGivenValueBeHandledCorrectly()
	{
		// given
		final double cellValue = 2.57;
		final Cell cell = mock(Cell.class);
		given(cell.getNumericCellValue()).willReturn(cellValue);
		doReturn(false).when(numericCellValue).isCellDateFormatted(any());

		// when
		final Optional<String> returnedValue = numericCellValue.getValue(cell);

		// then
		assertTrue(returnedValue.isPresent());
		assertThat(returnedValue.get()).isEqualTo(String.valueOf(cellValue));
	}
}
