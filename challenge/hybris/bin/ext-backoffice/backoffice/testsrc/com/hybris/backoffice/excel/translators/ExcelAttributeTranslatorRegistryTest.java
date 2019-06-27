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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.translators.classification.ExcelClassificationJavaTypeTranslator;


public class ExcelAttributeTranslatorRegistryTest
{

	@Test
	public void shouldReturnTrueIfAtLeastOneTranslatorIsAbleToHandleRequest()
	{
		// given
		final ExcelAttributeTranslator translator = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslatorRegistry registry = new ExcelAttributeTranslatorRegistry();
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
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
		final ExcelAttributeTranslator translator = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslatorRegistry registry = new ExcelAttributeTranslatorRegistry();
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
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
		final ExcelAttributeTranslator translator1 = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslator translator2 = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslator translator3 = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslatorRegistry registry = new ExcelAttributeTranslatorRegistry();
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		registry.setTranslators(Arrays.asList(translator1, translator2, translator3));
		given(translator2.canHandle(excelAttribute)).willReturn(true);

		// when
		final Optional<ExcelAttributeTranslator<ExcelAttribute>> foundTranslator = registry.findTranslator(excelAttribute);

		// them
		assertThat(foundTranslator).isPresent();
		assertThat(foundTranslator.get()).isEqualTo(translator2);
	}

	@Test
	public void shouldReturnFirstTranslatorWhichIsAbleToHandleRequestAndIsNotExcluded()
	{
		// given
		final ExcelAttributeTranslator<ExcelAttribute> translator1 = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslator<ExcelAttribute> translator2 = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslator<ExcelAttribute> translator3 = mock(ExcelAttributeTranslator.class);
		final ExcelAttributeTranslatorRegistry registry = Mockito.spy(ExcelAttributeTranslatorRegistry.class);
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final List translators = new ArrayList<>();
		translators.add(translator1);
		translators.add(translator2);
		translators.add(translator3);
		registry.setTranslators(translators);
		doReturn(ExcelClassificationJavaTypeTranslator.class).when(registry).getTranslatorClass(translator1);
		doReturn(ExcelAttributeTranslator.class).when(registry).getTranslatorClass(translator2);
		doReturn(ExcelAttributeTranslator.class).when(registry).getTranslatorClass(translator3);
		given(translator1.canHandle(excelAttribute)).willReturn(true);
		given(translator2.canHandle(excelAttribute)).willReturn(true);

		// when
		final Optional<ExcelAttributeTranslator<ExcelAttribute>> foundTranslator = registry.findTranslator(excelAttribute,
				ExcelClassificationJavaTypeTranslator.class);

		// them
		assertThat(foundTranslator).isPresent();
		assertThat(foundTranslator.get()).isEqualTo(translator2);
	}
}
