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
package com.hybris.backoffice.excel.validators.engine.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.parser.DefaultImportParameterParser;
import com.hybris.backoffice.excel.importing.parser.ParsedValues;
import com.hybris.backoffice.excel.importing.parser.matcher.DefaultExcelParserMatcher;
import com.hybris.backoffice.excel.importing.parser.splitter.DefaultExcelParserSplitter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMultiValueConverterTest
{

	private final ExcelValueConverter converter = new ExcelMultiValueConverter();
	final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsMultiValue()
	{
		// given
		given(excelAttribute.isMultiValue()).willReturn(true);

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldNotBeEligibleToConvertingWhenTypeIsSingleValue()
	{
		// given
		given(excelAttribute.isMultiValue()).willReturn(false);

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isFalse();
	}

	@Test
	public void shouldConvertCorrectMultiValue()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final ImportParameters importParameters = prepareImportParameters("firstValue,secondValue,thirdValue");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Collection.class);
		assertThat((Collection) convertedValue).hasSize(3);
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		final DefaultImportParameterParser parameterParser = new DefaultImportParameterParser();
		parameterParser.setSplitter(new DefaultExcelParserSplitter());
		parameterParser.setMatcher(new DefaultExcelParserMatcher());
		final ParsedValues parsedValues = parameterParser.parseValue(StringUtils.EMPTY, StringUtils.EMPTY,
				cellValue);
		return new ImportParameters(StringUtils.EMPTY, StringUtils.EMPTY, parsedValues.getCellValue(), null,
				parsedValues.getParameters());
	}
}
