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
package com.hybris.backoffice.excel.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.template.cell.ExcelCellService;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class WorkbookMaxRowValidatorTest
{

	public static final int MAX_ROWS = 2000;

	@Mock
	private ExcelSheetService excelSheetService;

	@Mock
	private ExcelCellService excelCellService;

	@Spy
	@InjectMocks
	private WorkbookMaxRowValidator workbookMaxRowValidator;

	@Test
	public void shouldNotReturnValidationErrorWhenNumberOfRowsIsLessThanMaxValue()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final Sheet productSheet = mock(Sheet.class);
		when(excelSheetService.getSheets(workbook)).thenReturn(Collections.singletonList(productSheet));
		doReturn(5).when(workbookMaxRowValidator).getNumberOfCorrectRows(productSheet);
		doReturn(MAX_ROWS).when(workbookMaxRowValidator).getMaxRow();

		// when
		final List<ExcelValidationResult> validationResults = workbookMaxRowValidator.validate(workbook);

		// then
		assertThat(validationResults).isEmpty();
	}

	@Test
	public void shouldNotReturnValidationErrorWhenNumberOfRowsExactlyEqualsToMaxValue()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final Sheet productSheet = mock(Sheet.class);
		when(excelSheetService.getSheets(workbook)).thenReturn(Collections.singletonList(productSheet));
		doReturn(MAX_ROWS).when(workbookMaxRowValidator).getNumberOfCorrectRows(productSheet);
		doReturn(MAX_ROWS).when(workbookMaxRowValidator).getMaxRow();

		// when
		final List<ExcelValidationResult> validationResults = workbookMaxRowValidator.validate(workbook);

		// then
		assertThat(validationResults).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenNumberOfRowsIsExceeded()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final Sheet productSheet = mock(Sheet.class);
		when(excelSheetService.getSheets(workbook)).thenReturn(Collections.singletonList(productSheet));
		doReturn(MAX_ROWS + WorkbookMaxRowValidator.FIRST_DATA_ROW_INDEX).when(workbookMaxRowValidator)
				.getNumberOfCorrectRows(productSheet);
		doReturn(MAX_ROWS).when(workbookMaxRowValidator).getMaxRow();

		// when
		final List<ExcelValidationResult> validationResults = workbookMaxRowValidator.validate(workbook);

		// then
		assertThat(validationResults).isNotEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenSumOfNumberOfRowsIsExceeded()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final Sheet productSheet = mock(Sheet.class);
		final Sheet shoeSheet = mock(Sheet.class);
		when(excelSheetService.getSheets(workbook)).thenReturn(Arrays.asList(productSheet, shoeSheet));
		doReturn(MAX_ROWS / 2 + WorkbookMaxRowValidator.FIRST_DATA_ROW_INDEX + 1).when(workbookMaxRowValidator)
				.getNumberOfCorrectRows(productSheet);
		doReturn(MAX_ROWS / 2 + WorkbookMaxRowValidator.FIRST_DATA_ROW_INDEX + 1).when(workbookMaxRowValidator)
				.getNumberOfCorrectRows(shoeSheet);
		doReturn(MAX_ROWS).when(workbookMaxRowValidator).getMaxRow();

		// when
		final List<ExcelValidationResult> validationResults = workbookMaxRowValidator.validate(workbook);

		// then
		assertThat(validationResults).isNotEmpty();
	}
}
