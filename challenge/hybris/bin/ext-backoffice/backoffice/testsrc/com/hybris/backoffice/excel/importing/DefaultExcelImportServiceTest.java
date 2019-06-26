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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.data.SelectedAttribute;
import com.hybris.backoffice.excel.importing.parser.DefaultImportParameterParser;
import com.hybris.backoffice.excel.importing.parser.ParserRegistry;
import com.hybris.backoffice.excel.importing.parser.matcher.DefaultExcelParserMatcher;
import com.hybris.backoffice.excel.importing.parser.splitter.DefaultExcelParserSplitter;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;
import com.hybris.backoffice.excel.template.header.ExcelHeaderService;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;
import com.hybris.backoffice.excel.translators.ExcelTranslatorRegistry;
import com.hybris.backoffice.excel.translators.ExcelValueTranslator;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelImportServiceTest
{
	private static final String PRODUCT_TYPE_CODE = ProductModel._TYPECODE;

	private static final String CATALOG_KEY = "catalog";
	private static final String VERSION_KEY = "version";
	private static final String CATALOG_CLOTHING_VALUE = "Clothing";
	private static final String CATALOG_DEFAULT_VALUE = "Default";
	private static final String VERSION_ONLINE_VALUE = "Online";
	private static final String VERSION_STAGED_VALUE = "Staged";
	private static final String APPROVAL_STATUS_APPROVED = "Approved";

	private static final String REFERENCE_VALUE = "productRef";
	private static final String CATALOG_VERSION_PATTERN = "%s:%s";
	private static final String CATALOG_VERSION_FORMAT = String.format(CATALOG_VERSION_PATTERN, CATALOG_KEY, VERSION_KEY);

	private static final String EMPTY_CELL_VALUE = "";
	private static final String CATEGORY_KEY = "category";

	@Mock
	private ParserRegistry parserRegistry;

	@Mock
	private ExcelSheetService excelSheetService;

	@Mock
	private ExcelCellService excelCellService;

	@Mock
	private ExcelHeaderService excelHeaderService;

	@Mock
	private ExcelTranslatorRegistry excelTranslatorRegistry;

	@Spy
	@InjectMocks
	private DefaultExcelImportService excelImportService;

	@Before
	public void setUp()
	{
		final DefaultImportParameterParser parameterParser = new DefaultImportParameterParser();
		parameterParser.setMatcher(new DefaultExcelParserMatcher());
		parameterParser.setSplitter(new DefaultExcelParserSplitter());
		given(parserRegistry.getParser(any())).willReturn(parameterParser);
	}

	@Test
	public void shouldFindDefaultValuesWhenCellIsNull()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();

		// when
		final Map<String, String> defaultValues = selectedAttribute.findDefaultValues();

		// then
		assertThat(defaultValues).isNotNull();
		assertThat(defaultValues.keySet()).isEmpty();
	}

	@Test
	public void shouldFindDefaultValuesWhenCellValueIsEmpty()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setReferenceFormat("");

		// when
		final Map<String, String> defaultValues = selectedAttribute.findDefaultValues();

		// then
		assertThat(defaultValues).isNotNull();
		assertThat(defaultValues.keySet()).isEmpty();
	}

	@Test
	public void shouldFindDefaultValuesWhenOnlyPatternIsProvided()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);

		// when
		final Map<String, String> defaultValues = selectedAttribute.findDefaultValues();

		// then
		assertThat(defaultValues).isNotNull();
		assertThat(defaultValues.keySet()).contains(CATALOG_KEY, VERSION_KEY);
	}

	@Test
	public void shouldFindDefaultValuesWhenFirstPatternHasDefault()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setReferenceFormat(String.format(CATALOG_VERSION_PATTERN, CATALOG_KEY, VERSION_KEY));
		selectedAttribute.setDefaultValues(CATALOG_CLOTHING_VALUE);

		// when
		final Map<String, String> defaultValues = selectedAttribute.findDefaultValues();

		// then
		assertThat(defaultValues).isNotNull();
		assertThat(defaultValues.keySet()).contains(CATALOG_KEY, VERSION_KEY);
		assertThat(defaultValues.values()).contains(CATALOG_CLOTHING_VALUE, null);
	}

	@Test
	public void shouldFindDefaultValuesWhenSecondPatternHasDefault()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setReferenceFormat(String.format(CATALOG_VERSION_PATTERN, CATALOG_KEY, VERSION_KEY));
		selectedAttribute.setDefaultValues(String.format(":%s", VERSION_ONLINE_VALUE));

		// when
		final Map<String, String> defaultValues = selectedAttribute.findDefaultValues();

		// then
		assertThat(defaultValues).isNotNull();
		assertThat(defaultValues.keySet()).contains(CATALOG_KEY, VERSION_KEY);
		assertThat(defaultValues.values()).contains(null, VERSION_ONLINE_VALUE);
	}


	@Test
	public void shouldFindDefaultValuesWhenBothPatternsHaveDefaults()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);
		selectedAttribute.setDefaultValues(String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE));

		// when
		final Map<String, String> defaultValues = selectedAttribute.findDefaultValues();

		// then
		assertThat(defaultValues).isNotNull();
		assertThat(defaultValues.keySet()).contains(CATALOG_KEY, VERSION_KEY);
		assertThat(defaultValues.values()).contains(CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE);
	}


	@Test
	public void shouldPrepareImportParametersForEmptyCellWithoutDefaults()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, EMPTY_CELL_VALUE,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo(EMPTY_CELL_VALUE);
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getMultiValueParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo(EMPTY_CELL_VALUE);
	}

	@Test
	public void shouldPrepareImportParametersForEmptyCellWithDefaults()
	{
		// given
		final String defaultValue = String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE);

		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);
		selectedAttribute.setDefaultValues(defaultValue);

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, EMPTY_CELL_VALUE,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo(selectedAttribute.getDefaultValues());
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getSingleValueParameters().keySet()).containsSequence(CATALOG_KEY, VERSION_KEY);
		assertThat(importParameters.getSingleValueParameters().values()).containsSequence(CATALOG_CLOTHING_VALUE,
				VERSION_ONLINE_VALUE);
	}

	@Test
	public void shouldPrepareImportParametersForNotReferenceCellWithoutDefaults()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute,
				APPROVAL_STATUS_APPROVED, PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo(APPROVAL_STATUS_APPROVED);
		assertThat(importParameters.getMultiValueParameters()).isNotNull();
		assertThat(importParameters.getMultiValueParameters().get(0).get(ImportParameters.RAW_VALUE))
				.isEqualTo(APPROVAL_STATUS_APPROVED);
	}

	@Test
	public void shouldPrepareImportParametersForReferenceCellWithoutDefaults()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		selectedAttribute.setDefaultValues(StringUtils.EMPTY);
		final String cellValue = String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE);
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, cellValue,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo(cellValue);
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getSingleValueParameters().keySet()).containsSequence(CATALOG_KEY, VERSION_KEY);
		assertThat(importParameters.getSingleValueParameters().values()).containsSequence(CATALOG_CLOTHING_VALUE,
				VERSION_ONLINE_VALUE);
	}

	@Test
	public void shouldPrepareImportParametersForReferenceCellWithDefaultFirstValue()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		final String cellValue = String.format(":%s", VERSION_ONLINE_VALUE);
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);
		selectedAttribute.setDefaultValues(String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_STAGED_VALUE));

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, cellValue,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue())
				.isEqualTo(String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE));
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getSingleValueParameters().keySet()).containsSequence(CATALOG_KEY, VERSION_KEY);
		assertThat(importParameters.getSingleValueParameters().values()).containsSequence(CATALOG_CLOTHING_VALUE,
				VERSION_ONLINE_VALUE);
	}

	@Test
	public void shouldPrepareImportParametersForReferenceCellWithDefaultSecondValue()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		final String cellValue = String.format("%s", CATALOG_CLOTHING_VALUE);
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);
		selectedAttribute.setDefaultValues(String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE));

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, cellValue,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo("Clothing:Online");
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getSingleValueParameters().keySet()).containsSequence(CATALOG_KEY, VERSION_KEY);
		assertThat(importParameters.getSingleValueParameters().values()).containsSequence(CATALOG_CLOTHING_VALUE,
				VERSION_ONLINE_VALUE);
	}

	@Test
	public void shouldPrepareImportParametersForReferenceCellWithDefaultBothValues()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		final String cellValue = ":";
		selectedAttribute.setReferenceFormat(CATALOG_VERSION_FORMAT);
		final String defaultValues = String.format(CATALOG_VERSION_PATTERN, CATALOG_CLOTHING_VALUE, VERSION_ONLINE_VALUE);
		selectedAttribute.setDefaultValues(defaultValues);

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, cellValue,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo(defaultValues);
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getSingleValueParameters().keySet()).containsSequence(CATALOG_KEY, VERSION_KEY);
		assertThat(importParameters.getSingleValueParameters().values()).containsSequence(CATALOG_CLOTHING_VALUE,
				VERSION_ONLINE_VALUE);
	}

	@Test
	public void shouldPrepareImportParametersForReferenceCellWith()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		final String cellValue = "15:EUR";
		selectedAttribute.setReferenceFormat("price:currency:scale:unit:unitFactor:pricing");
		selectedAttribute.setDefaultValues("15:EUR:1:piece:3:Gross");

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, cellValue,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue()).isEqualTo("15:EUR:1:piece:3:Gross");
		assertThat(importParameters.getMultiValueParameters()).hasSize(1);
		assertThat(importParameters.getSingleValueParameters().keySet()).containsSequence("price", "currency", "scale", "unit",
				"unitFactor", "pricing");
		assertThat(importParameters.getSingleValueParameters().values()).containsSequence("15", "EUR", "1", "piece", "3", "Gross");
	}

	@Test
	public void shouldPrepareImportParametersForMultiValue()
	{
		// given
		final SelectedAttribute selectedAttribute = new SelectedAttribute();
		final String cellValue = "Shoes:Online:Clothing,Hats,Jeans::Default,Shirts:Online:Default";
		selectedAttribute.setReferenceFormat("category:version:catalog");
		selectedAttribute.setDefaultValues(":Staged:Clothing");

		// when
		final ImportParameters importParameters = excelImportService.findImportParameters(selectedAttribute, cellValue,
				PRODUCT_TYPE_CODE, REFERENCE_VALUE);

		// then
		assertThat(importParameters).isNotNull();
		assertThat(importParameters.getCellValue())
				.isEqualTo("Shoes:Online:Clothing,Hats:Staged:Clothing,Jeans:Staged:Default,Shirts:Online:Default");
		assertThat(importParameters.getMultiValueParameters()).hasSize(4);
		assertThat(importParameters.getMultiValueParameters().get(0).keySet()).containsSequence(CATEGORY_KEY, VERSION_KEY,
				CATALOG_KEY);
		assertThat(importParameters.getMultiValueParameters().get(0).values()).containsSequence("Shoes", VERSION_ONLINE_VALUE,
				CATALOG_CLOTHING_VALUE);

		assertThat(importParameters.getMultiValueParameters().get(1).keySet()).containsSequence(CATEGORY_KEY, VERSION_KEY,
				CATALOG_KEY);
		assertThat(importParameters.getMultiValueParameters().get(1).values()).containsSequence("Hats", VERSION_STAGED_VALUE,
				CATALOG_CLOTHING_VALUE);

		assertThat(importParameters.getMultiValueParameters().get(2).keySet()).containsSequence(CATEGORY_KEY, VERSION_KEY,
				CATALOG_KEY);
		assertThat(importParameters.getMultiValueParameters().get(2).values()).containsSequence("Jeans", VERSION_STAGED_VALUE,
				CATALOG_DEFAULT_VALUE);

		assertThat(importParameters.getMultiValueParameters().get(3).keySet()).containsSequence(CATEGORY_KEY, VERSION_KEY,
				CATALOG_KEY);
		assertThat(importParameters.getMultiValueParameters().get(3).values()).containsSequence("Shirts", VERSION_ONLINE_VALUE,
				CATALOG_DEFAULT_VALUE);
	}

	@Test
	public void shouldInvokeTranslatorTwiceForTwoSelectedAttributes()
	{
		// given
		final int lastRowNumber = 3;
		final Sheet typeSystemSheet = mock(Sheet.class);
		final Sheet productSheet = mock(Sheet.class);
		final ExcelValueTranslator<Object> translator = mock(ExcelValueTranslator.class);
		final Optional<ExcelValueTranslator<Object>> translatorOpt = Optional.of(translator);
		final Row firstRow = mock(Row.class);
		final Cell codeFirstCell = mock(Cell.class);
		final Cell catalogVersionFirstCell = mock(Cell.class);
		final List<SelectedAttribute> selectedAttributes = new ArrayList<>();
		final List<String> documentRefs = Arrays.asList("documentRef1");

		final ImportParameters codeImportParameters = new ImportParameters(ProductModel._TYPECODE, "", "Abc",
				documentRefs.get(lastRowNumber - DefaultExcelImportService.FIRST_DATA_ROW_INDEX), new ArrayList<>());
		final ImportParameters catalogVersionImportParameters = new ImportParameters(ProductModel._TYPECODE, "", ":Online",
				documentRefs.get(lastRowNumber - DefaultExcelImportService.FIRST_DATA_ROW_INDEX), new ArrayList<>());
		selectedAttributes.add(prepareSelectedAttribute("code", "", ""));
		selectedAttributes.add(prepareSelectedAttribute("catalogVersion", "catalog:version", "Clothing:"));

		given(productSheet.getSheetName()).willReturn(ProductModel._TYPECODE);
		given(excelSheetService.findTypeCodeForSheetName(any(), eq(ProductModel._TYPECODE))).willReturn(ProductModel._TYPECODE);
		given(productSheet.getLastRowNum()).willReturn(lastRowNumber);
		given(excelTranslatorRegistry.getTranslator(any())).willReturn(translatorOpt);
		given(productSheet.getRow(lastRowNumber)).willReturn(firstRow);
		given(firstRow.getCell(0)).willReturn(codeFirstCell);
		given(firstRow.getCell(1)).willReturn(catalogVersionFirstCell);
		final short num = 1;
		given(firstRow.getLastCellNum()).willReturn(num);
		given(excelCellService.getCellValue(codeFirstCell)).willReturn(ProductModel.CODE);
		given(excelCellService.getCellValue(catalogVersionFirstCell)).willReturn(ProductModel.CATALOGVERSION);
		given(excelHeaderService.getHeaders(typeSystemSheet, productSheet)).willReturn(selectedAttributes);

		doReturn(documentRefs).when(excelImportService)
				.generateDocumentRefs(lastRowNumber - DefaultExcelImportService.FIRST_DATA_ROW_INDEX);
		doReturn(codeImportParameters).when(excelImportService).findImportParameters(selectedAttributes.get(0), "Abc",
				ProductModel._TYPECODE, documentRefs.get(lastRowNumber - DefaultExcelImportService.FIRST_DATA_ROW_INDEX));
		doReturn(catalogVersionImportParameters).when(excelImportService).findImportParameters(selectedAttributes.get(1), ":Online",
				ProductModel._TYPECODE, documentRefs.get(lastRowNumber - DefaultExcelImportService.FIRST_DATA_ROW_INDEX));

		// when
		final Impex impex = excelImportService.generateImpexForSheet(typeSystemSheet, productSheet);

		// then
		assertThat(impex).isNotNull();
		verify(translator, times(2)).importData(any(), any());
	}

	private SelectedAttribute prepareSelectedAttribute(final String qualifier, final String referenceFormat,
			final String defaultValue)
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getQualifier()).willReturn(qualifier);
		return new SelectedAttribute(null, referenceFormat, defaultValue, attributeDescriptor);
	}

}
