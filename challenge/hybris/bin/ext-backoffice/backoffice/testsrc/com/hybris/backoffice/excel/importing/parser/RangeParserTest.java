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

import static com.hybris.backoffice.excel.importing.parser.RangeParserUtils.RANGE_DELIMITER;
import static com.hybris.backoffice.excel.importing.parser.RangeParserUtils.RANGE_FROM_PREFIX;
import static com.hybris.backoffice.excel.importing.parser.RangeParserUtils.RANGE_TO_PREFIX;
import static com.hybris.backoffice.excel.importing.parser.RangeParserUtils.prependFromPrefix;
import static com.hybris.backoffice.excel.importing.parser.RangeParserUtils.prependToPrefix;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.importing.parser.splitter.DefaultExcelParserSplitter;


@RunWith(MockitoJUnitRunner.class)
public class RangeParserTest
{
	@Mock
	private ParserRegistry registry;

	private RangeParser rangeParser = new RangeParser();

	@Before
	public void setUp()
	{
		final DefaultImportParameterParser parameterParser = new DefaultImportParameterParser();
		parameterParser.setSplitter(new DefaultExcelParserSplitter());
		given(registry.getParser(any())).willReturn(parameterParser);
		rangeParser.setParserRegistry(registry);
	}

	private static final String rangeFormat = "RANGE[%s;%s]";


	@Test
	public void shouldRetrieveDefaultValues()
	{
		// given
		final String referenceFormatLeft = "from";
		final String referenceFormatRight = "to";
		final String referenceFormat = String.format(rangeFormat, referenceFormatLeft, referenceFormatRight);
		final String defaultLeftValue = "someValue";
		final String defaultValues = String.format(rangeFormat, defaultLeftValue, StringUtils.EMPTY);

		// when
		final DefaultValues output = rangeParser.parseDefaultValues(referenceFormat, defaultValues);

		// then
		assertThat(output.getDefaultValues()).isEqualTo(defaultLeftValue + RANGE_DELIMITER);
		assertThat(output.getReferenceFormat())
				.isEqualTo(prependFromPrefix(referenceFormatLeft) + RANGE_DELIMITER + prependToPrefix(referenceFormatRight));
		assertThat(output.getKeys()).containsExactly(prependFromPrefix(referenceFormatLeft), prependToPrefix(referenceFormatRight));
		assertThat(output.getValues()).containsExactly(defaultLeftValue, null);
	}

	@Test
	public void shouldParseValue()
	{
		// given
		final String referenceFormatLeft = "someValue1";
		final String referenceFormatRight = "someValue2";
		final String prependedReferenceFormatLeft = prependFromPrefix(referenceFormatLeft);
		final String prependedReferenceFormatRight = prependToPrefix(referenceFormatRight);
		final String referenceFormat = prependedReferenceFormatLeft + RANGE_DELIMITER + prependedReferenceFormatRight;
		final String defaultValue = "defaultValue";
		final String defaultValues = defaultValue + RANGE_DELIMITER;
		final Map<String, String> params = new LinkedHashMap<>();
		params.put(prependedReferenceFormatLeft, defaultValue);
		params.put(prependedReferenceFormatRight, StringUtils.EMPTY);

		final DefaultValues defValues = new DefaultValues(defaultValues, referenceFormat, params);
		final String cellLeft = StringUtils.EMPTY;
		final String cellRight = "value";
		final String cellValue = String.format(rangeFormat, cellLeft, cellRight);

		// when
		final ParsedValues parsedValues = rangeParser.parseValue(cellValue, defValues);

		// then
		assertThat(parsedValues.getParameters().size()).isEqualTo(2);
		assertThat(parsedValues.getParameters().get(0).values()).containsExactly(defaultValue, defaultValue);
		assertThat(parsedValues.getParameters().get(1).values()).containsExactly(cellRight, cellRight);
		assertThat(parsedValues.getParameters().get(0).keySet()).filteredOn(key -> key.contains(RANGE_FROM_PREFIX)).hasSize(2);
		assertThat(parsedValues.getParameters().get(1).keySet()).filteredOn(key -> key.contains(RANGE_TO_PREFIX)).hasSize(2);
	}

	@Test
	public void shouldDefaultValueBeUsedInCaseOfEmptyValueCell()
	{
		// given
		final String defaultValue = "RANGE[5:kg;6:kg]";
		final String cellValue = StringUtils.EMPTY;
		final String referenceFormat = "RANGE[value:unit[kg];value:unit[kg]]";

		// when
		final ParsedValues parsedValues = rangeParser.parseValue(referenceFormat, defaultValue, cellValue);

		// then
		assertThat(parsedValues.getCellValue()).isEqualTo(defaultValue);
	}

}
