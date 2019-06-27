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
package com.hybris.backoffice.excel.importing;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.data.ImpexForType;
import com.hybris.backoffice.excel.importing.data.ExcelImportResult;
import com.hybris.backoffice.excel.importing.parser.DefaultImportParameterParser;
import com.hybris.backoffice.excel.importing.parser.ParserRegistry;
import com.hybris.backoffice.excel.importing.parser.matcher.DefaultExcelParserMatcher;
import com.hybris.backoffice.excel.importing.parser.splitter.DefaultExcelParserSplitter;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;
import com.hybris.backoffice.excel.template.header.ExcelHeaderService;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;
import com.hybris.backoffice.excel.translators.ExcelAttributeTranslator;
import com.hybris.backoffice.excel.translators.ExcelAttributeTranslatorRegistry;
import com.hybris.backoffice.excel.validators.ExcelAttributeValidator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


@RunWith(MockitoJUnitRunner.class)
public class AbstractExcelImportWorkbookDecoratorTest
{

	@Mock
	private ParserRegistry parserRegistry;

	@Mock
	private Workbook workbook;

	@Mock
	private Sheet productSheet;

	@Mock
	private ExcelSheetService excelSheetService;

	@Mock
	private ExcelHeaderService excelHeaderService;

	@Mock
	private ExcelAttributeTranslatorRegistry excelAttributeTranslatorRegistry;

	@Mock
	private ExcelAttributeTranslator excelAttributeTranslator;

	@Mock
	private ExcelCellService excelCellService;

	@Spy
	@InjectMocks
	private DefaultExcelImportClassificationWorkbookDecorator defaultExcelImportClassificationWorkbookDecorator;

	@Before
	public void setup()
	{
		given(productSheet.getWorkbook()).willReturn(workbook);
		given(excelSheetService.getSheets(workbook)).willReturn(Arrays.asList(productSheet));
		given(productSheet.getSheetName()).willReturn(ProductModel._TYPECODE);
		given(excelSheetService.findTypeCodeForSheetName(workbook, ProductModel._TYPECODE)).willReturn(ProductModel._TYPECODE);
		given(excelAttributeTranslatorRegistry.findTranslator(any())).willReturn(Optional.of(excelAttributeTranslator));
		final DefaultImportParameterParser defaultImportParameterParser = new DefaultImportParameterParser();
		defaultImportParameterParser.setSplitter(new DefaultExcelParserSplitter());
		defaultImportParameterParser.setMatcher(new DefaultExcelParserMatcher());
		given(parserRegistry.getParser(any())).willReturn(defaultImportParameterParser);
		given(excelHeaderService.getHeaderValueWithoutSpecialMarks(any())).will(inv -> inv.getArguments()[0]);
	}

	@Test
	public void shouldValidateClassificationAttributes()
	{
		// given
		final ExcelAttribute dimensionsAttribute = prepareExcelAttribute("dimensions");
		final ExcelAttribute weightAttribute = prepareExcelAttribute("weight");
		final Collection<ExcelAttribute> attributes = Arrays.asList(dimensionsAttribute, weightAttribute);
		final Row headerRow = prepareRow(productSheet, ExcelTemplateConstants.HEADER_ROW_INDEX, "dimensions", "weight");
		doReturn(attributes).when(defaultExcelImportClassificationWorkbookDecorator).getExcelAttributes(productSheet);
		given(productSheet.getRow(ExcelTemplateConstants.HEADER_ROW_INDEX)).willReturn(headerRow);
		given(productSheet.getLastRowNum()).willReturn(ExcelTemplateConstants.FIRST_DATA_ROW);
		prepareRow(productSheet, ExcelTemplateConstants.REFERENCE_PATTERN_ROW_INDEX, "", "");
		prepareRow(productSheet, ExcelTemplateConstants.DEFAULT_VALUES_ROW_INDEX, "", "");
		prepareRow(productSheet, ExcelTemplateConstants.FIRST_DATA_ROW, "170 x 75 mm", "230.0");
		final ExcelAttributeValidator mockedValidator = mock(ExcelAttributeValidator.class);
		defaultExcelImportClassificationWorkbookDecorator.setValidators(Collections.singletonList(mockedValidator));
		final ExcelValidationResult expectedValidationResult = new ExcelValidationResult(
				new ValidationMessage("Incorrect format of dimensions attribute"));
		given(mockedValidator.canHandle(eq(dimensionsAttribute), any())).willReturn(true);
		given(mockedValidator.validate(eq(dimensionsAttribute), any(), any())).willReturn(expectedValidationResult);

		// when
		final List<ExcelValidationResult> validationResults = defaultExcelImportClassificationWorkbookDecorator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults).contains(expectedValidationResult);
	}

	@Test
	public void shouldDecorateImpexByClassificationAttributes()
	{
		// given
		final ExcelAttribute dimensionsAttribute = prepareExcelAttribute("dimensions");
		final ExcelAttribute weightAttribute = prepareExcelAttribute("weight");
		final Collection<ExcelAttribute> attributes = Arrays.asList(dimensionsAttribute, weightAttribute);
		final Row headerRow = prepareRow(productSheet, ExcelTemplateConstants.HEADER_ROW_INDEX, "dimensions", "weight");
		doReturn(attributes).when(defaultExcelImportClassificationWorkbookDecorator).getExcelAttributes(productSheet);
		given(productSheet.getRow(ExcelTemplateConstants.HEADER_ROW_INDEX)).willReturn(headerRow);
		given(productSheet.getLastRowNum()).willReturn(ExcelTemplateConstants.FIRST_DATA_ROW);
		prepareRow(productSheet, ExcelTemplateConstants.REFERENCE_PATTERN_ROW_INDEX, "", "");
		prepareRow(productSheet, ExcelTemplateConstants.DEFAULT_VALUES_ROW_INDEX, "", "");
		prepareRow(productSheet, ExcelTemplateConstants.FIRST_DATA_ROW, "170 x 75 mm", "230.0");

		final Impex dimensionsImpex = new Impex();
		final Impex weightImpex = new Impex();
		given(excelAttributeTranslator.importData(eq(dimensionsAttribute), any(), any())).willReturn(dimensionsImpex);
		given(excelAttributeTranslator.importData(eq(weightAttribute), any(), any())).willReturn(weightImpex);
		final Impex mainImpex = mock(Impex.class);
		final ImpexForType impexForType = mock(ImpexForType.class);
		given(impexForType.getRow(any())).willReturn(null);
		given(mainImpex.findUpdates(ProductModel._TYPECODE)).willReturn(impexForType);

		// when
		defaultExcelImportClassificationWorkbookDecorator.decorate(new ExcelImportResult(workbook, mainImpex));

		// then
		final ArgumentCaptor<Impex> impexArgumentCaptor = ArgumentCaptor.forClass(Impex.class);
		verify(mainImpex, times(2)).mergeImpex(impexArgumentCaptor.capture(), eq(ProductModel._TYPECODE), eq(3));
		assertThat(impexArgumentCaptor.getAllValues()).contains(dimensionsImpex, weightImpex);
	}

	private ExcelAttribute prepareExcelAttribute(final String code)
	{
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeModel attribute = mock(ClassificationAttributeModel.class);
		given(assignment.getClassificationAttribute()).willReturn(attribute);
		given(attribute.getCode()).willReturn(code);
		final ExcelClassificationAttribute classificationAttribute = mock(ExcelClassificationAttribute.class);
		given(classificationAttribute.getAttributeAssignment()).willReturn(assignment);
		given(classificationAttribute.getName()).willReturn(code);
		return classificationAttribute;
	}

	private Row prepareRow(final Sheet sheet, final int rowIndex, final String... values)
	{
		final Row row = mock(Row.class);
		given(sheet.getRow(rowIndex)).willReturn(row);
		given(row.getRowNum()).willReturn(rowIndex);
		given(row.getFirstCellNum()).willReturn((short) 0);
		given(row.getLastCellNum()).willReturn((short) (values.length - 1));

		for (int i = 0; i < values.length; i++)
		{
			final Cell cell = mock(Cell.class);
			given(cell.getColumnIndex()).willReturn(i);
			given(row.getCell(i)).willReturn(cell);
			given(cell.getSheet()).willReturn(sheet);
			given(excelCellService.getCellValue(cell)).willReturn(values[i]);
		}
		return row;
	}

}
