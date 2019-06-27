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
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FormulaCellValueTest extends AbstractCellValueTest
{
	private FormulaCellValue formulaCellValue = new FormulaCellValue();

	@Test
	@Override
	public void shouldGivenTypeCellBeHandled()
	{
		assertTrue(formulaCellValue.canHandle(CellType.FORMULA));
	}

	@Test
	@Override
	public void shouldGivenTypeCellNotBeHandled()
	{
		assertFalse(formulaCellValue.canHandle(CellType.STRING));
	}

	@Test
	@Override
	public void shouldGivenValueBeHandledCorrectly()
	{
		// given
		final String expectedValue = "val";
		final Cell cell = mock(Cell.class);
		final FormulaEvaluator formulaEvaluator = mock(FormulaEvaluator.class);
		final CreationHelper creationHelper = mock(CreationHelper.class);
		final Workbook workbook = mock(Workbook.class);
		final Sheet sheet = mock(Sheet.class);
		final Row row = mock(Row.class);
		final org.apache.poi.ss.usermodel.CellValue cellValue = new CellValue(expectedValue);

		given(cell.getRow()).willReturn(row);
		given(row.getSheet()).willReturn(sheet);
		given(sheet.getWorkbook()).willReturn(workbook);
		given(workbook.getCreationHelper()).willReturn(creationHelper);
		given(creationHelper.createFormulaEvaluator()).willReturn(formulaEvaluator);
		given(formulaEvaluator.evaluate(cell)).willReturn(cellValue);

		// when
		final Optional<String> returnedValue = formulaCellValue.getValue(cell);

		// then
		assertTrue(returnedValue.isPresent());
		assertThat(returnedValue.get()).isEqualTo(expectedValue);
	}
}
