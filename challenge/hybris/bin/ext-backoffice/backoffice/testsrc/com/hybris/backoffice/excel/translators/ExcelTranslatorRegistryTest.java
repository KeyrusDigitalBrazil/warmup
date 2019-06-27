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
package com.hybris.backoffice.excel.translators;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;


public class ExcelTranslatorRegistryTest
{

	@Test
	public void shouldReturnTrueIfAtLeastOneTranslatorIsAbleToHandleRequest()
	{
		// given
		final ExcelValueTranslator translator = mock(ExcelValueTranslator.class);
		final ExcelTranslatorRegistry registry = new ExcelTranslatorRegistry();
		final AttributeDescriptorModel excelAttribute = mock(AttributeDescriptorModel.class);
		registry.setTranslators(Arrays.asList(translator));
		given(translator.canHandle(excelAttribute)).willReturn(true);

		// when
		final boolean canHandle = registry.canHandle(excelAttribute);

		// them
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldReturnFalseIfNoTranslatorIsAbleToHandleRequest()
	{
		// given
		final ExcelValueTranslator translator = mock(ExcelValueTranslator.class);
		final ExcelTranslatorRegistry registry = new ExcelTranslatorRegistry();
		final AttributeDescriptorModel excelAttribute = mock(AttributeDescriptorModel.class);
		registry.setTranslators(Arrays.asList(translator));
		given(translator.canHandle(excelAttribute)).willReturn(false);

		// when
		final boolean canHandle = registry.canHandle(excelAttribute);

		// them
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldReturnFirstTranslatorWhichIsAbleToHandleRequest()
	{
		// given
		final ExcelValueTranslator translator1 = mock(ExcelValueTranslator.class);
		final ExcelValueTranslator translator2 = mock(ExcelValueTranslator.class);
		final ExcelValueTranslator translator3 = mock(ExcelValueTranslator.class);
		final ExcelTranslatorRegistry registry = new ExcelTranslatorRegistry();
		final AttributeDescriptorModel excelAttribute = mock(AttributeDescriptorModel.class);
		registry.setTranslators(Arrays.asList(translator1, translator2, translator3));
		given(translator2.canHandle(excelAttribute)).willReturn(true);

		// when
		final Optional<ExcelValueTranslator<Object>> foundTranslator = registry.getTranslator(excelAttribute);

		// them
		assertThat(foundTranslator).isPresent();
		assertThat(foundTranslator.get()).isEqualTo(translator2);
	}

	@Test
	public void shouldReturnFirstTranslatorWhichIsAbleToHandleRequestAndIsNotExcluded()
	{
		// given
		final ExcelValueTranslator translator1 = mock(ExcelValueTranslator.class);
		final ExcelValueTranslator translator2 = mock(ExcelValueTranslator.class);
		final ExcelValueTranslator translator3 = mock(ExcelValueTranslator.class);
		final ExcelTranslatorRegistry registry = Mockito.spy(ExcelTranslatorRegistry.class);
		final AttributeDescriptorModel excelAttribute = mock(AttributeDescriptorModel.class);
		final List translators = new ArrayList<>();
		translators.add(translator1);
		translators.add(translator2);
		translators.add(translator3);
		registry.setTranslators(translators);
		doReturn(ExcelJavaTypeTranslator.class).when(registry).getTranslatorClass(translator1);
		doReturn(ExcelEnumTypeTranslator.class).when(registry).getTranslatorClass(translator2);
		doReturn(ExcelEnumTypeTranslator.class).when(registry).getTranslatorClass(translator3);
		given(translator1.canHandle(excelAttribute)).willReturn(true);
		given(translator2.canHandle(excelAttribute)).willReturn(true);

		// when
		final Optional<ExcelValueTranslator<Object>> foundTranslator = registry.getTranslator(excelAttribute,
				ExcelJavaTypeTranslator.class);

		// them
		assertThat(foundTranslator).isPresent();
		assertThat(foundTranslator.get()).isEqualTo(translator2);
	}
}
