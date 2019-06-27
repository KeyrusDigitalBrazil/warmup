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

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;


@RunWith(MockitoJUnitRunner.class)
public class ExcelNumberValueConverterTest
{

	private final ExcelValueConverter converter = new ExcelNumberValueConverter();
	final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsByte()
	{
		// given
		given(excelAttribute.getType()).willReturn(Byte.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("1"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsShort()
	{
		// given
		given(excelAttribute.getType()).willReturn(Short.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("1"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsInteger()
	{
		// given
		given(excelAttribute.getType()).willReturn(Integer.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("1"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsLong()
	{
		// given
		given(excelAttribute.getType()).willReturn(Long.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("1"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsFloat()
	{
		// given
		given(excelAttribute.getType()).willReturn(Float.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("1"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsDouble()
	{
		// given
		given(excelAttribute.getType()).willReturn(Double.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("1"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldNotBeEligibleToConvertingWhenTypeIsNotANumber()
	{
		// given
		given(excelAttribute.getType()).willReturn(String.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isFalse();
	}

	@Test
	public void shouldConvertValueForByteType()
	{
		// given
		given(excelAttribute.getType()).willReturn(Byte.class.getName());
		final ImportParameters importParameters = prepareImportParameters("1");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Byte.class);
		assertThat(convertedValue).isEqualTo((byte) 1);
	}

	@Test
	public void shouldConvertValueForShortType()
	{
		// given
		given(excelAttribute.getType()).willReturn(Short.class.getName());
		final ImportParameters importParameters = prepareImportParameters("15");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Short.class);
		assertThat(convertedValue).isEqualTo((short) 15);
	}

	@Test
	public void shouldConvertValueForIntegerType()
	{
		// given
		given(excelAttribute.getType()).willReturn(Integer.class.getName());
		final ImportParameters importParameters = prepareImportParameters("123456");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Integer.class);
		assertThat(convertedValue).isEqualTo(123456);
	}

	@Test
	public void shouldConvertValueForLongType()
	{
		// given
		given(excelAttribute.getType()).willReturn(Long.class.getName());
		final ImportParameters importParameters = prepareImportParameters("1234567898");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Long.class);
		assertThat(convertedValue).isEqualTo((long) 1234567898);
	}

	@Test
	public void shouldConvertValueForFloatType()
	{
		// given
		given(excelAttribute.getType()).willReturn(Float.class.getName());
		final ImportParameters importParameters = prepareImportParameters("12345.67898");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Float.class);
		assertThat(convertedValue).isEqualTo((float) 12345.67898);
	}

	@Test
	public void shouldConvertValueForDoubleType()
	{
		// given
		given(excelAttribute.getType()).willReturn(Double.class.getName());
		final ImportParameters importParameters = prepareImportParameters("12345.678987654321");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Double.class);
		assertThat(convertedValue).isEqualTo((double) 12345.678987654321);
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		return new ImportParameters(null, null, cellValue, null, Collections.emptyList());
	}
}
