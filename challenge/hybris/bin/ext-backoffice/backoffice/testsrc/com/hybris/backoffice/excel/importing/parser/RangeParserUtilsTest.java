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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.hybris.backoffice.excel.data.ImportParameters;


@RunWith(JUnit4.class)
public class RangeParserUtilsTest
{

	@Test
	public void shouldPrependFromPrefix()
	{
		// given
		final String input = "input";

		// when
		final String output = RangeParserUtils.prependFromPrefix(input);

		// then
		assertThat(output).isEqualTo(RangeParserUtils.RANGE_FROM_PREFIX + input);
	}

	@Test
	public void shouldPrependToPrefix()
	{
		// given
		final String input = "input";

		// when
		final String output = RangeParserUtils.prependToPrefix(input);

		// then
		assertThat(output).isEqualTo(RangeParserUtils.RANGE_TO_PREFIX + input);
	}

	@Test
	public void shouldExceptionBeThrownWhenInputDoesntMatchToRangePattern()
	{
		// given

		/*
		 * should be "RANGE" instead of "RAGNE"
		 */
		final String input = "RAGNE[from;to]";

		// except
		assertThatThrownBy(() -> RangeParserUtils.parseRangePattern(input)).isInstanceOf(ExcelParserException.class);
	}

	@Test
	public void shouldRangePatternBeSplitCorrectly()
	{
		// given
		final String left = "left";
		final String right = "right";
		final String input = RangeParserUtils.RANGE_PREFIX + left + RangeParserUtils.RANGE_DELIMITER + right
				+ RangeParserUtils.RANGE_SUFFIX;

		// when
		final Pair<String, String> output = RangeParserUtils.parseRangePattern(input);

		// then
		assertThat(output.getLeft()).isEqualTo(left);
		assertThat(output.getRight()).isEqualTo(right);
	}

	@Test
	public void shouldExceptionNotBeThrownWhenTheresNoRangeDelimiter()
	{
		// given
		final String input = "left";

		// when
		final Pair<String, String> output = RangeParserUtils.splitByRangeSeparator(input);

		// then
		assertThat(output.getLeft()).isEqualTo(input);
		assertThat(output.getRight()).isEqualTo(StringUtils.EMPTY);
	}

	@Test
	public void shouldDeleteFromPrefix()
	{
		// given
		final String val = "val";
		final String input = RangeParserUtils.RANGE_FROM_PREFIX + val;

		// when
		final String output = RangeParserUtils.deleteFromPrefix(input);

		// then
		assertThat(output).isEqualTo(val);
	}

	@Test
	public void shouldDeleteToPrefix()
	{
		// given
		final String val = "val";
		final String input = RangeParserUtils.RANGE_TO_PREFIX + val;

		// when
		final String output = RangeParserUtils.deleteToPrefix(input);

		// then
		assertThat(output).isEqualTo(val);
	}

	@Test
	public void shouldDeletePrefixFromImportParameters()
	{
		// given
		final List<Map<String, String>> params = createParamsWithPrefix(RangeParserUtils.RangeBounds.FROM);
		final ImportParameters importParameters = new ImportParameters(null, null, null, null, params);

		// when
		final ImportParameters output = RangeParserUtils.deletePrefixFromImportParameters(importParameters,
				RangeParserUtils.RangeBounds.FROM);

		// then
		output.getMultiValueParameters().forEach( //
				p -> assertThat( //
						p.entrySet() //
								.stream() //
								.filter(entry -> entry.getKey().contains(RangeParserUtils.RANGE_FROM_PREFIX)) //
								.collect(getMapCollector()) //
				).isEmpty());
	}

	@Test
	public void shouldDeletePrefixFromParsedValues()
	{
		// given
		final List<Map<String, String>> params = createParamsWithPrefix(RangeParserUtils.RangeBounds.FROM);
		final ParsedValues parsedValues = new ParsedValues("any", params);

		// when
		final ParsedValues output = RangeParserUtils.deletePrefixFromParsedValues(parsedValues, RangeParserUtils.RangeBounds.FROM);

		// then
		output.getParameters().forEach( //
				p -> assertThat( //
						p.entrySet() //
								.stream() //
								.filter(entry -> entry.getKey().contains(RangeParserUtils.RANGE_FROM_PREFIX)) //
								.collect(getMapCollector()) //
				).isEmpty());
	}

	@Test
	public void shouldAppendPrefixToParsedValues()
	{
		// given
		final List<Map<String, String>> params = createParamsWithPrefix(null);
		final ParsedValues parsedValues = new ParsedValues("any", params);

		// when
		final ParsedValues output = RangeParserUtils.appendPrefixToParsedValues(parsedValues, RangeParserUtils.RangeBounds.FROM);

		// then
		output.getParameters().forEach( //
				p -> assertThat( //
						p.entrySet() //
								.stream() //
								.filter(entry -> entry.getKey().contains(RangeParserUtils.RANGE_FROM_PREFIX)) //
								.collect(getMapCollector()).size() //
				).isEqualTo(1));
	}

	private List<Map<String, String>> createParamsWithPrefix(final RangeParserUtils.RangeBounds rangeBounds)
	{
		final UnaryOperator<String> convert = val -> {
			if (rangeBounds == null)
			{
				return val;
			}

			return rangeBounds == RangeParserUtils.RangeBounds.FROM ? RangeParserUtils.prependFromPrefix(val)
					: RangeParserUtils.prependToPrefix(val);

		};
		final Map<String, String> map1 = new HashMap<>();
		map1.put(convert.apply(ImportParameters.RAW_VALUE), "val1");
		map1.put(convert.apply("key1"), "val2");
		final Map<String, String> map2 = new HashMap<>();
		map2.put(convert.apply(ImportParameters.RAW_VALUE), "val1");
		map2.put(convert.apply("key2"), "val2");

		return Lists.newArrayList( //
				map1, map2 //
		);
	}

	private Collector<Map.Entry<String, String>, ?, Map<String, String>> getMapCollector()
	{
		return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
	}

}
