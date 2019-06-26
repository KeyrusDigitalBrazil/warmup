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
package com.hybris.backoffice.excel.exporting;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.HEADER_ROW_INDEX;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.REFERENCE_PATTERN_ROW_INDEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import java.util.Collections;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.template.AttributeNameFormatter;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants.PkColumns;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants.UtilitySheet;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;
import com.hybris.backoffice.excel.template.populator.ExcelAttributeContext;
import com.hybris.backoffice.excel.translators.ExcelAttributeTranslator;
import com.hybris.backoffice.excel.translators.ExcelAttributeTranslatorRegistry;


@RunWith(MockitoJUnitRunner.class)
public class AbstractExcelExportWorkbookDecoratorTest
{
	@Mock
	ExcelCellService mockedExcelCellService;
	@Mock
	AttributeNameFormatter<ExcelClassificationAttribute> mockedAttributeNameFormatter;
	@Mock
	ExcelAttributeTranslatorRegistry mockedExcelAttributeTranslatorRegistry;
	@InjectMocks
	@Spy
	DefaultExcelExportClassificationWorkbookDecorator abstractExcelExportWorkbookDecorator;

	@Test
	public void shouldFindCellForAttributeAndItemAndFillCellWithDataFromTranslator()
	{
		// given
		final ItemModel item = mock(ItemModel.class);
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);

		final ExcelAttributeTranslator<ExcelAttribute> translator = mock(ExcelAttributeTranslator.class);
		given(translator.exportData(any(), any())).willReturn(Optional.of("exportedData"));
		given(translator.referenceFormat(any())).willReturn("referenceFormat");

		given(mockedAttributeNameFormatter.format(aContextOfAttribute(excelAttribute, ExcelClassificationAttribute.class)))
				.willReturn("headerValue");
		given(mockedExcelAttributeTranslatorRegistry.findTranslator(excelAttribute)).willReturn(Optional.of(translator));

		final Workbook workbook = mock(Workbook.class);
		final Sheet sheet = mock(Sheet.class);
		final Cell headerCell = mock(Cell.class);
		final Cell valueCell = mock(Cell.class);
		final Row row = mock(Row.class);
		given(row.getSheet()).willReturn(sheet);

		doReturn(headerCell).when(abstractExcelExportWorkbookDecorator).insertHeaderIfNecessary(sheet, "headerValue");
		doReturn(valueCell).when(abstractExcelExportWorkbookDecorator).createCellIfNecessary(row, 0);
		doNothing().when(abstractExcelExportWorkbookDecorator).insertReferenceFormatIfNecessary(valueCell, "referenceFormat");
		doReturn(Optional.of(row)).when(abstractExcelExportWorkbookDecorator).findRow(workbook, item);

		// when
		abstractExcelExportWorkbookDecorator.decorate(workbook, Collections.singletonList(excelAttribute),
				Collections.singletonList(item));

		// then
		then(translator).should().exportData(excelAttribute, item);
		then(mockedExcelCellService).should().insertAttributeValue(valueCell, "exportedData");
	}

	<T extends ExcelAttribute> ExcelAttributeContext<T> aContextOfAttribute(final T firstExcelAttribute, Class<T> type)
	{
		return argThat(new ArgumentMatcher<ExcelAttributeContext<T>>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o instanceof ExcelAttributeContext
						&& ((ExcelAttributeContext<T>) o).getExcelAttribute(type).equals(firstExcelAttribute);
			}
		});
	}

	@Test
	public void shouldFindRow()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final ItemModel itemModel = mock(ItemModel.class);
		final Sheet sheet = mock(Sheet.class);
		final Row firstRow = mock(Row.class);
		final Row secondRow = mock(Row.class);
		final Row expectedFoundRow = mock(Row.class);

		given(workbook.getSheet(UtilitySheet.PK.getSheetName())).willReturn(sheet);
		given(sheet.getRow(0)).willReturn(firstRow);
		given(sheet.getRow(1)).willReturn(secondRow);
		given(sheet.getFirstRowNum()).willReturn(0);
		given(sheet.getLastRowNum()).willReturn(1);
		given(sheet.getRow(2)).willReturn(expectedFoundRow);
		given(workbook.getSheet("foundSheetName")).willReturn(sheet);

		final Cell foundPkCell = mock(Cell.class);
		final Cell notMatchingPkCell = mock(Cell.class);
		given(firstRow.getCell(PkColumns.PK)).willReturn(notMatchingPkCell);
		given(secondRow.getCell(PkColumns.PK)).willReturn(foundPkCell);

		given(itemModel.getPk()).willReturn(PK.fromLong(1337L));
		given(mockedExcelCellService.getCellValue(foundPkCell)).willReturn("1337");
		given(mockedExcelCellService.getCellValue(notMatchingPkCell)).willReturn("wrongPK");

		final Cell sheetNameCell = mock(Cell.class);
		given(secondRow.getCell(PkColumns.SHEET_NAME)).willReturn(sheetNameCell);
		final Cell rowIndexCell = mock(Cell.class);
		given(secondRow.getCell(PkColumns.ROW_INDEX)).willReturn(rowIndexCell);
		given(mockedExcelCellService.getCellValue(sheetNameCell)).willReturn("foundSheetName");
		given(mockedExcelCellService.getCellValue(rowIndexCell)).willReturn("2");

		// when
		final Optional<Row> result = abstractExcelExportWorkbookDecorator.findRow(workbook, itemModel);

		// then
		assertThat(result).isPresent().hasValue(expectedFoundRow);
	}

	@Test
	public void shouldInsertReferenceFormat()
	{
		// given
		final Cell excelCellValue = mock(Cell.class);
		final String referenceFormat = "referenceFormat";

		final Sheet sheet = mock(Sheet.class);
		final Row referencePatternRow = mock(Row.class);
		final Cell referencePatternCell = mock(Cell.class);
		final int columnIndex = 1;

		given(excelCellValue.getSheet()).willReturn(sheet);
		given(excelCellValue.getColumnIndex()).willReturn(columnIndex);
		given(sheet.getRow(REFERENCE_PATTERN_ROW_INDEX)).willReturn(referencePatternRow);
		given(referencePatternRow.createCell(columnIndex)).willReturn(referencePatternCell);

		// when
		abstractExcelExportWorkbookDecorator.insertReferenceFormatIfNecessary(excelCellValue, referenceFormat);

		// then, should create cell as it was not present and insert reference format to it
		then(mockedExcelCellService).should().insertAttributeValue(referencePatternCell, referenceFormat);
	}

	@Test
	public void shouldNotInsertReferenceFormatIfItsBlank()
	{
		// given
		final Cell excelCellValue = mock(Cell.class);
		final String blankReferenceFormat = " ";

		// when
		abstractExcelExportWorkbookDecorator.insertReferenceFormatIfNecessary(excelCellValue, blankReferenceFormat);

		// then
		then(mockedExcelCellService).should(never()).insertAttributeValue(any(), any());
	}

	@Test
	public void shouldInsertHeaderFindingColumnByContent()
	{
		// given
		final Sheet sheet = mock(Sheet.class);
		final String headerValue = "headerValue";

		final Row row = mock(Row.class);
		final int columnIndex = 1;
		final Cell expectedCell = mock(Cell.class);

		given(sheet.getRow(HEADER_ROW_INDEX)).willReturn(row);
		doReturn(columnIndex).when(abstractExcelExportWorkbookDecorator).findColumnIndexByContentOrFirstEmptyCell(row, headerValue);
		doReturn(expectedCell).when(abstractExcelExportWorkbookDecorator).createNewHeaderCell(row, columnIndex, headerValue);

		// when
		final Cell result = abstractExcelExportWorkbookDecorator.insertHeaderIfNecessary(sheet, headerValue);

		// then
		assertThat(result).isEqualTo(expectedCell);
	}

	@Test
	public void shouldInsertHeaderToFirstEmptyCell()
	{
		// given
		final Sheet sheet = mock(Sheet.class);
		final String headerValue = "headerValue";

		final Row row = mock(Row.class);
		final int columnIndex = -1;
		final Cell expectedCell = mock(Cell.class);
		final short lastCellNumber = (short) 12;

		given(row.getLastCellNum()).willReturn(lastCellNumber);
		given(sheet.getRow(HEADER_ROW_INDEX)).willReturn(row);
		doReturn(columnIndex).when(abstractExcelExportWorkbookDecorator).findColumnIndexByContentOrFirstEmptyCell(row, headerValue);
		doReturn(expectedCell).when(abstractExcelExportWorkbookDecorator).createNewHeaderCell(row, lastCellNumber + 1, headerValue);

		// when
		final Cell result = abstractExcelExportWorkbookDecorator.insertHeaderIfNecessary(sheet, headerValue);

		// then
		assertThat(result).isEqualTo(expectedCell);
	}

	@Test
	public void shouldFindColumnIndexByContent()
	{
		// given
		final Row row = mock(Row.class);
		final String content = "secondCellValue";

		final Cell firstCell = mock(Cell.class);
		final Cell secondCell = mock(Cell.class);

		given(row.getFirstCellNum()).willReturn((short) 0);
		given(row.getLastCellNum()).willReturn((short) 1);
		given(row.getCell(0)).willReturn(firstCell);
		given(row.getCell(1)).willReturn(secondCell);

		given(mockedExcelCellService.getCellValue(firstCell)).willReturn("firstCellValue");
		given(mockedExcelCellService.getCellValue(secondCell)).willReturn("secondCellValue");

		// when
		final int result = abstractExcelExportWorkbookDecorator.findColumnIndexByContentOrFirstEmptyCell(row, content);

		// then
		assertThat(result).isEqualTo(1);
	}

	@Test
	public void shouldFindColumnIndexByFirstEmptyCell()
	{
		// given
		final Row row = mock(Row.class);
		final String content = "notExistingContent";

		final Cell nonEmptyCell = mock(Cell.class);
		final Cell firstEmptyCell = mock(Cell.class);

		given(row.getFirstCellNum()).willReturn((short) 0);
		given(row.getLastCellNum()).willReturn((short) 1);
		given(row.getCell(0)).willReturn(nonEmptyCell);
		given(row.getCell(1)).willReturn(firstEmptyCell);

		given(mockedExcelCellService.getCellValue(nonEmptyCell)).willReturn("cellValue");
		given(mockedExcelCellService.getCellValue(firstEmptyCell)).willReturn(" ");

		// when
		final int result = abstractExcelExportWorkbookDecorator.findColumnIndexByContentOrFirstEmptyCell(row, content);

		// then
		assertThat(result).isEqualTo(1);
	}

	@Test
	public void shouldCreateNewHeaderCell()
	{
		// given
		final Row row = mock(Row.class);
		final String headerValue = "headerValue";
		final int columnIndex = 0;

		final Cell expectedCell = mock(Cell.class);
		doReturn(expectedCell).when(abstractExcelExportWorkbookDecorator).createCellIfNecessary(row, columnIndex);

		// when
		final Cell result = abstractExcelExportWorkbookDecorator.createNewHeaderCell(row, columnIndex, headerValue);

		// then
		assertThat(result).isEqualTo(expectedCell);
		then(mockedExcelCellService).should().insertAttributeValue(expectedCell, headerValue);
	}

	@Test
	public void shouldCreateCell()
	{
		// given
		final Row row = mock(Row.class);
		final int columnIndex = 0;

		final Cell expectedCreatedCell = mock(Cell.class);

		given(row.getCell(columnIndex)).willReturn(null);
		given(row.createCell(columnIndex)).willReturn(expectedCreatedCell);

		// when
		final Cell result = abstractExcelExportWorkbookDecorator.createCellIfNecessary(row, columnIndex);

		// then
		then(row).should().createCell(columnIndex);
		assertThat(result).isEqualTo(expectedCreatedCell);
	}

	@Test
	public void shouldNotCreateCellAsItAlreadyExists()
	{
		// given
		final Row row = mock(Row.class);
		final int columnIndex = 0;

		final Cell expectedAlreadyCreatedCell = mock(Cell.class);

		given(row.getCell(columnIndex)).willReturn(expectedAlreadyCreatedCell);

		// when
		final Cell result = abstractExcelExportWorkbookDecorator.createCellIfNecessary(row, columnIndex);

		// then
		then(row).should(never()).createCell(columnIndex);
		assertThat(result).isEqualTo(expectedAlreadyCreatedCell);
	}
}
