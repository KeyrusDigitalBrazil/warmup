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

import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;


public class ExcelValueConverterRegistryTest
{

	@Test
	public void shouldFindAppropriateConverted()
	{
		// given
		final ExcelNullValueConverter excelNullValueConverter = mock(ExcelNullValueConverter.class);
		final ExcelNumberValueConverter excelNumberValueConverter = mock(ExcelNumberValueConverter.class);
		final ExcelValueConverterRegistry registry = new ExcelValueConverterRegistry();
		registry.setConverters(Arrays.asList(excelNullValueConverter, excelNumberValueConverter));
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, "en", "3.14", null,
				new ArrayList<>());
		given(excelNullValueConverter.canConvert(excelAttribute, importParameters)).willReturn(true);
		given(excelNumberValueConverter.canConvert(excelAttribute, importParameters)).willReturn(true);

		// when
		final Optional<ExcelValueConverter> foundConverted = registry.getConverter(excelAttribute, importParameters,
				excelNullValueConverter.getClass());

		// then
		assertThat(foundConverted).isPresent();
		assertThat(foundConverted.get()).isEqualTo(excelNumberValueConverter);
	}
}
