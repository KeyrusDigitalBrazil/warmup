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
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.util.ExcelDateUtils;


@RunWith(MockitoJUnitRunner.class)
public class ExcelDateValueConverterTest
{

	@Mock
	private ExcelDateUtils excelDateUtils;

	@Mock
	ExcelAttribute excelAttribute;

	@InjectMocks
	private ExcelDateValueConverter converter;

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsDate()
	{
		// given
		given(excelAttribute.getType()).willReturn(Date.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("09.04.2018 14:51:21"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldNotBeEligibleToConvertingWhenTypeIsNotDate()
	{
		// given
		given(excelAttribute.getType()).willReturn(String.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isFalse();
	}

	@Test
	public void shouldConvertCorrectDateValue()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final String cellValue = "09.04.2018 14:51:21";
		final ImportParameters importParameters = prepareImportParameters(cellValue);

		// when
		converter.convert(excelAttribute, importParameters);

		// then
		Mockito.verify(excelDateUtils).convertToImportedDate(cellValue);
	}

	@Test
	public void shouldThrowDateTimeParseExceptionWhenCellValueIsNull()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final ImportParameters importParameters = prepareImportParameters(null);

		// when
		converter.convert(excelAttribute, importParameters);

		// then exception is thrown
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		return new ImportParameters(null, null, cellValue, null, Collections.emptyList());
	}
}
