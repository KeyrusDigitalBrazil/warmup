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
package com.hybris.backoffice.excel.importing.parser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.parser.matcher.DefaultExcelParserMatcher;
import com.hybris.backoffice.excel.importing.parser.splitter.DefaultExcelParserSplitter;


@RunWith(MockitoJUnitRunner.class)
public class DefaultImportParameterParserTest
{

	private DefaultImportParameterParser parser = new DefaultImportParameterParser();

	@Before
	public void setUp()
	{
		parser.setMatcher(new DefaultExcelParserMatcher());
		parser.setSplitter(new DefaultExcelParserSplitter());
	}

	@Test
	public void shouldParseDefaultValuesWhenAllParametersAreProvided()
	{
		// given
		final String referenceFormat = "catalog:version";
		final String defaultValues = "Clothing:Online";

		// when
		final DefaultValues parsedDefaultValues = parser.parseDefaultValues(referenceFormat, defaultValues);

		// then
		assertThat(parsedDefaultValues.getKeys()).contains("catalog", "version");
		assertThat(parsedDefaultValues.getDefaultValue("catalog")).isEqualTo("Clothing");
		assertThat(parsedDefaultValues.getDefaultValue("version")).isEqualTo("Online");
	}

	@Test
	public void shouldParseDefaultValuesWhenOnlyFirstParameterIsProvided()
	{
		// given
		final String referenceFormat = "category:catalog:version";
		final String defaultValues = "Yellow::";

		// when
		final DefaultValues parsedDefaultValues = parser.parseDefaultValues(referenceFormat, defaultValues);

		// then
		assertThat(parsedDefaultValues.getKeys()).contains("category", "catalog", "version");
		assertThat(parsedDefaultValues.getDefaultValue("category")).isEqualTo("Yellow");
		assertThat(parsedDefaultValues.getDefaultValue("catalog")).isNull();
		assertThat(parsedDefaultValues.getDefaultValue("version")).isNull();
	}

	@Test
	public void shouldParseDefaultValuesWhenNoValueIsProvided()
	{
		// given
		final String referenceFormat = "category:catalog:version";
		final String defaultValues = "";

		// when
		final DefaultValues parsedDefaultValues = parser.parseDefaultValues(referenceFormat, defaultValues);

		// then
		assertThat(parsedDefaultValues.getKeys()).contains("category", "catalog", "version");
		assertThat(parsedDefaultValues.getDefaultValue("category")).isNull();
		assertThat(parsedDefaultValues.getDefaultValue("catalog")).isNull();
		assertThat(parsedDefaultValues.getDefaultValue("version")).isNull();
	}

	@Test
	public void shouldReturnEmptyMapWhenReferenceFormatIsEmpty()
	{
		// given
		final String referenceFormat = "";
		final String defaultValues = "Yellow:Clothing:Online";

		// when
		final DefaultValues parsedDefaultValues = parser.parseDefaultValues(referenceFormat, defaultValues);

		// then
		assertThat(parsedDefaultValues.getKeys()).isEmpty();
	}

	@Test
	public void shouldParseCellValueWhenAllParametersAreProvided()
	{
		// given
		final String referenceFormat = "category:catalog:version";
		final String defaultValues = "Yellow:Clothing:Online";
		final String cellValue = "Yellow:Clothing:Online";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo(cellValue);
		assertThat(parsedValues.getParameters()).hasSize(1);
		assertThat(parsedValues.getParameters().get(0).keySet()).contains("category", "catalog", "version");
		assertThat(parsedValues.getParameters().get(0).get("category")).isEqualTo("Yellow");
		assertThat(parsedValues.getParameters().get(0).get("catalog")).isEqualTo("Clothing");
		assertThat(parsedValues.getParameters().get(0).get("version")).isEqualTo("Online");
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo(cellValue);
	}

	@Test
	public void shouldParseCellValueWhenOneParameterIsNotProvided()
	{
		// given
		final String referenceFormat = "category:catalog:version";
		final String defaultValues = "Yellow:Clothing:Online";
		final String cellValue = "Yellow::Online";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo("Yellow:Clothing:Online");
		assertThat(parsedValues.getParameters()).hasSize(1);
		assertThat(parsedValues.getParameters().get(0).keySet()).contains("category", "catalog", "version");
		assertThat(parsedValues.getParameters().get(0).get("category")).isEqualTo("Yellow");
		assertThat(parsedValues.getParameters().get(0).get("catalog")).isEqualTo("Clothing");
		assertThat(parsedValues.getParameters().get(0).get("version")).isEqualTo("Online");
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo("Yellow:Clothing:Online");
	}

	@Test
	public void shouldParseCellValueWhenNoParametersAreProvided()
	{
		// given
		final String referenceFormat = "category:catalog:version";
		final String defaultValues = "Yellow:Clothing:Online";
		final String cellValue = "";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo(defaultValues);
		assertThat(parsedValues.getParameters()).hasSize(1);
		assertThat(parsedValues.getParameters().get(0).keySet()).contains("category", "catalog", "version");
		assertThat(parsedValues.getParameters().get(0).get("category")).isEqualTo("Yellow");
		assertThat(parsedValues.getParameters().get(0).get("catalog")).isEqualTo("Clothing");
		assertThat(parsedValues.getParameters().get(0).get("version")).isEqualTo("Online");
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo("Yellow:Clothing:Online");
	}

	@Test
	public void shouldTakeDefaultValueWhenReferenceFormatIsNotProvided()
	{
		// given
		final String referenceFormat = "";
		final String defaultValues = "Yellow:Clothing:Online";
		final String cellValue = "";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo(defaultValues);
		assertThat(parsedValues.getParameters()).hasSize(1);
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo("Yellow:Clothing:Online");
	}

	@Test
	public void shouldParseCellMultiValueWhenNoAllParametersAreProvided()
	{
		// given
		final String referenceFormat = "category:catalog:version";
		final String defaultValues = "Yellow:Clothing:Online";
		final String cellValue = "Black:Clothing2:,Green::Staged,::";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo("Black:Clothing2:Online,Green:Clothing:Staged,Yellow:Clothing:Online");
		assertThat(parsedValues.getParameters()).hasSize(3);
		assertThat(parsedValues.getParameters().get(0).keySet()).contains("category", "catalog", "version");
		assertThat(parsedValues.getParameters().get(0).get("category")).isEqualTo("Black");
		assertThat(parsedValues.getParameters().get(0).get("catalog")).isEqualTo("Clothing2");
		assertThat(parsedValues.getParameters().get(0).get("version")).isEqualTo("Online");
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo("Black:Clothing2:Online");
		assertThat(parsedValues.getParameters().get(1).get("category")).isEqualTo("Green");
		assertThat(parsedValues.getParameters().get(1).get("catalog")).isEqualTo("Clothing");
		assertThat(parsedValues.getParameters().get(1).get("version")).isEqualTo("Staged");
		assertThat(parsedValues.getParameters().get(1).get(ImportParameters.RAW_VALUE)).isEqualTo("Green:Clothing:Staged");
		assertThat(parsedValues.getParameters().get(2).get("category")).isEqualTo("Yellow");
		assertThat(parsedValues.getParameters().get(2).get("catalog")).isEqualTo("Clothing");
		assertThat(parsedValues.getParameters().get(2).get("version")).isEqualTo("Online");
		assertThat(parsedValues.getParameters().get(2).get(ImportParameters.RAW_VALUE)).isEqualTo("Yellow:Clothing:Online");
	}

	@Test
	public void shouldParseMultivalueCellWithoutReferenceFormat()
	{
		// given
		final String referenceFormat = "";
		final String defaultValues = "";
		final String cellValue = "Black,Green,Yellow";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo(cellValue);
		assertThat(parsedValues.getParameters()).hasSize(3);
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo("Black");
		assertThat(parsedValues.getParameters().get(1).get(ImportParameters.RAW_VALUE)).isEqualTo("Green");
		assertThat(parsedValues.getParameters().get(2).get(ImportParameters.RAW_VALUE)).isEqualTo("Yellow");
	}

	@Test
	public void shouldUseDefaultValueForDateType()
	{
		// given
		final String referenceFormat = "";
		final String defaultValues = "17.04.2018 07:23:12";
		final String cellValue = "    ";

		// when
		final ParsedValues parsedValues = parser.parseValue(referenceFormat, defaultValues, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo(defaultValues);
		assertThat(parsedValues.getParameters()).hasSize(1);
		assertThat(parsedValues.getParameters().get(0).get(ImportParameters.RAW_VALUE)).isEqualTo(defaultValues);
	}

}
